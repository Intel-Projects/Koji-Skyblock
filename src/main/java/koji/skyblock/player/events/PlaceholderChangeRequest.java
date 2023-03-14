package koji.skyblock.player.events;

import koji.skyblock.item.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlaceholderChangeRequest extends PlayerEvent {
   private CustomItem item;
   private static final HandlerList handlers = new HandlerList();

   public PlaceholderChangeRequest(Player player, CustomItem item) {
      super(player);
      this.item = item;
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
}
