package koji.skyblock.files.pets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import koji.skyblock.item.Rarity;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class FilePetData extends PetData {
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
      try {
         File file = new File("plugins/KojiSkyblock/playerData/" + p.getUniqueId() + "/" + p.getUniqueId());
         if (!file.exists()) {
            if (!(new File("plugins/KojiSkyblock/playerData")).exists()) {
               (new File("plugins/KojiSkyblock/playerData")).mkdir();
            }

            file.createNewFile();
         }

         return file;
      } catch (Throwable var2) {
         throw var2;
      }
   }

   public String getType(Player p, String uuid) {
      return playerConfig(p).getString(uuid + ".type");
   }

   public void setType(Player p, String uuid, String type) {
      set(p, uuid + ".type", type);
   }

   public int getLevel(Player p, String uuid) {
      return playerConfig(p).getInt(uuid + ".level");
   }

   public void setLevel(Player p, String uuid, int type) {
      set(p, uuid + ".level", type);
   }

   public double getCurrentExp(Player p, String uuid) {
      return playerConfig(p).getDouble(uuid + ".currentXP");
   }

   public void setCurrentExp(Player p, String uuid, double type) {
      set(p, uuid + ".currentXP", type);
   }

   public String getSkin(Player p, String uuid) {
      return playerConfig(p).getString(uuid + ".skin");
   }

   public void setSkin(Player p, String uuid, String type) {
      set(p, uuid + ".skin", type);
   }

   public Rarity getRarity(Player p, String uuid) {
      return Rarity.valueOf(playerConfig(p).getString(uuid + ".rarity").toUpperCase());
   }

   public void setRarity(Player p, String uuid, Rarity type) {
      set(p, uuid + ".rarity", type.getName());
   }

   public boolean getPetExists(Player p, String uuid) {
      return true;
   }

   public void createPet(Player p, String uuid, String type, int level, double currentXP, String skin, Rarity rarity) {
      this.setType(p, uuid, type);
      this.setLevel(p, uuid, level);
      this.setCurrentExp(p, uuid, currentXP);
      this.setSkin(p, uuid, skin);
      this.setRarity(p, uuid, rarity);
   }

   public boolean getPlayerExists(Player p) {
      File file = new File("plugins/KojiSkyblock/playerData/" + p.getUniqueId() + "/" + p.getUniqueId());
      return file.exists();
   }

   public void createPlayer(Player p) {
      playerFile(p);
   }

   public void addPet(Player p, String uuid) {
   }

   public List getPets(Player p) {
      return new ArrayList(playerConfig(p).getKeys(false));
   }

   public void erasePetData(Player p, String uuid) {
      set(p, uuid, (Object)null);
   }
}
