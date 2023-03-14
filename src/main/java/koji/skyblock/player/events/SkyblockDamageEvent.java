package koji.skyblock.player.events;

import koji.developerkit.utils.duplet.Duplet;
import koji.skyblock.item.CustomItem;
import koji.skyblock.utils.StatMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

public class SkyblockDamageEvent extends EntityDamageByEntityEvent {
   private CustomItem item;
   private final boolean playerReceivingDamage;
   private double additiveMultiplier;
   private boolean trueDamage;
   private double damageReduction;
   private Entity arrow;
   private final StatMap statModifiers;
   private double finalDamageMultiplier;
   private double multiplicativeMultiplier;
   private final SkyblockDamageEvent.Type type;
   private static final HandlerList handlers = new HandlerList();

   public void addToAdditiveMultiplier(double d) {
      this.additiveMultiplier += d;
   }

   public SkyblockDamageEvent(Entity damager, Entity damagee, boolean boo, SkyblockDamageEvent.Type type, double damage, boolean doesTrueDamage) {
      super(damager, damagee, DamageCause.CUSTOM, damage);
      this.additiveMultiplier = 0.0D;
      this.damageReduction = 0.0D;
      this.arrow = null;
      this.statModifiers = new StatMap(new Duplet[0]);
      this.finalDamageMultiplier = 1.0D;
      this.multiplicativeMultiplier = 0.0D;
      this.playerReceivingDamage = boo;
      this.type = type;
      this.trueDamage = doesTrueDamage;
   }

   public SkyblockDamageEvent(Entity damagee, boolean boo, SkyblockDamageEvent.Type type, double damage, boolean doesTrueDamage) {
      this((Entity)null, damagee, boo, type, damage, doesTrueDamage);
   }

   public SkyblockDamageEvent(Entity damager, Entity damagee, boolean playerReceivingDamage, SkyblockDamageEvent.Type type, double damage, CustomItem item) {
      this(damager, damagee, playerReceivingDamage, type, damage, false);
      this.item = item;
   }

   public SkyblockDamageEvent(Entity damager, Entity arrow, Entity damagee, boolean playerReceivingDamage, SkyblockDamageEvent.Type type, double damage, CustomItem item) {
      this(damager, damagee, playerReceivingDamage, type, damage, false);
      this.arrow = arrow;
      this.item = item;
   }

   public SkyblockDamageEvent(Entity damagee, boolean playerReceivingDamage, SkyblockDamageEvent.Type type, double damage) {
      this((Entity)null, damagee, playerReceivingDamage, type, damage, (CustomItem)null);
   }

   public ItemStack parseItem(Player p) {
      return this.getItem() == null ? p.getItemInHand() : this.getItem().buildWithAbilities();
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public CustomItem getItem() {
      return this.item;
   }

   public boolean isPlayerReceivingDamage() {
      return this.playerReceivingDamage;
   }

   public double getAdditiveMultiplier() {
      return this.additiveMultiplier;
   }

   public void setAdditiveMultiplier(double additiveMultiplier) {
      this.additiveMultiplier = additiveMultiplier;
   }

   public boolean isTrueDamage() {
      return this.trueDamage;
   }

   public void setTrueDamage(boolean trueDamage) {
      this.trueDamage = trueDamage;
   }

   public double getDamageReduction() {
      return this.damageReduction;
   }

   public void setDamageReduction(double damageReduction) {
      this.damageReduction = damageReduction;
   }

   public Entity getArrow() {
      return this.arrow;
   }

   public StatMap getStatModifiers() {
      return this.statModifiers;
   }

   public double getFinalDamageMultiplier() {
      return this.finalDamageMultiplier;
   }

   public void setFinalDamageMultiplier(double finalDamageMultiplier) {
      this.finalDamageMultiplier = finalDamageMultiplier;
   }

   public double getMultiplicativeMultiplier() {
      return this.multiplicativeMultiplier;
   }

   public void setMultiplicativeMultiplier(double multiplicativeMultiplier) {
      this.multiplicativeMultiplier = multiplicativeMultiplier;
   }

   public SkyblockDamageEvent.Type getType() {
      return this.type;
   }

   public static enum Type {
      BOW,
      SWORD,
      FIRE,
      VOID,
      FALL,
      DROWNING,
      MAGIC,
      FEROCITY,
      UNSPECIFIED;
   }
}
