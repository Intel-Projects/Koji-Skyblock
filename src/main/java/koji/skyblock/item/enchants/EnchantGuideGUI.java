package koji.skyblock.item.enchants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import koji.developerkit.gui.GUIClickableItem;
import koji.developerkit.inventory.KInventory;
import koji.developerkit.inventory.KInventory.PlayerInstance;
import koji.developerkit.runnable.KRunnable;
import koji.developerkit.utils.ItemBuilder;
import koji.developerkit.utils.xseries.XMaterial;
import koji.developerkit.utils.xseries.XSound;
import koji.skyblock.Skyblock;
import koji.skyblock.item.ItemType;
import koji.skyblock.item.utils.SignMenuFactory;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EnchantGuideGUI extends EnchantTableGUI {
   static final HashMap playerInstanceMap = new HashMap();

   public static void openEnchantGuide(Player p, Block b) {
      p.openInventory(getEnchantGuide(0, p, (PlayerInstance)null, b, Enchant.getRegisteredEnchants(), false, "", EnchantGuideGUI.Filter.NONE).getInventory());
   }

   public static PlayerInstance getEnchantGuide(final int page, final Player p, final PlayerInstance instance, final Block block, final List enchants, final boolean searched, final String filter, final EnchantGuideGUI.Filter eyeFilter) {
      int maxPages = (int)Math.ceil((double)enchants.size() / 28.0D);
      if (instance == null) {
         instance = new PlayerInstance(p, new KInventory(getTitle(page, maxPages), 54) {
            public Inventory getConstantInventory() {
               Inventory inv = this.getBaseCreatedInventory();
               setBorder(inv);
               setBorder(inv);
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
               set(inv, new GUIClickableItem() {
                  public void run(InventoryClickEvent e) {
                     playSound((Player)e.getWhoClicked(), XSound.UI_BUTTON_CLICK, 1.0F);
                     e.getWhoClicked().openInventory(EnchantTableGUI.openInventory((ItemStack)null, block, p, false));
                  }

                  public int getSlot() {
                     return 48;
                  }

                  public ItemStack getItem() {
                     return (new ItemBuilder(XMaterial.ARROW)).setName(ChatColor.GREEN + "Go Back").setLore(new String[]{ChatColor.GRAY + "To Enchant Item"}).build();
                  }
               });
               set(inv, new GUIClickableItem() {
                  public void run(InventoryClickEvent e) {
                     if (!e.getClick().isRightClick()) {
                        SignMenuFactory.Menu menu = SignMenuFactory.getFactory().newMenu(arrayList(new String[]{"", "", "^^^^^^^^^^^^^^^", "Enter query"})).reopenIfFail(false).response((player, strings) -> {
                           String combined = strings[0];
                           if (!combined.endsWith(" ")) {
                              combined = combined + " ";
                           }

                           combined = combined + strings[1];
                           combined = combined.trim();
                           PlayerInstance playerInstance = EnchantGuideGUI.getEnchantGuide(0, p, (PlayerInstance)null, block, EnchantGuideGUI.getEnchantsByFilter(combined, eyeFilter), true, combined, eyeFilter);
                           (new KRunnable((task) -> {
                              p.openInventory(playerInstance.getInventory());
                           })).runTaskLater(Skyblock.getPlugin(), 1L);
                           return true;
                        });
                        menu.open((Player)e.getWhoClicked());
                     } else {
                        playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
                        p.openInventory(EnchantGuideGUI.getEnchantGuide(0, p, (PlayerInstance)null, block, Enchant.getRegisteredEnchants(), false, "", eyeFilter).getInventory());
                     }

                  }

                  public int getSlot() {
                     return 50;
                  }

                  public ItemStack getItem() {
                     ArrayList lore;
                     if (!searched) {
                        lore = arrayList(new String[]{ChatColor.GRAY + "Find specific enchants in the", ChatColor.GRAY + "guide.", "", ChatColor.YELLOW + "Click to search!"});
                     } else {
                        lore = arrayList(new String[]{ChatColor.GRAY + "Find specific enchants in the", ChatColor.GRAY + "guide.", "", ChatColor.GRAY + "Filtered: " + ChatColor.YELLOW + filter, "", ChatColor.AQUA + "Rick-Click to clear!", ChatColor.YELLOW + "Click to edit filter!"});
                     }

                     return (new ItemBuilder(XMaterial.OAK_SIGN)).setName(ChatColor.GREEN + "Search").setLore(lore).build();
                  }
               });
               return inv;
            }

            public List getIgnoreSlots() {
               return new ArrayList();
            }
         });
         playerInstanceMap.put(p, instance);
      } else {
         instance.reset(getTitle(page, maxPages));
      }

      final PlayerInstance finalInstance = instance;
      instance.set(new GUIClickableItem() {
         public void run(InventoryClickEvent e) {
            playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
            EnchantGuideGUI.getEnchantGuide(0, p, instance, block, EnchantGuideGUI.getEnchantsByFilter(filter, EnchantGuideGUI.getNext(e.isRightClick(), eyeFilter)), searched, filter, EnchantGuideGUI.getNext(e.isRightClick(), eyeFilter));
         }

         public int getSlot() {
            return 51;
         }

         public ItemStack getItem() {
            List lore = arrayList(new String[]{""});
            Arrays.stream(EnchantGuideGUI.Filter.values()).forEach((f) -> {
               lore.add(f.getLine(eyeFilter));
            });
            lore.add("");
            if (eyeFilter != EnchantGuideGUI.Filter.NONE) {
               lore.add(ChatColor.AQUA + "Right-Click to go backwards!");
            }

            lore.add(ChatColor.YELLOW + "Click to switch!");
            return (new ItemBuilder(XMaterial.ENDER_EYE)).setName(ChatColor.GREEN + "Ultimate Filter").setLore(lore).build();
         }
      });
      boolean[] boos = new boolean[]{enchants.size() - (page + 1) * 28 > 0, page != 0};

      final int i;
      for(i = 0; i < boos.length; ++i) {
         boolean boo = boos[i];
         if (boo) {
            instance.set(new GUIClickableItem() {
               final int add = i == 0 ? 1 : -1;

               public void run(InventoryClickEvent e) {
                  playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
                  EnchantGuideGUI.getEnchantGuide(page + this.add, p, finalInstance, block, enchants, searched, filter, eyeFilter);
               }

               public int getSlot() {
                  return i == 0 ? 53 : 45;
               }

               public ItemStack getItem() {
                  return (new ItemBuilder(XMaterial.ARROW)).setName(ChatColor.GREEN + (i == 0 ? "Next" : "Previous") + " Page").setLore(new String[]{ChatColor.YELLOW + "Page " + (page + 1 + this.add)}).build();
               }
            });
         }
      }

      for(i = 0; i < 28; ++i) {
         Enchant enchant = (Enchant)getOrDefault(enchants, page * 28 + i, (Object)null);
         if (enchant == null) {
            break;
         }

         String name = ChatColor.GREEN + enchant.getDisplayName();
         if (enchant.isUltimate()) {
            name = bold(ChatColor.LIGHT_PURPLE + enchant.getDisplayName());
         }

         instance.set(GUIClickableItem.cantPickup((new ItemBuilder(XMaterial.ENCHANTED_BOOK)).setName(name + " " + toRomanNumeral(enchant.getMaxLevel())).setLore(getEnchantGuideLore(enchant)).build(), instance.getInventory().firstEmpty()));
      }

      return instance;
   }

   private static ArrayList getEnchantGuideLore(Enchant enchant) {
      ArrayList lore = new ArrayList(enchant.getLore(enchant.getHighestLevel()));
      if (enchant.canAppearInEnchantTable() || !enchant.getSources().isEmpty()) {
         lore.addAll(arrayList(new String[]{"", ChatColor.GOLD + "Sources:"}));
         if (enchant.canAppearInEnchantTable()) {
            lore.add(ChatColor.GRAY + " - Enchantment Table (" + ChatColor.GREEN + "I-" + toRomanNumeral(enchant.getMaxLevel()) + ChatColor.GRAY + ")");
         }

         enchant.getSources().forEach((a, b) -> {
            int rangeHigh = Math.max((Integer)b.getFirst(), (Integer)b.getSecond());
            int rangeLow = Math.max((Integer)b.getFirst(), (Integer)b.getSecond());
            lore.add(ChatColor.GRAY + " - " + a + "(" + ChatColor.GREEN + rangeLow + "-" + rangeHigh + ChatColor.GRAY + ")");
         });
      }

      lore.addAll(arrayList(new String[]{"", ChatColor.GOLD + "Applied To:"}));
      ItemType.getValuesFullList().forEach((a) -> {
         if (enchant.getTargets().contains(a)) {
            lore.add(ChatColor.GRAY + " - " + ChatColor.WHITE + a.getDisplayName());
         }

      });
      if (enchant.isUltimate()) {
         lore.addAll(arrayList(new String[]{"", ChatColor.RED + "You can only have 1 Ultimate", ChatColor.RED + "Enchantment on an item!"}));
      }

      if (!enchant.getExtraRequirements().isEmpty() && enchant.getBookshelfPowerRequirement() != 0) {
         lore.addAll(arrayList(new String[]{"", ChatColor.GOLD + "Requirements:"}));
         enchant.getExtraRequirements().forEach((a) -> {
            lore.add(ChatColor.GRAY + " - " + a);
         });
         if (enchant.getBookshelfPowerRequirement() != 0) {
            lore.add(ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + enchant.getBookshelfPowerRequirement() + " Bookshelf Power");
         }
      }

      if (enchant.getConflicts() != null && !enchant.getConflicts().isEmpty()) {
         lore.addAll(arrayList(new String[]{"", ChatColor.GOLD + "Conflicts:"}));
         enchant.getConflicts().forEach((c) -> {
            try {
               Enchant conflict = (Enchant)c.newInstance();
               lore.add(ChatColor.GRAY + " - " + ChatColor.RED + conflict.getDisplayName());
            } catch (IllegalAccessException | InstantiationException var3) {
            }

         });
      }

      return lore;
   }

   private static String getTitle(int currentPage, int maxPages) {
      String title = "";
      if (maxPages > 1) {
         title = title + "(" + (currentPage + 1) + "/" + maxPages + ") ";
      }

      title = title + "Enchantments Guide";
      return title;
   }

   public static List getEnchantsByFilter(String filter, EnchantGuideGUI.Filter eyeFilter) {
      List trimmedItems = new ArrayList();
      Enchant.getRegisteredEnchants().forEach((item) -> {
         if (item.getDisplayName().toLowerCase().contains(filter.toLowerCase())) {
            trimmedItems.add(item);
         } else {
            Iterator var3 = item.getLore(1).iterator();

            while(var3.hasNext()) {
               String s = (String)var3.next();
               if (s.toLowerCase().contains(filter.toLowerCase())) {
                  trimmedItems.add(item);
                  break;
               }
            }
         }

      });
      if (eyeFilter == EnchantGuideGUI.Filter.ULTIMATE) {
         trimmedItems.removeIf((e) -> {
            return !e.isUltimate();
         });
      }

      if (eyeFilter == EnchantGuideGUI.Filter.NORMAL) {
         trimmedItems.removeIf(Enchant::isUltimate);
      }

      return trimmedItems;
   }

   public static EnchantGuideGUI.Filter getNext(boolean reverse, EnchantGuideGUI.Filter currentFilter) {
      switch(currentFilter) {
      case NORMAL:
         return reverse ? EnchantGuideGUI.Filter.NONE : EnchantGuideGUI.Filter.ULTIMATE;
      case ULTIMATE:
         return reverse ? EnchantGuideGUI.Filter.NORMAL : EnchantGuideGUI.Filter.NONE;
      default:
         return reverse ? EnchantGuideGUI.Filter.ULTIMATE : EnchantGuideGUI.Filter.NORMAL;
      }
   }

   public static HashMap getPlayerInstanceMap() {
      return playerInstanceMap;
   }

   public static enum Filter {
      NONE(ChatColor.DARK_GRAY, "No Filter"),
      NORMAL(ChatColor.AQUA, "Normal Only"),
      ULTIMATE(ChatColor.LIGHT_PURPLE, "Ultimate Only");

      private ChatColor color;
      private String line;

      public String getLine(EnchantGuideGUI.Filter current) {
         String prefix = current == this ? this.color + "► " : ChatColor.GRAY + "";
         return prefix + this.line;
      }

      private Filter(ChatColor color, String line) {
         this.color = color;
         this.line = line;
      }
   }
}
