package koji.skyblock.reflection;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public interface Particles {
   void damage(Location var1);

   void witherImpact(Player var1);

   void leap(Player var1);

   void heatSeekingRose(Location var1);

   void radiantOrbRotate(ArmorStand var1, ArmorStand var2);

   void radiantOrbSpawn(ArmorStand var1);

   void radiantOrbPlayer(Player var1);

   void radiantOrbDespawn(ArmorStand var1);

   void pigmanSwordSpiral(Location var1);

   void pigmanDamage(Location var1);

   void dragonRage(Location var1);

   void doubleJump(Location var1);
}
