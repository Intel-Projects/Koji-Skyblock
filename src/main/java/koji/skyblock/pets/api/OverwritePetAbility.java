package koji.skyblock.pets.api;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class OverwritePetAbility extends PetAbility {
   private final PetAbility override = this.getOverride();

   public abstract PetAbility getOverride();

   public String getName() {
      return this.override.getName();
   }

   public String getDisplayName() {
      return this.override.getDisplayName();
   }

   public ArrayList validRarities() {
      return this.override.validRarities();
   }

   public HashMap getPlaceHolderSlotsBaseValue() {
      return this.override.getPlaceHolderSlotsBaseValue();
   }

   public ArrayList getLore() {
      return this.override.getLore();
   }
}
