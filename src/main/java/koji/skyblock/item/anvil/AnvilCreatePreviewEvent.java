package koji.skyblock.item.anvil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import koji.developerkit.utils.KStatic;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.enchants.Enchant;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class AnvilCreatePreviewEvent extends PlayerEvent {
   private CustomItem item;
   private final CustomItem leftItem;
   private final CustomItem rightItem;
   private int cost;
   private final List conflictEnchants;
   private static final HandlerList handlers = new HandlerList();

   public AnvilCreatePreviewEvent(Player p, CustomItem item, CustomItem leftItem, CustomItem rightItem, int cost, List conflictEnchants) {
      super(p);
      this.item = item;
      this.leftItem = leftItem;
      this.rightItem = rightItem;
      this.cost = cost;
      this.conflictEnchants = conflictEnchants;
   }

   public AnvilCreatePreviewEvent(Player p, CustomItem item, CustomItem leftItem, CustomItem rightItem, int cost) {
      this(p, item, leftItem, rightItem, cost, new ArrayList());
   }

   public CustomItem getFinishedItem() {
      this.item.setName(this.item.getName());
      List lore = new ArrayList(this.item.getLore());
      lore.addAll(KStatic.arrayList(new String[]{ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "------------------", ChatColor.GREEN + "This is the item you will get.", ChatColor.GREEN + "Click the " + ChatColor.RED + "ANVIL BELOW " + ChatColor.GREEN + "to", ChatColor.GREEN + "combine."}));
      if (!this.conflictEnchants.isEmpty()) {
         lore.addAll(KStatic.arrayList(new String[]{"", ChatColor.RED + "" + ChatColor.BOLD + "WARNING: This will remove"}));
         lore.addAll(this.format(this.conflictEnchants));
      }

      return this.item.setLore((List)lore);
   }

   public List format(List enchants) {
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < enchants.size(); ++i) {
         if (i > 0 && i != enchants.size() - 1) {
            sb.append(", ");
         }

         if (enchants.size() != 1 && i == enchants.size() - 1) {
            sb.append(" and ");
         }

         sb.append(((Enchant)enchants.get(i)).getDisplayName());
      }

      return (List)KStatic.wrapLine(sb.toString(), 30).stream().map((text) -> {
         return ChatColor.RED + "" + ChatColor.BOLD + text;
      }).collect(Collectors.toList());
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public CustomItem getItem() {
      return this.item;
   }

   public void setItem(CustomItem item) {
      this.item = item;
   }

   public CustomItem getLeftItem() {
      return this.leftItem;
   }

   public CustomItem getRightItem() {
      return this.rightItem;
   }

   public int getCost() {
      return this.cost;
   }

   public void setCost(int cost) {
      this.cost = cost;
   }

   public List getConflictEnchants() {
      return this.conflictEnchants;
   }
}
