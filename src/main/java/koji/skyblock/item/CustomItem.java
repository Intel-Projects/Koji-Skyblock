package koji.skyblock.item;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.stream.Collectors;
import koji.developerkit.utils.ItemBuilder;
import koji.developerkit.utils.duplet.Duplet;
import koji.developerkit.utils.duplet.Tuple;
import koji.developerkit.utils.xseries.XEnchantment;
import koji.developerkit.utils.xseries.XMaterial;
import koji.skyblock.Skyblock;
import koji.skyblock.files.Files;
import koji.skyblock.item.ability.Ability;
import koji.skyblock.item.anvil.EnchantedBook;
import koji.skyblock.item.enchants.Enchant;
import koji.skyblock.item.enchants.Priority;
import koji.skyblock.item.enchants.events.EnchantAddEvent;
import koji.skyblock.item.enchants.events.EnchantRemoveEvent;
import koji.skyblock.item.reforges.Reforge;
import koji.skyblock.player.Stats;
import koji.skyblock.utils.StatMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class CustomItem extends ItemBuilder {
   private static final HashMap abilities = new HashMap();

   public CustomItem(ItemStack item, short data) {
      super(item, data);
   }

   public CustomItem(ItemStack item, short data, int amount) {
      super(item, data, amount);
   }

   public CustomItem(XMaterial material) {
      super(material);
   }

   public CustomItem(XMaterial material, int amount) {
      super(material, amount);
   }

   public CustomItem(ItemStack item) {
      super(item);
   }

   public ItemStack build(List abilities) {
      if (this.isEnchantedBook()) {
         return (new EnchantedBook(this)).build();
      } else {
         ArrayList lore = new ArrayList(this.getStatsLore());
         if (!this.getEnchants().isEmpty()) {
            lore.addAll(this.getEnchantLore());
            lore.add("");
         }

         if (abilities.size() > 1) {
            lore.addAll(abilities);
         }

         String extra = !this.getExtraType().equals("") ? this.getExtraType().toUpperCase() + " " : "";
         lore.add(this.getRarity().getColor() + color("&l" + this.getRarity().getName() + " " + extra + (this.getType() != null ? this.getType().toUpperCase() : "")));
         this.setLore((List)lore);
         if (!this.canBeStacked()) {
            this.setString("stacked", UUID.randomUUID().toString());
         }

         return super.build();
      }
   }

   public ItemStack buildWithAbilities() {
      return this.build(this.getAbilitiesLore());
   }

   public CustomItem setLore(List lore) {
      super.setLore(lore);
      return this;
   }

   public CustomItem setLore(String... lore) {
      super.setLore(lore);
      return this;
   }

   public List getLore() {
      return this.buildWithAbilities().getItemMeta().getLore();
   }

   public StatMap getStats() {
      return this.getStats(false).combine(this.getStats(true));
   }

   public StatMap getStats(boolean reforge) {
      StatMap stats = new StatMap(new Duplet[0]);
      Stats[] var3 = Stats.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Stats s = var3[var5];
         String stat = reforge ? s.reforgeStat() : s.baseStat();
         if (this.hasKey(stat) && this.getDouble(stat) != 0.0D) {
            stats.put(s, this.getDouble(stat));
         }
      }

      return stats;
   }

   public CustomItem addStats(StatMap statMap, boolean reforge) {
      Iterator var3 = statMap.keySet().iterator();

      while(var3.hasNext()) {
         Stats stat = (Stats)var3.next();
         this.addStat(stat, statMap.get(stat), reforge);
      }

      return this;
   }

   public CustomItem removeStats(StatMap statMap, boolean reforge) {
      this.addStats(statMap.invert(), reforge);
      return this;
   }

   public CustomItem setStat(Stats s, double value, boolean reforge) {
      String stat = reforge ? s.reforgeStat() : s.baseStat();
      this.setDouble(stat, value);
      return this;
   }

   public CustomItem addStat(Stats s, double value, boolean reforge) {
      String stat = reforge ? s.reforgeStat() : s.baseStat();
      this.setDouble(stat, this.getDouble(stat) + value);
      return this;
   }

   public CustomItem removeStat(Stats s, double value, boolean reforge) {
      this.addStat(s, -value, reforge);
      return this;
   }

   public double getStat(Stats s, boolean reforge) {
      return this.getDouble(reforge ? s.reforgeStat() : s.baseStat());
   }

   public double getCombinedStat(Stats s) {
      return this.getStat(s, false) + this.getStat(s, true);
   }

   public ArrayList getStatsLore() {
      ArrayList lore = new ArrayList();
      if (this.getGearScore() > 0) {
         lore.add("§7Gear Score: §d" + this.getGearScore());
      }

      Stats[] var2 = Stats.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Stats s = var2[var4];
         if (s != Stats.MANA && s != Stats.HEALTH) {
            double baseStat = round(this.getDouble(s.baseStat()), 1);
            double reforgeStat = round(this.getDouble(s.reforgeStat()), 1);
            if (baseStat != 0.0D || reforgeStat != 0.0D) {
               String potatoBook = this.getPotatoBookStatAmount(s) != 0 ? " " + ChatColor.YELLOW + "(" + s.format((double)this.getPotatoBookStatAmount(s)) + ")" : "";
               String reforge = reforgeStat != 0.0D ? " " + ChatColor.BLUE + "(" + s.format(reforgeStat) + ")" : "";
               lore.add(ChatColor.GRAY + s.statDisplayName() + ": " + s.getDefensive() + s.format(baseStat + reforgeStat + (double)this.getPotatoBookStatAmount(s)) + potatoBook + reforge);
            }
         }
      }

      if (!lore.isEmpty()) {
         lore.add("");
      }

      return lore;
   }

   public String getReforgeName() {
      return this.getStringOrDefault("ReforgeName", "");
   }

   public CustomItem setReforgeName(Reforge reforge) {
      if (reforge == null) {
         this.removeKey("ReforgeName");
      } else {
         this.setString("ReforgeName", reforge.getName());
      }

      return this;
   }

   public CustomItem applyReforge(Reforge reforge) {
      String originalName = this.getName();
      Reforge originalReforge = null;
      if (this.hasKey("ReforgeName")) {
         originalReforge = Reforge.parseFromName(this.getReforgeName());
      }

      String retainedColors = this.getRetainedColor(originalName);
      String name;
      if (originalReforge != null) {
         name = originalReforge.getAddedString(originalName);
         int amountOfChars = name.length() + 1;
         originalName = retainedColors + this.getNameSplitByRetainedColor(originalName).substring(amountOfChars);
         this.removeStats(originalReforge.getStats(this.getRarity()), true);
      }

      name = this.getRetainedColor(originalName) + (reforge == null ? "" : reforge.getName() + " ") + this.getNameSplitByRetainedColor(originalName);
      StatMap stats = reforge == null ? new StatMap(new Duplet[0]) : reforge.getStats(this.getRarity());
      this.setReforgeName(reforge).addStats(stats, true).setName(name);
      return this;
   }

   public CustomItem cleanseReforge() {
      return this.applyReforge((Reforge)null);
   }

   public String getNameSplitByRetainedColor(String string) {
      while(string.startsWith("§")) {
         string = string.substring(2);
      }

      return string;
   }

   public String getRetainedColor(String name) {
      StringBuilder retainedColor = new StringBuilder();

      while(name.startsWith("§")) {
         name = name.substring(2);
         retainedColor.append(this.getName(), 0, 2);
      }

      return retainedColor.toString();
   }

   public int getGearScore() {
      return this.getInt("GearScore");
   }

   public CustomItem setGearScore(int score) {
      this.setInt("GearScore", score);
      return this;
   }

   public CustomItem applyHotPotatoBook() {
      this.getDefaultPotatoStats().forEach(this::applyHotPotatoBook);
      return this;
   }

   public CustomItem applyHotPotatoBook(Stats stat) {
      this.setPotatoBookAmount(stat, this.getPotatoBookAmount(stat) + 1);
      return this;
   }

   public int getPotatoBookAmount() {
      return this.getPotatoBookAmount((Stats)getOrDefault(this.getDefaultPotatoStats(), 0, Stats.FEROCITY));
   }

   public int getPotatoBookAmount(Stats stat) {
      return this.getInt(stat.getPlaceholderTag() + "PotatoBooks");
   }

   public int getPotatoBookStatAmount(Stats stat) {
      return this.getPotatoBookAmount(stat) * this.getPotatoBookStatBuff(stat);
   }

   public CustomItem setPotatoBookAmount(Stats stat, int value) {
      this.setInt(stat.getPlaceholderTag() + "PotatoBooks", value);
      return this;
   }

   public int getPotatoBookStatBuff(Stats stat) {
      return stat == Stats.MAX_HEALTH ? 4 : 2;
   }

   public List getDefaultPotatoStats() {
      if (this.getItemType() == ItemType.ARMOR) {
         return arrayList(new Stats[]{Stats.MAX_HEALTH, Stats.DEFENSE});
      } else {
         return this.getItemType() != ItemType.SWORD && this.getItemType() != ItemType.FISHING_WEAPON && this.getItemType() != ItemType.BOW ? new ArrayList() : arrayList(new Stats[]{Stats.DAMAGE, Stats.STRENGTH});
      }
   }

   public boolean isEnchantedBook() {
      return this.getItemType() == ItemType.BOOK;
   }

   public static HashMap getEnchantsFromVanilla(Map enchants) {
      HashMap returnMap = new HashMap();
      HashMap vanillaToKSB = Enchant.getVanillaEquivalent();
      Iterator var3 = enchants.keySet().iterator();

      while(var3.hasNext()) {
         Enchantment enchantment = (Enchantment)var3.next();
         if (matchingEnchant((Class)vanillaToKSB.get(enchantment)) != null) {
            returnMap.put(matchingEnchant((Class)vanillaToKSB.get(enchantment)), enchants.get(enchantment));
         }
      }

      return returnMap;
   }

   public CustomItem removeEnchant(Enchant enchant) {
      ArrayList enchants = fromArray(this.getString("Enchants").split(","));
      StatMap statMap = enchant.addStats(this.getEnchantLevel(enchant));
      this.removeStats(statMap, false);
      enchants.removeIf((sx) -> {
         return sx.split(" ")[0].startsWith(enchant.getName());
      });
      StringBuilder sb = new StringBuilder();

      String s;
      for(Iterator var5 = enchants.iterator(); var5.hasNext(); sb.append(s)) {
         s = (String)var5.next();
         if (sb.length() != 0) {
            sb.append(",");
         }
      }

      this.setString("Enchants", sb.toString());
      return this;
   }

   public CustomItem removeEnchant(Player p, Enchant enchant) {
      EnchantRemoveEvent enchantRemoveEvent = new EnchantRemoveEvent(p, enchant, this.removeEnchant(enchant));
      Bukkit.getServer().getPluginManager().callEvent(enchantRemoveEvent);
      return enchantRemoveEvent.isCancelled() ? this : enchantRemoveEvent.getItem();
   }

   public boolean canAddEnchant(Player p, Enchant enchant) {
      if (this.im.getType() == XMaterial.ENCHANTED_BOOK.parseMaterial()) {
         return true;
      } else {
         boolean worked = false;
         Iterator var4 = enchant.getTargets().iterator();

         while(var4.hasNext()) {
            ItemType itemTypes = (ItemType)var4.next();
            if (itemTypes.includes(this)) {
               worked = true;
            }
         }

         return worked && p.hasPermission("koji.skyblock.enchants." + enchant.getNameNoSpace().toLowerCase());
      }
   }

   public boolean hasEnchantGlow() {
      if (this.im.getItemMeta().getEnchants().isEmpty()) {
         return false;
      } else {
         return this.im.getItemMeta().getEnchants().containsKey(XEnchantment.LUCK.getEnchant()) || this.im.getItemMeta().getEnchants().containsKey(XEnchantment.PROTECTION_EXPLOSIONS.getEnchant());
      }
   }

   public CustomItem addEnchantGlow() {
      if (this.im.getItemMeta().getEnchants().isEmpty() && this.im.getType() != XMaterial.ENCHANTED_BOOK.parseMaterial()) {
         ItemMeta itemMeta = this.im.getItemMeta();
         if (this.im.getType() != XMaterial.FISHING_ROD.parseMaterial()) {
            itemMeta.addEnchant(XEnchantment.LUCK.getEnchant(), 1, false);
         } else {
            itemMeta.addEnchant(XEnchantment.PROTECTION_EXPLOSIONS.getEnchant(), 1, false);
         }

         this.im.setItemMeta(itemMeta);
      }

      return this;
   }

   public CustomItem addEnchants(Player p, HashMap enchants) {
      enchants.forEach((a, b) -> {
         this.addEnchant(p, a, b);
      });
      return this;
   }

   public CustomItem addEnchant(Player p, Enchant enchant, int level) {
      return this.addEnchant(p, enchant, level, false);
   }

   public CustomItem addEnchant(Player p, Enchant enchant, int level, boolean bypassLowerLevels) {
      if (!this.canAddEnchant(p, enchant)) {
         return this;
      } else {
         EnchantAddEvent enchantAddEvent = new EnchantAddEvent(p, enchant, this.addEnchant(enchant, level, bypassLowerLevels));
         Bukkit.getServer().getPluginManager().callEvent(enchantAddEvent);
         return enchantAddEvent.isCancelled() ? this : enchantAddEvent.getItem();
      }
   }

   public CustomItem addEnchant(Enchant enchant, int level) {
      return this.addEnchant(enchant, level, false);
   }

   public CustomItem addEnchant(Enchant enchant, int level, boolean bypassLowerLevels) {
      ArrayList enchants = fromArray(this.getString("Enchants").split(","));
      this.addEnchantGlow();
      HashMap levels = this.getEnchants();
      enchants.add(enchant.getName() + " " + toRomanNumeral(level));
      if (this.hasConflictingEnchant(enchant)) {
         Enchant conflict = this.getConflictingEnchant(enchant);
         StatMap statMap = enchant.addStats((Integer)levels.get(conflict));
         this.removeStats(statMap, false);
         enchants.removeIf((sx) -> {
            return sx.split(" ")[0].startsWith(conflict.getName());
         });
      }

      StringBuilder sb = new StringBuilder();

      String s;
      for(Iterator var10 = enchants.iterator(); var10.hasNext(); sb.append(s)) {
         s = (String)var10.next();
         if (sb.length() != 0) {
            sb.append(",");
         }
      }

      String enchantString = sb.toString();
      if (levels.containsKey(enchant)) {
         if (!bypassLowerLevels && (Integer)levels.get(enchant) > level) {
            return this;
         }

         if (level == (Integer)levels.get(enchant) && level + 1 <= enchant.getMaxLevel()) {
            ++level;
         }

         StatMap statMap = enchant.addStats((Integer)levels.get(enchant));
         this.removeStats(statMap, false);
         enchantString = enchantString.replace(enchant.getName() + " " + toRomanNumeral((Integer)levels.get(enchant)), enchant.getName() + " " + toRomanNumeral(level));
      }

      this.setString("Enchants", enchantString);
      this.addStats(enchant.addStats(level), false);
      return this;
   }

   public ArrayList getEnchantLore() {
      ArrayList lore = new ArrayList();
      ArrayList sorted = new ArrayList(this.getEnchants().keySet());
      sorted.sort(Comparator.comparing(Enchant::getName));
      HashMap loreBits = new HashMap();
      Iterator var4 = (new ArrayList(sorted)).iterator();

      Enchant enchant;
      while(var4.hasNext()) {
         enchant = (Enchant)var4.next();
         int level = (Integer)this.getEnchants().get(enchant);
         if (enchant.isUltimate()) {
            sorted.remove(enchant);
            sorted.add(0, enchant);
         }

         String path = "modules.enchants.enchantments." + enchant.getName().toLowerCase() + ".name";
         String name = Files.getConfig().get(path) != null ? Files.getConfig().getString(path) : "§9" + enchant.getDisplayName();
         loreBits.put(enchant, Tuple.of(color(name + " " + toRomanNumeral(level)), enchant.getLore(level)));
      }

      if (sorted.size() > 4) {
         int amountOfLines = (int)Math.ceil((double)sorted.size() / 3.0D);

         for(int i = 0; i < amountOfLines; ++i) {
            StringBuilder line = new StringBuilder();

            for(int name = 0; name < 3; ++name) {
               try {
                  Enchant enchant = (Enchant)sorted.get(name + 3 * i);
                  String displayName = (String)((Duplet)loreBits.get(enchant)).getFirst();
                  if (line.length() != 0) {
                     line.append(", ");
                  }

                  line.append(displayName);
               } catch (IndexOutOfBoundsException var10) {
                  break;
               }
            }

            lore.add(line.toString());
         }
      } else {
         var4 = sorted.iterator();

         while(var4.hasNext()) {
            enchant = (Enchant)var4.next();
            lore.add(((Duplet)loreBits.get(enchant)).getFirst());
            lore.addAll((Collection)((Duplet)loreBits.get(enchant)).getSecond());
         }
      }

      return lore;
   }

   public boolean hasConflictingEnchant(HashMap list) {
      return this.hasConflictingEnchant(list.keySet());
   }

   public boolean hasConflictingEnchant(Set list) {
      return this.hasConflictingEnchant((List)(new ArrayList(list)));
   }

   public boolean hasConflictingEnchant(List list) {
      boolean returnValue = false;
      Iterator var3 = list.iterator();

      while(var3.hasNext()) {
         Enchant e = (Enchant)var3.next();
         if (this.getConflictingEnchant(e) != null) {
            returnValue = true;
            break;
         }
      }

      return returnValue;
   }

   public boolean hasConflictingEnchant(Enchant e) {
      return this.getConflictingEnchant(e) != null;
   }

   public List getConflictingEnchants(HashMap list) {
      return this.getConflictingEnchants(list.keySet());
   }

   public List getConflictingEnchants(Set overEnchants) {
      return this.getConflictingEnchants((List)(new ArrayList(overEnchants)));
   }

   public List getConflictingEnchants(List overEnchants) {
      List enchants = new ArrayList();
      this.getEnchants().keySet().forEach((a) -> {
         overEnchants.forEach((b) -> {
            if (a.getConflicts() != null && a.getConflicts().contains(b.getClass())) {
               enchants.add(b);
            }

         });
      });
      return enchants;
   }

   public Enchant getConflictingEnchant(Enchant e) {
      Iterator var2;
      Enchant enchant;
      if (e.isUltimate()) {
         var2 = this.getEnchants().keySet().iterator();

         while(var2.hasNext()) {
            enchant = (Enchant)var2.next();
            if (enchant.isUltimate()) {
               return enchant;
            }
         }
      }

      var2 = this.getEnchants().keySet().iterator();

      do {
         if (!var2.hasNext()) {
            return null;
         }

         enchant = (Enchant)var2.next();
      } while(enchant.getConflicts() == null || !enchant.getConflicts().contains(e.getClass()));

      return enchant;
   }

   public int getEnchantLevel(Enchant enchant) {
      return (Integer)this.getEnchants().getOrDefault(enchant, 0);
   }

   public HashMap getEnchants() {
      if (this.im.getType() == XMaterial.AIR.parseMaterial()) {
         return new HashMap();
      } else {
         ArrayList enchants = new ArrayList();
         if (this.hasKey("Enchants")) {
            enchants = fromArray(this.getString("Enchants").split(","));
         }

         HashMap finalE = new HashMap();
         Iterator var3 = enchants.iterator();

         while(var3.hasNext()) {
            String e = (String)var3.next();
            String name = e.split(" ")[0];
            if (anyEnchantsMatch(name)) {
               finalE.put(matchingEnchant(name, false), romanToInteger(e.split(" ")[1]));
            }
         }

         return finalE;
      }
   }

   public ArrayList getValidEnchants(Player p) {
      ArrayList enchants = new ArrayList();
      Iterator var3 = Enchant.getRegisteredEnchants().iterator();

      while(var3.hasNext()) {
         Enchant e = (Enchant)var3.next();
         Iterator var5 = ItemType.getValuesFullList().iterator();

         while(var5.hasNext()) {
            ItemType target = (ItemType)var5.next();
            if (e.getTargets().contains(target) && target.includes(this) && p.hasPermission("koji.skyblock.enchants." + e.getNameNoSpace().toLowerCase()) && e.canAppearInEnchantTable()) {
               enchants.add(e);
            }
         }
      }

      return enchants;
   }

   public boolean hasEnchant(Enchant enchant) {
      return (Integer)this.getEnchants().getOrDefault(enchant, 0) != 0;
   }

   public static boolean anyEnchantsMatch(Enchant enchant) {
      return anyEnchantsMatch(enchant.getName());
   }

   public static boolean anyEnchantsMatch(String name) {
      Iterator var1 = Enchant.getRegisteredEnchants().iterator();

      Enchant e;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         e = (Enchant)var1.next();
      } while(!e.getName().equalsIgnoreCase(name));

      return true;
   }

   public static Enchant matchingEnchant(Class enchant) {
      Iterator var1 = Enchant.getRegisteredEnchants().iterator();

      Enchant e;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         e = (Enchant)var1.next();
      } while(!e.getClass().equals(enchant));

      return e;
   }

   public static Enchant matchingEnchant(String name, boolean noSpace) {
      Iterator var2 = Enchant.getRegisteredEnchants().iterator();

      Enchant e;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         e = (Enchant)var2.next();
         if (noSpace && e.getNameNoSpace().equalsIgnoreCase(name)) {
            return e;
         }
      } while(!e.getName().equalsIgnoreCase(name));

      return e;
   }

   public List enchantByPriority(HashMap map) {
      ArrayList keySet = new ArrayList(map.keySet());
      keySet.sort((a, b) -> {
         int aPriority = this.getPriority((Priority)map.get(a));
         int bPriority = this.getPriority((Priority)map.get(b));
         return bPriority - aPriority;
      });
      return keySet;
   }

   public int getPriority(Priority priority) {
      switch(priority) {
      case LOWEST:
         return 0;
      case LOW:
         return 1;
      case HIGH:
         return 3;
      case HIGHEST:
         return 4;
      default:
         return 2;
      }
   }

   public CustomItem setAllowEnchants(boolean allow) {
      this.setBoolean("allowEnchants", allow);
      if (!allow) {
         this.getEnchants().forEach((a, b) -> {
            this.removeEnchant(a);
         });
      }

      return this;
   }

   public boolean doesAllowEnchants() {
      return this.getBooleanOrDefault("allowEnchants", false);
   }

   public int getApplyCost() {
      int applyCost = 0;

      Enchant a;
      Integer b;
      for(Iterator var2 = this.getEnchants().entrySet().iterator(); var2.hasNext(); applyCost = (int)((double)applyCost + Math.ceil(getLevelFromExp((long)((double)getExpFromLevel(a.getTotalExperience(b)) * 0.75D))))) {
         Entry entry = (Entry)var2.next();
         a = (Enchant)entry.getKey();
         b = (Integer)entry.getValue();
      }

      return applyCost;
   }

   public Rarity getRarity() {
      return !this.hasKey("rarity") ? Rarity.COMMON : Rarity.valueOf(this.getString("rarity").toUpperCase());
   }

   public CustomItem setRarity(Rarity rarity) {
      return this.setRarity(rarity.name());
   }

   public CustomItem setRarity(String rarity) {
      this.setString("rarity", rarity);
      return this;
   }

   public CustomItem addAbility(Ability ability) {
      if (this.hasAbility(ability.getIdentifier())) {
         return this;
      } else {
         StringBuilder keys = new StringBuilder();

         String key;
         for(Iterator var3 = ability.getPlaceholderDefaults().keySet().iterator(); var3.hasNext(); keys.append(key)) {
            key = (String)var3.next();
            this.setString(ability.getIdentifier() + "_placeholder_" + key, (String)ability.getPlaceholderDefaults().get(key));
            if (keys.length() > 0) {
               keys.append(",");
            }
         }

         this.setString(ability.getIdentifier() + "_keys", keys.toString());
         StringBuilder[] name = new StringBuilder[]{new StringBuilder()};
         if (this.getAbilityNames().size() == 0) {
            this.setString("abilityNames", ability.getIdentifier());
         } else {
            List abilities = new ArrayList(this.getAbilityNames());
            abilities.add(ability.getIdentifier());
            abilities.forEach((s) -> {
               if (name[0].length() > 0) {
                  name[0].append(",");
               }

               name[0].append(s);
            });
            this.setString("abilityNames", name[0].toString());
         }

         return this;
      }
   }

   public CustomItem addAbility(String ability) {
      return getAbility(ability) == null ? this : this.addAbility(getAbility(ability));
   }

   public CustomItem removeAbility(Ability key) {
      return this.removeAbility(key.getIdentifier());
   }

   public CustomItem removeAbility(String ability) {
      if (!this.hasAbility(ability)) {
         return this;
      } else {
         this.removeKey(ability + "_keys");
         this.getPlaceholders(ability).forEach((a, b) -> {
            this.removeKey(ability + "_placeholder_" + a);
         });
         StringBuilder[] name = new StringBuilder[]{new StringBuilder()};
         if (this.getAbilityNames().size() == 1) {
            this.removeKey("abilityNames");
         } else {
            this.getAbilityNames().forEach((s) -> {
               if (!s.equals(ability)) {
                  if (name[0].length() > 0) {
                     name[0].append(",");
                  }

                  name[0].append(s);
               }

            });
            this.setString("abilityNames", name[0].toString());
         }

         return this;
      }
   }

   public boolean hasAbility(Ability key) {
      return this.hasAbility(key.getIdentifier());
   }

   public boolean hasAbility(String key) {
      return this.hasKey(key + "_keys");
   }

   public static Ability getAbility(String key) {
      return (Ability)abilities.getOrDefault(key, (Object)null);
   }

   public List getAbilities() {
      List abilitiesToReturn = new ArrayList();
      if (this.hasKey("abilityNames") && !this.getString("abilityNames").equals("")) {
         this.getAbilityNames().forEach((s) -> {
            abilitiesToReturn.add(abilities.get(s));
         });
         return abilitiesToReturn;
      } else {
         return abilitiesToReturn;
      }
   }

   public List getAbilityNames() {
      return (List)(this.hasKey("abilityNames") && !this.getString("abilityNames").equals("") ? (List)Arrays.stream(this.getString("abilityNames").split(",")).collect(Collectors.toList()) : new ArrayList());
   }

   public static HashMap getAllAbilities() {
      return abilities;
   }

   public static void registerAbility(String name, Ability ability) {
      abilities.put(name, ability);
   }

   public CustomItem changePlaceholder(String abilityName, String placeholder, String newValue) {
      if (this.hasAbility(abilityName) && this.hasKey(abilityName + "_placeholder_" + placeholder)) {
         this.setString(abilityName + "_placeholder_" + placeholder, newValue);
      }

      return this;
   }

   public HashMap getPlaceholders(String abilityName) {
      HashMap returnMap = new HashMap();
      if (this.hasAbility(abilityName)) {
         String[] var3 = this.getString(abilityName + "_keys").split(",");
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String keys = var3[var5];
            returnMap.put(keys, this.getString(abilityName + "_placeholder_" + keys));
         }
      }

      return returnMap;
   }

   public boolean hasEvent(Class e) {
      return this.getAbilities().stream().anyMatch((a) -> {
         return a.hasEvent(e);
      });
   }

   public boolean hasEvent(Event e) {
      return this.hasEvent(e.getClass());
   }

   public ItemType getItemType() {
      return ItemType.parse(this.getStringOrDefault("type", (String)null));
   }

   public String getType() {
      ItemType type = this.getItemType();
      return type != null ? type.getName() : "";
   }

   public CustomItem setType(ItemType type) {
      if (type != null) {
         this.setString("type", type.getName());
         Iterator var2 = this.getEnchants().keySet().iterator();

         while(var2.hasNext()) {
            Enchant enchants = (Enchant)var2.next();
            if (!enchants.getTargets().contains(type)) {
               this.removeEnchant(enchants);
            }
         }
      } else {
         this.removeKey("type");
         this.setAllowEnchants(false);
      }

      return this;
   }

   public ItemType getDefaultItemType() {
      ArrayList types = new ArrayList(ItemType.getValuesFullList());
      types.removeIf((type) -> {
         return type.isBroadType() || !type.includes(this);
      });
      return (ItemType)getOrDefault(types, 0, (Object)null);
   }

   public boolean canBeStacked() {
      return this.im.getType().getMaxStackSize() != 1 && this.getBoolean("canBeStacked");
   }

   public CustomItem setCanBeStacked(boolean result) {
      this.setBoolean("canBeStacked", result);
      return this;
   }

   public boolean canBePlaced() {
      return this.im.getType().isBlock() && this.getBoolean("canBePlaced");
   }

   public CustomItem setCanBePlaced(boolean result) {
      this.setBoolean("canBePlaced", result);
      return this;
   }

   public String getExtraType() {
      return this.getString("extraType");
   }

   public CustomItem setExtraType(String type) {
      this.setString("extraType", type.toUpperCase());
      return this;
   }

   public String getID() {
      return this.getString("id");
   }

   public static ItemStack createItem(FileConfiguration fC, String path) {
      if (!XMaterial.matchXMaterial(fC.getString(path + ".material")).isPresent()) {
         return null;
      } else {
         CustomItem im = new CustomItem((XMaterial)XMaterial.matchXMaterial(fC.getString(path + ".material")).get());
         im.setName(color(fC.getString(path + ".name")));
         im.setUnbreakable(true);
         im.HideFlags(63);
         if (fC.contains(path + ".texture")) {
            im.setTexture(fC.getString(path + ".texture"));
         }

         if (im.build().getType().toString().contains("LEATHER") && fC.contains(path + ".color")) {
            im.setColor(getColorByString(fC.getString(path + ".color")));
         }

         if (fC.getBoolean(path + ".enchantGlow")) {
            im.addEnchantGlow();
         }

         List abilityClasses = new ArrayList();
         if (fC.contains(path + ".abilities")) {
            Iterator var4 = fC.getStringList(path + ".abilities").iterator();

            while(var4.hasNext()) {
               final String ability = (String)var4.next();
               final File file = new File("plugins/KojiSkyblock/abilities/" + ability + ".yml");
               if (file.exists()) {
                  Ability abilityClass = new Ability() {
                     public String getIdentifier() {
                        return ability;
                     }

                     public String getDisplayName() {
                        return CustomItem.getAbilityDisplayName(ability);
                     }

                     public List getLoreDefault() {
                        return CustomItem.getAbilityLore(ability);
                     }

                     public double getManaCost(Player p) {
                        return file.exists() ? YamlConfiguration.loadConfiguration(file).getDouble("mana") : 0.0D;
                     }

                     public HashMap getPlaceholderDefaults() {
                        HashMap dupletList = new HashMap();
                        if (file.exists()) {
                           FileConfiguration itemFC = YamlConfiguration.loadConfiguration(file);
                           if (itemFC.contains("variables")) {
                              Iterator var3 = getKeys(itemFC, "variables.", false).iterator();

                              while(var3.hasNext()) {
                                 String keys = (String)var3.next();
                                 dupletList.put(getLast(keys.split("\\.")), itemFC.getString(keys + ".default"));
                              }
                           }

                           dupletList.put("mana_cost", num(commaify(itemFC.getDouble("mana"))));
                        } else {
                           dupletList.put("mana_cost", "0");
                        }

                        return dupletList;
                     }

                     public File getFile() {
                        return file;
                     }
                  };
                  registerAbility(ability, abilityClass);
                  abilityClasses.add(abilityClass);
                  im = im.addAbility(abilityClass);
               } else {
                  Skyblock.getPlugin().getLogger().log(Level.WARNING, "Couldn't add ability " + ability + " to " + ChatColor.stripColor(im.getName()) + " because it doesn't exist!");
               }
            }
         }

         StringBuilder abilityNames = new StringBuilder();

         Ability a;
         for(Iterator var14 = abilityClasses.iterator(); var14.hasNext(); abilityNames.append(a.getIdentifier())) {
            a = (Ability)var14.next();
            if (abilityNames.length() > 0) {
               abilityNames.append(",");
            }
         }

         im.setString("abilityNames", abilityNames.toString());
         Rarity rarity = Rarity.COMMON;

         try {
            rarity = Rarity.valueOf(fC.getString(path + ".rarity").toUpperCase());
         } catch (IllegalArgumentException var12) {
         }

         im.setRarity(rarity);
         im.setString("id", fC.getString(path + ".id"));
         List lores = new ArrayList();
         Iterator var18 = abilityClasses.iterator();

         while(var18.hasNext()) {
            Ability a = (Ability)var18.next();
            lores.addAll(a.getLore(im));
         }

         if (lores.isEmpty()) {
            lores.add("");
         }

         im.setGearScore(fC.getInt(path + ".gearscore"));
         Stats[] var19 = Stats.values();
         int var20 = var19.length;

         for(int var9 = 0; var9 < var20; ++var9) {
            Stats stat = var19[var9];
            String statName = stat.getNoSpaceName().toLowerCase().replace("max", "");
            im.setStat(stat, fC.getDouble(path + ".stats." + statName), false);
         }

         if (fC.get(path + ".type") != null) {
            im.setType(ItemType.parse(fC.getString(path + ".type")));
         }

         if (fC.get(path + ".extraType") != null) {
            im.setExtraType(fC.getString(path + ".extraType"));
         }

         im.setCanBeStacked(fC.getBoolean(path + ".canBeStacked")).setCanBePlaced(fC.getBoolean(path + ".canBePlaced")).setAllowEnchants(fC.getBoolean(path + ".allowEnchants"));
         return im.build(lores);
      }
   }

   public ArrayList getAbilitiesLore() {
      ArrayList abilitiesLore = new ArrayList();
      Iterator var2 = this.getAbilities().iterator();

      while(var2.hasNext()) {
         Ability a = (Ability)var2.next();
         abilitiesLore.addAll(a.getLore(this));
      }

      if (abilitiesLore.isEmpty()) {
         abilitiesLore.add("");
      }

      return abilitiesLore;
   }

   public static String getAbilityDisplayName(String name) {
      if (name != null) {
         File dir = new File("plugins/KojiSkyblock/abilities/");
         File[] filesInDir = dir.listFiles();
         if (filesInDir != null && dir.exists() && dir.isDirectory()) {
            File[] var3 = filesInDir;
            int var4 = filesInDir.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               File f = var3[var5];
               FileConfiguration fC = YamlConfiguration.loadConfiguration(f);
               if (name.equals(fC.getString("name"))) {
                  return color(fC.getString("display-name.text"));
               }
            }
         }
      }

      return "";
   }

   public static List getAbilityLore(String name) {
      List lore = new ArrayList();
      if (name != null) {
         FileConfiguration fC = null;
         if (getAllAbilities().containsKey(name)) {
            fC = ((Ability)getAllAbilities().get(name)).getFileConfiguration();
         } else {
            File dir = new File("plugins/KojiSkyblock/abilities/");
            File[] filesInDir = dir.listFiles();
            if (filesInDir != null && dir.exists() && dir.isDirectory()) {
               File[] var5 = filesInDir;
               int var6 = filesInDir.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  File f = var5[var7];
                  FileConfiguration test = YamlConfiguration.loadConfiguration(f);
                  if (name.equals(test.getString("name"))) {
                     fC = test;
                  }
               }
            }
         }

         ArrayList addAll = new ArrayList();
         if (fC == null) {
            return addAll;
         }

         if (((FileConfiguration)fC).contains("header")) {
            addAll.add(((FileConfiguration)fC).getString("header"));
         }

         if (((FileConfiguration)fC).getBoolean("display-name.display")) {
            addAll.add(getAbilityDisplayName(name));
         }

         if (((FileConfiguration)fC).contains("lore")) {
            addAll.addAll(((FileConfiguration)fC).getStringList("lore"));
         }

         if (((FileConfiguration)fC).contains("soulflow")) {
            addAll.add("§8Soulflow Cost: §3" + ((FileConfiguration)fC).getInt("soulflow") + "⸎");
         }

         if (((FileConfiguration)fC).contains("mana")) {
            addAll.add("§8Mana Cost: §3%mana_cost%");
         }

         if (((FileConfiguration)fC).contains("cooldown")) {
            addAll.add("§8Cooldown: §a" + ((FileConfiguration)fC).getInt("cooldown") + "s");
         }

         if (((FileConfiguration)fC).contains("extra-lore")) {
            addAll.addAll(((FileConfiguration)fC).getStringList("extra-lore"));
         }

         lore.addAll(addAll);
         lore.add("");
      }

      return lore;
   }

   public CustomItem setString(String key, String value) {
      super.setString(key, value);
      return this;
   }

   public CustomItem setInt(String key, int value) {
      super.setInt(key, value);
      return this;
   }

   public CustomItem setBoolean(String key, boolean value) {
      super.setBoolean(key, value);
      return this;
   }

   public CustomItem setDouble(String key, double value) {
      super.setDouble(key, value);
      return this;
   }

   public CustomItem setLong(String key, long value) {
      super.setLong(key, value);
      return this;
   }

   public CustomItem setByte(String key, byte value) {
      super.setByte(key, value);
      return this;
   }

   public CustomItem setShort(String key, short value) {
      super.setShort(key, value);
      return this;
   }

   public CustomItem setFloat(String key, float value) {
      super.setFloat(key, value);
      return this;
   }

   public CustomItem setByteArray(String key, byte[] value) {
      super.setByteArray(key, value);
      return this;
   }

   public CustomItem setIntArray(String key, int[] value) {
      super.setIntArray(key, value);
      return this;
   }

   public CustomItem HideFlags(int flags) {
      super.HideFlags(flags);
      return this;
   }

   public CustomItem setUnbreakable(boolean bol) {
      super.setUnbreakable(bol);
      return this;
   }

   public CustomItem setTexture(String texture) {
      super.setTexture(texture);
      return this;
   }

   public String getTexture() {
      return this.im.getType() != XMaterial.PLAYER_HEAD.parseMaterial() ? "" : ((SkullMeta)this.im.getItemMeta()).getOwner();
   }

   public CustomItem setColor(Color c) {
      super.setColor(c);
      return this;
   }

   public CustomItem removeKey(String key) {
      super.removeKey(key);
      return this;
   }

   public Color getColor() {
      if (!this.im.getType().toString().startsWith("LEATHER_")) {
         return null;
      } else {
         LeatherArmorMeta lAM = (LeatherArmorMeta)this.im.getItemMeta();
         return lAM.getColor();
      }
   }

   public CustomItem applyCompoundFromString(String compoundAsString) {
      super.applyCompoundFromString(compoundAsString);
      return this;
   }

   public static int getExpFromLevel(int level) {
      if (level > 30) {
         return (int)(4.5D * (double)level * (double)level - 162.5D * (double)level + 2220.0D);
      } else {
         return level > 15 ? (int)(2.5D * (double)level * (double)level - 40.5D * (double)level + 360.0D) : level * level + 6 * level;
      }
   }

   public static double getLevelFromExp(long exp) {
      if (exp > 1395L) {
         return (Math.sqrt((double)(72L * exp - 54215L)) + 325.0D) / 18.0D;
      } else if (exp > 315L) {
         return Math.sqrt((double)(40L * exp - 7839L)) / 10.0D + 8.1D;
      } else {
         return exp > 0L ? Math.sqrt((double)(exp + 9L)) - 3.0D : 0.0D;
      }
   }
}
