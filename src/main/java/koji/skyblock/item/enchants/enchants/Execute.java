package koji.skyblock.item.enchants.enchants;

import java.util.ArrayList;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.ItemType;
import koji.skyblock.item.enchants.Enchant;
import koji.skyblock.player.PClass;
import koji.skyblock.player.events.SkyblockDamageEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class Execute extends Enchant {
   public boolean isUltimate() {
      return false;
   }

   public int getMaxLevel() {
      return 5;
   }

   public String getName() {
      return "Execute";
   }

   public String getDisplayName() {
      return "Execute";
   }

   public ArrayList getTargets() {
      return this.targets(new ItemType[]{ItemType.SWORD, ItemType.FISHING_WEAPON});
   }

   @EventHandler
   public void onSkyblockDamage(SkyblockDamageEvent e) {
      if (e.getDamager() instanceof Player) {
         LivingEntity entity = (LivingEntity)e.getEntity();
         PClass pS = PClass.getPlayer((Player)e.getDamager());
         e.addToAdditiveMultiplier((entity.getMaxHealth() - entity.getHealth()) / entity.getMaxHealth() * this.getVar((Integer)(new CustomItem(e.parseItem(pS.player()))).getEnchants().get(this)));
      }

   }

   public double getVar(int level) {
      return 0.2D * (double)level;
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
      return 10;
   }

   public ArrayList getConflicts() {
      return arrayList(new Class[]{Prosecute.class});
   }
}
