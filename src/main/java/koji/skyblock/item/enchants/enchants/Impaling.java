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

public class Impaling extends Enchant {
   public boolean isUltimate() {
      return false;
   }

   public int getMaxLevel() {
      return 3;
   }

   public String getName() {
      return "Impaling";
   }

   public String getDisplayName() {
      return "Impaling";
   }

   public ArrayList getTargets() {
      return this.targets(new ItemType[]{ItemType.FISHING_ROD, ItemType.SWORD, ItemType.FISHING_WEAPON, ItemType.BOW});
   }

   @EventHandler
   public void onSkyblockDamage(SkyblockDamageEvent e) {
      if (e.getDamager() instanceof Player) {
         PClass pS = PClass.getPlayer((Player)e.getDamager());
         if (isMobType(e.getEntity(), new EntityType[]{EntityType.GUARDIAN, EntityType.SQUID})) {
            e.addToAdditiveMultiplier(0.125D * (double)(Integer)(new CustomItem(e.parseItem(pS.player()))).getEnchants().get(this));
         }
      }

   }

   public double getVar(int level) {
      return 12.5D * (double)level;
   }

   public boolean canAppearInEnchantTable() {
      return true;
   }

   public int getBaseExperienceCost() {
      return 30;
   }

   public int getExperienceAddedPerLevel(int level) {
      return 10;
   }

   public int getBookshelfPowerRequirement() {
      return 8;
   }

   public ArrayList getConflicts() {
      return null;
   }
}
