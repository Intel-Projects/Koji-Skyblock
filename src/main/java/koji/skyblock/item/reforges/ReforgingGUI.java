package koji.skyblock.item.reforges;

import koji.developerkit.gui.GUIClickableItem;
import koji.developerkit.listener.KListener;
import koji.developerkit.runnable.KRunnable;
import koji.developerkit.utils.ItemBuilder;
import koji.developerkit.utils.xseries.XMaterial;
import koji.developerkit.utils.xseries.XSound;
import koji.skyblock.Skyblock;
import koji.skyblock.files.Config;
import koji.skyblock.files.data.PlayerData;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.Rarity;
import koji.skyblock.player.PClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class ReforgingGUI extends KListener {
   static final int[] edges = new int[]{0, 8, 9, 17, 18, 26, 27, 35, 36, 44};

   public static Inventory getMainInventory(Player p) {
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, 45, "Reforge Item");
      fill(inv, GUIClickableItem.getBorderItem(0));
      setMultipleSlots(inv, edges, GUIClickableItem.cantPickup((new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE)).setName(" ").build(), 0));
      set(inv, new GUIClickableItem() {
         public void run(InventoryClickEvent e) {
            (new KRunnable((task) -> {
               e.getWhoClicked().closeInventory();
            })).runTaskLater(Skyblock.getPlugin(), 1L);
         }

         public int getSlot() {
            return 40;
         }

         public ItemStack getItem() {
            return (new ItemBuilder(XMaterial.BARRIER)).setName(ChatColor.RED + "Close").build();
         }
      });
      inv.setItem(13, (ItemStack)null);
      set(inv, getMainItem(p, inv));
      return inv;
   }

   public static GUIClickableItem getMainItem(final Player p, final Inventory inv) {
      return new GUIClickableItem() {
         public int getSlot() {
            return 22;
         }

         public ItemStack getItem() {
            if (inv.getItem(13) != null) {
               CustomItem item = new CustomItem(inv.getItem(13));
               double cost = Config.getReforgeCost(item.getRarity());
               return (new ItemBuilder(XMaterial.ANVIL)).setName(ChatColor.GREEN + "Reforge Item").setLore(new String[]{ChatColor.GRAY + "Reforges the above item, giving", ChatColor.GRAY + "it a random item modifier that", ChatColor.GRAY + "boosts its stats.", "", ChatColor.GRAY + "Cost", ChatColor.GOLD + num(PClass.format(cost)) + " Coins", "", ChatColor.YELLOW + "Click to reforge!"}).build();
            } else {
               return (new ItemBuilder(XMaterial.ANVIL)).setName(ChatColor.YELLOW + "Reforge Item").setLore(new String[]{ChatColor.GRAY + "Place an item above to reforge", ChatColor.GRAY + "it! Reforging items adds a", ChatColor.GRAY + "random modifier to the item that", ChatColor.GRAY + "grants stat boosts."}).build();
            }
         }

         public void run(InventoryClickEvent e) {
            if (e.getClickedInventory().getItem(13) != null) {
               if (PClass.getPlayer(p).isOnCooldown("reforge_item")) {
                  p.sendMessage(ChatColor.RED + "Wait a moment before reforging again!");
                  return;
               }

               CustomItem item = new CustomItem(e.getClickedInventory().getItem(13));
               Rarity rarity = item.getRarity();
               Player px = (Player)e.getWhoClicked();
               double cost = Config.getReforgeCost(rarity);
               if (Skyblock.getPlugin().getEconomy().has(px, cost)) {
                  playSound(px, XSound.BLOCK_ANVIL_USE, 1.0F);
                  String originalName = item.getName();
                  ReforgeType reforgeType = ReforgeType.getApplicableType(item);
                  Reforge reforge = Reforge.getRandomReforge(reforgeType);
                  item.applyReforge(reforge);
                  e.getClickedInventory().setItem(13, item.buildWithAbilities());
                  px.updateInventory();
                  Skyblock.getPlugin().getEconomy().withdrawPlayer(px, cost);
                  px.sendMessage(ChatColor.GREEN + "You reforged your " + originalName + ChatColor.GREEN + " into a " + item.getName() + ChatColor.GREEN + "!");
                  PClass.getPlayer(px).setCooldown("reforge_item", 0.5D);
               } else {
                  px.sendMessage(ChatColor.RED + "You don't have enough Coins!");
                  playSound(px, XSound.ENTITY_ENDERMAN_TELEPORT, 0.5F);
               }
            } else {
               playSound(p, XSound.ENTITY_ENDERMAN_TELEPORT, 0.5F);
               p.sendMessage(ChatColor.RED + "Place an item in the empty slot to reforge it!");
            }

         }
      };
   }

   public static void reset(Inventory inv, Player p, boolean can, boolean red) {
      XMaterial xMat = red ? XMaterial.RED_STAINED_GLASS_PANE : XMaterial.GREEN_STAINED_GLASS_PANE;
      setMultipleSlots(inv, edges, GUIClickableItem.cantPickup((new ItemBuilder(xMat)).setName(" ").build(), 0));
      if (can) {
         set(inv, getMainItem(p, inv));
      } else {
         set(inv, GUIClickableItem.cantPickup((new ItemBuilder(XMaterial.BARRIER)).setName(ChatColor.RED + "Error!").setLore(new String[]{ChatColor.GRAY + "You cannot reforge this item!"}).build(), 22));
      }

   }

   @EventHandler
   public void onMenuClose(InventoryCloseEvent e) {
      if (e.getView().getTitle().equals("Reforge Item") && e.getView().getTopInventory().getItem(13) != null) {
         Player p = (Player)e.getPlayer();
         if (!addItemUnlessFull(p.getInventory(), e.getView().getTopInventory().getItem(13))) {
            PlayerData.getPlayerData().addToItemStash(p, e.getView().getTopInventory().getItem(13));
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onInventoryClickEvent(InventoryClickEvent e) {
      Inventory inv = e.getView().getTopInventory();
      placeItem(e.getView().getTitle(), inv, (Player)e.getWhoClicked());
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onItemDragEvent(InventoryDragEvent e) {
      if (e.getRawSlots().stream().findAny().isPresent()) {
         Inventory inv = e.getView().getTopInventory();
         placeItem(e.getView().getTitle(), inv, (Player)e.getWhoClicked());
      }

   }

   public static void placeItem(String viewTitle, Inventory inv, Player p) {
      if (viewTitle.equals("Reforge Item") && inv != null && inv.getType() != InventoryType.PLAYER && inv.getSize() > 44) {
         (new KRunnable((task) -> {
            ItemStack is = inv.getItem(13);
            if (is != null) {
               boolean boo = ReforgeType.hasApplicableType(new CustomItem(is));
               reset(inv, p, boo, !boo);
            } else {
               reset(inv, p, true, true);
            }

            p.updateInventory();
         })).runTaskLater(Skyblock.getPlugin(), 1L);
      }

   }
}
