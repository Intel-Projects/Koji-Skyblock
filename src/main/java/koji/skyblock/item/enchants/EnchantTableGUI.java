package koji.skyblock.item.enchants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import koji.skyblock.item.enchants.events.EnchantAddEvent;
import koji.skyblock.item.enchants.events.EnchantRemoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EnchantTableGUI extends KListener {
   protected static final HashMap enchantTable = new HashMap();
   private static final int[] slots = new int[]{12, 13, 14, 15, 16, 21, 22, 23, 24, 25, 30, 31, 32, 33, 34};
   private static final int[] arrows = new int[]{17, 35};
   private static final String arrowUp = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTRhNTY2N2VmNzI4NWM5MjI1ZmMyNjdkNDUxMTdlYWI1NDc4Yzc4NmJkNWFmMGExOTljMjlhMmMxNGMxZiJ9fX0=";
   private static final String arrowDown = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDFiNjJkYjVjMGEzZmExZWY0NDFiZjcwNDRmNTExYmU1OGJlZGY5YjY3MzE4NTNlNTBjZTkwY2Q0NGZiNjkifX19";

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onMenuOpen(PlayerInteractEvent e) {
      if (e.getAction() == Action.RIGHT_CLICK_BLOCK && (e.getClickedBlock().getType().toString().equals("ENCHANTMENT_TABLE") || e.getClickedBlock().getType().toString().equals("ENCHANTING_TABLE"))) {
         e.setCancelled(true);
         KRunnable t = new KRunnable((task) -> {
            playSound(e.getPlayer(), XSound.UI_BUTTON_CLICK, 1.0F);
            e.getPlayer().openInventory(openInventory((ItemStack)null, e.getClickedBlock(), e.getPlayer(), false));
         });
         if (XMaterial.supports(14)) {
            t.runTaskLater(Skyblock.getPlugin(), 1L);
         } else {
            t.run();
         }
      }

   }

   public static Inventory openInventory(ItemStack is, Block b, Player p, boolean moving) {
      enchantTable.put(p, Tuple.of(b, getMainInventory(p, (EnchantTableGUI.EnchantTable)null, is, 0)));
      ((EnchantTableGUI.EnchantTable)((Duplet)enchantTable.get(p)).getSecond()).setMovingPages(moving);
      return ((EnchantTableGUI.EnchantTable)((Duplet)enchantTable.get(p)).getSecond()).getInventory();
   }

   public static EnchantTableGUI.EnchantTable getMainInventory(Player p, EnchantTableGUI.EnchantTable instance, ItemStack is, int page) {
      if (instance == null) {
         instance = getNewEnchantTable(p, page);
      }

      instance.getInventory().setItem(19, is);
      if (!instance.isEnchantingIndividually()) {
         reset(instance, p, is, page);
      }

      p.updateInventory();
      return instance;
   }

   @EventHandler
   public void onOpen(InventoryOpenEvent e) {
      Player p = (Player)e.getPlayer();
      if (enchantTable.containsKey(p)) {
         ((EnchantTableGUI.EnchantTable)((Duplet)enchantTable.get(p)).getSecond()).set(getBookShelf(p));
         p.updateInventory();
      }

      if (e.getInventory().getType() == InventoryType.ENCHANTING) {
         p.closeInventory();
      }

   }

   @EventHandler
   public void onMenuClose(InventoryCloseEvent e) {
      Player p = (Player)e.getPlayer();
      EnchantGuideGUI.getPlayerInstanceMap().remove(p);
      if (enchantTable.containsKey(p) && ((Duplet)enchantTable.get(p)).getSecond() != null) {
         if (!((EnchantTableGUI.EnchantTable)((Duplet)enchantTable.get(p)).getSecond()).isMovingPages()) {
            enchantTable.remove(p);
            ItemStack item = e.getView().getTopInventory().getItem(19);
            if (item == null || item.getType() == XMaterial.AIR.parseMaterial()) {
               return;
            }

            if (!addItemUnlessFull(p.getInventory(), item)) {
               PlayerData.getPlayerData().addToItemStash(p, item);
            }
         } else {
            ((EnchantTableGUI.EnchantTable)((Duplet)enchantTable.get(p)).getSecond()).setMovingPages(false);
         }
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void onInventoryClickEvent(InventoryClickEvent e) {
      if (!e.isCancelled()) {
         this.onPlaceItem((Player)e.getWhoClicked(), e.getView().getTopInventory(), e.getAction());
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void onItemDragEvent(InventoryDragEvent e) {
      if (!e.isCancelled() && e.getRawSlots().stream().findAny().isPresent()) {
         this.onPlaceItem((Player)e.getWhoClicked(), e.getView().getTopInventory(), InventoryAction.PLACE_ALL);
      }

   }

   public void onPlaceItem(Player p, Inventory inv, InventoryAction action) {
      if (enchantTable.containsKey(p)) {
         ItemStack beforeItem = inv == null ? null : inv.getItem(19);
         (new KRunnable((task) -> {
            if (inv != null) {
               EnchantTableGUI.EnchantTable playerInstance = (EnchantTableGUI.EnchantTable)((Duplet)enchantTable.get(p)).getSecond();
               String title = playerInstance.getTitle();
               if (title.equals("Enchant Item")) {
                  if (enchantTable.containsKey(p)) {
                     getMainInventory(p, playerInstance, inv.getItem(19), playerInstance.getCurrentPage());
                  }
               } else if (title.startsWith("Enchant Item ➜ ") && (isValidItem(inv.getItem(19)) && !inv.getItem(19).equals(beforeItem) || isValidItem(beforeItem) && !beforeItem.equals(inv.getItem(19)))) {
                  ItemStack item = isValidItem(beforeItem) ? beforeItem : inv.getItem(19);
                  if (action.toString().startsWith("PICKUP")) {
                     p.setItemOnCursor((ItemStack)null);
                     if (!addItemUnlessFull(p.getInventory(), item)) {
                        PlayerData.getPlayerData().addToItemStash(p, item);
                     }
                  }

                  playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
                  ((EnchantTableGUI.EnchantTable)((Duplet)enchantTable.get(p)).getSecond()).setEnchantingIndividually(false);
                  getMainInventory(p, playerInstance, inv.getItem(19), 0);
               }
            }

         })).runTaskLater(Skyblock.getPlugin(), 1L);
      }

   }

   private static void setEnchants(final int page, final EnchantTableGUI.EnchantTable inv, final Player p, final ItemStack is) {
      resetPanes(inv);
      if (is != null && is.getType() != XMaterial.AIR.parseMaterial() && (new CustomItem(is)).doesAllowEnchants()) {
         final CustomItem ci = new CustomItem(is);
         ArrayList validEnchants = ci.getValidEnchants(p);
         if (validEnchants.size() - (page + 1) * 15 > 0) {
            setPageItem(page, inv, p, is, true);
         }

         if (page != 0) {
            setPageItem(page, inv, p, is, false);
         }

         sort(ci, validEnchants, PlayerData.getPlayerData().getEnchantTableSorting(p));

         for(final int i = 0; i < slots.length && i + page * 15 < validEnchants.size(); ++i) {
            final Enchant enchant = (Enchant)validEnchants.get(i + page * 15);
            inv.set(new GUIClickableItem() {
               final int bookRequirement = enchant.getBookshelfPowerRequirement();
               final boolean enoughPower;

               {
                  this.enoughPower = EnchantTableGUI.getBookshelfPower(p) >= this.bookRequirement;
               }

               public void run(InventoryClickEvent e) {
                  if (this.enoughPower) {
                     EnchantTableGUI.resetPanes(inv);
                     playSound((Player)e.getWhoClicked(), XSound.UI_BUTTON_CLICK, 1.0F);
                     EnchantTableGUI.setEnchantSet(enchant, p, (EnchantTableGUI.EnchantTable)((Duplet)EnchantTableGUI.enchantTable.get(p)).getSecond(), is, page);
                  } else {
                     playSound((Player)e.getWhoClicked(), XSound.ENTITY_VILLAGER_NO, 1.0F);
                     e.getWhoClicked().sendMessage(ChatColor.RED + "This enchantment requires " + this.bookRequirement + " Bookshelf Power!");
                  }

               }

               public int getSlot() {
                  return EnchantTableGUI.slots[i];
               }

               public ItemStack getItem() {
                  ArrayList lore = new ArrayList(enchant.getLore(1));
                  lore.add("");
                  String has = " " + ChatColor.RED + enchant.getDisplayName() + " ✖";
                  if (ci.hasEnchant(enchant)) {
                     has = " " + ChatColor.GREEN + enchant.getDisplayName() + " " + toRomanNumeral(ci.getEnchantLevel(enchant)) + " ✔";
                  }

                  String view = this.enoughPower ? ChatColor.YELLOW + "Click to view!" : ChatColor.RED + "Requires " + this.bookRequirement + " Book Shelf Power!";
                  lore.addAll(arrayList(new String[]{has, "", view}));
                  return (new ItemBuilder(XMaterial.ENCHANTED_BOOK)).setName((this.enoughPower ? ChatColor.GREEN : ChatColor.RED) + enchant.getDisplayName()).setLore(lore).build();
               }
            });
         }

      } else {
         inv.set(canEnchant(is == null || is.getType() == XMaterial.AIR.parseMaterial()));
         inv.setCurrentPage(0);
      }
   }

   private static void setEnchantSet(final Enchant enchant, final Player p, final EnchantTableGUI.EnchantTable inv, ItemStack is, final int page) {
      ((EnchantTableGUI.EnchantTable)((Duplet)enchantTable.get(p)).getSecond()).setEnchantingIndividually(true);
      int amount = Math.min(enchant.getMaxLevel(), 5);
      inv.setTitle("Enchant Item ➜ " + enchant.getDisplayName());
      final CustomItem ci = new CustomItem(is);
      final ItemStack[] item = new ItemStack[]{is};
      inv.set(new GUIClickableItem() {
         public void run(InventoryClickEvent e) {
            ((EnchantTableGUI.EnchantTable)((Duplet)EnchantTableGUI.enchantTable.get(p)).getSecond()).setEnchantingIndividually(false);
            EnchantTableGUI.reset(inv, p, item[0], page);
            playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
         }

         public int getSlot() {
            return 45;
         }

         public ItemStack getItem() {
            return (new ItemBuilder(XMaterial.ARROW)).setName(ChatColor.GREEN + "Go Back").setLore(new String[]{ChatColor.GRAY + "To Enchant Item"}).build();
         }
      });
      inv.set(GUIClickableItem.getBorderItem(51));

      for(final int finalI = 1; finalI <= getSlots(amount).length; ++finalI) {
         final int index = getSlots(amount)[finalI - 1];
         inv.set(new GUIClickableItem() {
            final int enchantLevel = ci.getEnchantLevel(enchant);
            final int cost = enchant.getTotalExperience(finalI);
            final boolean alreadyHas = ci.hasEnchant(enchant);
            final boolean hasHigherLevel;

            {
               this.hasHigherLevel = this.alreadyHas && this.enchantLevel > finalI;
            }

            public void run(InventoryClickEvent e) {
               Player px = (Player)e.getWhoClicked();
               if (!this.hasHigherLevel) {
                  if (px.getLevel() < this.cost && px.getGameMode() != GameMode.CREATIVE) {
                     px.sendMessage(ChatColor.RED + "You don't have enough Experience Levels!");
                     playSound(px, XSound.ENTITY_ENDERMAN_TELEPORT, 0.5F);
                  } else if (this.alreadyHas && this.enchantLevel == finalI) {
                     EnchantRemoveEvent eRE = new EnchantRemoveEvent(px, enchant, ci.removeEnchant(px, enchant));
                     Bukkit.getPluginManager().callEvent(eRE);
                     if (eRE.isCancelled()) {
                        return;
                     }

                     px.sendMessage(ChatColor.RED + "You removed " + enchant.getDisplayName() + ChatColor.RED + " from your " + ci.getName() + "!");
                     playSound(px, XSound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0F);
                     item[0] = eRE.getItem().buildWithAbilities();
                     inv.set(19, item[0]);
                     if (px.getGameMode() != GameMode.CREATIVE) {
                        px.setLevel(px.getLevel() - this.cost);
                     }
                  } else {
                     EnchantAddEvent eAE = new EnchantAddEvent(px, enchant, ci.addEnchant(px, enchant, finalI, true));
                     Bukkit.getPluginManager().callEvent(eAE);
                     if (eAE.isCancelled()) {
                        return;
                     }

                     px.sendMessage(ChatColor.GREEN + "You enchanted your " + ci.getName() + ChatColor.GREEN + " with " + enchant.getDisplayName() + " " + toRomanNumeral(finalI));
                     playSound(px, XSound.ENTITY_PLAYER_LEVELUP, 1.0F);
                     item[0] = eAE.getItem().buildWithAbilities();
                     inv.set(19, item[0]);
                     if (px.getGameMode() != GameMode.CREATIVE) {
                        px.setLevel(px.getLevel() - this.cost);
                     }
                  }

                  e.setCancelled(true);
                  EnchantTableGUI.setEnchantSet(enchant, px, inv, item[0], page);
               }
            }

            public int getSlot() {
               return index;
            }

            public ItemStack getItem() {
               XMaterial type = this.alreadyHas && this.hasHigherLevel ? XMaterial.GRAY_DYE : XMaterial.ENCHANTED_BOOK;
               List bottomTag = arrayList(new String[]{ChatColor.YELLOW + "Click to enchant!"});
               ArrayList lore = new ArrayList(enchant.getLore(finalI));
               if (this.alreadyHas && this.enchantLevel == finalI) {
                  lore.addAll(arrayList(new String[]{"", ChatColor.RED + "This enchantment is already", ChatColor.RED + "present and can be removed."}));
                  bottomTag = arrayList(new String[]{ChatColor.YELLOW + "Click to remove!"});
               }

               if (ci.hasConflictingEnchant(enchant)) {
                  Enchant conflict = ci.getConflictingEnchant(enchant);
                  lore.addAll(arrayList(new String[]{"", ChatColor.RED + bold("WARNING: This will remove"), ChatColor.RED + bold(conflict.getDisplayName() + ".")}));
               }

               lore.addAll(arrayList(new String[]{"", ChatColor.GRAY + "Cost", ChatColor.DARK_AQUA + "" + this.cost + " Exp Levels " + EnchantTableGUI.hasEnoughLevels(p, this.cost)}));
               if (p.getLevel() < this.cost && p.getGameMode() != GameMode.CREATIVE) {
                  bottomTag = arrayList(new String[]{ChatColor.RED + "You don't have enough", ChatColor.RED + "Experience Levels!"});
               }

               if (this.hasHigherLevel) {
                  bottomTag = arrayList(new String[]{ChatColor.RED + "Higher level already present!"});
               }

               lore.add("");
               lore.addAll(bottomTag);
               return (new ItemBuilder(type)).setLore(lore).setName(ChatColor.BLUE + enchant.getDisplayName() + " " + toRomanNumeral(finalI)).build();
            }
         });
      }

   }

   private static void reset(EnchantTableGUI.EnchantTable inv, Player p, ItemStack is, int page) {
      inv.set(getSortingGUIItem(inv, p));
      inv.set(GUIClickableItem.getBorderItem(45));
      inv.set(19, is);
      setEnchants(page, inv, p, is);
      inv.setTitle("Enchant Item");
   }

   private static GUIClickableItem canEnchant(boolean nothing) {
      return !nothing ? GUIClickableItem.cantPickup((new ItemBuilder(XMaterial.RED_DYE)).setName(ChatColor.RED + "Cannot Enchant Item!").setLore(new String[]{ChatColor.GRAY + "This item cannot be enchanted!"}).build(), 23) : GUIClickableItem.cantPickup((new ItemBuilder(XMaterial.GRAY_DYE)).setName(ChatColor.RED + "Enchant Item").setLore(new String[]{ChatColor.GRAY + "Place and item in the open slot", ChatColor.GRAY + "to enchant it!"}).build(), 23);
   }

   private static void sort(CustomItem ci, ArrayList sorted, EnchantTableGUI.Sorting sorting) {
      switch(sorting) {
      case MISSING:
         Iterator var3 = (new ArrayList(sorted)).iterator();

         while(var3.hasNext()) {
            Enchant enchant = (Enchant)var3.next();
            if (ci.hasEnchant(enchant)) {
               sorted.remove(enchant);
               sorted.add(enchant);
            }
         }

         return;
      case AZ:
         sorted.sort(Comparator.comparing(Enchant::getName));
         break;
      case ZA:
         sorted.sort(Comparator.comparing(Enchant::getName));
         Collections.reverse(sorted);
      }

   }

   private static void resetPanes(EnchantTableGUI.EnchantTable inv) {
      Arrays.stream(slots).forEach((j) -> {
         inv.set(GUIClickableItem.getBorderItem(j));
      });
      Arrays.stream(arrows).forEach((j) -> {
         inv.set(GUIClickableItem.getBorderItem(j));
      });
   }

   private static String hasEnoughLevels(Player p, int requirement) {
      return p.getLevel() < requirement && p.getGameMode() != GameMode.CREATIVE ? ChatColor.RED + "✖" : ChatColor.GREEN + "✔";
   }

   private static Integer[] getSlots(int amount) {
      List ints = new ArrayList();
      int center = 23;
      if (amount % 2 != 0) {
         ints.add(Integer.valueOf(center));
      }

      for(int i = 1; i <= amount / 2; ++i) {
         ints.add(center + i);
         ints.add(center - i);
      }

      Integer[] returnValue = (Integer[])ints.toArray(new Integer[0]);
      Arrays.sort(returnValue);
      return returnValue;
   }

   private static void setPageItem(int page, final EnchantTableGUI.EnchantTable inv, final Player p, final ItemStack ci, final boolean next) {
      final int pageTo = next ? page + 1 : page - 1;
      final int slot = next ? 35 : 17;
      final String string = next ? "Next" : "Previous";
      inv.set(new GUIClickableItem() {
         public void run(InventoryClickEvent e) {
            inv.setCurrentPage(pageTo);
            EnchantTableGUI.setEnchants(pageTo, inv, p, ci);
         }

         public int getSlot() {
            return slot;
         }

         public ItemStack getItem() {
            return (new ItemBuilder(XMaterial.PLAYER_HEAD)).setName(ChatColor.GREEN + string + " Page").setLore(new String[]{ChatColor.DARK_GRAY + "Page " + (pageTo + 1)}).setTexture(next ? "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDFiNjJkYjVjMGEzZmExZWY0NDFiZjcwNDRmNTExYmU1OGJlZGY5YjY3MzE4NTNlNTBjZTkwY2Q0NGZiNjkifX19" : "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTRhNTY2N2VmNzI4NWM5MjI1ZmMyNjdkNDUxMTdlYWI1NDc4Yzc4NmJkNWFmMGExOTljMjlhMmMxNGMxZiJ9fX0=").build();
         }
      });
   }

   private static int getBookshelfPower(Player p) {
      int bookshelfPower = 0;
      if (enchantTable.containsKey(p)) {
         if (((Duplet)enchantTable.get(p)).getFirst() == null) {
            return 30;
         }

         for(int x = -2; x <= 2; ++x) {
            for(int y = 0; y <= 1; ++y) {
               for(int z = -2; z <= 2; ++z) {
                  Block b = ((Block)((Duplet)enchantTable.get(p)).getFirst()).getRelative(x, y, z);
                  if (b.getType() == XMaterial.BOOKSHELF.parseMaterial()) {
                     ++bookshelfPower;
                  }
               }
            }
         }
      }

      return bookshelfPower;
   }

   private static GUIClickableItem getSortingGUIItem(final EnchantTableGUI.EnchantTable inv, final Player p) {
      return new GUIClickableItem() {
         public void run(InventoryClickEvent e) {
            Player px = (Player)e.getWhoClicked();
            PlayerData.getPlayerData().setEnchantTableSorting(px, EnchantTableGUI.getNextSorting(PlayerData.getPlayerData().getEnchantTableSorting(px)));
            playSound((Player)e.getWhoClicked(), XSound.BLOCK_WOODEN_BUTTON_CLICK_OFF, 1.0F);
            if (inv != null) {
               inv.set(this);
               EnchantTableGUI.setEnchants(0, inv, px, inv.getInventory().getItem(19));
            }

            e.setCancelled(true);
         }

         public int getSlot() {
            return 51;
         }

         public ItemStack getItem() {
            return EnchantTableGUI.getSortingItem(PlayerData.getPlayerData().getEnchantTableSorting(p));
         }
      };
   }

   private static ItemStack getSortingItem(EnchantTableGUI.Sorting sorting) {
      String prefix = ChatColor.AQUA + "► ";
      return (new ItemBuilder(XMaterial.HOPPER)).setName(ChatColor.GREEN + "Sort").setLore(new String[]{ChatColor.GRAY + "Change how Enchantments are", ChatColor.GRAY + "sorted.", "", (sorting == EnchantTableGUI.Sorting.DEFAULT ? prefix : ChatColor.GRAY) + "Default", (sorting == EnchantTableGUI.Sorting.MISSING ? prefix : ChatColor.GRAY) + "Missing Enchantments First", (sorting == EnchantTableGUI.Sorting.AZ ? prefix : ChatColor.GRAY) + "A to Z", (sorting == EnchantTableGUI.Sorting.ZA ? prefix : ChatColor.GRAY) + "Z to A", "", ChatColor.YELLOW + "Click to switch sort!"}).setString("Sort", sorting.name()).build();
   }

   private static EnchantTableGUI.Sorting getNextSorting(EnchantTableGUI.Sorting sorting) {
      switch(sorting) {
      case MISSING:
         return EnchantTableGUI.Sorting.AZ;
      case AZ:
         return EnchantTableGUI.Sorting.ZA;
      case ZA:
      default:
         return EnchantTableGUI.Sorting.DEFAULT;
      case DEFAULT:
         return EnchantTableGUI.Sorting.MISSING;
      }
   }

   public static EnchantTableGUI.EnchantTable getNewEnchantTable(Player p, int page) {
      return new EnchantTableGUI.EnchantTable(p, EnchantTableGUI.EnchantTable.getBase(p), page);
   }

   public static GUIClickableItem getBookShelf(Player p) {
      return GUIClickableItem.cantPickup((new ItemBuilder(XMaterial.BOOKSHELF)).setName(ChatColor.LIGHT_PURPLE + "Bookshelf Power").setLore(new String[]{ChatColor.GRAY + "Stronger enchantments require", ChatColor.GRAY + "more Bookshelf Power, which can", ChatColor.GRAY + "be increased by placing", ChatColor.GRAY + "bookshelves nearby.", "", ChatColor.GRAY + "Bookshelf Power: " + ChatColor.LIGHT_PURPLE + getBookshelfPower(p)}).build(), 48);
   }

   public static HashMap getEnchantTable() {
      return enchantTable;
   }

   public static class EnchantTable extends PlayerInstance {
      private boolean movingPages = false;
      private boolean enchantingIndividually = false;
      private int currentPage;
      private static KInventory kInventory = null;

      public EnchantTable(Player player, KInventory base, int page) {
         super(player, base);
         this.currentPage = page;
      }

      public static KInventory getBase(final Player p) {
         if (kInventory == null) {
            kInventory = new KInventory("Enchant Item", 54) {
               public Inventory getConstantInventory() {
                  final Inventory inv = this.getBaseCreatedInventory();
                  fill(inv, GUIClickableItem.getBorderItem(0));
                  set(inv, EnchantTableGUI.getBookShelf(p));
                  set(inv, GUIClickableItem.cantPickup((new ItemBuilder(XMaterial.ENCHANTING_TABLE)).setName(ChatColor.GREEN + "Enchant Item").setLore(new String[]{ChatColor.GRAY + "Add and remove enchantments from", ChatColor.GRAY + "the item in the slot above"}).build(), 28));
                  set(inv, EnchantTableGUI.canEnchant(true));
                  set(inv, new GUIClickableItem() {
                     public void run(InventoryClickEvent e) {
                        EnchantGuideGUI.openEnchantGuide(p, (Block)((Duplet)EnchantTableGUI.enchantTable.get(p)).getFirst());
                        playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
                        inv.setItem(19, (ItemStack)null);
                     }

                     public int getSlot() {
                        return 50;
                     }

                     public ItemStack getItem() {
                        return (new ItemBuilder(XMaterial.BOOK)).setName(ChatColor.GREEN + "Enchantments Guide").setLore(new String[]{ChatColor.GRAY + "View a complete list of all", ChatColor.GRAY + "enchantments and their", ChatColor.GRAY + "requirements.", "", ChatColor.YELLOW + "Click to view!"}).build();
                     }
                  });
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
                  set(inv, EnchantTableGUI.getSortingGUIItem((EnchantTableGUI.EnchantTable)null, p));
                  return inv;
               }

               public List getIgnoreSlots() {
                  return arrayList(new Integer[]{19});
               }
            };
         }

         return kInventory;
      }

      public boolean isMovingPages() {
         return this.movingPages;
      }

      public void setMovingPages(boolean movingPages) {
         this.movingPages = movingPages;
      }

      public boolean isEnchantingIndividually() {
         return this.enchantingIndividually;
      }

      public void setEnchantingIndividually(boolean enchantingIndividually) {
         this.enchantingIndividually = enchantingIndividually;
      }

      public int getCurrentPage() {
         return this.currentPage;
      }

      public void setCurrentPage(int currentPage) {
         this.currentPage = currentPage;
      }
   }

   public static enum Sorting {
      DEFAULT,
      MISSING,
      AZ,
      ZA;
   }
}
