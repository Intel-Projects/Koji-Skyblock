package koji.skyblock.item.enchants.enchants;

import java.util.ArrayList;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.ItemType;
import koji.skyblock.item.enchants.Enchant;
import koji.skyblock.player.PClass;
import koji.skyblock.player.events.SkyblockDamageEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class Cubism extends Enchant {
   public boolean isUltimate() {
      return false;
   }

   public int getMaxLevel() {
      return 5;
   }

   public String getName() {
      return "Cubism";
   }

   public String getDisplayName() {
      return "Cubism";
   }

   public ArrayList getTargets() {
      return this.targets(new ItemType[]{ItemType.SWORD, ItemType.FISHING_WEAPON, ItemType.BOW});
   }

   @EventHandler
   public void onSkyblockDamage(SkyblockDamageEvent e) {
      if (e.getDamager() instanceof Player) {
         PClass pS = PClass.getPlayer((Player)e.getDamager());
         if (isMobType(e.getEntity(), new EntityType[]{EntityType.CREEPER, EntityType.MAGMA_CUBE, EntityType.SLIME})) {
            e.addToAdditiveMultiplier(0.1D * (double)(Integer)(new CustomItem(e.parseItem(pS.player()))).getEnchants().get(this));
         }
      }

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
      return 3;
   }

   public ArrayList getConflicts() {
      return null;
   }
}
