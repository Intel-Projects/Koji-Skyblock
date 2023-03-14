package koji.skyblock.api.placeholderapi;

import koji.skyblock.player.PClass;
import koji.skyblock.player.Stats;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderRegister extends PlaceholderExpansion {
   public boolean canRegister() {
      return true;
   }

   @NotNull
   public String getAuthor() {
      return "GK";
   }

   @NotNull
   public String getIdentifier() {
      return "kojiskyblock";
   }

   @NotNull
   public String getVersion() {
      return "1.0.0";
   }

   public String onPlaceholderRequest(Player p, @NotNull String identifier) {
      PClass pS = PClass.getPlayer(p);
      Stats[] var4 = Stats.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Stats sbS = var4[var6];
         if (sbS.name().toLowerCase().equalsIgnoreCase(identifier)) {
            return String.valueOf((int)Math.ceil(pS.getStat(sbS)));
         }
      }

      if (identifier.equalsIgnoreCase("current_pet")) {
         return pS.getPetInstance().getPet().getNameSpace();
      } else {
         return null;
      }
   }
}
