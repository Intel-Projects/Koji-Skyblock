package koji.skyblock.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import koji.developerkit.KBase;
import koji.developerkit.utils.ItemBuilder;
import koji.developerkit.utils.duplet.Duplet;
import koji.developerkit.utils.xseries.XMaterial;
import koji.skyblock.Skyblock;
import koji.skyblock.item.Rarity;
import koji.skyblock.item.reforges.Reforge;
import koji.skyblock.item.reforges.ReforgeType;
import koji.skyblock.pets.Pet;
import koji.skyblock.pets.api.PetAbility;
import koji.skyblock.pets.api.PetKind;
import koji.skyblock.player.Stats;
import koji.skyblock.utils.StatMap;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Files extends KBase {
   private static final File configFile = new File("plugins/KojiSkyblock/config.yml");
   private static final FileConfiguration config;
   private static final File customItemsFile;
   private static final FileConfiguration customItems;
   private static final File defaultItemOverridesFile;
   private static final FileConfiguration defaultItemOverrides;
   public static JavaPlugin plugin;
   private static final HashMap registeredPets;
   private static final HashMap petSkins;

   public static void initFiles() throws IOException, InvalidConfigurationException {
      if (!(new File("plugins/KojiSkyblock")).exists()) {
         (new File("plugins/KojiSkyblock")).mkdir();
      }

      try {
         plugin.saveDefaultConfig();
         if (!defaultItemOverridesFile.exists()) {
            defaultItemOverridesFile.createNewFile();
         }

         if (!customItemsFile.exists()) {
            plugin.saveResource("custom-items.yml", false);
         }

         if (!(new File("plugins/KojiSkyblock/pet-skills.yml")).exists()) {
            plugin.saveResource("pet-skills.yml", false);
         }
      } catch (Exception var1) {
         var1.printStackTrace();
      }

      loadItemAbilities();
      copyPetsFromPlugin();
      loadPets();
      loadReforges();
      File playerData = new File("plugins/KojiSkyblock/playerData");
      if (!playerData.exists()) {
         playerData.mkdir();
         playerData.createNewFile();
      }

      loadFiles();
   }

   private static void loadReforges() {
      plugin.getLogger().log(Level.INFO, "Copying Reforges...");
      if (!(new File("plugins/KojiSkyblock/reforges")).exists()) {
         (new File("plugins/KojiSkyblock/reforges")).mkdir();
      }

      Reforge.getReforges().clear();
      ReforgeType[] var0 = ReforgeType.values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         ReforgeType type = var0[var2];
         File f = new File("plugins/KojiSkyblock/reforges/" + type.name().toLowerCase().replace("_", "-") + ".yml");
         if (!f.exists() && plugin.getResource("reforges/" + type.name().toLowerCase().replace("_", "-") + ".yml") != null) {
            plugin.saveResource("reforges/" + type.name().toLowerCase().replace("_", "-") + ".yml", false);
         }

         FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
         fc.getKeys(false).forEach((key) -> {
            Reforge.registerReforge(new Reforge(fc.getString(key + ".name"), type) {
               public StatMap getStats(Rarity rarity) {
                  StatMap map = new StatMap(new Duplet[0]);
                  getKeys(fc, key + "." + rarity.name().toLowerCase() + ".", false).forEach((s) -> {
                     Stats stat = Stats.parseFromPlaceholder((String)getLast(s.split("\\.")));
                     if (stat != null) {
                        map.put(stat, fc.getDouble(s));
                     }

                  });
                  return map;
               }
            });
         });
      }

   }

   public static void copyPetsFromPlugin() {
      plugin.getLogger().log(Level.INFO, "Copying Pets...");
      String[] s = (String[])array(new String[]{"pets/pets", "pets/skins"});
      String[] var1 = s;
      int var2 = s.length;

      label35:
      for(int var3 = 0; var3 < var2; ++var3) {
         String label = var1[var3];
         File dir = new File("plugins/KojiSkyblock/" + label);
         if (!dir.exists()) {
            dir.mkdirs();
         }

         Iterator var6 = getResources(label).iterator();

         while(true) {
            String pet;
            do {
               if (!var6.hasNext()) {
                  continue label35;
               }

               pet = (String)var6.next();
            } while(!pet.endsWith(".yml") && !pet.endsWith(".yaml"));

            if (!(new File("plugins/KojiSkyblock/" + pet)).exists()) {
               plugin.saveResource(pet, false);
            }
         }
      }

   }

   public static void loadPets() {
      plugin.getLogger().log(Level.INFO, "Loading Pets...");
      File dir = new File("plugins/KojiSkyblock/pets/pets");
      File skillsFile = new File("plugins/KojiSkyblock/pet-skills.yml");
      FileConfiguration skillsFC = YamlConfiguration.loadConfiguration(skillsFile);
      File[] var3 = dir.listFiles();
      int var4 = var3.length;

      int var5;
      final HashMap baseStats;
      for(var5 = 0; var5 < var4; ++var5) {
         File f = var3[var5];
         if (f.isFile()) {
            final FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
            final ArrayList rarities = new ArrayList();
            baseStats = new HashMap();
            final HashMap statsPerLevel = new HashMap();
            final ArrayList petAbilities = new ArrayList();
            HashMap abilityValues = new HashMap();
            Rarity[] var13 = Rarity.values();
            int var14 = var13.length;

            final String key;
            for(int var15 = 0; var15 < var14; ++var15) {
               Rarity rarity = var13[var15];
               key = rarity.getName().toLowerCase();
               if (fc.contains(key)) {
                  rarities.add(rarity);
               }

               String[] keys = new String[]{"base_stats", "upgrade_per_level"};
               Iterator var19 = getKeys(fc, key + ".abilities.", false).iterator();

               String key;
               while(var19.hasNext()) {
                  String abilities = (String)var19.next();
                  String[] splitKeys = abilities.split("\\.");
                  key = (String)getLast(splitKeys);
                  if (!abilityValues.containsKey(key)) {
                     abilityValues.put(key, new HashMap());
                  }

                  HashMap placeholderValues = (HashMap)abilityValues.get(key);
                  HashMap placeholders = new HashMap();

                  for(int i = 0; i < 2; ++i) {
                     Iterator var26 = getKeys(fc, abilities + "." + keys[i] + ".", false).iterator();

                     while(var26.hasNext()) {
                        String placeholderKey = (String)var26.next();
                        Double[] doubles = (Double[])placeholders.getOrDefault(getLast(placeholderKey.split("\\.")), new Double[2]);
                        doubles[i] = fc.getDouble(placeholderKey);
                        placeholders.put(getLast(placeholderKey.split("\\.")), doubles);
                     }
                  }

                  placeholderValues.put(rarity, placeholders);
                  abilityValues.put(key, placeholderValues);
               }

               for(int i = 0; i < 2; ++i) {
                  StatMap map = new StatMap(new Duplet[0]);
                  Iterator var44 = getKeys(fc, key + ".stats." + keys[i] + ".", false).iterator();

                  while(var44.hasNext()) {
                     key = (String)var44.next();
                     String[] splitKey = key.split("\\.");
                     String keyName = (String)getLast(splitKey);
                     Stats[] var47 = Stats.values();
                     int var48 = var47.length;

                     for(int var49 = 0; var49 < var48; ++var49) {
                        Stats stat = var47[var49];
                        if (stat != Stats.HEALTH && stat != Stats.MANA && keyName.equals(stat.getPlaceholderTag())) {
                           map.put(stat, fc.getDouble(key));
                        }
                     }
                  }

                  if (i == 0) {
                     baseStats.put(rarity, map);
                  } else {
                     statsPerLevel.put(rarity, map);
                  }
               }
            }

            final PetKind petKind;
            try {
               petKind = PetKind.valueOf(fc.getString("type").toUpperCase());
            } catch (Exception var30) {
               Skyblock.getPlugin().getLogger().log(Level.WARNING, "Skipped " + f.getName() + " because the type wasn't valid!");
               continue;
            }

            boolean canContinue = false;
            ArrayList continueString = new ArrayList();
            Iterator var39 = abilityValues.keySet().iterator();

            while(var39.hasNext()) {
               key = (String)var39.next();

               try {
                  final List lore = skillsFC.getStringList(fc.getString("type").toLowerCase() + "." + key);
                  final HashMap placeholders = (HashMap)abilityValues.get(key);
                  petAbilities.add(new PetAbility() {
                     public String getName() {
                        return key;
                     }

                     public String getDisplayName() {
                        return Files.space(Files.capitalize(key));
                     }

                     public ArrayList validRarities() {
                        return new ArrayList(placeholders.keySet());
                     }

                     public HashMap getPlaceHolderSlotsBaseValue() {
                        return placeholders;
                     }

                     public ArrayList getLore() {
                        return new ArrayList(lore);
                     }
                  });
               } catch (NullPointerException var29) {
                  continueString.add(key);
                  canContinue = true;
               }
            }

            if (canContinue) {
               Skyblock.getPlugin().getLogger().log(Level.WARNING, "Skipped " + f.getName() + " because the following abilities aren't valid: " + formatList(continueString));
            } else {
               registeredPets.put(fc.getString("name"), new Pet() {
                  public String getName() {
                     return fc.getString("name");
                  }

                  public PetKind getPetKind() {
                     return petKind;
                  }

                  public ArrayList rarities() {
                     return rarities;
                  }

                  public HashMap baseStats() {
                     return baseStats;
                  }

                  public HashMap addStatPerLevel() {
                     return statsPerLevel;
                  }

                  public PetAbility getFirstAbility() {
                     return (PetAbility)getOrDefault(petAbilities, 0, (Object)null);
                  }

                  public PetAbility getSecondAbility() {
                     return (PetAbility)getOrDefault(petAbilities, 1, (Object)null);
                  }

                  public PetAbility getThirdAbility() {
                     return (PetAbility)getOrDefault(petAbilities, 2, (Object)null);
                  }

                  public String getTexture() {
                     return fc.getString("texture");
                  }
               });
            }
         }
      }

      File skinDir = new File("plugins/KojiSkyblock/pets/skins");
      if (skinDir.exists() && ((File[])Objects.requireNonNull(skinDir.listFiles())).length != 0) {
         File[] var32 = (File[])Objects.requireNonNull(skinDir.listFiles());
         var5 = var32.length;

         for(int var33 = 0; var33 < var5; ++var33) {
            File f = var32[var33];
            FileConfiguration fC = YamlConfiguration.loadConfiguration(f);
            baseStats = (HashMap)petSkins.getOrDefault(fC.getString("pet-id-name"), new HashMap());
            baseStats.put(fC.getString("skin-id").toLowerCase(), getSkinItem(fC));
            petSkins.put(fC.getString("pet-id-name"), baseStats);
         }
      }

   }

   private static ItemStack getSkinItem(FileConfiguration fC) {
      String petIdName = fC.getString("pet-id-name");
      String skullId = fC.getString("skull-id");
      ItemBuilder ib = (new ItemBuilder(XMaterial.PLAYER_HEAD)).setTexture(skullId).setName(ChatColor.GOLD + fC.getString("display-name") + " Skin").setLore(new String[]{"§8Consumed on use", "", "§7Pet skins change the look and", "§7particle trail of your pet but", "§7only one skin can be active at a", "§7time!", "", "§7This skin can only be applied", "§7to " + space(petIdName) + " pets.", "", "§eRight click on your pet to", "§eapply this skin!", "", "§6§lLEGENDARY COSMETIC"}).setString("petSkinType", petIdName).setString("petSkin", skullId);
      return ib.build();
   }

   public static void loadFiles() throws IOException, InvalidConfigurationException {
      plugin.getLogger().log(Level.INFO, "Reading Files...");
      customItems.load(customItemsFile);
      config.load(configFile);
      defaultItemOverrides.load(defaultItemOverridesFile);
      File skills = new File("plugins/KojiSkyblock/pet-skills.yml");
      FileConfiguration skillsFC = YamlConfiguration.loadConfiguration(skills);
      skillsFC.load(skills);
   }

   private static void loadItemAbilities() {
      File dir = new File("plugins/KojiSkyblock/abilities");
      if (!dir.exists()) {
         dir.mkdirs();
      }

      Iterator var1 = getResources("abilities").iterator();

      while(true) {
         String string;
         do {
            if (!var1.hasNext()) {
               return;
            }

            string = (String)var1.next();
         } while(!string.endsWith(".yml") && !string.endsWith(".yaml"));

         File f = new File("plugins/KojiSkyblock/" + string);
         if (!f.exists()) {
            plugin.saveResource(string, false);
         }
      }
   }

   public static ArrayList getResources(String path) {
      try {
         File jarFile = Paths.get(Files.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toFile();
         ArrayList strings = new ArrayList();
         JarInputStream jis = new JarInputStream(new FileInputStream(jarFile));

         for(JarEntry jarEntry = jis.getNextJarEntry(); jarEntry != null; jarEntry = jis.getNextJarEntry()) {
            if (jarEntry.getName().startsWith(path + "/")) {
               strings.add(jarEntry.getName());
            }
         }

         return strings;
      } catch (Throwable var5) {
         throw var5;
      }
   }

   public static FileConfiguration getConfig() {
      return config;
   }

   public static File getCustomItemsFile() {
      return customItemsFile;
   }

   public static FileConfiguration getCustomItems() {
      return customItems;
   }

   public static File getDefaultItemOverridesFile() {
      return defaultItemOverridesFile;
   }

   public static FileConfiguration getDefaultItemOverrides() {
      return defaultItemOverrides;
   }

   public static HashMap getRegisteredPets() {
      return registeredPets;
   }

   public static HashMap getPetSkins() {
      return petSkins;
   }

   static {
      config = YamlConfiguration.loadConfiguration(configFile);
      customItemsFile = new File("plugins/KojiSkyblock/custom-items.yml");
      customItems = YamlConfiguration.loadConfiguration(customItemsFile);
      defaultItemOverridesFile = new File("plugins/KojiSkyblock/default-item-overrides.yml");
      defaultItemOverrides = YamlConfiguration.loadConfiguration(defaultItemOverridesFile);
      plugin = Skyblock.getPlugin();
      registeredPets = new HashMap();
      petSkins = new HashMap();
   }
}
