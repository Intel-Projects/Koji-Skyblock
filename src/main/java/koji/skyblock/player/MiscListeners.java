package koji.skyblock.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import koji.developerkit.listener.KListener;
import koji.developerkit.runnable.KRunnable;
import koji.developerkit.utils.duplet.Tuple;
import koji.developerkit.utils.xseries.XMaterial;
import koji.developerkit.utils.xseries.XSound;
import koji.skyblock.Skyblock;
import koji.skyblock.api.armorevents.enums.ArmorType;
import koji.skyblock.api.armorevents.event.ArmorEquipEvent;
import koji.skyblock.commands.Messages;
import koji.skyblock.files.Config;
import koji.skyblock.files.Files;
import koji.skyblock.files.data.PlayerData;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.ability.Ability;
import koji.skyblock.item.anvil.EnchantedBook;
import koji.skyblock.item.enchants.events.EnchantAddEvent;
import koji.skyblock.item.enchants.events.EnchantRemoveEvent;
import koji.skyblock.pets.api.PetDequipEvent;
import koji.skyblock.pets.api.PetEquipEvent;
import koji.skyblock.player.api.FullSetEvent;
import koji.skyblock.player.api.ManaUseEvent;
import koji.skyblock.player.api.RightClickAbilityCastEvent;
import koji.skyblock.player.events.PlaceholderChangeRequest;
import koji.skyblock.player.events.SkyblockDamageEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

