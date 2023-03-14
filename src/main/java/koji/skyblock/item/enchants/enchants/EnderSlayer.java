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

public class EnderSlayer extends Enchant {
   public boolean isUltimate() {
      return false;
   }

   public int getMaxLevel() {
      return 5;
   }

   public String getName() {
      return "Ender_Slayer";
   }

   public String getDisplayName() {
      return "Ender Slayer";
   }

   public ArrayList getTargets() {
      return this.targets(new ItemType[]{ItemType.SWORD, ItemType.FISHING_WEAPON});
   }

   @EventHandler
   public void onSkyblockDamage(SkyblockDamageEvent e) {
      if (e.getDamager() instanceof Player) {
         PClass pS = PClass.getPlayer((Player)e.getDamager());
         if (isMobType(e.getEntity(), new EntityType[]{EntityType.ENDER_DRAGON, EntityType.ENDERMAN})) {
            e.addToAdditiveMultiplier(0.12D * (double)(Integer)(new CustomItem(e.parseItem(pS.player()))).getEnchants().get(this));
         }
      }

   }

   public double getVar(int level) {
      return (double)(12 * level);
   }

   public boolean canAppearInEnchantTable() {
      return true;
   }

   public int getBaseExperienceCost() {
      return 10;
   }

   public int getExperienceAddedPerLevel(int level) {
      return level != 2 && level != 5 ? 5 : 10;
   }

   public int getBookshelfPowerRequirement() {
      return 5;
   }

   public ArrayList getConflicts() {
      return null;
   }
}
