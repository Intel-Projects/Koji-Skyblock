package koji.skyblock.reflection.armorstand;

import koji.skyblock.reflection.UncollidableArmorStand;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EnumMoveType;
import net.minecraft.server.v1_16_R3.Vec3D;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class UncollidableArmorStand_1_16 extends EntityArmorStand implements UncollidableArmorStand {
   public UncollidableArmorStand_1_16(Location loc) {
      super(EntityTypes.ARMOR_STAND, ((CraftWorld)loc.getWorld()).getHandle());
   }

   public void g(Vec3D vec3D) {
      if (!this.isNoGravity()) {
         super.g(vec3D);
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
