package koji.skyblock.reflection.armorstand;

import koji.skyblock.reflection.UncollidableArmorStand;
import net.minecraft.server.v1_11_R1.EntityArmorStand;
import net.minecraft.server.v1_11_R1.EnumMoveType;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class UncollidableArmorStand_1_11 extends EntityArmorStand implements UncollidableArmorStand {
   public UncollidableArmorStand_1_11(Location loc) {
      super(((CraftWorld)loc.getWorld()).getHandle());
   }

   public void g(float f, float f1) {
      if (!this.isNoGravity()) {
         super.g(f, f1);
      } else {
         this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
      }

   }

   public LivingEntity spawn(Location location) {
      this.setPosition(location.getX(), location.getY(), location.getZ());
      this.setYawPitch(location.getYaw(), location.getPitch());
      this.setInvisible(true);
      this.setMarker(true);
      this.noclip = true;
      this.world.addEntity(this, SpawnReason.CUSTOM);
      return (LivingEntity)this.getBukkitEntity();
   }
}
