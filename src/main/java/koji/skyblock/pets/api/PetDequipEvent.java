package koji.skyblock.pets.api;

import koji.skyblock.item.Rarity;
import koji.skyblock.pets.Pet;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class PetDequipEvent extends PetEvent implements Cancellable {
   boolean cancelled = false;
   Rarity rarity;
   int level;

   public PetDequipEvent(Player p, Pet pet, Rarity rarity, int level) {
      super(p, pet);
      this.rarity = rarity;
      this.level = level;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }

   public Rarity getRarity() {
      return this.rarity;
   }

   public int getLevel() {
      return this.level;
   }
}
