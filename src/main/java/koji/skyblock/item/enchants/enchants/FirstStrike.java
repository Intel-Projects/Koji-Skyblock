package koji.skyblock.item.enchants.enchants;

import java.util.ArrayList;
import koji.skyblock.item.ItemType;
import koji.skyblock.item.enchants.Enchant;
import koji.skyblock.player.PClass;
import koji.skyblock.player.events.SkyblockDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class FirstStrike extends Enchant {
   public boolean isUltimate() {
      return false;
   }

   public int getMaxLevel() {
      return 4;
   }

   public String getName() {
      return "First_Strike";
   }

   public String getDisplayName() {
      return "First Strike";
   }

   public ArrayList getTargets() {
      return this.targets(new ItemType[]{ItemType.SWORD, ItemType.FISHING_WEAPON});
   }

   @EventHandler
   public void onSkyblockDamage(SkyblockDamageEvent e) {
      if (e.getDamager() instanceof Player) {
         PClass pS = PClass.getPlayer((Player)e.getDamager());
         if (pS.getLastAttackedEntity() == null || !pS.getLastAttackedEntity().equals(e.getEntity())) {
            e.addToAdditiveMultiplier(0.25D * (double)(Integer)pS.getEnchantLevelsInHand().get(this));
         }
      }

   }

   public double getVar(int level) {
      return (double)(25 * level);
   }

   public boolean canAppearInEnchantTable() {
      return true;
   }

   public int getBaseExperienceCost() {
      return 20;
   }

   public int getExperienceAddedPerLevel(int level) {
      return level == 4 ? 35 : 10;
   }

   public int getBookshelfPowerRequirement() {
      return 5;
   }

   public ArrayList getConflicts() {
      return null;
   }
}
