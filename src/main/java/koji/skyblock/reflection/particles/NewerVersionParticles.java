package koji.skyblock.reflection.particles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import koji.developerkit.runnable.KRunnable;
import koji.developerkit.utils.KStatic;
import koji.developerkit.utils.xseries.XMaterial;
import koji.developerkit.utils.xseries.particles.ParticleDisplay;
import koji.skyblock.Skyblock;
import koji.skyblock.player.MiscListeners;
import koji.skyblock.reflection.Particles;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class NewerVersionParticles implements Particles {
   private static final List colors;

   public void damage(Location loc) {
      ParticleDisplay.display(loc, Particle.FLAME).spawn();
   }

   public void witherImpact(Player p) {
      for(int i = 0; i < 6; ++i) {
         ParticleDisplay.display(p.getLocation(), Particle.EXPLOSION_LARGE).spawn();
      }

   }

   public void leap(Player p) {
      for(int i = 0; i < 20; ++i) {
         if (i < 3) {
            ParticleDisplay.display(p.getLocation(), Particle.EXPLOSION_LARGE).spawn();
         }

         ParticleDisplay.display(p.getLocation(), Particle.LAVA).spawn();
      }

      double[][] velocities = new double[][]{{0.5D, 0.5D}, {-0.5D, -0.5D}, {0.5D, -0.5D}, {-0.5D, 0.5D}, {0.0D, 0.5D}, {0.0D, -0.5D}, {0.5D, 0.0D}, {-0.5D, 0.0D}};
      double[][] var3 = velocities;
      int var4 = velocities.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         double[] velocity = var3[var5];
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

   }

   public void heatSeekingRose(Location loc) {
      double randomX = randomNegative() ? -Math.random() : Math.random();
      double randomY = Math.random();
      double randomZ = randomNegative() ? -Math.random() : Math.random();
      randomY += 0.25D;
      ParticleDisplay.display(loc.add(randomX, randomY, randomZ), Particle.SNOWBALL).spawn();
   }

   public void radiantOrbRotate(ArmorStand loc, ArmorStand nameTag) {
      Location particle = loc.getLocation().add(loc.getLocation().getDirection().normalize().multiply(0.5D));
      particle.setY(nameTag.getLocation().getY());
      ParticleDisplay.display(particle, Particle.VILLAGER_HAPPY).spawn();
   }

   public void radiantOrbSpawn(ArmorStand nameTag) {
      for(int i = 0; i < 4; ++i) {
         Color color = (Color)KStatic.getRandom(colors);
         ParticleDisplay.display(nameTag.getLocation(), Particle.SPELL_MOB).withColor((float)color.getRed(), (float)color.getGreen(), (float)color.getBlue(), 1.0F).spawn();
      }

   }

   public void radiantOrbPlayer(Player p) {
      for(int i = 0; i < 4; ++i) {
         double randomX = (randomNegative() ? -Math.random() : Math.random()) * 0.3D;
         double randomY = Math.random();
         double randomZ = (randomNegative() ? -Math.random() : Math.random()) * 0.3D;
         Location loc = p.getLocation().add(randomX, 0.1D + randomY, randomZ);
         ParticleDisplay.display(loc, Particle.VILLAGER_HAPPY);
      }

   }

   public void radiantOrbDespawn(ArmorStand body) {
      for(int i = 0; i < 4; ++i) {
         double randomX = (randomNegative() ? -Math.random() : Math.random()) * 0.5D;
         double randomY = Math.random();
         double randomZ = (randomNegative() ? -Math.random() : Math.random()) * 0.5D;
         Location loc = body.getLocation().add(randomX, randomY, randomZ);
         ParticleDisplay.display(loc, Particle.CLOUD);
      }

   }

   public void pigmanSwordSpiral(Location loc) {
      ParticleDisplay.display(loc, Particle.FLAME).spawn();
   }

   public void pigmanDamage(Location loc) {
      for(int i = 0; i < 15; ++i) {
         double randomX = (randomNegative() ? -Math.random() : Math.random()) * 0.5D;
         double randomY = Math.random();
         double randomZ = (randomNegative() ? -Math.random() : Math.random()) * 0.5D;
         ParticleDisplay.display(loc, Particle.FLAME).offset(randomX, randomY, randomZ).spawn();
      }

   }

   public void dragonRage(Location loc) {
      ParticleDisplay.display(loc, Particle.FLAME).spawn();
   }

   public void doubleJump(Location playerLoc) {
      double degree = 0.0D;

      for(int i = 0; i < 15; ++i) {
         ParticleDisplay.display(playerLoc, Particle.SMOKE_NORMAL).offset(Math.cos(Math.toRadians(degree)), -1.0D, Math.sin(Math.toRadians(degree))).withExtra(1.0D).spawn();
         degree += 24.0D;
      }

   }

   public static boolean randomNegative() {
      return (int)KStatic.random(2.0F) == 1;
   }

   static {
      colors = new ArrayList(Arrays.asList(Color.AQUA, Color.GREEN, Color.RED, Color.YELLOW, Color.BLACK, Color.WHITE, Color.BLUE, Color.FUCHSIA));
   }
}
