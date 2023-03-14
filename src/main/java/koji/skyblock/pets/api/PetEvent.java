package koji.skyblock.pets.api;

import koji.skyblock.pets.Pet;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class PetEvent extends EntityEvent {
   Pet pet;
   Player player;
   private static final HandlerList handlers = new HandlerList();

   public PetEvent(Player p, Pet pet) {
      super(p);
      this.player = p;
      this.pet = pet;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public Pet getPet() {
      return this.pet;
   }

   public Player getPlayer() {
      return this.player;
   }
}
