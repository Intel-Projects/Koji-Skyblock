package koji.skyblock.item.creation;

import koji.skyblock.item.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class GrabCustomItemEvent extends PlayerEvent implements Cancellable {
   CustomItem item;
   boolean cancelled = false;
   private static final HandlerList handlers = new HandlerList();

   public GrabCustomItemEvent(Player p, CustomItem ci) {
      super(p);
      this.item = ci;
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

   public void setItem(CustomItem item) {
      this.item = item;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }
}
