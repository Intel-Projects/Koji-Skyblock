package koji.skyblock.item.creation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import koji.developerkit.gui.GUIClickableItem;
import koji.developerkit.inventory.KInventory;
import koji.developerkit.inventory.KInventory.PlayerInstance;
import koji.developerkit.listener.KListener;
import koji.developerkit.runnable.KRunnable;
import koji.developerkit.utils.ItemBuilder;
import koji.developerkit.utils.xseries.XMaterial;
import koji.developerkit.utils.xseries.XSound;
import koji.skyblock.Skyblock;
import koji.skyblock.files.Files;
import koji.skyblock.files.data.PlayerData;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.ItemType;
import koji.skyblock.item.Rarity;
import koji.skyblock.item.ability.Ability;
import koji.skyblock.item.enchants.EnchantTableGUI;
import koji.skyblock.item.reforges.Reforge;
import koji.skyblock.item.utils.SignMenuFactory;
import koji.skyblock.player.Stats;
import koji.skyblock.utils.StatMap;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemBuilderGUI extends KListener {
   private static final HashMap hashMap = new HashMap();
   private static final int[] slots = new int[]{19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};

   @EventHandler
   public void onInventoryClose(InventoryCloseEvent e) {
      Player p = (Player)e.getPlayer();
      if (hashMap.containsKey(p)) {
         if (!((ItemBuilderGUI.ItemBuilderMenu)hashMap.get(p)).isSignClose() && !((ItemBuilderGUI.ItemBuilderMenu)hashMap.get(p)).isMovingPages()) {
            ItemStack item = ((ItemBuilderGUI.ItemBuilderMenu)hashMap.get(p)).getItem();
            if (isValidItem(item) && !addItemUnlessFull(p.getInventory(), item)) {
               PlayerData.getPlayerData().addToItemStash(p, item);
            }

            hashMap.remove(p);
         } else {
            ((ItemBuilderGUI.ItemBuilderMenu)hashMap.get(p)).setMovingPages(false);
            ((ItemBuilderGUI.ItemBuilderMenu)hashMap.get(p)).setSignClose(false);
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onInventoryClickEvent(InventoryClickEvent e) {
      Inventory inv = e.getView().getTopInventory();
      placeItem(inv, (Player)e.getWhoClicked());
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onItemDragEvent(InventoryDragEvent e) {
      if (e.getRawSlots().stream().findAny().isPresent()) {
         Inventory inv = e.getView().getTopInventory();
         placeItem(inv, (Player)e.getWhoClicked());
      }

   }

   public static void placeItem(Inventory inv, Player p) {
      if (inv != null && hashMap.containsKey(p)) {
         (new KRunnable((task) -> {
            if (hashMap.containsKey(p)) {
               getMainMenu(p, inv.getItem(13), (ItemBuilderGUI.ItemBuilderMenu)hashMap.get(p));
            }

         })).runTaskLater(Skyblock.getPlugin(), 1L);
      }

   }

   public static ItemBuilderGUI.ItemBuilderMenu getMainMenu(Player p, ItemStack is, ItemBuilderGUI.ItemBuilderMenu menu) {
      return getMainMenu(p, is, menu, ItemBuilderGUI.SubMenu.NONE);
   }

   public static ItemBuilderGUI.ItemBuilderMenu getMainMenu(Player p, ItemStack is, ItemBuilderGUI.ItemBuilderMenu menu, ItemBuilderGUI.SubMenu subMenu) {
      if (menu == null) {
         menu = getNewItemBuilderMenu(p);
         menu.setSubMenu(subMenu);
         p.openInventory(menu.getInventory());
         (new KRunnable((task) -> {
            ItemBuilderGUI.ItemBuilderMenu var10000 = (ItemBuilderGUI.ItemBuilderMenu)hashMap.put(p, menu);
         })).runTaskLater(Skyblock.getPlugin(), 1L);
      }

      menu.getInventory().setItem(13, is);
      getRow(p, is, menu.getSubMenu(), menu.getPage()).forEach(menu::set);
      p.updateInventory();
      return menu;
   }

   public static ItemBuilderGUI.ItemBuilderMenu getNewItemBuilderMenu(Player p) {
      return new ItemBuilderGUI.ItemBuilderMenu(p, ItemBuilderGUI.ItemBuilderMenu.getBase(p));
   }

   private static List getRow(final Player p, final ItemStack putItem, ItemBuilderGUI.SubMenu subMenu, int page) {
      List items = new ArrayList();
      Arrays.stream(ItemBuilderGUI.slots).forEach((i) -> {
         items.add(GUIClickableItem.getBorderItem(i));
      });
      items.add(GUIClickableItem.getBorderItem(45));
      items.add(GUIClickableItem.getBorderItem(53));
      if (putItem != null && putItem.getType() != XMaterial.AIR.parseMaterial()) {
         final CustomItem item;
         final Integer[] slots;
         final int size;
         boolean[] pages;
         final int thing;
         label193:
         switch(subMenu) {
         case NONE:
            item = new CustomItem(putItem);
            if (!item.hasKey("id")) {
               items.add(new GUIClickableItem() {
                  public void run(InventoryClickEvent e) {
                     playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
                     item.setType(item.getDefaultItemType()).setString("id", "null").HideFlags(63).setUnbreakable(true).setName(item.getRarity().getColor() + item.getName());
                     ItemBuilderGUI.getMainMenu(p, item.buildWithAbilities(), (ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p));
                  }

                  public int getSlot() {
                     return 30;
                  }

                  public ItemStack getItem() {
                     return (new ItemBuilder(XMaterial.LIME_TERRACOTTA)).setName(ChatColor.GREEN + "Continue Converting Item").setLore(new String[]{ChatColor.GRAY + "This will convert your item", ChatColor.GRAY + "into a Koji Skyblock item for", ChatColor.GRAY + "you to edit.", "", ChatColor.RED + "" + ChatColor.BOLD + "WARNING: THIS WILL MOST LIKELY", ChatColor.RED + "" + ChatColor.BOLD + "RESET YOUR ITEM'S LORE, AND", ChatColor.RED + "" + ChatColor.BOLD + "THERE IS NO GOING BACK, WOULD", ChatColor.RED + "" + ChatColor.BOLD + "YOU LIKE TO CONTINUE?", "", ChatColor.YELLOW + "Click to continue!"}).build();
                  }
               });
               items.add(new GUIClickableItem() {
                  public void run(InventoryClickEvent e) {
                     playSound(p, XSound.ENTITY_ITEM_PICKUP, 1.0F);
                     if (!addItemUnlessFull(e.getWhoClicked().getInventory(), putItem)) {
                        PlayerData.getPlayerData().addToItemStash(p, putItem);
                     }

                     e.getInventory().setItem(13, (ItemStack)null);
                     ItemBuilderGUI.getMainMenu(p, (ItemStack)null, (ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p));
                  }

                  public int getSlot() {
                     return 32;
                  }

                  public ItemStack getItem() {
                     return (new ItemBuilder(XMaterial.RED_TERRACOTTA)).setName(ChatColor.RED + "Cancel Converting Item").setLore(new String[]{ChatColor.GRAY + "This will cancel converting", ChatColor.GRAY + "your item into a Koji Skyblock", ChatColor.GRAY + "item for editing.", "", ChatColor.RED + "" + ChatColor.BOLD + "WARNING: CONTINUING WILL MOST", ChatColor.RED + "" + ChatColor.BOLD + "LIKELY RESET YOUR ITEM'S LORE,", ChatColor.RED + "" + ChatColor.BOLD + "AND THERE IS NO GOING BACK,", ChatColor.RED + "" + ChatColor.BOLD + "WOULD YOU LIKE TO CONTINUE?", "", ChatColor.YELLOW + "Click to cancel!"}).build();
                  }
               });
            } else {
               items.add(new GUIClickableItem() {
                  public void run(InventoryClickEvent e) {
                     SignMenuFactory.Menu menu = SignMenuFactory.getFactory().newMenu(arrayList(new String[]{"", "", "^^^^^^^^^^^^^^^", "Enter name"})).reopenIfFail(true).response((player, strings) -> {
                        String combined = strings[0];
                        if (!combined.endsWith(" ")) {
                           combined = combined + " ";
                        }

                        combined = combined + strings[1];
                        combined = combined.trim();
                        if (!combined.equals("")) {
                           item.setName(item.getRarity().getColor() + combined);
                        }

                        (new KRunnable((task) -> {
                           ItemBuilderGUI.getMainMenu(p, item.buildWithAbilities(), (ItemBuilderGUI.ItemBuilderMenu)null);
                        })).runTaskLater(Skyblock.getPlugin(), 1L);
                        return true;
                     });
                     ((ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p)).setSignClose(true);
                     menu.open(p);
                  }

                  public int getSlot() {
                     return 19;
                  }

                  public ItemStack getItem() {
                     return (new ItemBuilder(XMaterial.NAME_TAG)).setName(ChatColor.GREEN + "Change Name").setLore(new String[]{ChatColor.GRAY + "This will allow you to change", ChatColor.GRAY + "the name of the item you are", ChatColor.GRAY + "changing.", "", ChatColor.GRAY + "Current name: " + ChatColor.WHITE + item.getName(), "", ChatColor.YELLOW + "Click to change!"}).build();
                  }
               });
               items.add(new GUIClickableItem() {
                  public void run(InventoryClickEvent e) {
                     playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
                     ((ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p)).setSubMenu(ItemBuilderGUI.SubMenu.RARITY);
                     ItemBuilderGUI.getMainMenu(p, item.buildWithAbilities(), (ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p));
                  }

                  public int getSlot() {
                     return 20;
                  }

                  public ItemStack getItem() {
                     return (new ItemBuilder(XMaterial.QUARTZ)).setName(ChatColor.GREEN + "Set Rarity").setLore(new String[]{ChatColor.GRAY + "This will allow you to change", ChatColor.GRAY + "the rarity of the item you are", ChatColor.GRAY + "changing.", "", ChatColor.GRAY + "Current rarity: " + item.getRarity().getDisplay(), "", ChatColor.YELLOW + "Click to change!"}).build();
                  }
               });
               items.add(new GUIClickableItem() {
                  public void run(InventoryClickEvent e) {
                     playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
                     ((ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p)).setSubMenu(ItemBuilderGUI.SubMenu.STATS);
                     ItemBuilderGUI.getMainMenu(p, item.buildWithAbilities(), (ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p));
                  }

                  public int getSlot() {
                     return 21;
                  }

                  public ItemStack getItem() {
                     return (new ItemBuilder(XMaterial.STONE_SWORD)).HideFlags(63).setName(ChatColor.GREEN + "Change Stats").setLore(new String[]{ChatColor.GRAY + "This will allow you to change", ChatColor.GRAY + "the stats of the item you are", ChatColor.GRAY + "changing.", "", ChatColor.YELLOW + "Click to change!"}).build();
                  }
               });
               items.add(new GUIClickableItem() {
                  public void run(InventoryClickEvent e) {
                     playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
                     ((ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p)).setSubMenu(ItemBuilderGUI.SubMenu.ITEM_TYPE);
                     ItemBuilderGUI.getMainMenu(p, item.buildWithAbilities(), (ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p));
                  }

                  public int getSlot() {
                     return 22;
                  }

                  public ItemStack getItem() {
                     return (new ItemBuilder(XMaterial.IRON_CHESTPLATE)).setName(ChatColor.GREEN + "Change Item Type").setLore(new String[]{ChatColor.GRAY + "This will allow you to change", ChatColor.GRAY + "the type of the item you are", ChatColor.GRAY + "changing.", "", ChatColor.GRAY + "Current item type: " + ChatColor.GREEN + item.getType(), "", ChatColor.YELLOW + "Click to change!"}).build();
                  }
               });
               items.add(new GUIClickableItem() {
                  public void run(InventoryClickEvent e) {
                     if (e.getClick().isLeftClick()) {
                        ((ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p)).setSignClose(true);
                        SignMenuFactory.Menu menu = SignMenuFactory.getFactory().newMenu(arrayList(new String[]{"", "", "^^^^^^^^^^^^^^^", "Enter sub type"})).reopenIfFail(true).response((player, strings) -> {
                           String combined = strings[0];
                           if (!combined.endsWith(" ")) {
                              combined = combined + " ";
                           }

                           combined = combined + strings[1];
                           combined = combined.trim();
                           item.setExtraType(combined);
                           (new KRunnable((task) -> {
                              ItemBuilderGUI.getMainMenu(p, item.buildWithAbilities(), (ItemBuilderGUI.ItemBuilderMenu)null);
                           })).runTaskLater(Skyblock.getPlugin(), 1L);
                           return true;
                        });
                        menu.open(p);
                     } else {
                        playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
                        ItemBuilderGUI.getMainMenu(p, item.setExtraType("").buildWithAbilities(), (ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p));
                     }

                  }

                  public int getSlot() {
                     return 23;
                  }

                  public ItemStack getItem() {
                     List lore = arrayList(new String[]{ChatColor.GRAY + "This will allow you to change", ChatColor.GRAY + "the sub type of the item you", ChatColor.GRAY + "are changing.", "", ChatColor.GRAY + "Current sub type: " + ChatColor.GREEN + item.getExtraType(), ""});
                     if (!item.getExtraType().equals("")) {
                        lore.add(ChatColor.AQUA + "Right-Click to reset!");
                     }

                     lore.add(ChatColor.YELLOW + "Click to change!");
                     return (new ItemBuilder(XMaterial.IRON_LEGGINGS)).setName(ChatColor.GREEN + "Set Sub Type").setLore(lore).build();
                  }
               });
               items.add(new GUIClickableItem() {
                  public void run(InventoryClickEvent e) {
                     SignMenuFactory.Menu menu = SignMenuFactory.getFactory().newMenu(arrayList(new String[]{"", "", "^^^^^^^^^^^^^^^", "Enter ID"})).reopenIfFail(true).response((player, strings) -> {
                        String combined = strings[0];
                        combined = combined + strings[1];
                        combined = combined.trim();
                        if (!combined.equals("")) {
                           item.setString("id", combined.toUpperCase());
                        }

                        (new KRunnable((task) -> {
                           ItemBuilderGUI.getMainMenu(p, item.buildWithAbilities(), (ItemBuilderGUI.ItemBuilderMenu)null);
                        })).runTaskLater(Skyblock.getPlugin(), 1L);
                        return true;
                     });
                     ((ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p)).setSignClose(true);
                     menu.open(p);
                  }

                  public int getSlot() {
                     return 24;
                  }

                  public ItemStack getItem() {
                     return (new ItemBuilder(XMaterial.EMERALD)).setName(ChatColor.GREEN + "Set Internal ID").setLore(new String[]{ChatColor.GRAY + "This will allow you to change", ChatColor.GRAY + "the internal id of the item", ChatColor.GRAY + "you are changing.", "", ChatColor.GRAY + "Current ID: " + ChatColor.GREEN + (item.getID().equals("null") ? "" : item.getID()), "", ChatColor.YELLOW + "Click to change!"}).build();
                  }
               });
               items.add(new GUIClickableItem() {
                  public void run(InventoryClickEvent e) {
                     playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
                     ((ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p)).setSubMenu(ItemBuilderGUI.SubMenu.ABILITY);
                     ItemBuilderGUI.getMainMenu(p, item.buildWithAbilities(), (ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p));
                  }

                  public int getSlot() {
                     return 25;
                  }

                  public ItemStack getItem() {
                     return (new ItemBuilder(XMaterial.NETHER_STAR)).setName(ChatColor.GREEN + "Set Abilities").setLore(new String[]{ChatColor.GRAY + "This will allow you to edit", ChatColor.GRAY + "the abilities of the item you", ChatColor.GRAY + "are changing.", "", ChatColor.YELLOW + "Click to change!"}).build();
                  }
               });
               items.add(new GUIClickableItem() {
                  public void run(InventoryClickEvent e) {
                     ((ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p)).setMovingPages(true);
                     (new KRunnable((task) -> {
                        playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
                        p.openInventory(EnchantTableGUI.openInventory(putItem, (Block)null, p, true));
                        ItemBuilderGUI.hashMap.remove(p);
                     })).runTaskLater(Skyblock.getPlugin(), 1L);
                  }

                  public int getSlot() {
                     return 29;
                  }

                  public ItemStack getItem() {
                     return (new ItemBuilder(XMaterial.ENCHANTING_TABLE)).setName(ChatColor.GREEN + "Enchant Item").setLore(new String[]{ChatColor.GRAY + "This will allow you to edit", ChatColor.GRAY + "enchants the item you're", ChatColor.GRAY + "changing has.", "", ChatColor.YELLOW + "Click to open!"}).build();
                  }
               });
               items.add(new GUIClickableItem() {
                  boolean allowsEnchants = item.doesAllowEnchants();

                  public void run(InventoryClickEvent e) {
                     playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
                     item.setAllowEnchants(!this.allowsEnchants);
                     this.allowsEnchants = item.doesAllowEnchants();
                     ((ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p)).set(this);
                     ((ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p)).getInventory().setItem(13, item.buildWithAbilities());
                  }

                  public int getSlot() {
                     return 30;
                  }

                  public ItemStack getItem() {
                     return (new ItemBuilder(this.allowsEnchants ? XMaterial.LIME_DYE : XMaterial.RED_DYE)).setName(ChatColor.GREEN + "Allow Enchants").setLore(new String[]{ChatColor.GRAY + "Some items don't allow", ChatColor.GRAY + "enchants, toggle that with", ChatColor.GRAY + "this item.", "", ChatColor.GRAY + "Currently allows: " + ChatColor.GREEN + this.allowsEnchants, "", ChatColor.YELLOW + "Click to toggle."}).build();
                  }
               });
               if (putItem.getType().isBlock()) {
                  items.add(new GUIClickableItem() {
                     boolean canBePlaced = item.canBePlaced();

                     public void run(InventoryClickEvent e) {
                        playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
                        item.setCanBePlaced(!this.canBePlaced);
                        this.canBePlaced = item.canBePlaced();
                        ((ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p)).set(this);
                        ((ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p)).getInventory().setItem(13, item.buildWithAbilities());
                     }

                     public int getSlot() {
                        return 31;
                     }

                     public ItemStack getItem() {
                        return (new ItemBuilder(this.canBePlaced ? XMaterial.LIME_DYE : XMaterial.RED_DYE)).setName(ChatColor.GREEN + "Can Be Placed").setLore(new String[]{ChatColor.GRAY + "Some items aren't allowed to", ChatColor.GRAY + "be placed, toggle that with", ChatColor.GRAY + "this item.", "", ChatColor.GRAY + "Currently can be: " + ChatColor.GREEN + this.canBePlaced, "", ChatColor.YELLOW + "Click to toggle."}).build();
                     }
                  });
               }

               items.add(new GUIClickableItem() {
                  public void run(InventoryClickEvent e) {
                     playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
                     ((ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p)).setSubMenu(ItemBuilderGUI.SubMenu.REFORGE);
                     ItemBuilderGUI.getMainMenu(p, item.buildWithAbilities(), (ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p));
                  }

                  public int getSlot() {
                     return 32;
                  }

                  public ItemStack getItem() {
                     return (new ItemBuilder(XMaterial.ANVIL)).setName(ChatColor.GREEN + "Change Reforge").setLore(new String[]{ChatColor.GRAY + "Change the reforge of the", ChatColor.GRAY + "current item being changed", "", ChatColor.GRAY + "Current reforge: " + ChatColor.GREEN + item.getReforgeName(), "", ChatColor.YELLOW + "Click to change!"}).build();
                  }
               });
               items.add(new GUIClickableItem() {
                  public void run(InventoryClickEvent e) {
                     playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
                     int i = e.isRightClick() ? -1 : 1;
                     item.getDefaultPotatoStats().forEach((s) -> {
                        item.setPotatoBookAmount(s, item.getPotatoBookAmount(s) + i);
                     });
                     ItemBuilderGUI.getMainMenu(p, item.buildWithAbilities(), (ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p));
                  }

                  public int getSlot() {
                     return 33;
                  }

                  public ItemStack getItem() {
                     return (new ItemBuilder(XMaterial.BOOK)).setName(ChatColor.GREEN + "Hot Potato Books").setLore(new String[]{ChatColor.GRAY + "Change the amount of potato", ChatColor.GRAY + "books the item has.", "", ChatColor.GRAY + "Current book amount: " + ChatColor.GREEN + item.getPotatoBookAmount(), "", ChatColor.AQUA + "Right-Click to decrease!", ChatColor.YELLOW + "Click to increase!"}).build();
                  }
               });
            }
            break;
         case RARITY:
            item = new CustomItem(putItem);
            slots = getCenteredSlots(Rarity.values().length, 22);
            size = 0;

            while(true) {
               if (size >= Rarity.values().length) {
                  break label193;
               }

               final Rarity rarity = (Rarity)Rarity.getOrdered().get(size);
               items.add(new GUIClickableItem() {
                  public void run(InventoryClickEvent e) {
                     playSound(p, XSound.ENTITY_PLAYER_LEVELUP, 1.0F);
                     ((ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p)).setSubMenu(ItemBuilderGUI.SubMenu.NONE);
                     String oldName = item.getName();
                     if (oldName.charAt(0) == 167) {
                        oldName = oldName.substring(2);
                     }

                     item.setRarity(rarity).setName(rarity.getColor() + oldName);
                     ItemBuilderGUI.getMainMenu(p, item.buildWithAbilities(), (ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p));
                  }

                  public int getSlot() {
                     return slots[size];
                  }

                  public ItemStack getItem() {
                     return (new ItemBuilder(rarity.getBlock())).setName(rarity.getDisplay()).setLore(new String[]{ChatColor.GRAY + "This will set the rarity to", rarity.getDisplay() + ChatColor.GRAY + ".", "", ChatColor.YELLOW + "Click to apply this rarity!"}).build();
                  }
               });
               ++size;
            }
         case STATS:
            item = new CustomItem(putItem);
            size = Stats.getNormalValues().size();
            slots = getCenteredSlots(Math.min(size - page * 21, 21), 22);

            for(final int i1 = 0; i1 < slots.length; ++i1) {
               final Stats stat = (Stats)Stats.getNormalValues().get(i1 + page * 21);
               final List lore = wrapLine("This will set the amount of " + stat.statDisplayName() + " your item has.", 30);
               lore = (List)lore.stream().map((line) -> {
                  return ChatColor.GRAY + line;
               }).collect(Collectors.toList());
               lore.addAll(arrayList(new String[]{"", ChatColor.GRAY + "Current " + stat.statDisplayName().toLowerCase() + ": " + ChatColor.GREEN + stat.format(item.getStat(stat, false)), "", ChatColor.YELLOW + "Click to change!"}));
               items.add(new GUIClickableItem() {
                  public void run(InventoryClickEvent e) {
                     SignMenuFactory.Menu menu = SignMenuFactory.getFactory().newMenu(arrayList(new String[]{"", "^^^^^^^^^^^^^^^", "Enter Amount", "Item Stat"})).reopenIfFail(false).response((player, strings) -> {
                        String digits = strings[0].replaceAll("[^0-9.]", "");
                        double amount = digits.equals("") ? 0.0D : Double.parseDouble(digits);
                        item.setStat(stat, amount, false);
                        (new KRunnable((task) -> {
                           ItemBuilderGUI.getMainMenu(p, item.buildWithAbilities(), (ItemBuilderGUI.ItemBuilderMenu)null, ItemBuilderGUI.SubMenu.STATS);
                        })).runTaskLater(Skyblock.getPlugin(), 1L);
                        return true;
                     });
                     ((ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p)).setSignClose(true);
                     menu.open(p);
                  }

                  public int getSlot() {
                     return slots[i1];
                  }

                  public ItemStack getItem() {
                     return (new ItemBuilder(stat.getItem())).HideFlags(63).setName(stat.getColor() + stat.getSymbol() + " " + stat.statDisplayName()).setLore(lore).build();
                  }
               });
            }

            pages = new boolean[]{page != 0, size - (page + 1) * 21 > 0};
            int thing = 0;

            while(true) {
               if (thing >= 2) {
                  break label193;
               }

               if (pages[thing]) {
                  items.add(addArrows(p, thing == 1, page, putItem));
               }

               ++thing;
            }
         case ITEM_TYPE:
            item = new CustomItem(putItem);
            ArrayList types = new ArrayList(ItemType.getValuesFullList());
            types.add(new ItemType() {
               public String getName() {
                  return null;
               }

               public String getDisplayName() {
                  return null;
               }

               public boolean includes(CustomItem ci) {
                  return false;
               }

               public ItemStack getItem() {
                  return XMaterial.BARRIER.parseItem();
               }
            });
            size = types.size();
            slots = getCenteredSlots(Math.min(size - page * 21, 21), 22);

            for(thing = 0; thing < slots.length; ++thing) {
               final ItemType type = (ItemType)types.get(thing + page * 21);
               items.add(new GUIClickableItem() {
                  public void run(InventoryClickEvent e) {
                     playSound(p, XSound.ENTITY_PLAYER_LEVELUP, 1.0F);
                     ((ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p)).setSubMenu(ItemBuilderGUI.SubMenu.NONE);
                     item.setType(type.getName() != null ? type : null);
                     ItemBuilderGUI.getMainMenu(p, item.buildWithAbilities(), (ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p));
                  }

                  public int getSlot() {
                     return slots[thing];
                  }

                  public ItemStack getItem() {
                     return (new ItemBuilder(type.getItem())).HideFlags(63).setName(ChatColor.GREEN + type.getDisplayName()).setLore(new String[]{ChatColor.GRAY + "Sets the item type to be a", ChatColor.GRAY + type.getDisplayName() + " item.", "", ChatColor.YELLOW + "Click to change!"}).build();
                  }
               });
            }

            pages = new boolean[]{page != 0, size - (page + 1) * 21 > 0};
            thing = 0;

            while(true) {
               if (thing >= 2) {
                  break label193;
               }

               if (pages[thing]) {
                  items.add(addArrows(p, thing == 1, page, putItem));
               }

               ++thing;
            }
         case ABILITY:
            item = new CustomItem(putItem);
            size = CustomItem.getAllAbilities().size();
            slots = getCenteredSlots(Math.min(size - page * 21, 21), 22);

            for(thing = 0; thing < slots.length; ++thing) {
               final Ability ability = (Ability)(new ArrayList(CustomItem.getAllAbilities().values())).get(thing + page * 21);
               items.add(new GUIClickableItem() {
                  public void run(InventoryClickEvent e) {
                     playSound(p, XSound.ENTITY_PLAYER_LEVELUP, 1.0F);
                     ItemBuilderGUI.getMainMenu(p, e.isLeftClick() ? item.addAbility(ability).buildWithAbilities() : item.removeAbility(ability).buildWithAbilities(), (ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p));
                  }

                  public int getSlot() {
                     return slots[thing];
                  }

                  public ItemStack getItem() {
                     List lore = new ArrayList(ability.getLore(item));
                     lore.addAll(arrayList(new String[]{ChatColor.GRAY + "Equipped: " + ChatColor.GREEN + item.hasAbility(ability.getIdentifier()), ""}));
                     if (item.hasAbility(ability.getIdentifier())) {
                        lore.add(ChatColor.AQUA + "Right-Click to remove!");
                     }

                     lore.add(ChatColor.YELLOW + "Click to add!");
                     return (new ItemBuilder(XMaterial.NETHER_STAR)).setLore(lore).setName(ChatColor.GREEN + capitalize(space(ability.getIdentifier()).replace("_", " "))).build();
                  }
               });
            }

            pages = new boolean[]{page != 0, size - (page + 1) * 21 > 0};
            thing = 0;

            while(true) {
               if (thing >= 2) {
                  break label193;
               }

               if (pages[thing]) {
                  items.add(addArrows(p, thing == 1, page, putItem));
               }

               ++thing;
            }
         case REFORGE:
            item = new CustomItem(putItem);
            size = Reforge.getReforges().size();
            slots = getCenteredSlots(Math.min(size - page * 21, 21), 22);

            for(thing = 0; thing < slots.length; ++thing) {
               final Reforge reforge = (Reforge)Reforge.getReforges().get(thing + page * 21);
               items.add(new GUIClickableItem() {
                  public void run(InventoryClickEvent e) {
                     if (e.isRightClick() && item.getReforgeName().equals(reforge.getName())) {
                        p.sendMessage(ChatColor.GREEN + "Added Reforge " + reforge.getName() + "!");
                        playSound(p, XSound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0F);
                        ItemBuilderGUI.getMainMenu(p, item.cleanseReforge().buildWithAbilities(), (ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p));
                     } else {
                        playSound(p, XSound.BLOCK_ANVIL_USE, 1.0F);
                        p.sendMessage(ChatColor.RED + "Removed Reforge!");
                        ItemBuilderGUI.getMainMenu(p, item.applyReforge(reforge).buildWithAbilities(), (ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p));
                     }

                  }

                  public int getSlot() {
                     return slots[thing];
                  }

                  public ItemStack getItem() {
                     List lore = new ArrayList(ItemBuilderGUI.getStatsLore(reforge.getStats(Rarity.COMMON)));
                     lore.addAll(arrayList(new String[]{ChatColor.GRAY + "Equipped: " + ChatColor.GREEN + item.getReforgeName().equals(reforge.getName()), ""}));
                     if (item.getReforgeName().equals(reforge.getName())) {
                        lore.add(ChatColor.AQUA + "Right-Click to remove!");
                     }

                     lore.add(ChatColor.YELLOW + "Click to change!");
                     return (new ItemBuilder(XMaterial.ANVIL)).setName(ChatColor.GREEN + reforge.getName()).setLore(lore).build();
                  }
               });
            }

            pages = new boolean[]{page != 0, size - (page + 1) * 21 > 0};
            thing = 0;

            while(true) {
               if (thing >= 2) {
                  break label193;
               }

               if (pages[thing]) {
                  items.add(addArrows(p, thing == 1, page, putItem));
               }

               ++thing;
            }
         case SAVE_ITEM:
            item = new CustomItem(putItem);
            items.add(new GUIClickableItem() {
               public void run(InventoryClickEvent e) {
                  try {
                     playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
                     FileConfiguration fc = Files.getCustomItems();

                     int i;
                     for(i = 0; fc.get("items." + i + ".material") != null && XMaterial.matchXMaterial(fc.getString("items." + i + ".material")).isPresent(); ++i) {
                     }

                     ItemBuilderGUI.save(fc, "items." + i + ".", XMaterial.matchXMaterial(item.build()).name(), item, Files.getCustomItemsFile());
                     CustomItemsMenu.loadItems(false);
                     p.sendMessage(ChatColor.GREEN + "Saved!");
                     ((ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p)).setSubMenu(ItemBuilderGUI.SubMenu.NONE);
                     ((ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p)).setPage(0);
                     ItemBuilderGUI.getMainMenu(p, putItem, (ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p));
                  } catch (Throwable var4) {
                     throw var4;
                  }
               }

               public int getSlot() {
                  return 30;
               }

               public ItemStack getItem() {
                  List lore = arrayList(new String[]{ChatColor.GRAY + "Clicking this will save the", ChatColor.GRAY + "current item to the custom", ChatColor.GRAY + "items menu to be picked up", ChatColor.GRAY + "whenever.", ""});
                  lore.addAll(ItemBuilderGUI.getLore(item));
                  lore.addAll(arrayList(new String[]{"", ChatColor.YELLOW + "Click to save!"}));
                  return (new ItemBuilder(XMaterial.CHEST)).setName(ChatColor.GREEN + "Save to Custom Items Menu").setLore(lore).build();
               }
            });
            items.add(new GUIClickableItem() {
               public void run(InventoryClickEvent e) {
                  try {
                     playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
                     FileConfiguration fc = Files.getDefaultItemOverrides();
                     XMaterial mat = XMaterial.matchXMaterial(item.build());
                     ItemBuilderGUI.save(fc, mat.name() + ".", mat.name(), item, Files.getDefaultItemOverridesFile());
                     p.sendMessage(ChatColor.GREEN + "Saved!");
                     ((ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p)).setSubMenu(ItemBuilderGUI.SubMenu.NONE);
                     ((ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p)).setPage(0);
                     ItemBuilderGUI.getMainMenu(p, putItem, (ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p));
                  } catch (Throwable var4) {
                     throw var4;
                  }
               }

               public int getSlot() {
                  return 32;
               }

               public ItemStack getItem() {
                  List lore = arrayList(new String[]{ChatColor.GRAY + "Clicking this will set the", ChatColor.GRAY + "current item to be the default", ChatColor.GRAY + "item for this item type, ", ChatColor.GRAY + "meaning every time this item", ChatColor.GRAY + "picked up for the first time,", ChatColor.GRAY + "it will default to the item", ChatColor.GRAY + "saved by the server.", ""});
                  lore.addAll(ItemBuilderGUI.getLore(item));
                  lore.addAll(arrayList(new String[]{"", ChatColor.YELLOW + "Click to set as default!"}));
                  return (new ItemBuilder(XMaterial.GRASS_BLOCK)).setName(ChatColor.GREEN + "Set as Default Item").setLore(lore).build();
               }
            });
         }
      } else {
         if (hashMap.containsKey(p)) {
            ((ItemBuilderGUI.ItemBuilderMenu)hashMap.get(p)).setSubMenu(ItemBuilderGUI.SubMenu.NONE);
         }

         items.add(GUIClickableItem.cantPickup((new ItemBuilder(XMaterial.RED_DYE)).setName(ChatColor.RED + "Not a valid item").setLore(new String[]{ChatColor.GRAY + "Place any item in the slot to be", ChatColor.GRAY + "made into a Koji Skyblock item", "", ChatColor.RED + "" + ChatColor.BOLD + "WARNING: THIS WILL RESET LORE", ChatColor.RED + "" + ChatColor.BOLD + "OF ITEM IF NOT VALID", ChatColor.RED + "" + ChatColor.BOLD + "KOJI SKYBLOCK ITEM"}).build(), 31));
      }

      if (subMenu != ItemBuilderGUI.SubMenu.NONE) {
         items.add(new GUIClickableItem() {
            public void run(InventoryClickEvent e) {
               ((ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p)).setSubMenu(ItemBuilderGUI.SubMenu.NONE);
               ((ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p)).setPage(0);
               ItemBuilderGUI.getMainMenu(p, putItem, (ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p));
            }

            public int getSlot() {
               return 48;
            }

            public ItemStack getItem() {
               return (new ItemBuilder(XMaterial.ARROW)).setName(ChatColor.GREEN + "Go Back").setLore(new String[]{ChatColor.GRAY + "To Main Menu"}).build();
            }
         });
      } else {
         items.add(GUIClickableItem.getBorderItem(48));
      }

      items.add(new GUIClickableItem() {
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
      if (isValidItem(putItem) && (new CustomItem(putItem)).hasKey("id")) {
         items.add(new GUIClickableItem() {
            public void run(InventoryClickEvent e) {
               if (!(new CustomItem(putItem)).getString("id").equals("null")) {
                  ((ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p)).setSubMenu(ItemBuilderGUI.SubMenu.SAVE_ITEM);
                  ItemBuilderGUI.getMainMenu(p, putItem, (ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p));
               } else {
                  p.sendMessage(ChatColor.RED + "You cannot save an item with no ID! Please assign an ID before continuing!");
               }

            }

            public int getSlot() {
               return 50;
            }

            public ItemStack getItem() {
               return (new ItemBuilder(XMaterial.HOPPER)).setName(ChatColor.GREEN + "Save Item").setLore(new String[]{ChatColor.GRAY + "Save your item to the", ChatColor.GRAY + "custom-items.yml file to be", ChatColor.GRAY + "picked up whenever", "", ChatColor.YELLOW + "Click to save!"}).build();
            }
         });
      } else {
         items.add(GUIClickableItem.getBorderItem(50));
      }

      return items;
   }

   private static List getLore(CustomItem item) {
      List lore = arrayList(new String[]{ChatColor.GRAY + "The following attributes will", ChatColor.GRAY + "be saved:", ChatColor.GRAY + "Rarity: " + item.getRarity().getDisplay(), ChatColor.GRAY + "Name: " + item.getName()});
      if (item.getAbilities().isEmpty()) {
         lore.add(ChatColor.GRAY + "Abilities: " + ChatColor.GOLD + "None");
      } else {
         lore.add(ChatColor.GRAY + "Abilities: ");
         Iterator var2 = item.getAbilities().iterator();

         while(var2.hasNext()) {
            Ability ability = (Ability)var2.next();
            lore.add(ChatColor.GRAY + " - " + ChatColor.GOLD + capitalize(space(ability.getIdentifier()).replace("_", " ")));
         }
      }

      lore.addAll(arrayList(new String[]{ChatColor.GRAY + "Allows Enchants: " + ChatColor.GOLD + item.doesAllowEnchants(), ChatColor.GRAY + "Item Type: " + ChatColor.GOLD + item.getType(), ChatColor.GRAY + "Sub Type: " + ChatColor.GOLD + item.getExtraType()}));
      if (item.getStats(false).isEmpty()) {
         lore.add(ChatColor.GRAY + "Stats: " + ChatColor.GOLD + "None");
      } else {
         lore.add(ChatColor.GRAY + "Stats: ");
         item.getStats(false).forEach((a, b) -> {
            lore.add(ChatColor.GRAY + " - " + a.statDisplayName() + ": " + ChatColor.GOLD + item.getStat(a, false));
         });
      }

      return lore;
   }

   private static void save(FileConfiguration fc, String path, String mat, CustomItem item, File file) {
      try {
         fc.set(path + "material", mat);
         if (!item.getTexture().equals("")) {
            fc.set(path + "texture", item.getTexture());
         }

         if (item.getColor() != null) {
            fc.set(path + "color", item.getColor().asRGB());
         }

         fc.set(path + "rarity", item.getRarity().name());
         fc.set(path + "name", item.getName().replace('§', '&'));
         if (!item.getType().equals("")) {
            fc.set(path + "type", item.getType());
         }

         if (!item.getExtraType().equals("")) {
            fc.set(path + "extraType", item.getExtraType());
         }

         if (item.getGearScore() != 0) {
            fc.set(path + "stats.gearscore", item.getGearScore());
         }

         item.getStats(false).forEach((a, b) -> {
            fc.set(path + "stats." + a.getPlaceholderTag(), b);
         });
         if (!item.getAbilityNames().isEmpty()) {
            fc.set(path + "abilities", item.getAbilityNames());
         }

         if (item.doesAllowEnchants()) {
            fc.set(path + "allowEnchants", item.doesAllowEnchants());
         }

         if (item.hasEnchantGlow()) {
            fc.set(path + "enchantGlow", item.hasEnchantGlow());
         }

         if (item.canBeStacked()) {
            fc.set(path + "canBeStacked", item.canBeStacked());
         }

         if (item.canBePlaced()) {
            fc.set(path + "canBePlaced", item.canBePlaced());
         }

         fc.set(path + "id", item.getID());
         fc.save(file);
         fc.load(file);
      } catch (Throwable var6) {
         throw var6;
      }
   }

   private static ArrayList getStatsLore(StatMap map) {
      ArrayList lore = new ArrayList();
      Iterator var2 = map.keySet().iterator();

      while(var2.hasNext()) {
         Stats s = (Stats)var2.next();
         double baseStat = round(map.get(s), 1);
         if (baseStat != 0.0D) {
            lore.add(ChatColor.GRAY + s.statDisplayName() + ": " + s.getDefensive() + s.format(baseStat));
         }
      }

      if (!lore.isEmpty()) {
         lore.add("");
      }

      return lore;
   }

   public static GUIClickableItem addArrows(final Player p, boolean right, int page, final ItemStack putItem) {
      final int index;
      final byte slot;
      final String next;
      final String texture;
      if (right) {
         next = "Next";
         texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTU2YTM2MTg0NTllNDNiMjg3YjIyYjdlMjM1ZWM2OTk1OTQ1NDZjNmZjZDZkYzg0YmZjYTRjZjMwYWI5MzExIn19fQ==";
         slot = 53;
         index = page + 1;
      } else {
         next = "Previous";
         texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19";
         slot = 45;
         index = page - 1;
      }

      return new GUIClickableItem() {
         public int getSlot() {
            return slot;
         }

         public ItemStack getItem() {
            return (new ItemBuilder(XMaterial.PLAYER_HEAD)).setName("§a" + next + " Page").setLore(new String[]{ChatColor.YELLOW + "Page " + (index + 1)}).setTexture(texture).build();
         }

         public void run(InventoryClickEvent e) {
            playSound((Player)e.getWhoClicked(), XSound.UI_BUTTON_CLICK, 1.0F);
            ((ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p)).setPage(index);
            ItemBuilderGUI.getMainMenu(p, putItem, (ItemBuilderGUI.ItemBuilderMenu)ItemBuilderGUI.hashMap.get(p));
         }
      };
   }

   public static enum SubMenu {
      NONE("Item Builder"),
      RARITY("Set Rarity"),
      STATS("Set Stats"),
      ITEM_TYPE("Set Item Type"),
      ABILITY("Set Abilities"),
      REFORGE("Add Reforge"),
      SAVE_ITEM("Save Item");

      private final String title;

      private SubMenu(String title) {
         this.title = title;
      }

      public String getTitle() {
         return this.title;
      }
   }

   public static class ItemBuilderMenu extends PlayerInstance {
      private boolean movingPages = false;
      private ItemBuilderGUI.SubMenu subMenu;
      private int page;
      private boolean signClose;

      public ItemBuilderMenu(Player p, KInventory base) {
         super(p, base);
         this.subMenu = ItemBuilderGUI.SubMenu.NONE;
         this.page = 0;
         this.signClose = false;
      }

      public void setSubMenu(ItemBuilderGUI.SubMenu subMenu) {
         this.subMenu = subMenu;
         this.setTitle(subMenu.getTitle());
      }

      public ItemStack getItem() {
         return this.getInventory().getItem(13);
      }

      public static KInventory getBase(final Player p) {
         return new KInventory("Item Builder", 54) {
            public Inventory getConstantInventory() {
               Inventory inv = this.getBaseCreatedInventory();
               fill(inv, GUIClickableItem.getBorderItem(0));
               inv.setItem(13, (ItemStack)null);
               ItemBuilderGUI.getRow(p, (ItemStack)null, ItemBuilderGUI.SubMenu.NONE, 0).forEach((gui) -> {
                  set(inv, gui);
               });
               return inv;
            }

            public List getIgnoreSlots() {
               return arrayList(new Integer[]{13});
            }
         };
      }

      public boolean isMovingPages() {
         return this.movingPages;
      }

      public void setMovingPages(boolean movingPages) {
         this.movingPages = movingPages;
      }

      public ItemBuilderGUI.SubMenu getSubMenu() {
         return this.subMenu;
      }

      public int getPage() {
         return this.page;
      }

      public void setPage(int page) {
         this.page = page;
      }

      public boolean isSignClose() {
         return this.signClose;
      }

      public void setSignClose(boolean signClose) {
         this.signClose = signClose;
      }
   }
}
