package koji.skyblock.item.enchants.enchants;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import koji.skyblock.Skyblock;
import koji.skyblock.item.ItemType;
import koji.skyblock.item.enchants.Enchant;
import koji.skyblock.player.PClass;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DragonTracer extends Enchant {
   public boolean isUltimate() {
      return false;
   }

   public int getMaxLevel() {
      return 5;
   }

   public String getName() {
      return "Dragon_Tracer";
   }

   public String getDisplayName() {
      return "Dragon Tracer";
   }

   public ArrayList getTargets() {
      return this.targets(new ItemType[]{ItemType.BOW});
   }

   @EventHandler
   public void arrowFired(EntityShootBowEvent e) {
      if (e.getEntity() != null && e.getEntity() instanceof Player && e.getProjectile() instanceof Arrow) {
         Player p = (Player)e.getEntity();
         int level = (Integer)PClass.getPlayer(p).getEnchantLevelsInHand().get(this);
         (new DragonTracer.HomingArrowRunnable(e.getProjectile(), this.getVar(level), (entity) -> {
            return entity instanceof EnderDragon;
         })).runTaskTimer(Skyblock.getPlugin(), 5L, 1L);
      }

   }

   public double getVar(int level) {
      return (double)(2 * level);
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
      return 8;
   }

   public ArrayList getConflicts() {
      return null;
   }

   public static class HomingArrowRunnable extends BukkitRunnable {
      private final Entity arrow;
      private Entity target;
      private final Predicate extraCondition;
      private final double range;

      public HomingArrowRunnable(Entity arrow, double range) {
         this(arrow, range, (Predicate)null);
      }

      public HomingArrowRunnable(Entity arrow, double range, Predicate extraCondition) {
         this.arrow = arrow;
         this.extraCondition = extraCondition;
         this.range = range;
      }

      public void run() {
         if (this.target == null) {
            this.setTarget();
         }

         if (this.target != null) {
            if (!this.arrow.isDead() && !this.target.isDead()) {
               Vector newVector = this.target.getLocation().toVector().subtract(this.arrow.getLocation().toVector()).normalize();
               this.arrow.setVelocity(newVector);
            } else {
               this.cancel();
            }
         }
      }

      private void setTarget() {
         List nearbyEntities = this.arrow.getNearbyEntities(this.range, this.range, this.range);
         if (nearbyEntities.size() != 0) {
            Optional optionalEntity = nearbyEntities.stream().filter((entity) -> {
               return !(entity instanceof Player) && !(entity instanceof ArmorStand) && entity instanceof LivingEntity && ((LivingEntity)entity).hasLineOfSight(this.arrow);
            }).filter((entity) -> {
               return this.extraCondition == null || this.extraCondition.test(entity);
            }).min(Comparator.comparing((entity) -> {
               return entity.getLocation().distanceSquared(this.arrow.getLocation());
            }));
            if (optionalEntity.isPresent()) {
               this.target = (Entity)optionalEntity.get();
            }
         }
      }
   }
}
