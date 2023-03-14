package koji.skyblock.player.api;

import koji.skyblock.item.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class RightClickAbilityCastEvent extends PlayerEvent {
   private final CustomItem item;
   private final PlayerInteractEvent originalEvent;
   private static final HandlerList handlers = new HandlerList();

   public RightClickAbilityCastEvent(Player p, CustomItem item, PlayerInteractEvent originalEvent) {
      super(p);
      this.item = item;
      this.originalEvent = originalEvent;
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

   public PlayerInteractEvent getOriginalEvent() {
      return this.originalEvent;
   }
}
