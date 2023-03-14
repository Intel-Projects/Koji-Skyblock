package koji.skyblock.item.ability;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiPredicate;
import org.bukkit.entity.Player;

public abstract class OverwriteAbility extends Ability {
   protected final Ability original = this.getOriginalAbility();

   public abstract Ability getOriginalAbility();

   public String getIdentifier() {
      return this.original.getIdentifier();
   }

   public String getDisplayName() {
      return this.original.getDisplayName();
   }

   public List getLoreDefault() {
      return this.original.getLoreDefault();
   }

   public HashMap getPlaceholderDefaults() {
      return this.original.getPlaceholderDefaults();
   }

   public BiPredicate getExtraConditions() {
      return this.original.getExtraConditions();
   }

   public double getActualManaCost(Player p) {
      return p == null ? this.original.getManaCost((Player)null) : this.getManaCost(p);
   }

   public double getManaCost(Player p) {
      return this.original.getManaCost(p);
   }
}
