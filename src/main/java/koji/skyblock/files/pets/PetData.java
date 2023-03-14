package koji.skyblock.files.pets;

import java.util.List;
import koji.developerkit.KBase;
import koji.skyblock.item.Rarity;
import org.bukkit.entity.Player;

public abstract class PetData extends KBase {
   private static PetData petData;

   public abstract String getType(Player var1, String var2);

   public abstract void setType(Player var1, String var2, String var3);

   public abstract int getLevel(Player var1, String var2);

   public abstract void setLevel(Player var1, String var2, int var3);

   public abstract double getCurrentExp(Player var1, String var2);

   public abstract void setCurrentExp(Player var1, String var2, double var3);

   public abstract String getSkin(Player var1, String var2);

   public abstract void setSkin(Player var1, String var2, String var3);

   public abstract Rarity getRarity(Player var1, String var2);

   public abstract void setRarity(Player var1, String var2, Rarity var3);

   public abstract boolean getPetExists(Player var1, String var2);

   public abstract void createPet(Player var1, String var2, String var3, int var4, double var5, String var7, Rarity var8);

   public abstract boolean getPlayerExists(Player var1);

   public abstract void createPlayer(Player var1);

   public abstract void addPet(Player var1, String var2);

   public abstract List getPets(Player var1);

   public abstract void erasePetData(Player var1, String var2);

   public static PetData getPetData() {
      return petData;
   }

   public static void setPetData(PetData petData) {
      PetData.petData = petData;
   }
}
