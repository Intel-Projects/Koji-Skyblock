package koji.skyblock.item.enchants.enchants;

import java.util.ArrayList;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.ItemType;
import koji.skyblock.item.enchants.Enchant;
import koji.skyblock.player.PClass;
import koji.skyblock.player.events.SkyblockDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class Sharpness extends Enchant {
   public boolean isUltimate() {
      return false;
   }

   public int getMaxLevel() {
      return 5;
   }

   public String getName() {
      return "Sharpness";
   }

   public String getDisplayName() {
      return "Sharpness";
   }

   public ArrayList getTargets() {
      return this.targets(new ItemType[]{ItemType.SWORD, ItemType.FISHING_WEAPON});
   }

   @EventHandler
   public void onSkyblockDamage(SkyblockDamageEvent e) {
      if (e.getDamager() instanceof Player) {
         PClass pS = PClass.getPlayer((Player)e.getDamager());
         if (e.getType() == SkyblockDamageEvent.Type.SWORD) {
            e.addToAdditiveMultiplier(0.05D * (double)(Integer)(new CustomItem(e.parseItem(pS.player()))).getEnchants().get(this));
         }
      }

   }

   public double getVar(int level) {
      return (double)(5 * level);
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
      return arrayList(new Class[]{BaneOfArthropods.class, Smite.class});
   }
}
