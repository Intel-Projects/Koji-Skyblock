package koji.skyblock.reflection.particles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import koji.developerkit.runnable.KRunnable;
import koji.developerkit.utils.KStatic;
import koji.developerkit.utils.xseries.XMaterial;
import koji.skyblock.Skyblock;
import koji.skyblock.player.MiscListeners;
import koji.skyblock.reflection.Particles;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class OldestVersionParticles implements Particles {
   private static final List colors;
   private static final List colorValues;

   public void damage(Location loc) {
      loc.getWorld().playEffect(loc, Effect.FLAME, 0);
   }

   public void witherImpact(Player p) {
      PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.EXPLOSION_LARGE, true, (float)p.getLocation().getX(), (float)p.getLocation().getY(), (float)p.getLocation().getZ(), 0.0F, 0.0F, 0.0F, 7.0F, 6, new int[0]);
      this.sendParticle(p.getLocation(), packet);
   }

   public void leap(Player p) {
      PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.LAVA, true, (float)p.getLocation().getX(), (float)p.getLocation().getY(), (float)p.getLocation().getZ(), 0.0F, 0.0F, 0.0F, 0.0F, 20, new int[0]);
      PacketPlayOutWorldParticles packet2 = new PacketPlayOutWorldParticles(EnumParticle.EXPLOSION_LARGE, true, (float)p.getLocation().getX(), (float)p.getLocation().getY(), (float)p.getLocation().getZ(), 0.0F, 0.0F, 0.0F, 3.0F, 3, new int[0]);
      double[][] velocities = new double[][]{{0.5D, 0.5D}, {-0.5D, -0.5D}, {0.5D, -0.5D}, {-0.5D, 0.5D}, {0.0D, 0.5D}, {0.0D, -0.5D}, {0.5D, 0.0D}, {-0.5D, 0.0D}};
      double[][] var5 = velocities;
      int var6 = velocities.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         double[] velocity = var5[var7];
         FallingBlock fallingBlock = p.getWorld().spawnFallingBlock(p.getLocation(), XMaterial.COBWEB.parseMaterial(), (byte)0);
         fallingBlock.setVelocity(new Vector(velocity[0], 0.0D, velocity[1]));
         fallingBlock.setDropItem(false);
         MiscListeners.getBlocks().add(fallingBlock);
         (new KRunnable((task) -> {
            if (fallingBlock.isOnGround()) {
               MiscListeners.getBlocks().remove(fallingBlock);
               fallingBlock.remove();
            }

         })).runTaskLater(Skyblock.getPlugin(), 1L);
      }

      this.sendParticle(p.getLocation(), packet, packet2);
   }

   public void heatSeekingRose(Location loc) {
      float randomX = randomNegative() ? (float)(-Math.random()) : (float)Math.random();
      float randomY = (float)Math.random();
      float randomZ = randomNegative() ? (float)(-Math.random()) : (float)Math.random();
      randomY = (float)((double)randomY + 0.25D);
      PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.SNOWBALL, true, (float)loc.getX() + randomX, (float)loc.getY() + randomY, (float)loc.getZ() + randomZ, 0.0F, 0.0F, 0.0F, 0.0F, 1, new int[0]);
      this.sendParticle(loc, packet);
   }

   public void radiantOrbRotate(ArmorStand loc, ArmorStand nameTag) {
      Location particle = loc.getLocation().add(loc.getLocation().getDirection().normalize().multiply(0.5D));
      PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.VILLAGER_HAPPY, true, (float)particle.getX(), (float)nameTag.getLocation().getY(), (float)particle.getZ(), 0.0F, 0.0F, 0.0F, 0.0F, 1, new int[0]);
      this.sendParticle(particle, packet);
   }

   public void radiantOrbSpawn(ArmorStand nameTag) {
      if (colorValues.isEmpty()) {
         colors.forEach((c) -> {
            colorValues.add(new float[]{(float)c.getRed() / 255.0F, (float)c.getGreen() / 255.0F, (float)c.getBlue() / 255.0F});
         });
      }

      PacketPlayOutWorldParticles[] packets = new PacketPlayOutWorldParticles[4];

      for(int i = 0; i < 4; ++i) {
         float[] random = (float[])KStatic.getRandom(colorValues);
         packets[i] = new PacketPlayOutWorldParticles(EnumParticle.SPELL_MOB, true, (float)nameTag.getLocation().getX(), (float)nameTag.getLocation().getY(), (float)nameTag.getLocation().getZ(), random[0], random[1], random[2], 1.0F, 0, new int[0]);
      }

      this.sendParticle(nameTag.getLocation(), packets);
   }

   public void radiantOrbPlayer(Player p) {
      PacketPlayOutWorldParticles[] packets = new PacketPlayOutWorldParticles[4];

      for(int i = 0; i < 4; ++i) {
         float randomX = (randomNegative() ? (float)(-Math.random()) : (float)Math.random()) * 0.3F;
         float randomY = (float)Math.random() * 0.3F;
         float randomZ = (randomNegative() ? (float)(-Math.random()) : (float)Math.random()) * 0.3F;
         Location loc = p.getLocation().add((double)randomX, 0.1D + (double)randomY, (double)randomZ);
         packets[i] = new PacketPlayOutWorldParticles(EnumParticle.VILLAGER_HAPPY, true, (float)loc.getX(), (float)loc.getY(), (float)loc.getZ(), 0.0F, 0.0F, 0.0F, 0.0F, 1, new int[0]);
      }

      this.sendParticle(p.getLocation(), packets);
   }

   public void radiantOrbDespawn(ArmorStand body) {
      PacketPlayOutWorldParticles[] packets = new PacketPlayOutWorldParticles[4];

      for(int i = 0; i < 4; ++i) {
         float randomX = (randomNegative() ? (float)(-Math.random()) : (float)Math.random()) * 0.5F;
         float randomY = (float)Math.random();
         float randomZ = (randomNegative() ? (float)(-Math.random()) : (float)Math.random()) * 0.5F;
         packets[i] = new PacketPlayOutWorldParticles(EnumParticle.CLOUD, true, (float)body.getLocation().getX() + randomX, (float)body.getLocation().getY() + randomY, (float)body.getLocation().getZ() + randomZ, 0.0F, 0.0F, 0.0F, 0.0F, 4, new int[0]);
      }

      this.sendParticle(body.getLocation(), packets);
   }

   public void pigmanSwordSpiral(Location loc) {
      PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.FLAME, true, (float)loc.getX(), (float)loc.getY(), (float)loc.getZ(), 0.05F, 0.05F, 0.05F, 0.0F, 5, new int[0]);
      this.sendParticle(loc, packet);
   }

   public void pigmanDamage(Location loc) {
      PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.FLAME, true, (float)loc.getX(), (float)loc.getY(), (float)loc.getZ(), 0.3F, 0.3F, 0.3F, 0.0F, 30, new int[0]);
      this.sendParticle(loc, packet);
   }

   public void dragonRage(Location loc) {
      PacketPlayOutWorldParticles pPOWP = new PacketPlayOutWorldParticles(EnumParticle.FLAME, true, (float)loc.getX(), (float)loc.getY(), (float)loc.getZ(), 0.0F, 0.0F, 0.0F, 0.0F, 1, new int[0]);
      this.sendParticle(loc, pPOWP);
   }

   public void doubleJump(Location playerLoc) {
      PacketPlayOutWorldParticles[] packets = new PacketPlayOutWorldParticles[24];
      double degree = 0.0D;

      for(int i = 0; i < 24; ++i) {
         packets[i] = new PacketPlayOutWorldParticles(EnumParticle.SMOKE_NORMAL, true, (float)(playerLoc.getX() + 0.5D * Math.cos(Math.toRadians(degree))), (float)playerLoc.getY() - 1.0F, (float)(playerLoc.getZ() + 0.5D * Math.sin(Math.toRadians(degree))), 0.0F, 0.0F, 0.0F, 1.0F, 0, new int[0]);
         degree += 15.0D;
      }

      this.sendParticle(playerLoc, packets);
   }

   public void sendParticle(Location loc, PacketPlayOutWorldParticles... packets) {
      Collection entityList = loc.getWorld().getNearbyEntities(loc, 64.0D, 64.0D, 64.0D);
      Iterator var4 = ((List)entityList.stream().filter((entity) -> {
         return entity instanceof Player;
      }).map((entity) -> {
         return (Player)entity;
      }).collect(Collectors.toList())).iterator();

      while(var4.hasNext()) {
         Player p = (Player)var4.next();
         Arrays.stream(packets).forEach((packet) -> {
            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
         });
      }

   }

   public static boolean randomNegative() {
      return (int)KStatic.random(2.0F) == 1;
   }

   static {
      colors = new ArrayList(Arrays.asList(Color.AQUA, Color.GREEN, Color.RED, Color.YELLOW, Color.BLACK, Color.WHITE, Color.BLUE, Color.FUCHSIA));
      colorValues = new ArrayList();
   }
}
