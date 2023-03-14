package koji.skyblock.item.enchants.enchants.ultimate;

import java.util.ArrayList;
import java.util.Iterator;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.ItemType;
import koji.skyblock.item.enchants.Enchant;
import koji.skyblock.player.PClass;
import koji.skyblock.player.events.SkyblockDamageEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class Swarm extends Enchant {
   public boolean isUltimate() {
      return true;
   }

   public int getMaxLevel() {
      return 5;
   }

   public String getName() {
      return "Swarm";
   }

   public String getDisplayName() {
      return "Swarm";
   }

   public ArrayList getTargets() {
      return this.targets(new ItemType[]{ItemType.SWORD, ItemType.FISHING_WEAPON});
   }

   @EventHandler
   public void onSkyblockDamage(SkyblockDamageEvent e) {
      if (e.getDamager() instanceof Player) {
         PClass pS = PClass.getPlayer((Player)e.getDamager());
         int limit = 0;
         Iterator var4 = e.getEntity().getNearbyEntities(10.0D, 10.0D, 10.0D).iterator();

         while(var4.hasNext()) {
            Entity entity = (Entity)var4.next();
            if (!(entity instanceof Player) && entity instanceof LivingEntity && limit < 10) {
               e.addToAdditiveMultiplier(0.0125D * (double)(Integer)(new CustomItem(e.parseItem(pS.player()))).getEnchants().get(this));
               ++limit;
            }
         }
      }

   }

   public double getVar(int level) {
      return 1.25D * (double)level;
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
