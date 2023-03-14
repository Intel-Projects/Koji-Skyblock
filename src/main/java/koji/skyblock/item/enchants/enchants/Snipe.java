package koji.skyblock.item.enchants.enchants;

import java.util.ArrayList;
import java.util.HashMap;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.ItemType;
import koji.skyblock.item.enchants.Enchant;
import koji.skyblock.player.events.SkyblockDamageEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class Snipe extends Enchant {
   HashMap shootLocation = new HashMap();

   public boolean isUltimate() {
      return false;
   }

   public int getMaxLevel() {
      return 4;
   }

   public String getName() {
      return "Snipe";
   }

   public String getDisplayName() {
      return "Snipe";
   }

   public ArrayList getTargets() {
      return this.targets(new ItemType[]{ItemType.BOW});
   }

   @EventHandler
   public void onArrowLaunch(ProjectileLaunchEvent e) {
      if (e.getEntity() != null && e.getEntity().getShooter() != null && e.getEntity().getShooter() instanceof Player) {
         this.shootLocation.put(e.getEntity(), e.getEntity().getLocation());
      }

   }

   @EventHandler
   public void onArrowHit(SkyblockDamageEvent e) {
      if (!e.isPlayerReceivingDamage() && e.getEntity() != null && e.getDamager() != null && e.getArrow() != null && e.getType() == SkyblockDamageEvent.Type.BOW) {
         int level = (Integer)(new CustomItem(e.parseItem((Player)e.getDamager()))).getEnchants().get(this);
         if (this.shootLocation.containsKey(e.getArrow())) {
            double distance = ((Location)this.shootLocation.get(e.getArrow())).distance(e.getDamager().getLocation());
            int added = (int)distance / 10;
            e.addToAdditiveMultiplier((double)added * 0.01D * (double)level);
         }
      }

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
      return 5;
   }

   public int getBookshelfPowerRequirement() {
      return 8;
   }

   public ArrayList getConflicts() {
      return null;
   }
}
