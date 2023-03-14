package koji.skyblock.item.anvil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import koji.developerkit.gui.GUIClickableItem;
import koji.developerkit.inventory.KInventory;
import koji.developerkit.inventory.KInventory.PlayerInstance;
import koji.developerkit.listener.KListener;
import koji.developerkit.runnable.KRunnable;
import koji.developerkit.utils.ItemBuilder;
import koji.developerkit.utils.duplet.Duplet;
import koji.developerkit.utils.duplet.Tuple;
import koji.developerkit.utils.xseries.XMaterial;
import koji.developerkit.utils.xseries.XSound;
import koji.skyblock.Skyblock;
import koji.skyblock.files.data.PlayerData;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.Rarity;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AnvilGUI extends KListener {
   private static final HashMap anvilHashMap = new HashMap();
   private static final int[] upgradePanes = new int[]{11, 12, 20};
   private static final int[] sacrificePanes = new int[]{14, 15, 24};
   private static final int[] bottom = new int[]{45, 46, 47, 48, 50, 51, 52, 53};
   private static final int[] empty = new int[]{29, 33};

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onMenuOpen(PlayerInteractEvent e) {
      if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType().toString().equals("ANVIL")) {
         e.setCancelled(true);
         KRunnable t = new KRunnable((task) -> {
            playSound(e.getPlayer(), XSound.UI_BUTTON_CLICK, 1.0F);
            e.getPlayer().openInventory(openInventory(e.getPlayer()));
         });
         if (XMaterial.supports(14)) {
            t.runTaskLater(Skyblock.getPlugin(), 1L);
         } else {
            t.run();
         }
      }

   }

   @EventHandler
   public void onMenuOpen(InventoryOpenEvent e) {
      Inventory inv = e.getPlayer().getInventory();

      for(int i = 0; i < inv.getSize(); ++i) {
         if (inv.getItem(i) != null && inv.getItem(i).getType() != XMaterial.AIR.parseMaterial()) {
            inv.setItem(i, this.updateBook(inv.getItem(i)));
         }
      }

   }

   public ItemStack updateBook(ItemStack item) {
      return item.getType() == XMaterial.ENCHANTED_BOOK.parseMaterial() ? (new EnchantedBook(item)).build() : item;
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void onInvInteract(InventoryClickEvent e) {
      Player p = (Player)e.getWhoClicked();
      if (anvilHashMap.containsKey(p) && !e.isCancelled()) {
         AnvilGUI.Anvil anvil = (AnvilGUI.Anvil)anvilHashMap.get(p);
         (new KRunnable((task) -> {
            if (anvil.isWaitingForPickup() && e.getInventory().equals(e.getView().getTopInventory()) && e.getSlot() == 13) {
               anvil.setWaitingForPickup(false);
               anvil.reset("Anvil");
               p.setItemOnCursor((new CustomItem(p.getItemOnCursor())).removeKey("ClickItem").buildWithAbilities());
            }

            getMainInventory(p, anvil);
         })).runTaskLater(Skyblock.getPlugin(), 1L);
      }

   }

   @EventHandler
   public void onDrag(InventoryDragEvent e) {
      Player p = (Player)e.getWhoClicked();
      if (e.getRawSlots().stream().findAny().isPresent() && anvilHashMap.containsKey(p)) {
         (new KRunnable((task) -> {
            getMainInventory(p, (AnvilGUI.Anvil)anvilHashMap.get(p));
         })).runTaskLater(Skyblock.getPlugin(), 1L);
      }

   }

   @EventHandler
   public void onMenuClose(InventoryCloseEvent e) {
      Player p = (Player)e.getPlayer();
      if (anvilHashMap.containsKey(p)) {
         Inventory inv = e.getView().getTopInventory();
         ItemStack[] items = new ItemStack[]{inv.getItem(13), inv.getItem(29), inv.getItem(33)};
         boolean[] extraConditions = new boolean[]{((AnvilGUI.Anvil)anvilHashMap.get(p)).isWaitingForPickup(), true, true};

         for(int i = 0; i < 3; ++i) {
            ItemStack item = items[i];
            if (isValidItem(item)) {
               ItemStack customItem = (new CustomItem(item)).removeKey("ClickItem").buildWithAbilities();
               if (item.getType() != XMaterial.BARRIER.parseMaterial() && extraConditions[i] && !addItemUnlessFull(p.getInventory(), customItem)) {
                  PlayerData.getPlayerData().addToItemStash(p, customItem);
               }
            }
         }

         anvilHashMap.remove(p);
      }

   }

   public static Inventory openInventory(Player p) {
      anvilHashMap.put(p, getMainInventory(p, (AnvilGUI.Anvil)null));
      return ((AnvilGUI.Anvil)anvilHashMap.get(p)).getInventory();
   }

   public static AnvilGUI.Anvil getMainInventory(Player p, AnvilGUI.Anvil anvil) {
      if (anvil == null) {
         anvil = newAnvil(p);
      }

      ItemStack left = anvil.getInventory().getItem(29);
      ItemStack right = anvil.getInventory().getItem(33);
      if (anvil.isWaitingForPickup() && left == null && right == null) {
         return anvil;
      } else {
         if (anvil.isWaitingForPickup()) {
            anvil.setWaitingForPickup(false);
            if (!addItemUnlessFull(p.getInventory(), anvil.getInventory().getItem(13))) {
               PlayerData.getPlayerData().addToItemStash(p, anvil.getInventory().getItem(13));
            }
         }

         boolean enchantedBookCombination = left != null && left.getType() == XMaterial.ENCHANTED_BOOK.parseMaterial() && right != null && right.getType() == XMaterial.ENCHANTED_BOOK.parseMaterial();
         boolean enchantItemCombination = true;
         boolean error = false;
         AnvilCreatePreviewEvent aCPE = null;
         EnchantedBook book;
         if (enchantedBookCombination) {
            EnchantedBook leftBook = new EnchantedBook(left.clone());
            book = new EnchantedBook(right.clone());
            Duplet tuple = combineEnchantedBooks(p, leftBook, book);
            if (!((EnchantedBook)tuple.getFirst()).getEnchants().equals(leftBook.getEnchants())) {
               aCPE = new AnvilCreatePreviewEvent(p, (CustomItem)tuple.getFirst(), leftBook, book, 0, (List)tuple.getSecond());
            } else {
               error = true;
            }
         } else if (left != null && right != null) {
            CustomItem leftCustomItem = new CustomItem(left.clone());
            HashMap enchants;
            CustomItem finalItem;
            List conflicts;
            AtomicBoolean atLeastOne;
            if (right.getType() == XMaterial.ENCHANTED_BOOK.parseMaterial()) {
               book = new EnchantedBook(right);
               atLeastOne = new AtomicBoolean(false);
               book.getEnchants().forEach((a, b) -> {
                  a.getTargets().forEach((c) -> {
                     if (c.includes(leftCustomItem)) {
                        atLeastOne.set(true);
                     }

                  });
               });
               enchants = leftCustomItem.getEnchants();
               finalItem = leftCustomItem.addEnchants(p, book.getEnchants());
               if (atLeastOne.get() && !enchants.equals(finalItem.getEnchants())) {
                  conflicts = book.getConflictingEnchants(leftCustomItem.getEnchants());
                  aCPE = new AnvilCreatePreviewEvent(p, finalItem, leftCustomItem, book, book.getApplyCost(), conflicts);
               } else {
                  error = true;
               }
            } else if ((leftCustomItem.getDefaultPotatoStats().isEmpty() || !(new CustomItem(right)).hasAbility("hotPotatoBook") || leftCustomItem.getPotatoBookAmount() >= 10) && (leftCustomItem.getDefaultPotatoStats().isEmpty() || !(new CustomItem(right)).hasAbility("fumingPotatoBook") || leftCustomItem.getPotatoBookAmount() >= 15)) {
               if (!leftCustomItem.getID().equals("null") && (new CustomItem(right)).getID().equals(leftCustomItem.getID())) {
                  CustomItem rightCustomItem = new CustomItem(right.clone());
                  atLeastOne = new AtomicBoolean(false);
                  rightCustomItem.getEnchants().forEach((a, b) -> {
                     a.getTargets().forEach((c) -> {
                        if (c.includes(leftCustomItem)) {
                           atLeastOne.set(true);
                        }

                     });
                  });
                  enchants = leftCustomItem.getEnchants();
                  finalItem = leftCustomItem.addEnchants(p, rightCustomItem.getEnchants());
                  if (atLeastOne.get() && !enchants.equals(finalItem.getEnchants())) {
                     conflicts = rightCustomItem.getConflictingEnchants(leftCustomItem.getEnchants());
                     aCPE = new AnvilCreatePreviewEvent(p, finalItem, leftCustomItem, rightCustomItem, (int)((double)rightCustomItem.getApplyCost() * 1.125D), conflicts);
                  } else {
                     error = true;
                  }
               } else {
                  error = true;
                  enchantItemCombination = false;
               }
            } else {
               aCPE = new AnvilCreatePreviewEvent(p, leftCustomItem.applyHotPotatoBook(), leftCustomItem, new CustomItem(right), 0);
            }
         }

         if (aCPE != null) {
            anvil.set(getFinalResult(anvil, aCPE.getFinishedItem().build()));
         } else {
            anvil.set(getMainBarrierBlock(error, enchantItemCombination));
         }

         setMultipleSlots(anvil.getInventory(), upgradePanes, getPane(aCPE != null || left != null && !error, false));
         setMultipleSlots(anvil.getInventory(), sacrificePanes, getPane(aCPE != null || right != null && !error, true));
         setMultipleSlots(anvil.getInventory(), bottom, getBottomRow(aCPE != null));
         anvil.set(getAnvilItem(aCPE != null, aCPE != null ? aCPE.getCost() : 0));
         p.updateInventory();
         return anvil;
      }
   }

   public static GUIClickableItem getBottomRow(boolean success) {
      return GUIClickableItem.cantPickup((new ItemBuilder(success ? XMaterial.GREEN_STAINED_GLASS_PANE : XMaterial.RED_STAINED_GLASS_PANE)).setName(" ").build(), 0);
   }

   public static GUIClickableItem getFinalResult(final AnvilGUI.Anvil anvil, final ItemStack finalItem) {
      return new GUIClickableItem() {
         public void run(InventoryClickEvent e) {
            if (!e.getAction().toString().startsWith("DROP") && e.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY && !e.getAction().toString().startsWith("HOTBAR") && e.getAction() != InventoryAction.SWAP_WITH_CURSOR) {
               if (!anvil.isWaitingForPickup()) {
                  playSound(anvil.getPlayer(), XSound.ENTITY_ENDERMAN_TELEPORT, 0.5F);
                  anvil.getPlayer().sendMessage(ChatColor.RED + "You must click the Anvil below to combine the two items!");
               }
            } else {
               e.setCancelled(true);
            }

         }

         public int getSlot() {
            return 13;
         }

         public ItemStack getItem() {
            return finalItem;
         }

         public boolean canPickup() {
            return anvil.isWaitingForPickup();
         }
      };
   }

   public static Duplet combineEnchantedBooks(Player p, EnchantedBook left, EnchantedBook right) {
      EnchantedBook ci = new EnchantedBook(XMaterial.ENCHANTED_BOOK);
      Rarity rarity = Rarity.COMMON;
      switch(Math.max(left.getHighestLevelEnchant(), right.getHighestLevelEnchant())) {
      case 1:
      case 2:
      case 3:
      case 4:
         break;
      case 5:
         rarity = Rarity.UNCOMMON;
         break;
      case 6:
         rarity = Rarity.RARE;
         break;
      case 7:
         rarity = Rarity.EPIC;
         break;
      case 8:
         rarity = Rarity.LEGENDARY;
         break;
      default:
         rarity = Rarity.MYTHIC;
      }

      ci.setRarity(rarity);
      left.getEnchants().forEach((a, b) -> {
         ci.addEnchant(p, a, b);
      });
      right.getEnchants().forEach((a, b) -> {
         ci.addEnchant(p, a, b);
      });
      return Tuple.of(ci, right.getConflictingEnchants(left.getEnchants().keySet()));
   }

   public static AnvilGUI.Anvil newAnvil(Player p) {
      return new AnvilGUI.Anvil(p, AnvilGUI.Anvil.getBase());
   }

   public static GUIClickableItem getPane(boolean valid, boolean sacrifice) {
      return GUIClickableItem.cantPickup((new ItemBuilder(valid ? XMaterial.GREEN_STAINED_GLASS_PANE : XMaterial.RED_STAINED_GLASS_PANE)).setName(ChatColor.GOLD + "Item To " + (sacrifice ? "Sacrifice" : "Upgrade")).setLore(getPaneLore(sacrifice)).build(), 0);
   }

   private static List getPaneLore(boolean sacrifice) {
      return sacrifice ? arrayList(new String[]{ChatColor.GRAY + "The item you are sacrificing in", ChatColor.GRAY + "in order to upgrade the item on the", ChatColor.GRAY + "left should be placed in the", ChatColor.GRAY + "slot on this side."}) : arrayList(new String[]{ChatColor.GRAY + "The item you are upgrading", ChatColor.GRAY + "should be placed in the slot on", ChatColor.GRAY + "this side."});
   }

   public static GUIClickableItem getAnvilItem(boolean finalProduct, final int cost) {
      return finalProduct ? new GUIClickableItem() {
         public void run(InventoryClickEvent e) {
            Player p = (Player)e.getWhoClicked();
            Inventory inv = e.getInventory();
            if (inv.getItem(13).getType() != XMaterial.BARREL.parseMaterial()) {
               e.setCancelled(true);
               this.combine(p, inv);
               CustomItem item = new CustomItem(inv.getItem(13));
               if (item.getPotatoBookAmount() == 10) {
                  p.sendMessage(ChatColor.RED + "You have already applied the maximum number of Hot Potato books to this item! Use Fuming Potato Books to continue to upgrade this item!");
               } else if (item.getPotatoBookAmount() == 15) {
                  p.sendMessage(ChatColor.RED + "You have applied the maximum amount of Potato Books to this item!");
               }
            }

         }

         public void combine(Player p, Inventory inv) {
            inv.setItem(29, (ItemStack)null);
            inv.setItem(33, (ItemStack)null);
            setMultipleSlots(inv, AnvilGUI.sacrificePanes, AnvilGUI.getPane(false, true));
            setMultipleSlots(inv, AnvilGUI.upgradePanes, AnvilGUI.getPane(false, false));
            setMultipleSlots(inv, AnvilGUI.bottom, AnvilGUI.getBottomRow(false));
            playSound(p, XSound.BLOCK_ANVIL_USE, 1.0F);
            set(inv, GUIClickableItem.cantPickup((new ItemBuilder(XMaterial.OAK_SIGN)).setName(ChatColor.GREEN + "Anvil").setLore(new String[]{ChatColor.GRAY + "Claim the result item above!"}).build(), 22));
            ((AnvilGUI.Anvil)AnvilGUI.anvilHashMap.get(p)).setWaitingForPickup(true);
         }

         public int getSlot() {
            return 22;
         }

         public ItemStack getItem() {
            return (new CustomItem(XMaterial.ANVIL)).addEnchantGlow().setName(ChatColor.GREEN + "Combine Items").setLore(new String[]{ChatColor.GRAY + "Combine the items in the slots", ChatColor.GRAY + "to the left and right below.", "", ChatColor.GRAY + "Cost", ChatColor.DARK_AQUA + "" + cost + " Exp Levels", "", ChatColor.YELLOW + "Click to combine!"}).HideFlags(63).build();
         }
      } : GUIClickableItem.cantPickup((new ItemBuilder(XMaterial.ANVIL)).setName(ChatColor.GREEN + "Combine Items").setLore(new String[]{ChatColor.GRAY + "Combine the items in the slots", ChatColor.GRAY + "to the left and the right below."}).build(), 22);
   }

   private static GUIClickableItem getMainBarrierBlock() {
      return getMainBarrierBlock(false, false);
   }

   private static GUIClickableItem getMainBarrierBlock(boolean error, boolean addingEnchant) {
      String name = ChatColor.RED + "Anvil";
      List lore = arrayList(new String[]{ChatColor.GRAY + "Place a target item in the left", ChatColor.GRAY + "slot and a sacrifice item in the", ChatColor.GRAY + "right slot to combine them!"});
      if (error) {
         name = ChatColor.RED + "Error!";
         lore = !addingEnchant ? arrayList(new String[]{ChatColor.GRAY + "You cannot combine those items!"}) : arrayList(new String[]{ChatColor.GRAY + "You cannot add that enchantment", ChatColor.GRAY + "to that item!"});
      }

      return GUIClickableItem.cantPickup((new ItemBuilder(XMaterial.BARRIER)).setName(name).setLore(lore).build(), 13);
   }

   public static class Anvil extends PlayerInstance {
      private boolean waitingForPickup = false;

      public Anvil(Player player, KInventory base) {
         super(player, base);
      }

      public static KInventory getBase() {
         return new KInventory("Anvil", 54) {
            public Inventory getConstantInventory() {
               Inventory inv = this.getBaseCreatedInventory();
               fill(inv, GUIClickableItem.getBorderItem(0));
               setMultipleSlots(inv, AnvilGUI.sacrificePanes, AnvilGUI.getPane(false, true));
               setMultipleSlots(inv, AnvilGUI.upgradePanes, AnvilGUI.getPane(false, false));
               setMultipleSlots(inv, AnvilGUI.bottom, AnvilGUI.getBottomRow(false));
               setMultipleSlots(inv, AnvilGUI.empty, (ItemStack)null);
               set(inv, new GUIClickableItem() {
                  public void run(InventoryClickEvent e) {
                     (new KRunnable((task) -> {
                        e.getWhoClicked().closeInventory();
                     })).runTaskLater(Skyblock.getPlugin(), 1L);
                  }

                  public int getSlot() {
                     return 49;
                  }

                  public ItemStack getItem() {
                     return (new ItemBuilder(XMaterial.BARRIER)).setName(ChatColor.RED + "Close").build();
                  }
               });
               set(inv, AnvilGUI.getMainBarrierBlock());
               set(inv, AnvilGUI.getAnvilItem(false, 0));
               return inv;
            }

            public List getIgnoreSlots() {
               return new ArrayList();
            }
         };
      }

      public void setWaitingForPickup(boolean waitingForPickup) {
         this.waitingForPickup = waitingForPickup;
      }

      public boolean isWaitingForPickup() {
         return this.waitingForPickup;
      }
   }
}
