package koji.skyblock.item.enchants.enchants;

import java.util.ArrayList;
import java.util.List;
import koji.developerkit.runnable.KRunnable;
import koji.skyblock.Skyblock;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.ItemType;
import koji.skyblock.item.enchants.Enchant;
import koji.skyblock.player.events.SkyblockDamageEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class FireAspect extends Enchant {
   public boolean isUltimate() {
      return false;
   }

   public int getMaxLevel() {
      return 2;
   }

   public String getName() {
      return "Fire_Aspect";
   }

   public String getDisplayName() {
      return "Fire Aspect";
   }

   public ArrayList getTargets() {
      return this.targets(new ItemType[]{ItemType.SWORD, ItemType.FISHING_WEAPON});
   }

   @EventHandler
   public void onSkyblockDamage(SkyblockDamageEvent e) {
      if (e.getDamager() instanceof Player && e.getType() == SkyblockDamageEvent.Type.SWORD) {
         CustomItem ci = new CustomItem(e.parseItem((Player)e.getDamager()));
         int level = ci.getEnchantLevel(this);
         (new KRunnable((task) -> {
            if (((LivingEntity)e.getEntity()).getHealth() > 0.0D) {
               (new KRunnable((task1) -> {
                  if (e.getEntity().isDead()) {
                     task1.cancel();
                  }

                  Bukkit.getPluginManager().callEvent(new SkyblockDamageEvent(e.getDamager(), e.getEntity(), false, SkyblockDamageEvent.Type.FIRE, e.getDamage() * 0.3D * (double)level, ci));
               }, (long)(20.0D * this.getVar(level)))).runTaskTimer(Skyblock.getPlugin(), 0L, 20L);
            }

         })).runTaskLater(Skyblock.getPlugin(), 1L);
      }

   }

   public double getVar(int level) {
      return level == 1 ? 3.0D : (double)(2 * level);
   }

   public List getExtraVars(int level) {
      return arrayList(new Integer[]{3 * level});
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
