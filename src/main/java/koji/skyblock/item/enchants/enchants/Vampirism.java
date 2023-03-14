package koji.skyblock.item.enchants.enchants;

import java.util.ArrayList;
import koji.skyblock.item.ItemType;
import koji.skyblock.item.enchants.Enchant;
import koji.skyblock.player.PClass;
import koji.skyblock.player.Stats;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

public class Vampirism extends Enchant {
   public boolean isUltimate() {
      return false;
   }

   public int getMaxLevel() {
      return 5;
   }

   public String getName() {
      return "Vampirism";
   }

   public String getDisplayName() {
      return "Vampirism";
   }

   public ArrayList getTargets() {
      return this.targets(new ItemType[]{ItemType.SWORD, ItemType.FISHING_WEAPON});
   }

   @EventHandler
   public void onDeath(EntityDeathEvent e) {
      PClass pS = PClass.getPlayer(e.getEntity().getKiller());
      double amountToHeal = 0.01D * (double)(Integer)pS.getEnchantLevelsInHand().get(this) * pS.player().getMaxHealth();
      pS.addStat(Stats.HEALTH, amountToHeal);
   }

   public double getVar(int level) {
      return (double)level;
   }

   public boolean canAppearInEnchantTable() {
      return true;
   }

   public int getBaseExperienceCost() {
      return 20;
   }

   public int getExperienceAddedPerLevel(int level) {
      return level != 2 && level != 3 ? 10 : 5;
   }

   public int getBookshelfPowerRequirement() {
      return 15;
   }

   public ArrayList getConflicts() {
      return null;
   }
}
