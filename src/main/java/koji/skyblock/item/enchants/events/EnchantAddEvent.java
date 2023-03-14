package koji.skyblock.item.enchants.events;

import koji.skyblock.item.CustomItem;
import koji.skyblock.item.enchants.Enchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class EnchantAddEvent extends PlayerEvent implements Cancellable {
   boolean cancelled = false;
   private final Enchant enchant;
   private CustomItem item;
   private static final HandlerList handlers = new HandlerList();

   public EnchantAddEvent(Player p, Enchant enchant, CustomItem item) {
      super(p);
      this.enchant = enchant;
      this.item = item;
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

   public Enchant getEnchant() {
      return this.enchant;
   }

   public CustomItem getItem() {
      return this.item;
   }

   public void setItem(CustomItem item) {
      this.item = item;
   }
}
