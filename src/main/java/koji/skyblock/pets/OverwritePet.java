package koji.skyblock.pets;

import java.util.ArrayList;
import java.util.HashMap;
import koji.skyblock.pets.api.PetKind;

public abstract class OverwritePet extends Pet {
   protected final Pet override = this.getPetOverride();

   public abstract Pet getPetOverride();

   public String getName() {
      return this.override.getName();
   }

   public PetKind getPetKind() {
      return this.override.getPetKind();
   }

   public ArrayList rarities() {
      return this.override.rarities();
   }

   public HashMap baseStats() {
      return this.override.baseStats();
   }

   public HashMap addStatPerLevel() {
      return this.override.addStatPerLevel();
   }

   public String getTexture() {
      return this.override.getTexture();
   }
}
