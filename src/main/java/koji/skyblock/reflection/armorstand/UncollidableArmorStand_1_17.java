package koji.skyblock.reflection.armorstand;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import koji.developerkit.KBase;
import koji.developerkit.utils.xseries.ReflectionUtils;
import koji.developerkit.utils.xseries.XMaterial;
import koji.skyblock.reflection.UncollidableArmorStand;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class UncollidableArmorStand_1_17 extends KBase implements UncollidableArmorStand {
   public LivingEntity spawn(Location location) {
      try {
         Class entityClass = ReflectionUtils.getNMSClass("world.entity", "Entity");
         Class armorStandClass = ReflectionUtils.getNMSClass("world.entity.decoration", "EntityArmorStand");
         Class worldClass = ReflectionUtils.getNMSClass("world.level", "World");
         Object craftWorld = getNMSWorld(location.getWorld());
         Object armorStand = armorStandClass.getConstructor(worldClass, Double.TYPE, Double.TYPE, Double.TYPE).newInstance(craftWorld, location.getX(), location.getY(), location.getZ());
         String setYawPitchName = XMaterial.getVersion() == 17 ? "setYawPitch" : "a";
         Method setYawPitch = armorStandClass.getSuperclass().getSuperclass().getDeclaredMethod(setYawPitchName, Float.TYPE, Float.TYPE);
         setYawPitch.setAccessible(true);
         setYawPitch.invoke(armorStand, location.getYaw(), location.getPitch());
         String setInvisibleName = XMaterial.getVersion() == 17 ? "setInvisible" : "j";
         Method setInvisible = armorStandClass.getMethod(setInvisibleName, Boolean.TYPE);
         setInvisible.invoke(armorStand, true);
         String setMarkerName = XMaterial.getVersion() == 17 ? "setMarker" : "t";
         Method setMarker = armorStandClass.getMethod(setMarkerName, Boolean.TYPE);
         setMarker.invoke(armorStand, true);
         Field noClip = armorStandClass.getField(XMaterial.getVersion() == 17 ? "P" : "Q");
         noClip.set(armorStand, true);
         String addEntityName = XMaterial.getVersion() == 17 ? "addEntity" : "b";
         Method addEntity = craftWorld.getClass().getMethod(addEntityName, entityClass);
         addEntity.invoke(craftWorld, armorStand);
         Method getBukkitEntity = armorStandClass.getMethod("getBukkitEntity");
         return (LivingEntity)getBukkitEntity.invoke(armorStand);
      } catch (Throwable var17) {
         throw var17;
      }
   }
}
