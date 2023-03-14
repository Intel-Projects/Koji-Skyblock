package koji.skyblock.item.enchants.enchants;

import java.util.ArrayList;
import koji.developerkit.utils.duplet.Duplet;
import koji.skyblock.item.ItemType;
import koji.skyblock.item.enchants.Enchant;
import koji.skyblock.player.Stats;
import koji.skyblock.utils.StatMap;

public class Vicious extends Enchant {
   public boolean isUltimate() {
      return false;
   }

   public int getMaxLevel() {
      return 3;
   }

   public String getName() {
      return "Vicious";
   }

   public String getDisplayName() {
      return "Vicious";
   }

   public ArrayList getTargets() {
      return this.targets(new ItemType[]{ItemType.SWORD, ItemType.FISHING_WEAPON});
   }

   public StatMap addStats(int level) {
      StatMap stats = new StatMap(new Duplet[0]);
      stats.put(Stats.FEROCITY, level);
      return stats;
   }

   public double getVar(int level) {
      return (double)level;
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
