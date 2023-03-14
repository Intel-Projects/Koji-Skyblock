package koji.skyblock.player.api;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public class FullSetEvent extends PlayerEvent {
   private final ItemStack[] armor;
   private static final HandlerList handlers = new HandlerList();

   public FullSetEvent(Player who, ItemStack[] armor) {
      super(who);
      this.armor = armor;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public ItemStack[] getArmor() {
      return this.armor;
   }
}
