package koji.skyblock.item.enchants.enchants;

import java.util.ArrayList;
import koji.developerkit.utils.duplet.Duplet;
import koji.skyblock.item.ItemType;
import koji.skyblock.item.enchants.Enchant;
import koji.skyblock.player.Stats;
import koji.skyblock.utils.StatMap;

public class Critical extends Enchant {
   public boolean isUltimate() {
      return false;
   }

   public int getMaxLevel() {
      return 5;
   }

   public String getName() {
      return "Critical";
   }

   public String getDisplayName() {
      return "Critical";
   }

   public ArrayList getTargets() {
      return this.targets(new ItemType[]{ItemType.SWORD, ItemType.FISHING_WEAPON});
   }

   public StatMap addStats(int level) {
      StatMap returnMap = new StatMap(new Duplet[0]);
      returnMap.put(Stats.CRIT_DAMAGE, (double)(10 * level));
      return returnMap;
   }

   public double getVar(int level) {
      return (double)(10 * level);
   }

   public boolean canAppearInEnchantTable() {
      return true;
   }

   public int getBaseExperienceCost() {
      return 10;
   }

   public int getExperienceAddedPerLevel(int level) {
      return 10;
   }

   public int getBookshelfPowerRequirement() {
      return 4;
   }

   public ArrayList getConflicts() {
      return null;
   }
}
