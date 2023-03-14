package koji.skyblock.item.reforges;

import java.util.ArrayList;
import java.util.List;
import koji.developerkit.KBase;
import koji.skyblock.item.Rarity;
import koji.skyblock.utils.StatMap;

public abstract class Reforge extends KBase {
   private static final List reforges = new ArrayList();
   private final String name;
   private final ReforgeType reforgeType;

   public Reforge(String name, ReforgeType reforgeType) {
      this.name = name;
      this.reforgeType = reforgeType;
   }

   public static void registerReforge(Reforge reforge) {
      reforges.add(reforge);
   }

   public abstract StatMap getStats(Rarity var1);

   public static Reforge getRandomReforge(ReforgeType type) {
      List reforgesWithType = new ArrayList(reforges);
      reforgesWithType.removeIf((reforge) -> {
         return reforge.getReforgeType() != type;
      });
      return (Reforge)getRandom(reforgesWithType);
   }

   public static Reforge parseFromName(String name) {
      List reforge = new ArrayList(reforges);
      reforge.removeIf((reforge1) -> {
         return !reforge1.getName().equals(name);
      });
      return reforge.isEmpty() ? null : (Reforge)reforge.get(0);
   }

   public String getAddedString(String name) {
      return name.startsWith(this.getName()) ? "Very" : this.getName();
   }

   public static List getReforges() {
      return reforges;
   }

   public String getName() {
      return this.name;
   }

   public ReforgeType getReforgeType() {
      return this.reforgeType;
   }
}