public class MiscListeners extends KListener {
   private static final List blocks = new ArrayList();
   static HashMap arrows = new HashMap();

   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent e) {
      GenericListeners.runFunction(new GenericListeners.PlayerEventToEntity(e.getPlayer(), e));
   }

   @EventHandler
   public void onPlayerRightClick(RightClickAbilityCastEvent e) {
      GenericListeners.runFunction(new GenericListeners.PlayerEventToEntity(e.getPlayer(), e));
   }

   @EventHandler
   public void onPlaceholderChange(PlaceholderChangeRequest e) {
      GenericListeners.runFunction(new GenericListeners.PlayerEventToEntity(e.getPlayer(), e), e.getItem().buildWithAbilities());
   }

   @EventHandler
   public void onPlayerToggleFlight(PlayerToggleFlightEvent e) {
      GenericListeners.runFunction(new GenericListeners.PlayerEventToEntity(e.getPlayer(), e));
   }

   @EventHandler
   public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
      GenericListeners.runFunction(new GenericListeners.PlayerEventToEntity(e.getPlayer(), e));
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent e) {
      Arrays.stream(e.getPlayer().getInventory().getArmorContents()).filter((i) -> {
         return isValidItem(i) && (new CustomItem(i)).hasEvent(ArmorEquipEvent.class);
      }).forEach((i) -> {
         (new CustomItem(i)).getAbilities().forEach((a) -> {
            a.runEvent(new ArmorEquipEvent(e.getPlayer(), ArmorEquipEvent.EquipMethod.DEATH, ArmorType.matchType(i), i, i));
         });
      });
      GenericListeners.runFunction(new GenericListeners.PlayerEventToEntity(e.getPlayer(), e));
   }

   @EventHandler
   public void onPlayerHoldItem(PlayerItemHeldEvent e) {
      ItemStack newItem = e.getPlayer().getInventory().getItem(e.getNewSlot());
      if (isValidItem(newItem) && !(new CustomItem(newItem)).getAbilities().isEmpty()) {
         CustomItem item = new CustomItem(newItem);
         item.getAbilities().forEach((a) -> {
            double mana = a.getActualManaCost(e.getPlayer());
            if (mana != 0.0D) {
               ManaUseEvent mUE = new ManaUseEvent(e.getPlayer(), item, mana);
               GenericListeners.runFunction(mUE, item.buildWithAbilities());
               item.changePlaceholder(a.getIdentifier(), "mana_cost", num(PClass.format(mUE.getManaCost())));
            }

         });
         PlaceholderChangeRequest request = new PlaceholderChangeRequest(e.getPlayer(), item);
         Bukkit.getPluginManager().callEvent(request);
         newItem = request.getItem().buildWithAbilities();
      }

      GenericListeners.runFunction(new GenericListeners.PlayerEventToEntity(e.getPlayer(), e), newItem);
      e.getPlayer().getInventory().setItem(e.getNewSlot(), newItem);
   }

   @EventHandler
   public void onPetEquip(PetEquipEvent e) {
      GenericListeners.runFunction(e);
   }

   @EventHandler
   public void onPetDequip(PetDequipEvent e) {
      GenericListeners.runFunction(e);
   }

   @EventHandler
   public void onSkyblockDamage(SkyblockDamageEvent e) {
      GenericListeners.runFunction(e);
   }

   @EventHandler
   public void onManaEvent(ManaUseEvent e) {
      GenericListeners.runFunction(e, e.getItem().buildWithAbilities());
   }

   @EventHandler
   public void enchantAddEvent(EnchantAddEvent e) {
      e.getEnchant().runEvent(e);
      e.getItem().getAbilities().forEach((a) -> {
         a.runEvent(e);
      });
   }

   @EventHandler
   public void enchantRemoveEvent(EnchantRemoveEvent e) {
      e.getEnchant().runEvent(e);
      e.getItem().getAbilities().forEach((a) -> {
         a.runEvent(e);
      });
   }

   @EventHandler
   public void onArmorEquip(ArmorEquipEvent e) {
      Arrays.stream(e.getNewArmorContents()).forEach((item) -> {
         if (isValidItem(item)) {
            CustomItem ci = new CustomItem(item);
            Iterator var3 = ci.getAbilities().iterator();

            while(var3.hasNext()) {
               Ability a = (Ability)var3.next();
               if (!Ability.isFullSet(e.getNewArmorContents(), a.getIdentifier())) {
                  break;
               }

               a.runEvent(new FullSetEvent(e.getPlayer(), e.getNewArmorContents()));
            }
         }

      });
      GenericListeners.runFunction(new GenericListeners.PlayerEventToEntity(e.getPlayer(), e), e.getNewArmorPiece());
   }

   @EventHandler
   public void onBlockChange(EntityChangeBlockEvent e) {
      if (e.getEntityType() == EntityType.FALLING_BLOCK && blocks.contains((FallingBlock)e.getEntity())) {
         FallingBlock block = (FallingBlock)e.getEntity();
         blocks.remove(block);
         e.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onDeath(EntityDeathEvent e) {
      if (!(e.getEntity() instanceof Player)) {
         if (Config.willAutoPickup()) {
            Player p = e.getEntity().getKiller();
            if (e.getEntity().getKiller() == null || !(e.getEntity() instanceof Player)) {
               return;
            }

            p.giveExp(e.getDroppedExp());
            e.setDroppedExp(0);
            ArrayList toInv = new ArrayList();
            Iterator var4 = (new ArrayList(e.getDrops())).iterator();

            while(var4.hasNext()) {
               ItemStack is = (ItemStack)var4.next();
               if (getEmptySlots(p.getInventory()) - toInv.size() <= 0) {
                  p.sendMessage(Messages.FULL_INVENTORY.getMessage());
                  p.playSound(p.getLocation(), XSound.BLOCK_CHEST_OPEN.parseSound(), 1.0F, 1.0F);
                  break;
               }

               toInv.add(is);
               e.getDrops().remove(is);
            }

            ItemStack[] array = (ItemStack[])toInv.toArray(new ItemStack[0]);
            if (array.length != 0) {
               p.getInventory().addItem(array);
               if (!PlayerData.getPlayerData().getsIt(p)) {
                  TextComponent main = new TextComponent("AUTO-PICKUP! ");
                  main.setColor(ChatColor.GREEN);
                  TextComponent extra1 = new TextComponent("Drop sent to your inventory! ");
                  extra1.setColor(ChatColor.WHITE);
                  TextComponent extra2 = new TextComponent("[I GET IT]");
                  extra2.setBold(true);
                  extra2.setColor(ChatColor.DARK_GREEN);
                  extra2.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/kojiskyblock:attribute set getsit true"));
                  extra2.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(org.bukkit.ChatColor.YELLOW + "Click to forever disable this notification!")}));
                  main.addExtra(extra1);
                  main.addExtra(extra2);
                  p.spigot().sendMessage(main);
               }
            }
         }

         if (!Config.areDropsVisibleToNonKiller()) {
            ArrayList droppedItems = new ArrayList();
            e.getDrops().forEach((drop) -> {
               droppedItems.add(e.getEntity().getWorld().dropItem(e.getEntity().getLocation(), drop));
            });
            List players = e.getEntity().getWorld().getPlayers();
            players.forEach((px) -> {
               if (px != e.getEntity().getKiller()) {
                  droppedItems.forEach((item) -> {
                     Skyblock.getEntityHider().hideEntity(px, item);
                  });
               }

            });
            e.getDrops().clear();
         }

      }
   }

   @EventHandler
   public void onItemPlace(BlockPlaceEvent e) {
      if (isValidItem(e.getItemInHand())) {
         CustomItem item = new CustomItem(e.getItemInHand());
         if (item.hasKey("id") && !item.canBePlaced()) {
            e.setCancelled(true);
         }
      }

   }

   @EventHandler
   public void onProjectileShoot(ProjectileLaunchEvent e) {
      if (e.getEntity().getShooter() instanceof Player) {
         arrows.put(e.getEntity(), Tuple.of((Player)e.getEntity().getShooter(), new CustomItem(((Player)e.getEntity().getShooter()).getItemInHand()), 1.0D));
      }

   }

   @EventHandler
   public void onArrowLand(ProjectileHitEvent e) {
      if (arrows.containsKey(e.getEntity())) {
         e.getEntity().remove();
      }

   }

   @EventHandler
   public void onItemDrop(PlayerDropItemEvent e) {
      Player p = e.getPlayer();
      if (!Config.areDropsVisibleToEveryone()) {
         if (PlayerData.getPlayerData().getDropItemAlert(p)) {
            TextComponent component = new TextComponent(ChatColor.YELLOW + "⚠ ");
            TextComponent component1 = new TextComponent(ChatColor.GREEN + "Your drops can't be seen by other players in ");
            component1.setColor(ChatColor.GREEN);
            TextComponent component2 = new TextComponent("Skyblock");
            component2.setColor(ChatColor.AQUA);
            TextComponent component3 = new TextComponent("!\nOnly you can pickup your dropped items!");
            component3.setColor(ChatColor.GREEN);
            TextComponent component4 = new TextComponent("\nClick here to disable this alert forever!");
            component4.setColor(ChatColor.YELLOW);
            component.addExtra(component1);
            component.addExtra(component2);
            component.addExtra(component3);
            component.addExtra(component4);
            component.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(org.bukkit.ChatColor.YELLOW + "Click to disable the alert!")}));
            component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/kojiskyblock:attribute set dropitemalert false"));
            p.spigot().sendMessage(component);
         }

         e.getItemDrop().getWorld().getPlayers().stream().filter((player) -> {
            return !player.equals(p);
         }).forEach((player) -> {
            Skyblock.getEntityHider().setHide(player, e.getItemDrop(), false);
         });
      }

   }

   @EventHandler
   public void onItemSpawn(ItemSpawnEvent e) {
      e.getEntity().setItemStack(this.onDrop(e.getEntity().getItemStack()));
   }

   @EventHandler
   public void onCraft(CraftItemEvent e) {
      e.getInventory().setResult(this.onDrop(e.getInventory().getResult()));
   }

   @EventHandler
   public void onDrag(InventoryDragEvent e) {
      if (e.getRawSlots().stream().findAny().isPresent()) {
         e.setCursor(this.onDrop(e.getCursor()));
      }

   }

   @EventHandler
   public void onItemPick(InventoryClickEvent e) {
      (new KRunnable((task) -> {
         if (isValidItem(e.getCurrentItem())) {
            e.getWhoClicked().getInventory().all(e.getCurrentItem().getType()).forEach((a, b) -> {
               e.getWhoClicked().getInventory().setItem(a, this.onDrop(b));
            });
         }

      })).runTaskLater(Skyblock.getPlugin(), 1L);
   }

   public ItemStack onDrop(ItemStack original) {
      if (isValidItem(original)) {
         XMaterial mat = XMaterial.matchXMaterial(original);
         if (!(new CustomItem(original)).hasKey("id") && Files.getDefaultItemOverrides().contains(mat.name())) {
            return CustomItem.createItem(Files.getDefaultItemOverrides(), mat.name());
         }

         if (original.getType() == XMaterial.ENCHANTED_BOOK.parseMaterial()) {
            return (new EnchantedBook(original)).build();
         }
      }

      return original;
   }

   public static List getBlocks() {
      return blocks;
   }

   public static HashMap getArrows() {
      return arrows;
   }
}
