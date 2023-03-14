package koji.skyblock.pets.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.stream.Collectors;
import koji.developerkit.commands.KCommand;
import koji.developerkit.gui.GUIClickableItem;
import koji.developerkit.runnable.KRunnable;
import koji.developerkit.utils.ItemBuilder;
import koji.developerkit.utils.xseries.XMaterial;
import koji.developerkit.utils.xseries.XSound;
import koji.skyblock.Skyblock;
import koji.skyblock.commands.Messages;
import koji.skyblock.files.Config;
import koji.skyblock.files.Files;
import koji.skyblock.files.data.PlayerData;
import koji.skyblock.files.pets.PetData;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.Rarity;
import koji.skyblock.pets.Pet;
import koji.skyblock.pets.PetInstance;
import koji.skyblock.player.PClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class OpenPetsMenuCMD extends KCommand {
   private static final ArrayList playersWithPetMenuOpen = new ArrayList();

   public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
      if (Config.getPetsEnabled()) {
         if (!(sender instanceof Player)) {
            return true;
         }

         Player p = (Player)sender;
         String msg = handleOpenPetInventory(p, 0);
         if (msg == null) {
            return true;
         }

         if (msg.equals("unknownCMD")) {
            p.sendMessage("§cUnknown Command");
            p.sendMessage("§cUsage: /pets");
            return false;
         }

         p.sendMessage(msg);
      } else {
         sender.sendMessage(YamlConfiguration.loadConfiguration(new File("spigot.yml")).getString("messages.unknown-command"));
      }

      return true;
   }

   private static String handleOpenPetInventory(Player p, int page) {
      p.openInventory(getPetInventory(p, page));
      playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
      return null;
   }

   private static Inventory getPetInventory(final Player p, final int page) {
      final PetInstance petInstance = PClass.getPlayer(p).getPetInstance();
      PetData petData = PetData.getPetData();
      List pets = petData.getPets(p);
      final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, pets.size() > 28 ? "(" + (page + 1) + "/" + (int)Math.ceil((double)pets.size() / 28.0D) + ") Pets" : "Pets");
      if (petInstance.isActive()) {
         petInstance.save();
      }

      setBorder(inv);
      set(inv, GUIClickableItem.cantPickup((new ItemBuilder(XMaterial.BONE)).setName(ChatColor.GREEN + "Pets").setLore(new String[]{ChatColor.GRAY + "View and manage all of your", ChatColor.GRAY + "Pets.", "", ChatColor.GRAY + "Level up your pets faster by", ChatColor.GRAY + "gaining skill xp in their favorite", ChatColor.GRAY + "skill!", "", ChatColor.GRAY + "Selected pet: " + petInstance.getName()}).build(), 4));
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
      final AtomicBoolean converting = new AtomicBoolean(false);
      set(inv, new GUIClickableItem() {
         public void run(InventoryClickEvent e) {
            converting.set(!converting.get());
            set(inv, this);
         }

         public int getSlot() {
            return 50;
         }

         public ItemStack getItem() {
            XMaterial mat = converting.get() ? XMaterial.LIME_DYE : XMaterial.GRAY_DYE;
            return (new ItemBuilder(mat)).setName(ChatColor.RED + "Convert Pet to Item").setLore(new String[]{ChatColor.GRAY + "Enabled this setting and click", ChatColor.GRAY + "any pet to convert it to an", ChatColor.GRAY + "item.", "", converting.get() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"}).build();
         }
      });
      set(inv, new GUIClickableItem() {
         boolean canSee = PlayerData.getPlayerData().canSeePets(p);
         String prefix;

         {
            this.prefix = !this.canSee ? "§a" : "§c";
         }

         public void run(InventoryClickEvent e) {
            petInstance.setVisible(!this.canSee);
            set(inv, this);
            playSound(p, XSound.BLOCK_NOTE_BLOCK_PLING, 2.0F);
            p.sendMessage(this.prefix + "Hide Pets is now " + (!this.canSee ? "enabled!" : "disabled!"));
         }

         public int getSlot() {
            return 51;
         }

         public ItemStack getItem() {
            this.canSee = PlayerData.getPlayerData().canSeePets(p);
            String prefix;
            String hidden;
            String hide;
            if (!this.canSee) {
               prefix = "§c";
               hidden = "hidden";
               hide = "show";
            } else {
               prefix = "§a";
               hidden = "shown";
               hide = "hide";
            }

            this.prefix = prefix;
            return (new ItemBuilder(XMaterial.STONE_BUTTON)).setName(prefix + "Hide Pets").setLore(new String[]{ChatColor.GRAY + "Hide all pets which are little", ChatColor.GRAY + "heads from being visible in the", ChatColor.GRAY + "world.", "", ChatColor.GRAY + "Pet effects remain active.", "", ChatColor.GRAY + "Currently: " + prefix + "Pets " + hidden + "!", ChatColor.GRAY + "Selected pet: " + petInstance.getName(), "", ChatColor.YELLOW + "Click to " + hide + "!"}).build();
         }
      });
      pets = pets.size() <= 1 ? pets : pets.subList(28 * page, Math.min(pets.size() - 1, 27 + 28 * page));
      boolean[] boos = new boolean[]{page != 0, pets.size() == 28};

      for(final int finalI = 0; finalI < 2; ++finalI) {
         if (boos[finalI]) {
            set(inv, new GUIClickableItem() {
               final String prefix = finalI == 0 ? "Previous" : "Next";
               final int nextPage = finalI == 0 ? -1 : 1;

               public void run(InventoryClickEvent e) {
                  OpenPetsMenuCMD.handleOpenPetInventory(p, page + this.nextPage);
               }

               public int getSlot() {
                  return finalI == 0 ? 45 : 53;
               }

               public ItemStack getItem() {
                  return (new ItemBuilder(XMaterial.ARROW)).setName(ChatColor.GREEN + this.prefix + " Page").setLore(new String[]{ChatColor.YELLOW + "Page " + (page + this.nextPage)}).build();
               }
            });
         }
      }

      Iterator var18 = pets.iterator();

      while(true) {
         while(var18.hasNext()) {
            final String key = (String)var18.next();
            Rarity r = petData.getRarity(p, key);
            int level = petData.getLevel(p, key);
            String type = petData.getType(p, key);
            double currentXP = petData.getCurrentExp(p, key);
            String skin = petData.getSkin(p, key);
            Pet pet = Pet.matchFromType(type);
            if (pet != null) {
               final ItemStack petItem = pet.getItem(r, level, currentXP, skin, true, petInstance.isActive() && petInstance.getUuid().equalsIgnoreCase(key));
               set(inv, new GUIClickableItem() {
                  public void run(InventoryClickEvent e) {
                     try {
                        if (XMaterial.matchXMaterial(e.getInventory().getItem(50)).equals(XMaterial.LIME_DYE)) {
                           if (p.getInventory().firstEmpty() == -1) {
                              p.sendMessage(Messages.FULL_INVENTORY.getMessage());
                           } else {
                              if (petInstance.isActive() && petInstance.getUuid().equalsIgnoreCase(key)) {
                                 petInstance.clear();
                                 p.openInventory(OpenPetsMenuCMD.getPetInventory(p, page));
                              }

                              p.getInventory().addItem(new ItemStack[]{petInstance.getItemFromUUID(key)});
                              playSound(p, XSound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F);
                              p.sendMessage(ChatColor.GREEN + "Converted Pet to Item!");
                              converting.set(!converting.get());
                              p.openInventory(OpenPetsMenuCMD.getPetInventory(p, page));
                           }

                        } else if (petInstance.isActive() && petInstance.getUuid().equalsIgnoreCase(key)) {
                           petInstance.clear();
                           if (Config.willClosePetMenuOnChange()) {
                              (new KRunnable((task) -> {
                                 p.closeInventory();
                              })).runTaskLater(Skyblock.getPlugin(), 1L);
                           } else {
                              p.openInventory(OpenPetsMenuCMD.getPetInventory(p, page));
                           }

                           playSound(p, XSound.ENTITY_ITEM_PICKUP, 1.0F);
                        } else {
                           if (petInstance.isActive()) {
                              petInstance.clear();
                           }

                           petInstance.set(key);
                           petInstance.refreshVisibility();
                           if (Config.willClosePetMenuOnChange()) {
                              (new KRunnable((task) -> {
                                 p.closeInventory();
                              })).runTaskLater(Skyblock.getPlugin(), 1L);
                           } else {
                              p.openInventory(OpenPetsMenuCMD.getPetInventory(p, page));
                           }

                           playSound(p, XSound.ENTITY_ITEM_PICKUP, 1.0F);
                        }
                     } catch (Throwable var3) {
                        throw var3;
                     }
                  }

                  public int getSlot() {
                     return inv.firstEmpty();
                  }

                  public ItemStack getItem() {
                     return (new CustomItem(petItem)).setString("petUUID", key).setCanBeStacked(false).build();
                  }
               });
            } else {
               p.sendMessage("§cSomething went wrong!");
               Skyblock.getPlugin().getLogger().log(Level.WARNING, "Could not find pet for " + type);
            }
         }

         if (p.hasPermission("koji.skyblock.pet.give")) {
            set(inv, new GUIClickableItem() {
               public void run(InventoryClickEvent e) {
                  p.openInventory(OpenPetsMenuCMD.getPetGiveInventory(p, 0));
                  playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
               }

               public int getSlot() {
                  return 48;
               }

               public ItemStack getItem() {
                  return (new ItemBuilder(XMaterial.CHEST)).setName(ChatColor.GREEN + "Open Pet Give Menu").build();
               }
            });
         }

         return inv;
      }
   }

   private static Inventory getPetGiveInventory(final Player p, final int page) {
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, "Give Pets");
      fill(inv, GUIClickableItem.getBorderItem(0));
      set(inv, GUIClickableItem.cantPickup((new ItemBuilder(XMaterial.BONE)).setName(ChatColor.GREEN + "Pets").setLore(new String[]{ChatColor.GRAY + "View and manage all of your", ChatColor.GRAY + "Pets.", "", ChatColor.GRAY + "Level up your pets faster by", ChatColor.GRAY + "gaining skill xp in their favorite", ChatColor.GRAY + "skill!", "", ChatColor.GRAY + "Selected pet: " + PClass.getPlayer(p).getPetInstance().getName()}).build(), 4));
      set(inv, new GUIClickableItem() {
         public void run(InventoryClickEvent e) {
            OpenPetsMenuCMD.handleOpenPetInventory(p, 0);
         }

         public int getSlot() {
            return 48;
         }

         public ItemStack getItem() {
            return (new ItemBuilder(XMaterial.ARROW)).setName(ChatColor.GREEN + "Go Back").setLore(new String[]{ChatColor.GRAY + "To Pets Menu"}).build();
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
      final List pets = Files.getRegisteredPets().values().size() <= 1 ? new ArrayList(Files.getRegisteredPets().values()) : (new ArrayList(Files.getRegisteredPets().values())).subList(page * 28, Math.min(Files.getRegisteredPets().size() - 1, 27 + 28 * page));
      Integer[] centeredSlots = getCenteredSlots(((List)pets).size(), 31, true);
      boolean[] boos = new boolean[]{page != 0, ((List)pets).size() == 28};

      final int finalI;
      for(finalI = 0; finalI < 2; ++finalI) {
         if (boos[finalI]) {
            set(inv, new GUIClickableItem() {
               final String prefix = finalI == 0 ? "Previous" : "Next";
               final int nextPage = finalI == 0 ? -1 : 1;

               public void run(InventoryClickEvent e) {
                  p.openInventory(OpenPetsMenuCMD.getPetGiveInventory(p, page + this.nextPage));
                  playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
               }

               public int getSlot() {
                  return finalI == 0 ? 45 : 53;
               }

               public ItemStack getItem() {
                  return (new ItemBuilder(XMaterial.ARROW)).setName(ChatColor.GREEN + this.prefix + " Page").setLore(new String[]{ChatColor.YELLOW + "Page " + (page + this.nextPage)}).build();
               }
            });
         }
      }

      for(finalI = 0; finalI < ((List)pets).size(); ++finalI) {
         final int slot = centeredSlots[finalI];
         set(inv, new GUIClickableItem() {
            public void run(InventoryClickEvent e) {
               if (Files.getPetSkins().containsKey(((Pet)((List)pets).get(finalI)).getName()) && e.isRightClick()) {
                  p.openInventory(OpenPetsMenuCMD.getPetSkinGiveInventory(p, (Pet)((List)pets).get(finalI), this.getItem(), 0));
                  playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
               } else {
                  p.openInventory(OpenPetsMenuCMD.getRarityInventory(p, (Pet)((List)pets).get(finalI), this.getItem()));
                  playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
               }
            }

            public int getSlot() {
               return slot;
            }

            public ItemStack getItem() {
               Rarity rarity = (Rarity)getLast((List)Arrays.stream(Rarity.values()).filter((r) -> {
                  return ((Pet)pets.get(finalI)).rarities().contains(r);
               }).collect(Collectors.toList()));
               ItemBuilder ib = new ItemBuilder(((Pet)((List)pets).get(finalI)).getItem(rarity, 1, 0.0D, (String)null, true, false));
               ArrayList lore = new ArrayList(ib.build().getItemMeta().getLore());
               if (Files.getPetSkins().containsKey(((Pet)((List)pets).get(finalI)).getName())) {
                  lore.add(lore.size() - 1, ChatColor.AQUA + "Right-Click to get Pet Skins!");
                  ib.setLore(lore);
               }

               return ib.build();
            }
         });
      }

      return inv;
   }

   private static Inventory getRarityInventory(final Player p, final Pet pet, ItemStack item) {
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, "Choose Rarity");
      final Integer[] slots = getCenteredSlots(pet.rarities().size(), 31, true);
      fill(inv, GUIClickableItem.getBorderItem(0));
      set(inv, GUIClickableItem.cantPickup(item, 13));
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
            p.openInventory(OpenPetsMenuCMD.getPetGiveInventory(p, 0));
            playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
         }

         public int getSlot() {
            return 48;
         }

         public ItemStack getItem() {
            return (new ItemBuilder(XMaterial.ARROW)).setName(ChatColor.GREEN + "Go Back").setLore(new String[]{ChatColor.GRAY + "To Give Pets Menu"}).build();
         }
      });

      for(final int finalI = 0; finalI < slots.length; ++finalI) {
         set(inv, new GUIClickableItem() {
            public void run(InventoryClickEvent e) {
               playSound(p, XSound.ENTITY_ARROW_HIT_PLAYER, 1.0F);
               PClass.getPlayer(p).getPetInstance().saveItemToFile(pet.getItem((Rarity)pet.rarities().get(finalI), 1, 0.0D, (String)null));
               p.openInventory(OpenPetsMenuCMD.getPetGiveInventory(p, 0));
            }

            public int getSlot() {
               return slots[finalI];
            }

            public ItemStack getItem() {
               Rarity rarity = (Rarity)pet.rarities().get(finalI);
               return (new ItemBuilder(rarity.getBlock())).setName(rarity.getDisplay()).build();
            }
         });
      }

      return inv;
   }

   private static Inventory getPetSkinGiveInventory(final Player p, final Pet pet, final ItemStack item, final int page) {
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, "Choose Skin");
      List stacks = new ArrayList(((HashMap)Files.getPetSkins().getOrDefault(pet.getName(), new HashMap())).values());
      final List stacks = stacks.size() <= 1 ? stacks : stacks.subList(28 * page, Math.min(stacks.size() - 1, 27 + 28 * page));
      final Integer[] slots = getCenteredSlots(((List)stacks).size(), 31, true);
      fill(inv, GUIClickableItem.getBorderItem(0));
      set(inv, GUIClickableItem.cantPickup(item, 13));
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
            p.openInventory(OpenPetsMenuCMD.getPetGiveInventory(p, 0));
            playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
         }

         public int getSlot() {
            return 48;
         }

         public ItemStack getItem() {
            return (new ItemBuilder(XMaterial.ARROW)).setName(ChatColor.GREEN + "Go Back").setLore(new String[]{ChatColor.GRAY + "To Give Pets Menu"}).build();
         }
      });
      boolean[] boos = new boolean[]{page != 0, ((List)stacks).size() == 28};

      final int finalI;
      for(finalI = 0; finalI < 2; ++finalI) {
         if (boos[finalI]) {
            set(inv, new GUIClickableItem() {
               final String prefix = finalI == 0 ? "Previous" : "Next";
               final int nextPage = finalI == 0 ? -1 : 1;

               public void run(InventoryClickEvent e) {
                  p.openInventory(OpenPetsMenuCMD.getPetSkinGiveInventory(p, pet, item, page + this.nextPage));
                  playSound(p, XSound.UI_BUTTON_CLICK, 1.0F);
               }

               public int getSlot() {
                  return finalI == 0 ? 45 : 53;
               }

               public ItemStack getItem() {
                  return (new ItemBuilder(XMaterial.ARROW)).setName(ChatColor.GREEN + this.prefix + " Page").setLore(new String[]{ChatColor.YELLOW + "Page " + (page + this.nextPage)}).build();
               }
            });
         }
      }

      for(finalI = 0; finalI < slots.length; ++finalI) {
         set(inv, new GUIClickableItem() {
            public void run(InventoryClickEvent e) {
               if (!addItemUnlessFull(p.getInventory(), this.getItem())) {
                  p.sendMessage(Messages.FULL_INVENTORY.getMessage());
               } else {
                  playSound(p, XSound.ENTITY_ARROW_HIT_PLAYER, 1.0F);
                  p.openInventory(OpenPetsMenuCMD.getPetGiveInventory(p, 0));
               }

            }

            public int getSlot() {
               return slots[finalI];
            }

            public ItemStack getItem() {
               return (ItemStack)((List)stacks).get(finalI);
            }
         });
      }

      return inv;
   }

   public static ArrayList getPlayersWithPetMenuOpen() {
      return playersWithPetMenuOpen;
   }
}
