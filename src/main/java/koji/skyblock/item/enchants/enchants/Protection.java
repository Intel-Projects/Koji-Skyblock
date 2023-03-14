package koji.skyblock.item.enchants.enchants;

import java.util.ArrayList;
import koji.developerkit.utils.duplet.Duplet;
import koji.skyblock.item.ItemType;
import koji.skyblock.item.enchants.Enchant;
import koji.skyblock.player.Stats;
import koji.skyblock.utils.StatMap;

public class Protection extends Enchant {
   public boolean isUltimate() {
      return false;
   }

   public int getMaxLevel() {
      return 5;
   }

   public String getName() {
      return "Protection";
   }

   public String getDisplayName() {
      return "Protection";
   }

   public ArrayList getTargets() {
      return this.targets(new ItemType[]{ItemType.ARMOR});
   }

   public StatMap addStats(int level) {
      StatMap stats = new StatMap(new Duplet[0]);
      stats.put(Stats.DEFENSE, (double)(3 * level));
      return stats;
   }

   public double getVar(int level) {
      return (double)(3 * level);
   }

   public boolean canAppearInEnchantTable() {
      return true;
   }

   public int getBaseExperienceCost() {
      return 10;
   }

   public int getExperienceAddedPerLevel(int level) {
      return 5;
   }

   public int getBookshelfPowerRequirement() {
      return 0;
   }

   public ArrayList getConflicts() {
      return null;
   }
}
