package koji.skyblock.item.enchants.enchants;

import java.util.ArrayList;
import koji.developerkit.utils.duplet.Duplet;
import koji.skyblock.item.ItemType;
import koji.skyblock.item.enchants.Enchant;
import koji.skyblock.player.Stats;
import koji.skyblock.utils.StatMap;

public class SmartyPants extends Enchant {
   public boolean isUltimate() {
      return false;
   }

   public int getMaxLevel() {
      return 5;
   }

   public String getName() {
      return "Smarty_Pants";
   }

   public String getDisplayName() {
      return "Smarty Pants";
   }

   public ArrayList getTargets() {
      return this.targets(new ItemType[]{ItemType.LEGGINGS});
   }

   public StatMap addStats(int level) {
      StatMap stats = new StatMap(new Duplet[0]);
      stats.put(Stats.MAX_MANA, 5 * level);
      return stats;
   }

   public double getVar(int level) {
      return (double)(5 * level);
   }

   public boolean canAppearInEnchantTable() {
      return false;
   }

   public int getBaseExperienceCost() {
      return 0;
   }

   public int getExperienceAddedPerLevel(int level) {
      return 0;
   }

   public int getBookshelfPowerRequirement() {
      return 0;
   }

   public ArrayList getConflicts() {
      return null;
   }
}
