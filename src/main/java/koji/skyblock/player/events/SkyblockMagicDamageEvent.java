package koji.skyblock.player.events;

import java.util.function.Consumer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class SkyblockMagicDamageEvent extends PlayerEvent implements Cancellable {
   private boolean cancelled;
   private final Location location;
   private double baseAbilityDamage;
   private double abilityScaling;
   private double nearbyX;
   private double nearbyY;
   private double nearbyZ;
   private String name;
   private Consumer task;
   private final boolean sendMessage;
   private static final HandlerList handlers = new HandlerList();

   public SkyblockMagicDamageEvent(Player p, Location location, double baseAbilityDamage, double abilityScaling, double nearbyX, double nearbyY, double nearbyZ, String name, boolean sendMessage, Consumer task) {
      super(p);
      this.location = location;
      this.baseAbilityDamage = baseAbilityDamage;
      this.abilityScaling = abilityScaling;
      this.nearbyX = nearbyX;
      this.nearbyY = nearbyY;
      this.nearbyZ = nearbyZ;
      this.name = name;
      this.sendMessage = sendMessage;
      this.task = task;
   }

   public SkyblockMagicDamageEvent(Player p, Location location, double baseAbilityDamage, double abilityScaling, double nearbyX, double nearbyY, double nearbyZ, String name, boolean sendMessage) {
      this(p, location, baseAbilityDamage, abilityScaling, nearbyX, nearbyY, nearbyZ, name, sendMessage, (Consumer)null);
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }

   public Location getLocation() {
      return this.location;
   }

   public double getBaseAbilityDamage() {
      return this.baseAbilityDamage;
   }

   public void setBaseAbilityDamage(double baseAbilityDamage) {
      this.baseAbilityDamage = baseAbilityDamage;
   }

   public double getAbilityScaling() {
      return this.abilityScaling;
   }

   public void setAbilityScaling(double abilityScaling) {
      this.abilityScaling = abilityScaling;
   }

   public double getNearbyX() {
      return this.nearbyX;
   }

   public void setNearbyX(double nearbyX) {
      this.nearbyX = nearbyX;
   }

   public double getNearbyY() {
      return this.nearbyY;
   }

   public void setNearbyY(double nearbyY) {
      this.nearbyY = nearbyY;
   }

   public double getNearbyZ() {
      return this.nearbyZ;
   }

   public void setNearbyZ(double nearbyZ) {
      this.nearbyZ = nearbyZ;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Consumer getTask() {
      return this.task;
   }

   public void setTask(Consumer task) {
      this.task = task;
   }

   public boolean isSendMessage() {
      return this.sendMessage;
   }
}
