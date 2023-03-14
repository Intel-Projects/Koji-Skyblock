package koji.skyblock.item.enchants.enchants;

import java.util.ArrayList;
import koji.skyblock.item.ItemType;
import koji.skyblock.item.enchants.Enchant;
import koji.skyblock.player.PClass;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

public class Experience extends Enchant {
   public boolean isUltimate() {
      return false;
   }

   public int getMaxLevel() {
      return 3;
   }

   public String getName() {
      return "Experience";
   }

   public String getDisplayName() {
      return "Experience";
   }

   public ArrayList getTargets() {
      return this.targets(new ItemType[]{ItemType.SWORD, ItemType.FISHING_WEAPON, ItemType.PICKAXE});
   }

   @EventHandler
   public void onDeath(EntityDeathEvent e) {
      int r = (int)random(101.0F);
      int level = (Integer)PClass.getPlayer(e.getEntity().getKiller()).getEnchantLevelsInHand().get(this);
      if ((double)r <= 12.5D * (double)level) {
         e.setDroppedExp(e.getDroppedExp() * 2);
      }

   }

   public double getVar(int level) {
      return 12.5D * (double)level;
   }

   public boolean canAppearInEnchantTable() {
      return true;
   }

   public int getBaseExperienceCost() {
      return 15;
   }

   public int getExperienceAddedPerLevel(int level) {
      return 15;
   }

   public int getBookshelfPowerRequirement() {
      return 2;
   }

   public ArrayList getConflicts() {
      return null;
   }
}
