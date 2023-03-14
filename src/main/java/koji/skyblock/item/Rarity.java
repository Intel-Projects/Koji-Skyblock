package koji.skyblock.item;

import java.util.ArrayList;
import java.util.Arrays;
import koji.developerkit.utils.xseries.XMaterial;
import org.bukkit.ChatColor;

public enum Rarity {
   VERY_SPECIAL("VERY SPECIAL", "§c", XMaterial.RED_TERRACOTTA, (Rarity)null),
   SPECIAL("SPECIAL", "§c", XMaterial.RED_TERRACOTTA, VERY_SPECIAL),
   DIVINE("DIVINE", "§b", XMaterial.LIGHT_BLUE_TERRACOTTA, SPECIAL),
   MYTHIC("MYTHIC", "§d", XMaterial.PINK_TERRACOTTA, DIVINE),
   LEGENDARY("LEGENDARY", "§6", XMaterial.YELLOW_TERRACOTTA, MYTHIC),
   EPIC("EPIC", "§5", XMaterial.PURPLE_TERRACOTTA, LEGENDARY),
   RARE("RARE", "§9", XMaterial.LIGHT_BLUE_TERRACOTTA, EPIC),
   UNCOMMON("UNCOMMON", "§a", XMaterial.GREEN_TERRACOTTA, RARE),
   COMMON("COMMON", "§f", XMaterial.GRAY_TERRACOTTA, UNCOMMON);

   private final String name;
   private final String prefix;
   private final XMaterial b;
   private final Rarity next;
   private static final ArrayList ordered = new ArrayList(Arrays.asList(COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, MYTHIC, DIVINE, SPECIAL, VERY_SPECIAL));

   public Rarity upgrade() {
      return (Rarity)ordered.get(Math.min(this.ordinal() + 1, values().length - 1));
   }

   public Rarity downgrade() {
      return this.ordinal() - 1 < 0 ? this : (Rarity)ordered.get(this.ordinal() - 1);
   }

   private Rarity(String name, String prefix, XMaterial b, Rarity next) {
      this.name = name;
      this.prefix = prefix;
      this.b = b;
      this.next = next;
   }

   public boolean isAtLeast(Rarity rarity) {
      return this.ordinal() >= rarity.ordinal();
   }

   public String getDisplay() {
      return this.prefix + ChatColor.BOLD + this.getName();
   }

   public String getBoldedColor() {
      return this.prefix + ChatColor.BOLD;
   }

   public static Rarity getRarity(String string) {
      try {
         return valueOf(string.toUpperCase());
      } catch (IllegalArgumentException var2) {
         return null;
      }
   }

   public String getColor() {
      return this.prefix;
   }

   public String getName() {
      return this.name;
   }

   public XMaterial getBlock() {
      return this.b;
   }

   public Rarity getNextRarity() {
      return this.next;
   }

   public static ArrayList getOrdered() {
      return ordered;
   }
}
