package koji.skyblock.utils;

import java.util.HashMap;
import java.util.Iterator;
import koji.developerkit.utils.duplet.Duplet;
import koji.skyblock.player.Stats;

public class StatMap extends HashMap {
   public Double get(Object stat) {
      return (Double)super.getOrDefault(stat, 0.0D);
   }

   public void put(Stats stat, Integer i) {
      super.put(stat, (double)i);
   }

   @SafeVarargs
   public StatMap(Duplet... stats) {
      Duplet[] var2 = stats;
      int var3 = stats.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Duplet stat = var2[var4];
         this.put(stat.getFirst(), stat.getSecond());
      }

   }

   public StatMap invert() {
      Iterator var1 = this.keySet().iterator();

      while(var1.hasNext()) {
         Stats stat = (Stats)var1.next();
         this.put(stat, -this.get(stat));
      }

      return this;
   }

   public StatMap combine(StatMap other) {
      Iterator var2 = other.keySet().iterator();

      while(var2.hasNext()) {
         Stats stats = (Stats)var2.next();
         this.put(stats, this.get(stats) + other.get(stats));
      }

      return this;
   }
}
