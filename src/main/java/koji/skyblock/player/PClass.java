package koji.skyblock.player;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import koji.developerkit.KBase;
import koji.developerkit.runnable.KRunnable;
import koji.developerkit.utils.duplet.Duplet;
import koji.developerkit.utils.duplet.DupletList;
import koji.developerkit.utils.duplet.Triplet;
import koji.developerkit.utils.duplet.Tuple;
import koji.developerkit.utils.xseries.XMaterial;
import koji.developerkit.utils.xseries.messages.ActionBar;
import koji.skyblock.Skyblock;
import koji.skyblock.commands.Messages;
import koji.skyblock.files.Config;
import koji.skyblock.files.Files;
import koji.skyblock.files.data.PlayerData;
import koji.skyblock.files.pets.PetData;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.Rarity;
import koji.skyblock.item.ability.Ability;
import koji.skyblock.pets.Pet;
import koji.skyblock.pets.PetInstance;
import koji.skyblock.player.api.ManaUseEvent;
import koji.skyblock.player.scoreboard.ScoreboardState;
import koji.skyblock.player.scoreboard.Scoreboards;
import koji.skyblock.utils.StatMap;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PClass extends KBase {
   private final HashMap scoreboards = new HashMap();
   private static final HashMap playerStats = new HashMap();
   private final Player p;
   private final StatMap stats = new StatMap(new Duplet[0]);
   private final HashMap statMultipliers = new HashMap();
   private final HashMap itemStatGain = new HashMap();
   private final HashMap bonusStats = new HashMap();
   private final HashMap absorption = new HashMap();
   private final HashMap damageReduction = new HashMap();
   private static final HashMap abilityActionMap = new HashMap();
   ScoreboardState state;
   private int bits;
   private final HashMap cooldownRunnable;
   private final HashMap cooldownVisual;
   ArrayList itemStash;
   private boolean onFerocityCooldown;
   private Entity lastAttackedEntity;
   private final PetInstance petInstance;

   public static boolean hasPlayer(Player p) {
      return playerStats.containsKey(p.getUniqueId());
   }

   public static PClass getPlayer(Player p) {
      if (!playerStats.containsKey(p.getUniqueId())) {
         PClass pS = new PClass(p);
         addPlayer(pS);
         return pS;
      } else {
         return (PClass)playerStats.get(p.getUniqueId());
      }
   }

   public static void addPlayer(PClass pS) {
      playerStats.put(pS.player().getUniqueId(), pS);
   }

   public Player player() {
      return this.p;
   }

   public PClass(Player p) {
      this.state = ScoreboardState.DEFAULT;
      this.itemStash = new ArrayList();
      this.onFerocityCooldown = false;
      this.lastAttackedEntity = null;
      this.p = p;
      this.cooldownVisual = new HashMap();
      this.cooldownRunnable = new HashMap();
      this.loadDefaultStats();
      ScoreboardState[] var2 = ScoreboardState.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ScoreboardState pSS = var2[var4];
         Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
         Objective obj = board.registerNewObjective("KSB", "Dummy");
         obj.setDisplaySlot(DisplaySlot.SIDEBAR);
         obj.setDisplayName(Messages.SCOREBOARD_TITLE.getMessage());
         List scoreList = Files.getConfig().getStringList("modules.scoreboard." + pSS.getState() + ".text");
         DupletList scoreboardInfo = new DupletList();
         ArrayList teams = new ArrayList();
         String teamEntries = "abcdefghijklmnopqrstuvwxyz0123456789";
         Iterator var12 = scoreList.iterator();

         while(var12.hasNext()) {
            String s = (String)var12.next();
            Team team = board.registerNewTeam(UUID.randomUUID().toString().substring(0, 15));
            String teamEntry = "§" + teamEntries.charAt(scoreboardInfo.size()) + "§r";
            team.addEntry(teamEntry);
            scoreboardInfo.add(Tuple.of(s, teamEntry));
            teams.add(team);
         }

         this.scoreboards.put(pSS, Tuple.of(board, teams, scoreboardInfo));
      }

      p.setScoreboard((Scoreboard)((Triplet)this.scoreboards.get(this.state)).getFirst());
      this.bits = PlayerData.getPlayerData().getBits(p);
      this.petInstance = new PetInstance(p);
      String activePet = PlayerData.getPlayerData().getActivePet(p);
      if (activePet != null && !activePet.equalsIgnoreCase("null") && PetData.getPetData().getPetExists(p, activePet)) {
         String uuid = PlayerData.getPlayerData().getActivePet(p);
         PetData petData = PetData.getPetData();
         Pet pet = Pet.matchFromType(petData.getType(p, uuid));
         int level = petData.getLevel(p, uuid);
         double currentXp = petData.getCurrentExp(p, uuid);
         String skin = petData.getSkin(p, uuid);
         Rarity rarity = petData.getRarity(p, uuid);
         this.petInstance.set(uuid, pet, level, currentXp, skin, rarity);
      }

   }

   public StatMap getStatDifference(CustomItem item) {
      if (item == null) {
         return this.getStats();
      } else {
         StatMap stats = this.stats;
         StatMap itemInHandStats;
         if (isValidItem(this.p.getItemInHand())) {
            CustomItem itemInHand = new CustomItem(this.p.getItemInHand());
            itemInHandStats = itemInHand.getStats();
         } else {
            itemInHandStats = new StatMap(new Duplet[0]);
         }

         return stats.combine(itemInHandStats.invert()).combine(item.getStats());
      }
   }

   public void setStat(Stats s, double i) {
      if (s == Stats.HEALTH || s == Stats.MANA) {
         double stat = s == Stats.HEALTH ? this.getStat(Stats.MAX_HEALTH) : this.getStat(Stats.MAX_MANA);
         i = Math.max(0.0D, Math.min(i, stat));
      }

      this.stats.put(s, i);
   }

   public void addStat(Stats s, double i) {
      this.setStat(s, this.getStat(s) + i);
   }

   public void removeStat(Stats s, double i) {
      this.setStat(s, this.getStat(s) - i);
   }

   public StatMap getStats() {
      return this.stats;
   }

   public double getStat(Stats statName) {
      return this.stats.get(statName);
   }

   public void setMultiplyStat(String reason, Stats s, double i) {
      StatMap map = (StatMap)this.statMultipliers.getOrDefault(reason, new StatMap(new Duplet[0]));
      map.put(s, i);
      this.statMultipliers.put(reason, map);
   }

   public void addMultiplyStat(String reason, Stats s, double i) {
      StatMap map = (StatMap)this.statMultipliers.getOrDefault(reason, new StatMap(new Duplet[0]));
      map.put(s, (Double)map.getOrDefault(s, 1.0D) + i);
      this.statMultipliers.put(reason, map);
   }

   public void removeMultiplyStat(String reason, Stats s, double i) {
      StatMap map = (StatMap)this.statMultipliers.getOrDefault(reason, new StatMap(new Duplet[0]));
      map.put(s, (Double)map.getOrDefault(s, 1.0D) - i);
      this.statMultipliers.put(reason, map);
   }

   public StatMap getMultiplyingStats(String reason) {
      return (StatMap)this.statMultipliers.getOrDefault(reason, new StatMap(new Duplet[0]));
   }

   public void resetMultiplyingStats(String reason) {
      this.statMultipliers.put(reason, new StatMap(new Duplet[0]));
   }

   public void setItemStatGain(String reason, String itemId, Stats s, double i) {
      StatMap map = (StatMap)((HashMap)this.itemStatGain.getOrDefault(reason, new HashMap())).getOrDefault(itemId, new StatMap(new Duplet[0]));
      map.put(s, i);
      ((HashMap)this.itemStatGain.getOrDefault(reason, new HashMap())).put(itemId, map);
   }

   public void addItemStatGain(String reason, String itemId, Stats s, double i) {
      StatMap map = (StatMap)((HashMap)this.itemStatGain.getOrDefault(reason, new HashMap())).getOrDefault(itemId, new StatMap(new Duplet[0]));
      map.put(s, map.get(s) + i);
      ((HashMap)this.itemStatGain.getOrDefault(reason, new HashMap())).put(itemId, map);
   }

   public void removeItemStatGain(String reason, String itemId, Stats s, double i) {
      StatMap map = (StatMap)((HashMap)this.itemStatGain.getOrDefault(reason, new HashMap())).getOrDefault(itemId, new StatMap(new Duplet[0]));
      map.put(s, map.get(s) - i);
      ((HashMap)this.itemStatGain.getOrDefault(reason, new HashMap())).put(itemId, map);
   }

   public HashMap getItemStatGainFromReason(String reason) {
      return (HashMap)this.itemStatGain.getOrDefault(reason, new HashMap());
   }

   public StatMap getItemStatGain(String reason, String itemId) {
      return (StatMap)((HashMap)this.itemStatGain.getOrDefault(reason, new HashMap())).getOrDefault(itemId, new StatMap(new Duplet[0]));
   }

   public void resetItemStatGain(String reason, String itemId) {
      ((HashMap)this.itemStatGain.getOrDefault(reason, new HashMap())).put(itemId, new StatMap(new Duplet[0]));
   }

   public void resetItemStatGain(String reason) {
      this.itemStatGain.put(reason, new HashMap());
   }

   public void setBonusStats(String id, StatMap map) {
      this.bonusStats.put(id, map);
   }

   public void setBonusStat(String id, Stats s, double i) {
      StatMap map = (StatMap)this.bonusStats.getOrDefault(id, new StatMap(new Duplet[0]));
      map.put(s, i);
      this.bonusStats.put(id, map);
   }

   public void addBonusStats(String id, StatMap map) {
      this.bonusStats.put(id, ((StatMap)this.bonusStats.getOrDefault(id, new StatMap(new Duplet[0]))).combine(map));
   }

   public void addBonusStat(String id, Stats s, double i) {
      StatMap map = (StatMap)this.bonusStats.getOrDefault(id, new StatMap(new Duplet[0]));
      map.put(s, map.get(s) + i);
      this.bonusStats.put(id, map);
   }

   public void removeBonusStat(String id, Stats s, double i) {
      StatMap map = (StatMap)this.bonusStats.getOrDefault(id, new StatMap(new Duplet[0]));
      map.put(s, map.get(s) - i);
      this.bonusStats.put(id, map);
   }

   public StatMap getBonusStats(String id) {
      return (StatMap)this.bonusStats.getOrDefault(id, new StatMap(new Duplet[0]));
   }

   public void resetBonusStats(String id) {
      this.bonusStats.put(id, new StatMap(new Duplet[0]));
   }

   public double getAbsorption() {
      AtomicReference total = new AtomicReference(0.0D);
      (new HashMap(this.absorption)).forEach((key, value) -> {
         Double var10000 = (Double)total.updateAndGet((v) -> {
            return v + value;
         });
      });
      return (Double)total.get();
   }

   public double subtractAbsorption(double minus) {
      double value;
      for(Iterator var3 = this.absorption.keySet().iterator(); var3.hasNext(); minus -= value) {
         String key = (String)var3.next();
         value = (Double)this.absorption.get(key);
         if (value - minus > 0.0D) {
            this.absorption.put(key, value - minus);
            break;
         }

         this.absorption.remove(key);
      }

      return Math.max(0.0D, minus);
   }

   public double getAbsorption(String reason) {
      return (Double)this.absorption.getOrDefault(reason, 0.0D);
   }

   public void setAbsorption(String reason, double value) {
      this.absorption.put(reason, value);
   }

   public void addAbsorption(String reason, double value) {
      this.absorption.put(reason, (Double)this.absorption.getOrDefault(reason, 0.0D) + value);
   }

   public void resetAbsorption(String reason) {
      this.absorption.remove(reason);
   }

   public double getTotalDamageReducAsPercent() {
      AtomicReference total = new AtomicReference(0.0D);
      (new HashMap(this.damageReduction)).forEach((key, value) -> {
         if ((Boolean)value.getFirst()) {
            this.damageReduction.remove(key);
         }

         total.updateAndGet((v) -> {
            return v + (Double)value.getSecond();
         });
      });
      return (Double)total.get() / 100.0D;
   }

   public double getDamageReduction(String reason) {
      return (Double)((Duplet)this.damageReduction.getOrDefault(reason, Tuple.of(false, 0.0D))).getSecond();
   }

   public void setDamageReduction(String reason, double added, boolean selfRemoves) {
      this.damageReduction.put(reason, Tuple.of(selfRemoves, added));
   }

   public void addDamageReduction(String reason, double added, boolean selfRemoves) {
      Duplet tuple = (Duplet)this.damageReduction.getOrDefault(reason, Tuple.of(selfRemoves, 0.0D));
      tuple.setSecond((Double)tuple.getSecond() + added);
      this.damageReduction.put(reason, tuple);
   }

   public void resetDamageReduction(String reason) {
      this.damageReduction.remove(reason);
   }

   public void regenStats() {
      double healthScale = Math.min(20.0D + (this.getStat(Stats.MAX_HEALTH) - PlayerData.getPlayerData().getStat(this.p, Stats.MAX_HEALTH)) / 100.0D, 40.0D);
      this.p.setHealthScale(healthScale);

      try {
         this.p.setMaxHealth(this.getStat(Stats.MAX_HEALTH));
      } catch (Exception var10) {
      }

      this.addStat(Stats.MANA, this.getStat(Stats.MAX_MANA) * 0.02D);
      EntityRegainHealthEvent eRHE = new EntityRegainHealthEvent(this.p, (this.getStat(Stats.MAX_HEALTH) * 0.01D + 1.5D) * (this.getStat(Stats.HEALTH_REGEN) / 100.0D), RegainReason.REGEN);
      this.addStat(Stats.HEALTH, eRHE.getAmount());

      try {
         this.p.setWalkSpeed((float)(this.getStat(Stats.SPEED) * 0.002D));
      } catch (Exception var9) {
         this.p.setWalkSpeed(1.0F);
      }

      try {
         this.p.setHealth(this.getStat(Stats.HEALTH));
      } catch (Exception var11) {
         File spigotYML = new File("spigot.yml");
         FileConfiguration spigotFC = YamlConfiguration.loadConfiguration(spigotYML);
         if (spigotFC.getDouble("settings.attribute.maxHealth.max") < this.getStat(Stats.HEALTH)) {
            spigotFC.set("settings.attribute.maxHealth.max", 1.5E7D);

            try {
               spigotFC.save(spigotYML);
            } catch (IOException var8) {
               var8.printStackTrace();
            }
         }
      }

   }

   public void updateActionbar() {
      String healthPart = "§c" + (int)(this.getStat(Stats.HEALTH) + this.getAbsorption()) + "/" + (int)this.getStat(Stats.MAX_HEALTH) + "❤";
      String defensePart = this.getStat(Stats.DEFENSE) > 0.0D ? "   §a" + (int)this.getStat(Stats.DEFENSE) + "❈ Defense" : "";
      String manaPart = "    §b" + (int)this.getStat(Stats.MANA) + "/" + (int)this.getStat(Stats.MAX_MANA) + "✎ Mana";
      if (abilityActionMap.containsKey(this.p)) {
         Triplet list = (Triplet)abilityActionMap.get(this.p);
         if (!((String)list.getFirst()).equals("NO_MANA")) {
            defensePart = "    §b-" + list.getSecond() + " Mana (§6" + (String)list.getFirst() + "§b)";
         } else {
            manaPart = "    §c§lNOT ENOUGH MANA ";
         }

         if ((Integer)list.getThird() - 1 != 0) {
            abilityActionMap.put(this.p, Tuple.of(list.getFirst(), list.getSecond(), (Integer)list.getThird() - 1));
         } else {
            abilityActionMap.remove(this.p);
         }
      }

      if (this.getAbsorption() != 0.0D) {
         healthPart = healthPart.replace("§c", "§6");
      }

      if (Files.getConfig().getBoolean("modules.actionbar.enabled")) {
         ActionBar.sendActionBar(this.p, healthPart + defensePart + manaPart);
      }

   }

   public void updateStats() {
      if (!PlayerData.getPlayerData().doesPlayerDataExist(this.p)) {
         Skyblock.getPlugin().getLogger().log(Level.WARNING, "Player data file does not exist for " + this.p.getDisplayName() + ", resetting data");
         PlayerData.getPlayerData().createPlayer(this.p);
      }

      Stats[] var1 = Stats.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Stats s = var1[var3];
         if (!s.equals(Stats.HEALTH) && !s.equals(Stats.MANA)) {
            double finalAmount = PlayerData.getPlayerData().getStat(this.p, s);
            ItemStack[] var7 = this.p.getInventory().getArmorContents();
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               ItemStack is = var7[var9];
               if (isValidItem(is) && is.hasItemMeta()) {
                  CustomItem ci = new CustomItem(is);
                  finalAmount += ci.getCombinedStat(s);
               }
            }

            if (this.petInstance.isActive()) {
               Pet pet = this.petInstance.getPet();
               if (pet != null) {
                  finalAmount += ((StatMap)pet.baseStats().getOrDefault(this.petInstance.getRarity(), new StatMap(new Duplet[0]))).get(s);
                  finalAmount += ((StatMap)pet.addStatPerLevel().getOrDefault(this.petInstance.getRarity(), new StatMap(new Duplet[0]))).get(s) * (double)this.petInstance.getLevel();
               }
            }

            ItemStack iS = this.p.getItemInHand();
            if (isValidItem(iS)) {
               CustomItem ci = new CustomItem(iS);
               if (!ci.hasKey("petType") && !iS.getType().toString().toUpperCase().contains("HELMET") && !iS.getType().toString().toUpperCase().contains("CHESTPLATE") && !iS.getType().toString().toUpperCase().contains("LEGGINGS") && !iS.getType().toString().toUpperCase().contains("BOOTS") && !iS.getType().equals(XMaterial.PLAYER_HEAD.parseMaterial())) {
                  finalAmount += ci.getCombinedStat(s);
                  finalAmount += (double)(ci.getPotatoBookAmount(s) * ci.getPotatoBookStatBuff(s));
                  Iterator var16 = this.itemStatGain.keySet().iterator();

                  while(var16.hasNext()) {
                     String reasons = (String)var16.next();
                     if (!ci.getID().equals("null") && !((StatMap)((HashMap)this.itemStatGain.get(reasons)).getOrDefault(ci.getID(), new StatMap(new Duplet[0]))).isEmpty()) {
                        finalAmount += ((StatMap)((HashMap)this.itemStatGain.get(reasons)).get(ci.getID())).get(s);
                     }
                  }
               }
            }

            Iterator var15;
            String reason;
            for(var15 = this.bonusStats.keySet().iterator(); var15.hasNext(); finalAmount += ((StatMap)this.bonusStats.get(reason)).get(s)) {
               reason = (String)var15.next();
            }

            for(var15 = this.statMultipliers.keySet().iterator(); var15.hasNext(); finalAmount *= (Double)((StatMap)this.statMultipliers.get(reason)).getOrDefault(s, 1.0D)) {
               reason = (String)var15.next();
            }

            this.setStat(s, finalAmount);
         }
      }

   }

   private void loadDefaultStats() {
      Iterator var1 = Stats.getNormalValues().iterator();

      while(var1.hasNext()) {
         Stats stat = (Stats)var1.next();
         this.setStat(stat, PlayerData.getPlayerData().getStat(this.p, stat));
      }

      this.setStat(Stats.HEALTH, this.getStat(Stats.MAX_HEALTH));
      this.setStat(Stats.MANA, this.getStat(Stats.MAX_MANA));
   }

   public static String format(double amount) {
      DecimalFormat formatter = (DecimalFormat)NumberFormat.getNumberInstance(Locale.US);
      String pattern = "###.#";
      if (amount < 1000.0D) {
         pattern = "##0.0";
      }

      formatter.applyPattern(pattern);
      formatter.setGroupingUsed(true);
      formatter.setGroupingSize(3);
      return formatter.format(amount);
   }

   public void updateScoreboard() {
      double balance = Skyblock.getPlugin().getEconomy().getBalance(this.p);
      HashMap placeholders = new HashMap();
      LocalDate l_date = LocalDate.now();
      placeholders.put("localtime", l_date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
      placeholders.put("worldtime", Scoreboards.parseTime(this.p.getWorld().getTime()));
      placeholders.put("bits", commaify(this.bits));
      String purse = format(balance);
      placeholders.put("purse", purse);
      if (Files.getConfig().getBoolean("modules.scoreboard." + this.state.getState() + ".enabled")) {
         Scoreboard scoreboard = (Scoreboard)((Triplet)this.scoreboards.get(this.state)).getFirst();
         Objective obj = scoreboard.getObjective("KSB");
         ArrayList teams = (ArrayList)((Triplet)this.scoreboards.get(this.state)).getSecond();
         DupletList scoreboardInfo = (DupletList)((Triplet)this.scoreboards.get(this.state)).getThird();

         for(int i = 0; i < scoreboardInfo.size(); ++i) {
            Duplet triplet = (Duplet)scoreboardInfo.get(i);
            Team team = (Team)teams.get(i);
            String entry = (String)triplet.getSecond();
            String line = (String)triplet.getFirst();
            if (Skyblock.getPlugin().isPAPIEnabled()) {
               line = PlaceholderAPI.setPlaceholders(this.p, line);
            }

            Iterator var15 = placeholders.keySet().iterator();

            while(var15.hasNext()) {
               String key = (String)var15.next();
               String place = "%" + key + "%";
               if (line.contains(place)) {
                  line = line.replace(place, (CharSequence)placeholders.get(key));
               }
            }

            String[] parsedLine = Scoreboards.parseText(color(line));
            team.setPrefix(parsedLine[0]);
            team.setSuffix(parsedLine[1]);
            obj.getScore(entry).setScore(scoreboardInfo.size() - i);
         }
      }

   }

   public void setCooldown(String s, double i) {
      if (this.cooldownRunnable.containsKey(s)) {
         ((KRunnable)((Duplet)this.cooldownRunnable.get(s)).getFirst()).cancel();
         ((KRunnable)((Duplet)this.cooldownRunnable.get(s)).getSecond()).cancel();
         this.cooldownRunnable.remove(s);
         this.cooldownVisual.remove(s);
      }

      KRunnable one = new KRunnable((task) -> {
         this.cooldownRunnable.remove(s);
         this.cooldownVisual.remove(s);
      });
      KRunnable two = new KRunnable((task) -> {
      });
      if (i > 1.0D) {
         int num = (int)i;
         two = new KRunnable((task) -> {
            Integer var10000 = (Integer)this.cooldownVisual.put(s, (Integer)this.cooldownVisual.getOrDefault(s, num) - 1);
         }, (long)(i * 20.0D));
      }

      this.cooldownVisual.put(s, (int)i);
      this.cooldownRunnable.put(s, Tuple.of(one, two));
      one.runTaskLater(Skyblock.getPlugin(), (long)(i * 20.0D));
      two.runTaskTimer(Skyblock.getPlugin(), 0L, 20L);
   }

   public Integer getCooldown(String s) {
      return (Integer)this.cooldownVisual.getOrDefault(s, 0) + 1;
   }

   public boolean isOnCooldown(String s) {
      return this.cooldownRunnable.containsKey(s) && !((KRunnable)((Duplet)this.cooldownRunnable.get(s)).getFirst()).isCancelled();
   }

   public HashMap getEnchantLevelsInHand() {
      return (new CustomItem(this.p.getItemInHand())).getEnchants();
   }

   public boolean subtractMana(CustomItem item, String name, String displayName, double cost) {
      try {
         double finalCost = cost == 0.0D ? -1.0D : cost;
         ManaUseEvent mUE = new ManaUseEvent(this.p, item, finalCost);
         Bukkit.getPluginManager().callEvent(mUE);
         finalCost = mUE.getManaCost();
         if (this.isOnCooldown(name)) {
            this.p.sendMessage("§cThis ability is on cooldown for " + this.getCooldown(name) + "s.");
            return false;
         } else {
            boolean actionBar = Config.sendToActionBar();
            Ability ability = CustomItem.getAbility(name);
            if (this.getStat(Stats.MANA) <= this.getStat(Stats.MAX_MANA) && this.getStat(Stats.MANA) < finalCost) {
               if (ability.sendMessage()) {
                  if (!actionBar) {
                     this.p.sendMessage("§cYou don't have enough mana to use §6" + displayName + "§c!");
                  } else {
                     abilityActionMap.put(this.p, Tuple.of("NO_MANA", (int)finalCost, 2));
                  }
               }

               return false;
            } else {
               this.removeStat(Stats.MANA, finalCost);
               if (finalCost != -1.0D && ability.sendMessage()) {
                  if (!actionBar) {
                     this.p.sendMessage("§aUsed §6" + displayName + "§a! §b(" + (int)finalCost + " Mana)");
                  } else {
                     abilityActionMap.put(this.p, Tuple.of(displayName, (int)finalCost, 2));
                  }
               }

               if (ability.getDouble("cooldown") != 0.0D) {
                  this.setCooldown(name, ability.getDouble("cooldown"));
               }

               return true;
            }
         }
      } catch (Throwable var11) {
         throw var11;
      }
   }

   public double getAbilityDamage(double initialDamage, double scaling) {
      return initialDamage * (1.0D + this.getStat(Stats.MANA) / 100.0D * scaling) * (1.0D + this.getStat(Stats.ABILITY_DAMAGE) / 100.0D);
   }

   public ScoreboardState getState() {
      return this.state;
   }

   public void setState(ScoreboardState state) {
      this.state = state;
   }

   public void setBits(int bits) {
      this.bits = bits;
   }

   public ArrayList getItemStash() {
      return this.itemStash;
   }

   public void setItemStash(ArrayList itemStash) {
      this.itemStash = itemStash;
   }

   public boolean isOnFerocityCooldown() {
      return this.onFerocityCooldown;
   }

   public void setOnFerocityCooldown(boolean onFerocityCooldown) {
      this.onFerocityCooldown = onFerocityCooldown;
   }

   public Entity getLastAttackedEntity() {
      return this.lastAttackedEntity;
   }

   public void setLastAttackedEntity(Entity lastAttackedEntity) {
      this.lastAttackedEntity = lastAttackedEntity;
   }

   public PetInstance getPetInstance() {
      return this.petInstance;
   }
}
