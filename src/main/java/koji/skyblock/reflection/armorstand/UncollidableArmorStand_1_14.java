package koji.skyblock.reflection.armorstand;

import koji.skyblock.reflection.UncollidableArmorStand;
import net.minecraft.server.v1_14_R1.EntityArmorStand;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.EnumMoveType;
import net.minecraft.server.v1_14_R1.Vec3D;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class UncollidableArmorStand_1_14 extends EntityArmorStand implements UncollidableArmorStand {
   public UncollidableArmorStand_1_14(Location loc) {
      super(EntityTypes.ARMOR_STAND, ((CraftWorld)loc.getWorld()).getHandle());
   }

   public void e(Vec3D vec3D) {
      if (!this.isNoGravity()) {
         super.e(vec3D);
      } else {
         this.move(EnumMoveType.SELF, this.getMot());
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
