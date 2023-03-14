package koji.skyblock.files.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import koji.skyblock.files.Config;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.enchants.EnchantTableGUI;
import koji.skyblock.item.utils.ItemStackSerializer;
import koji.skyblock.player.PClass;
import koji.skyblock.player.Stats;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class FileData extends PlayerData {
   private static FileConfiguration playerConfig(Player p) {
      return YamlConfiguration.loadConfiguration(playerFile(p));
   }

   private static void set(Player p, String path, Object set) {
      try {
         FileConfiguration config = playerConfig(p);
         config.set(path, set);
         config.save(playerFile(p));
      } catch (Throwable var4) {
         throw var4;
      }
   }

   private static File playerFile(Player p) {
      File file = new File("plugins/KojiSkyblock/playerData/" + p.getUniqueId() + "/data.yml");
      if (!file.exists()) {
         PlayerData.getPlayerData().createPlayer(p);
      }

      return file;
   }

   public void setBits(Player p, Integer i) {
      set(p, "bits", i);
      PClass.getPlayer(p).setBits(i);
   }

   public int getBits(Player p) {
      return playerConfig(p).getInt("bits");
   }

   public void setStat(Player p, Stats statType, Double integer) {
      set(p, "stats.base_" + statType.getPlaceholderTag(), integer);
   }

   public double getStat(Player p, Stats statType) {
      return playerConfig(p).getDouble("stats.base_" + statType.getPlaceholderTag());
   }

   public String getActivePet(Player p) {
      return playerConfig(p).getString("active-pet");
   }

   public void setActivePet(Player p, String pet) {
      set(p, "active-pet", pet);
   }

   public Boolean canSeePets(Player p) {
      return playerConfig(p).getBoolean("pets-visible");
   }

   public void setCanSeePets(Player p, Boolean pet) {
      set(p, "pets-visible", pet);
   }

   public Boolean getsIt(Player p) {
      return playerConfig(p).getBoolean("gets-it");
   }

   public void setGetsIt(Player p, Boolean getsIt) {
      set(p, "gets-it", getsIt);
   }

   public Boolean getDropItemAlert(Player p) {
      return playerConfig(p).getBoolean("has-item-drop-alert");
   }

   public void setDropItemAlert(Player p, Boolean boo) {
      set(p, "has-item-drop-alert", boo);
   }

   public EnchantTableGUI.Sorting getEnchantTableSorting(Player p) {
      return EnchantTableGUI.Sorting.valueOf(playerConfig(p).getString("enchant-table-sorting").toUpperCase());
   }

   public void setEnchantTableSorting(Player p, EnchantTableGUI.Sorting sorting) {
      set(p, "enchant-table-sorting", sorting.name().toLowerCase());
   }

   public List getItemStash(Player p) {
      String itemsString = playerConfig(p).getString("item-stash");
      if (itemsString == null) {
         return new ArrayList();
      } else {
         List stringsAsList = Arrays.asList(itemsString.split("~itemstash--item~"));
         List items = new ArrayList();
         stringsAsList.forEach((item) -> {
            CustomItem ci = ItemStackSerializer.deserialize(item);
            if (ci != null) {
               items.add(ci.buildWithAbilities());
            }
         });
         return items;
      }
   }

   public void setItemStash(Player p, List items) {
      List strings = new ArrayList();
      items.forEach((item) -> {
         strings.add(ItemStackSerializer.serialize(item));
      });
      StringBuilder sb = new StringBuilder();

      String s;
      for(Iterator var5 = strings.iterator(); var5.hasNext(); sb.append(s)) {
         s = (String)var5.next();
         if (sb.length() > 0) {
            sb.append("~itemstash--item~");
         }
      }

      if (sb.length() == 0) {
         sb = new StringBuilder("null");
      }

      set(p, "item-stash", sb.toString());
   }

   public void createPlayer(Player p) {
      try {
         File folder = new File("plugins/KojiSkyblock/playerData/" + p.getUniqueId());
         if (!folder.exists()) {
            folder.mkdirs();
         }

         File playerFile = new File("plugins/KojiSkyblock/playerData/" + p.getUniqueId() + "/data.yml");
         if (!playerFile.exists()) {
            playerFile.createNewFile();
            FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
            config.set("bits", 0);
            Stats[] var5 = Stats.values();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               Stats stats = var5[var7];
               if (stats.getPlaceholder() != null) {
                  config.set("stats.base_" + stats.getPlaceholderTag(), Config.getBaseValue(stats));
               }
            }

            config.set("active-pet", (Object)null);
            config.set("pets-visible", Config.getPetsVisibleDefault());
            config.set("gets-it", Config.getGetsItDefault());
            config.set("has-item-drop-alert", Config.getItemDropAlertDefault());
            config.set("enchant-table-sorting", "default");
            config.save(playerFile);
         } else {
            playerFile.delete();
            this.createPlayer(p);
         }

      } catch (Throwable var9) {
         throw var9;
      }
   }

   public boolean doesPlayerDataExist(Player p) {
      return p == null ? false : playerFile(p).exists();
   }

   public boolean register() {
      return true;
   }
}
