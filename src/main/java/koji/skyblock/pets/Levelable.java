package koji.skyblock.pets;

import koji.developerkit.KBase;
import koji.skyblock.files.Files;
import koji.skyblock.item.Rarity;
import koji.skyblock.player.PClass;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Levelable extends KBase {
   public static final double[] COMMON_XP_REQUIREMENTS = new double[100];
   public static final double[] UNCOMMON_XP_REQUIREMENTS = new double[100];
   public static final double[] RARE_XP_REQUIREMENTS = new double[100];
   public static final double[] EPIC_XP_REQUIREMENTS = new double[100];
   public static final double[] LEGENDARY_XP_REQUIREMENTS = new double[100];
   private double currentXP;
   private int level;
   private double[] requirements;

   public static double getRequirementsForLevel(Rarity r, int level) {
      switch(r) {
      case COMMON:
         return COMMON_XP_REQUIREMENTS[level - 1];
      case UNCOMMON:
         return UNCOMMON_XP_REQUIREMENTS[level - 1];
      case RARE:
         return RARE_XP_REQUIREMENTS[level - 1];
      case EPIC:
         return EPIC_XP_REQUIREMENTS[level - 1];
      default:
         return LEGENDARY_XP_REQUIREMENTS[level - 1];
      }
   }

   public static double[] getRequirements(Rarity r) {
      switch(r) {
      case COMMON:
         return COMMON_XP_REQUIREMENTS;
      case UNCOMMON:
         return UNCOMMON_XP_REQUIREMENTS;
      case RARE:
         return RARE_XP_REQUIREMENTS;
      case EPIC:
         return EPIC_XP_REQUIREMENTS;
      default:
         return LEGENDARY_XP_REQUIREMENTS;
      }
   }

   public Levelable(int currentLevel, double currentXP, double[] requirements) {
      this.requirements = requirements;
      this.currentXP = currentXP;
      this.level = currentLevel;
   }

   public double[] getRequirements() {
      return this.requirements;
   }

   public int getLevel() {
      return this.level;
   }

   public double getCurrentXP() {
      return this.currentXP;
   }

   public double getRequiredXP() {
      return this.requirements[this.level];
   }

   public void setRequirements(double[] reqs) {
      this.requirements = reqs;
   }

   public void setLevel(int level) {
      this.level = level;
   }

   public void setCurrentXP(double xp) {
      this.currentXP = xp;
   }

   public void addXP(double xp, Player p) {
      if (this.level == 0) {
         this.level = 100;
         this.currentXP = 0.0D;
      }

      if (this.level != 100) {
         this.currentXP += xp;
         boolean up = false;

         while(this.currentXP >= this.requirements[this.level - 1]) {
            this.currentXP -= this.requirements[this.level - 1];
            ++this.level;
            up = true;
            p.sendMessage("§aYour " + PClass.getPlayer(p).getPetInstance().getName() + " §alevelled up to level §9" + this.level + "§a!");
         }

         if (up) {
            PClass.getPlayer(p).getPetInstance().save();
         }
      }

   }

   static {
      FileConfiguration fc = Files.getConfig();

      for(int i = 0; i <= 98; ++i) {
         String str = fc.getString("modules.pets.xp_requirements.Lvl" + (i + 2));
         COMMON_XP_REQUIREMENTS[i] = (double)Integer.parseInt(str.split(" ")[0]);
         UNCOMMON_XP_REQUIREMENTS[i] = (double)Integer.parseInt(str.split(" ")[1]);
         RARE_XP_REQUIREMENTS[i] = (double)Integer.parseInt(str.split(" ")[2]);
         EPIC_XP_REQUIREMENTS[i] = (double)Integer.parseInt(str.split(" ")[3]);
         LEGENDARY_XP_REQUIREMENTS[i] = (double)Integer.parseInt(str.split(" ")[4]);
      }

   }
}
