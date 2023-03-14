package koji.skyblock.pets.api;

public enum PetKind {
   COMBAT("Combat"),
   FARMING("Farming"),
   FORAGING("Foraging"),
   MINING("Mining"),
   ALCHEMY("Alchemy"),
   ENCHANTING("Enchanting"),
   FISHING("Fishing");

   String name;

   private PetKind(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }
}
