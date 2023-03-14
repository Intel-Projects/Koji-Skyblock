package koji.skyblock.item.creation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import koji.developerkit.commands.KCommand;
import koji.developerkit.gui.GUIClickableItem;
import koji.developerkit.inventory.KInventory;
import koji.developerkit.inventory.KInventory.PlayerInstance;
import koji.developerkit.runnable.KRunnable;
import koji.developerkit.utils.ItemBuilder;
import koji.developerkit.utils.xseries.XMaterial;
import koji.developerkit.utils.xseries.XSound;
import koji.skyblock.Skyblock;
import koji.skyblock.commands.Messages;
import koji.skyblock.files.Config;
import koji.skyblock.files.Files;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.utils.SignMenuFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CustomItemsMenu extends KCommand {
   private static List createdItems = new ArrayList();

   public static void loadItems() {
      loadItems(true);
   }

   public static void loadItems(boolean log) {
      createdItems = new ArrayList();
      if (log) {
         Skyblock.getPlugin().getLogger().log(Level.INFO, "Loading Custom Items...");
      }

      Iterator var1 = getKeys(Files.getCustomItems(), "items.", false).iterator();

      while(var1.hasNext()) {
         String key = (String)var1.next();

         try {
            if (!XMaterial.matchXMaterial(Files.getCustomItems().getString(key + ".material")).isPresent()) {
               if (log) {
                  Skyblock.getPlugin().getLogger().log(Level.WARNING, "Item #" + (String)getLast(key.split("\\.")) + " was skipped because the material wasn't valid!");
               }
            } else {
               createdItems.add(CustomItem.createItem(Files.getCustomItems(), key));
            }
         } catch (Exception var4) {
            if (log) {
               var4.printStackTrace();
            }
         }
      }

   }

   public static PlayerInstance getMainInventory(Player p, PlayerInstance invInstance, int page, List itemStacks) {
      return getMainInventory(p, invInstance, page, itemStacks, false, "");
   }

   public static PlayerInstance getMainInventory(final Player p, final PlayerInstance invInstance, final int page, final List items, final boolean searched, final String filter) {
      if (invInstance == null) {
         invInstance = new PlayerInstance(p, new KInventory("Custom Items - Page " + (page + 1), 54) {
            public Inventory getConstantInventory() {
               Inventory inv = this.getBaseCreatedInventory();
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
               if (Config.getItemBuilderEnabled() && p.hasPermission("koji.skyblock.itembuilder")) {
                  set(inv, new GUIClickableItem() {
                     public void run(InventoryClickEvent inventoryClickEvent) {
                        (new KRunnable((task) -> {
                           p.openInventory(ItemBuilderGUI.getMainMenu(p, (ItemStack)null, (ItemBuilderGUI.ItemBuilderMenu)null).getInventory());
                        })).runTaskLater(Skyblock.getPlugin(), 1L);
                     }

                     public int getSlot() {
                        return 47;
                     }

                     public ItemStack getItem() {
                        return (new ItemBuilder(XMaterial.ANVIL)).setName(ChatColor.GREEN + "Item Creator").setLore(new String[]{ChatColor.GRAY + "Create custom items on the fly", ChatColor.GRAY + "and add them to the custom", ChatColor.GRAY + "items menu.", "", ChatColor.YELLOW + "Click to open!"}).build();
                     }
                  });
               }

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
                           PlayerInstance playerInstance = CustomItemsMenu.getMainInventory(p, (PlayerInstance)null, 0, CustomItemsMenu.getItemsByFilter(combined), !combined.equals(""), combined);
                           (new KRunnable((task) -> {
                              p.openInventory(playerInstance.getInventory());
                           })).runTaskLater(Skyblock.getPlugin(), 1L);
                           return true;
                        });
                        menu.open((Player)e.getWhoClicked());
                     } else {
                        p.openInventory(CustomItemsMenu.getMainInventory(p, (PlayerInstance)null, 0, CustomItemsMenu.createdItems).getInventory());
                     }

                  }

                  public int getSlot() {
                     return 48;
                  }

                  public ItemStack getItem() {
                     ArrayList lore;
                     if (!searched) {
                        lore = arrayList(new String[]{ChatColor.GRAY + "Find items by name, type, lore", ChatColor.GRAY + "or enchants.", "", ChatColor.YELLOW + "Click to search!"});
                     } else {
                        lore = arrayList(new String[]{ChatColor.GRAY + "Find items by name, type, lore", ChatColor.GRAY + "or enchants.", "", ChatColor.GRAY + "Filtered: " + ChatColor.YELLOW + filter, "", ChatColor.AQUA + "Rick-Click to clear!", ChatColor.YELLOW + "Click to edit filter!"});
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
      } else {
         invInstance.reset("Custom Items - Page " + (page + 1));
      }

      if (page > 0) {
         addArrows(false, page, invInstance, items);
      }

      if (items.size() - (page + 1) * 28 > 0) {
         addArrows(true, page, invInstance, items);
      }

      for(final int index = 0; index < 28; ++index) {
         try {
            if (invInstance.getInventory().firstEmpty() != -1 && getOrDefault(items, index + 28 * page, (Object)null) != null) {
               invInstance.set(new GUIClickableItem() {
                  public void run(InventoryClickEvent e) {
                     Player p = (Player)e.getWhoClicked();
                     CustomItem ci = new CustomItem(this.getItem());
                     GrabCustomItemEvent event = new GrabCustomItemEvent(p, ci);
                     Bukkit.getPluginManager().callEvent(event);
                     if (!event.isCancelled()) {
                        addItemUnlessFull(p.getInventory(), event.getItem().buildWithAbilities());
                     }

                  }

                  public int getSlot() {
                     return invInstance.getInventory().firstEmpty();
                  }

                  public ItemStack getItem() {
                     return (ItemStack)items.get(index + 28 * page);
                  }
               });
            }
         } catch (Exception var9) {
            var9.printStackTrace();
         }
      }

      return invInstance;
   }

   public static List getItemsByFilter(String key) {
      List trimmedItems = new ArrayList();
      createdItems.forEach((item) -> {
         if (item.getItemMeta() != null && (item.getItemMeta().getDisplayName().toLowerCase().contains(key.toLowerCase()) || item.getItemMeta().getLore().stream().anyMatch((s) -> {
            return s.toLowerCase().contains(key.toLowerCase());
         }))) {
            trimmedItems.add(item);
         }

      });
      return trimmedItems;
   }

   private static void addArrows(boolean right, int page, final PlayerInstance playerInstance, final List items) {
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

      set(playerInstance.getInventory(), new GUIClickableItem() {
         public int getSlot() {
            return slot;
         }

         public ItemStack getItem() {
            return (new ItemBuilder(XMaterial.PLAYER_HEAD)).setName("§a" + next + " Page").setLore(new String[]{ChatColor.YELLOW + "Page " + (index + 1)}).setTexture(texture).build();
         }

         public void run(InventoryClickEvent e) {
            playSound((Player)e.getWhoClicked(), XSound.UI_BUTTON_CLICK, 1.0F);
            CustomItemsMenu.getMainInventory(playerInstance.getPlayer(), playerInstance, index, items);
         }
      });
   }

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (sender instanceof Player) {
         Player p = (Player)sender;
         if (args.length != 0) {
            p.sendMessage(Messages.COMMAND_CI_USAGE.getMessage());
            return true;
         }

         p.openInventory(getMainInventory(p, (PlayerInstance)null, 0, createdItems).getInventory());
      } else {
         sender.sendMessage(Messages.NOT_PLAYER.getMessage());
      }

      return false;
   }
}
