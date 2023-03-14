package koji.skyblock;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import koji.developerkit.runnable.KRunnable;
import koji.developerkit.utils.KStatic;
import koji.developerkit.utils.xseries.XEnchantment;
import koji.developerkit.utils.xseries.XMaterial;
import koji.skyblock.api.armorevents.listeners.ArmorListener;
import koji.skyblock.api.placeholderapi.PlaceholderAPISupport;
import koji.skyblock.api.placeholderapi.PlaceholderAPISupportNo;
import koji.skyblock.api.placeholderapi.PlaceholderAPISupportYes;
import koji.skyblock.api.placeholderapi.PlaceholderRegister;
import koji.skyblock.commands.AddStatsCMD;
import koji.skyblock.commands.AttributeCMD;
import koji.skyblock.commands.ClearStashCMD;
import koji.skyblock.commands.EnchantsCMD;
import koji.skyblock.commands.PickupStashCMD;
import koji.skyblock.commands.ReloadCMD;
import koji.skyblock.commands.ResetDataCMD;
import koji.skyblock.commands.SetRarityCMD;
import koji.skyblock.files.Config;
import koji.skyblock.files.Files;
import koji.skyblock.files.data.FileData;
import koji.skyblock.files.data.PlayerData;
import koji.skyblock.files.data.SQLData;
import koji.skyblock.files.pets.FilePetData;
import koji.skyblock.files.pets.PetData;
import koji.skyblock.files.pets.SQLPetData;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.ability.Abilities;
import koji.skyblock.item.anvil.AnvilGUI;
import koji.skyblock.item.creation.CustomItemsMenu;
import koji.skyblock.item.creation.ItemBuilderCMD;
import koji.skyblock.item.creation.ItemBuilderGUI;
import koji.skyblock.item.creation.MidasSword;
import koji.skyblock.item.enchants.Enchant;
import koji.skyblock.item.enchants.EnchantTableGUI;
import koji.skyblock.item.enchants.enchants.BaneOfArthropods;
import koji.skyblock.item.enchants.enchants.BigBrain;
import koji.skyblock.item.enchants.enchants.Critical;
import koji.skyblock.item.enchants.enchants.Cubism;
import koji.skyblock.item.enchants.enchants.DivineGift;
import koji.skyblock.item.enchants.enchants.DragonHunter;
import koji.skyblock.item.enchants.enchants.DragonTracer;
import koji.skyblock.item.enchants.enchants.EnderSlayer;
import koji.skyblock.item.enchants.enchants.Execute;
import koji.skyblock.item.enchants.enchants.Experience;
import koji.skyblock.item.enchants.enchants.FireAspect;
import koji.skyblock.item.enchants.enchants.FirstStrike;
import koji.skyblock.item.enchants.enchants.GiantKiller;
import koji.skyblock.item.enchants.enchants.Growth;
import koji.skyblock.item.enchants.enchants.Impaling;
import koji.skyblock.item.enchants.enchants.LifeSteal;
import koji.skyblock.item.enchants.enchants.Power;
import koji.skyblock.item.enchants.enchants.Prosecute;
import koji.skyblock.item.enchants.enchants.Protection;
import koji.skyblock.item.enchants.enchants.Rejuvenate;
import koji.skyblock.item.enchants.enchants.Sharpness;
import koji.skyblock.item.enchants.enchants.SmartyPants;
import koji.skyblock.item.enchants.enchants.Smite;
import koji.skyblock.item.enchants.enchants.Snipe;
import koji.skyblock.item.enchants.enchants.SugarRush;
import koji.skyblock.item.enchants.enchants.Syphon;
import koji.skyblock.item.enchants.enchants.Vampirism;
import koji.skyblock.item.enchants.enchants.Vicious;
import koji.skyblock.item.enchants.enchants.ultimate.Swarm;
import koji.skyblock.item.enchants.enchants.ultimate.UltimateWise;
import koji.skyblock.item.reforges.ReforgeCMD;
import koji.skyblock.item.reforges.ReforgingGUI;
import koji.skyblock.item.utils.EntityHider;
import koji.skyblock.item.utils.KojiSkyblockLoadEvent;
import koji.skyblock.pets.Pet;
import koji.skyblock.pets.api.pets.EnderDragon;
import koji.skyblock.pets.commands.OpenPetsMenuCMD;
import koji.skyblock.pets.commands.PetGiveCMD;
import koji.skyblock.pets.commands.PetSetLevelCMD;
import koji.skyblock.pets.commands.PetSkinCMD;
import koji.skyblock.pets.listeners.PetMenuListener;
import koji.skyblock.pets.listeners.PetRightClickListener;
import koji.skyblock.pets.listeners.WorldChange;
import koji.skyblock.player.GenericListeners;
import koji.skyblock.player.MiscListeners;
import koji.skyblock.player.PClass;
import koji.skyblock.player.Stats;
import koji.skyblock.player.damage.DamageListener;
import koji.skyblock.player.scoreboard.ScoreboardState;
import koji.skyblock.reflection.Particles;
import koji.skyblock.reflection.armorstand.UncollidableArmorStand_1_10;
import koji.skyblock.reflection.armorstand.UncollidableArmorStand_1_11;
import koji.skyblock.reflection.armorstand.UncollidableArmorStand_1_12;
import koji.skyblock.reflection.armorstand.UncollidableArmorStand_1_13;
import koji.skyblock.reflection.armorstand.UncollidableArmorStand_1_14;
import koji.skyblock.reflection.armorstand.UncollidableArmorStand_1_15;
import koji.skyblock.reflection.armorstand.UncollidableArmorStand_1_16;
import koji.skyblock.reflection.armorstand.UncollidableArmorStand_1_17;
import koji.skyblock.reflection.armorstand.UncollidableArmorStand_1_8;
import koji.skyblock.reflection.armorstand.UncollidableArmorStand_1_9;
import koji.skyblock.reflection.particles.NewerVersionParticles;
import koji.skyblock.reflection.particles.OldestVersionParticles;
import koji.skyblock.utils.SkyblockWorld;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Skyblock extends JavaPlugin implements Listener {
   static Skyblock plugin;
   private PlaceholderAPISupport papiAPI;
   private Economy economy;
   ArrayList setupPlayers = new ArrayList();
   private static EntityHider entityHider;
   private static final Particles abilityParticleManager;

   public void onEnable() {
      try {
         plugin = this;
         boolean protocolWorking = this.getServer().getPluginManager().isPluginEnabled("ProtocolLib");
         if (this.setupEconomy() && protocolWorking) {
            this.loadPlaceholderAPISupport();
            entityHider = new EntityHider(this, EntityHider.Policy.BLACKLIST);
            if (load() && initialLoad()) {
               this.registerCommands();
               this.updateCommands();
               this.registerListeners();
               this.startPlayerTasks();
            } else {
               this.getServer().getPluginManager().disablePlugin(this);
               this.getLogger().log(Level.WARNING, "Failed to load Koji Skyblock! Please fix above error!");
            }
         } else {
            this.getServer().getPluginManager().disablePlugin(this);
            if (!protocolWorking) {
               this.getServer().getLogger().log(Level.SEVERE, "Could not load plugin! ProtocolLib was disabled!");
            }

         }
      } catch (Throwable var2) {
         throw var2;
      }
   }

   public void registerCommands() {
      this.getCommand("ci").setExecutor(new CustomItemsMenu());
      this.getCommand("petgive").setExecutor(new PetGiveCMD());
      this.getCommand("petsetlevel").setExecutor(new PetSetLevelCMD());
      this.getCommand("pet").setExecutor(new OpenPetsMenuCMD());
      this.getCommand("petskin").setExecutor(new PetSkinCMD());
      this.getCommand("ksbreload").setExecutor(new ReloadCMD());
      this.getCommand("attribute").setExecutor(new AttributeCMD());
      this.getCommand("pickupstash").setExecutor(new PickupStashCMD());
      this.getCommand("clearstash").setExecutor(new ClearStashCMD());
      this.getCommand("reforge").setExecutor(new ReforgeCMD());
      this.getCommand("enchants").setExecutor(new EnchantsCMD());
      this.getCommand("addstat").setExecutor(new AddStatsCMD());
      this.getCommand("itembuilder").setExecutor(new ItemBuilderCMD());
      this.getCommand("resetdata").setExecutor(new ResetDataCMD());
      this.getCommand("setrarity").setExecutor(new SetRarityCMD());
   }

   public void registerListeners() {
      PluginManager pm = Bukkit.getPluginManager();
      pm.registerEvents(this, this);
      pm.registerEvents(new WorldChange(), this);
      if (Config.getPetsEnabled()) {
         pm.registerEvents(new PetRightClickListener(), this);
         pm.registerEvents(new PetMenuListener(), this);
      }

      pm.registerEvents(new DamageListener(), this);
      if (Config.getEnchantsEnabled()) {
         pm.registerEvents(new EnchantTableGUI(), this);
      }

      if (Config.getReforgeEnabled()) {
         pm.registerEvents(new ReforgingGUI(), this);
      }

      pm.registerEvents(new MidasSword(), this);
      pm.registerEvents(new AnvilGUI(), this);
      if (Config.getItemBuilderEnabled()) {
         pm.registerEvents(new ItemBuilderGUI(), this);
      }

      pm.registerEvents(new Abilities(), this);
      pm.registerEvents(new ArmorListener(), this);
      if (Files.getConfig().getBoolean("enable-generic-events")) {
         pm.registerEvents(new GenericListeners(), this);
      }

      pm.registerEvents(new MiscListeners(), this);
   }

   public void updateCommands() {
      if (!Config.getPetsEnabled()) {
         PluginCommand[] cmds = new PluginCommand[]{this.getCommand("pet"), this.getCommand("petsetlevel"), this.getCommand("petgive"), this.getCommand("petskin")};
         Arrays.stream(cmds).forEach(Skyblock::unregisterBukkitCommand);
      } else {
         this.registerCommand("pet", this.getCommand("pet"));
         this.registerCommand("petsetlevel", this.getCommand("petsetlevel"));
         this.registerCommand("petgive", this.getCommand("petgive"));
         this.registerCommand("petskin", this.getCommand("petskin"));
      }

      if (!Config.getEnchantsEnabled()) {
         unregisterBukkitCommand(this.getCommand("enchants"));
      } else {
         this.registerCommand("enchants", this.getCommand("enchants"));
      }

      if (!Config.getReforgeEnabled()) {
         unregisterBukkitCommand(this.getCommand("reforge"));
      } else {
         this.registerCommand("reforge", this.getCommand("reforge"));
      }

      if (!Config.getReforgeEnabled()) {
         unregisterBukkitCommand(this.getCommand("itembuilder"));
      } else {
         this.registerCommand("itembuilder", this.getCommand("itembuilder"));
      }

   }

   public static boolean initialLoad() {
      try {
         if (Pet.matchFromType("EnderDragon") != null) {
            Pet.registerPet(new EnderDragon());
         }

         Bukkit.getOnlinePlayers().forEach((s) -> {
            getPlugin().onJoin(s);
         });
         Enchant.getRegisteredEnchants().clear();
         Enchant.registerEnchants(new Sharpness(), new Protection(), new Power(), new Smite(), new BaneOfArthropods(), new DivineGift(), new FireAspect(), new Experience(), new Cubism(), new LifeSteal(), new Growth(), new Snipe(), new SugarRush(), new GiantKiller(), new DragonTracer(), new Critical(), new Rejuvenate(), new FirstStrike(), new EnderSlayer(), new Impaling(), new Execute(), new Syphon(), new Vampirism(), new DragonHunter(), new UltimateWise(), new BigBrain(), new SmartyPants(), new Prosecute(), new Vicious(), new Swarm());
         Enchant.getVanillaEquivalent().put(XEnchantment.DAMAGE_ALL.getEnchant(), Sharpness.class);
         Enchant.getVanillaEquivalent().put(XEnchantment.PROTECTION_ENVIRONMENTAL.getEnchant(), Protection.class);
         Enchant.getVanillaEquivalent().put(XEnchantment.FIRE_ASPECT.getEnchant(), FireAspect.class);
         Enchant.getVanillaEquivalent().put(XEnchantment.DAMAGE_ARTHROPODS.getEnchant(), BaneOfArthropods.class);
         Enchant.getVanillaEquivalent().put(XEnchantment.DAMAGE_UNDEAD.getEnchant(), Smite.class);
         return true;
      } catch (Exception var1) {
         var1.printStackTrace();
         return false;
      }
   }

   public static boolean load() {
      try {
         Files.initFiles();
         CustomItemsMenu.loadItems();
         CustomItem.getAllAbilities().forEach((name, ability) -> {
            try {
               ability.reload();
            } catch (InvalidConfigurationException | IOException var3) {
               getPlugin().getLogger().log(Level.WARNING, "Couldn't reload ability!");
            }

         });
         Abilities.putAbilities();
         if (Config.willUseFileStorage()) {
            PlayerData.setPlayerData(new FileData());
            PetData.setPetData(new FilePetData());
         } else {
            SQLData data = new SQLData();
            if (PlayerData.setPlayerData(data)) {
               Connection conn = data.connection();
               PetData.setPetData(new SQLPetData(conn));
            } else {
               PetData.setPetData(new FilePetData());
               PlayerData.setPlayerData(new FileData());
            }
         }

         Bukkit.getPluginManager().callEvent(new KojiSkyblockLoadEvent());
         return true;
      } catch (Exception var2) {
         var2.printStackTrace();
         return false;
      }
   }

   public void onDisable() {
      if (entityHider != null) {
         entityHider.close();
         entityHider = null;
      }

      this.setupPlayers.forEach((s) -> {
         PClass.getPlayer(s).getPetInstance().delete();
      });
      this.setupPlayers.clear();
   }

   public boolean isPAPIEnabled() {
      return this.papiAPI.getClass().equals(PlaceholderAPISupportYes.class);
   }

   private void loadPlaceholderAPISupport() {
      if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
         this.papiAPI = new PlaceholderAPISupportYes();
         (new PlaceholderRegister()).register();
      } else {
         this.papiAPI = new PlaceholderAPISupportNo();
      }

   }

   private boolean setupEconomy() {
      if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
         plugin.getLogger().log(Level.WARNING, "Unable to find Vault plugin, is it installed?");
         return false;
      } else {
         RegisteredServiceProvider economyProvider = this.getServer().getServicesManager().getRegistration(Economy.class);
         if (economyProvider != null) {
            this.economy = (Economy)economyProvider.getProvider();
         } else {
            this.getLogger().log(Level.INFO, "Disabled due to no economy handler plugin found!");
         }

         return this.economy != null;
      }
   }

   public void startPlayerTasks() {
      (new KRunnable((task) -> {
         this.setupPlayers.forEach((p) -> {
            PClass pS = PClass.getPlayer(p);
            pS.updateStats();
            pS.updateScoreboard();
         });
      })).runTaskTimer(this, 0L, 3L);
      AtomicInteger i = new AtomicInteger(0);
      (new KRunnable((task) -> {
         if (i.get() == 1) {
            i.set(0);
            this.setupPlayers.forEach((p) -> {
               PClass.getPlayer(p).regenStats();
            });
         } else {
            i.getAndIncrement();
         }

         this.setupPlayers.forEach((p) -> {
            PClass.getPlayer(p).updateActionbar();
         });
      })).runTaskTimer(this, 0L, 20L);
      (new KRunnable((task) -> {
         this.setupPlayers.forEach((p) -> {
            PlayerData.getPlayerData().sendItemStashMessage(p);
         });
      })).runTaskTimer(this, 0L, 1200L);
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent e) {
      this.onJoin(e.getPlayer());
   }

   @EventHandler
   public void onPlayerLeave(PlayerQuitEvent e) {
      Player p = e.getPlayer();
      PClass.getPlayer(p).getPetInstance().delete();
      this.setupPlayers.remove(p);
   }

   public void onJoin(Player p) {
      PClass pS = new PClass(p);
      PClass.addPlayer(pS);
      SkyblockWorld sbWorld = SkyblockWorld.getWorld(p.getWorld());
      pS.setState(sbWorld.isInDragonFight() ? ScoreboardState.DRAGON_BATTLE : ScoreboardState.DEFAULT);
      if (!Config.willSetToBaseStatsOnJoin()) {
         pS.updateStats();
         pS.setStat(Stats.HEALTH, pS.getStat(Stats.MAX_HEALTH));
         pS.setStat(Stats.MANA, pS.getStat(Stats.MAX_MANA));
         p.setMaxHealth(pS.getStat(Stats.MAX_HEALTH));
         p.setHealth(p.getMaxHealth());
      }

      if (!this.setupPlayers.contains(p)) {
         this.setupPlayers.add(p);
      }

   }

   public static LivingEntity getArmorStand(Location loc) {
      switch(XMaterial.getVersion()) {
      case 8:
         return (new UncollidableArmorStand_1_8(loc)).spawn(loc);
      case 9:
         return (new UncollidableArmorStand_1_9(loc)).spawn(loc);
      case 10:
         return (new UncollidableArmorStand_1_10(loc)).spawn(loc);
      case 11:
         return (new UncollidableArmorStand_1_11(loc)).spawn(loc);
      case 12:
         return (new UncollidableArmorStand_1_12(loc)).spawn(loc);
      case 13:
         return (new UncollidableArmorStand_1_13(loc)).spawn(loc);
      case 14:
         return (new UncollidableArmorStand_1_14(loc)).spawn(loc);
      case 15:
         return (new UncollidableArmorStand_1_15(loc)).spawn(loc);
      case 16:
         return (new UncollidableArmorStand_1_16(loc)).spawn(loc);
      default:
         return (new UncollidableArmorStand_1_17()).spawn(loc);
      }
   }

   public void registerCommand(String label, PluginCommand actualCommand) {
      try {
         Object result = KStatic.getPrivateField(getPlugin().getServer().getPluginManager(), "commandMap");
         SimpleCommandMap commandMap = (SimpleCommandMap)result;
         commandMap.register(label, getPlugin().getName(), actualCommand);
      } catch (IllegalAccessException | NoSuchFieldException var5) {
         var5.printStackTrace();
      }

   }

   private static void unregisterBukkitCommand(PluginCommand cmd) {
      try {
         Object result = KStatic.getPrivateField(getPlugin().getServer().getPluginManager(), "commandMap");
         SimpleCommandMap commandMap = (SimpleCommandMap)result;
         Object map = KStatic.getPrivateField(commandMap, "knownCommands");
         HashMap knownCommands = (HashMap)map;
         knownCommands.remove(cmd.getName());
         Iterator var5 = cmd.getAliases().iterator();

         while(var5.hasNext()) {
            String alias = (String)var5.next();
            if (knownCommands.containsKey(alias) && ((Command)knownCommands.get(alias)).toString().contains(getPlugin().getName())) {
               knownCommands.remove(alias);
            }
         }
      } catch (Exception var7) {
         var7.printStackTrace();
      }

   }

   public static Skyblock getPlugin() {
      return plugin;
   }

   public Economy getEconomy() {
      return this.economy;
   }

   public static EntityHider getEntityHider() {
      return entityHider;
   }

   public static Particles getAbilityParticleManager() {
      return abilityParticleManager;
   }

   static {
      if (XMaterial.getVersion() == 8) {
         abilityParticleManager = new OldestVersionParticles();
      } else {
         abilityParticleManager = new NewerVersionParticles();
      }

   }
}
