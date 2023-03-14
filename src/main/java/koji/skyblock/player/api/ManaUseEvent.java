package koji.skyblock.player.api;

import koji.skyblock.item.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class ManaUseEvent extends EntityEvent {
   private double manaCost;
   private final Player player;
   private final CustomItem item;
   private static final HandlerList handlers = new HandlerList();

   public void addManaCost(double d) {
      this.manaCost += d;
   }

   public ManaUseEvent(Player p, CustomItem item, double manaCost) {
      super(p);
      this.player = p;
      this.item = item;
      this.manaCost = manaCost;
   }

   public ManaUseEvent(Player p, double manaCost) {
      super(p);
      this.player = p;
      this.item = new CustomItem(p.getItemInHand());
      this.manaCost = manaCost;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public double getManaCost() {
      return this.manaCost;
   }

   public void setManaCost(double manaCost) {
      this.manaCost = manaCost;
   }

   public Player getPlayer() {
      return this.player;
   }

   public CustomItem getItem() {
      return this.item;
   }
}
