package koji.skyblock.files.data;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import koji.developerkit.KBase;
import koji.developerkit.utils.duplet.Duplet;
import koji.developerkit.utils.duplet.DupletList;
import koji.skyblock.Skyblock;
import koji.skyblock.files.Config;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.enchants.EnchantTableGUI;
import koji.skyblock.player.Stats;
import koji.skyblock.utils.StatMap;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class PlayerData extends KBase {
   private static PlayerData playerData;

   public static boolean setPlayerData(PlayerData data) {
      playerData = data;
      if (!playerData.register()) {
         playerData = new FileData();
         Skyblock.getPlugin().getLogger().log(Level.WARNING, "Couldn't connect to database! Defaulting to file storage...");
         return false;
      } else {
         return true;
      }
   }

   public abstract void setBits(Player var1, Integer var2);

   public abstract int getBits(Player var1);

   public abstract void setStat(Player var1, Stats var2, Double var3);

   public abstract double getStat(Player var1, Stats var2);

   public abstract String getActivePet(Player var1);

   public abstract void setActivePet(Player var1, String var2);

   public abstract Boolean canSeePets(Player var1);

   public abstract void setCanSeePets(Player var1, Boolean var2);

   public abstract Boolean getsIt(Player var1);

   public abstract void setGetsIt(Player var1, Boolean var2);

   public abstract Boolean getDropItemAlert(Player var1);

   public abstract void setDropItemAlert(Player var1, Boolean var2);

   public abstract EnchantTableGUI.Sorting getEnchantTableSorting(Player var1);

   public abstract void setEnchantTableSorting(Player var1, EnchantTableGUI.Sorting var2);

   public abstract List getItemStash(Player var1);

   public abstract void setItemStash(Player var1, List var2);

   public void addToItemStash(Player p, ItemStack item) {
      List itemStash = getPlayerData().getItemStash(p);
      itemStash.add(item);
      getPlayerData().setItemStash(p, itemStash);
      p.sendMessage(ChatColor.RED + "An item didn't fit in your inventory and was added to your item stash! Use /pickupstash to get it back!");
   }

   public void grabFromItemStash(Player p) {
      List items = this.getItemStash(p);

      DupletList toAdd;
      for(toAdd = new DupletList(); p.getInventory().firstEmpty() != -1 && !items.isEmpty(); items.remove(items.get(0))) {
         if (isValidItem((ItemStack)items.get(0))) {
            toAdd.add((new CustomItem((ItemStack)items.get(0))).getName(), items.get(0));
         }
      }

      p.getInventory().addItem((ItemStack[])toAdd.secondToList().toArray(new ItemStack[0]));
      String pluralOriginalStash = items.size() > 1 ? "items" : "item";
      String pluralToBeAdded = toAdd.size() > 1 ? "items" : "item";
      toAdd.firstToList().forEach((name) -> {
         p.sendMessage(ChatColor.YELLOW + "From stash: " + name);
      });
      if (items.isEmpty()) {
         if (!toAdd.isEmpty()) {
            p.sendMessage(ChatColor.YELLOW + "You picked up " + ChatColor.GREEN + "all " + ChatColor.YELLOW + "items from your item stash!");
         } else {
            p.sendMessage(ChatColor.RED + "Your stash isn't holding any items!");
         }
      } else if (!toAdd.isEmpty()) {
         p.sendMessage(ChatColor.YELLOW + "You picked up " + ChatColor.GREEN + toAdd.size() + ChatColor.YELLOW + " " + pluralToBeAdded + " from your item stash!\n" + ChatColor.YELLOW + "You still have " + ChatColor.AQUA + items.size() + ChatColor.YELLOW + " " + pluralOriginalStash + " in there");
      } else {
         p.sendMessage(ChatColor.RED + "Couldn't unstash your items! Your inventory is full!");
      }

      this.setItemStash(p, items);
   }

   public void clearStash(Player p, boolean confirm) {
      List items = this.getItemStash(p);
      if (items.isEmpty()) {
         p.sendMessage(ChatColor.RED + "Your stash is already empty!");
      } else if (confirm) {
         p.sendMessage(ChatColor.GREEN + "Your stash has been cleared!");
         getPlayerData().setItemStash(p, new ArrayList());
      } else {
         TextComponent main = new TextComponent("WARNING: This action is irreversible and deletes all ");
         main.setColor(net.md_5.bungee.api.ChatColor.RED);
         TextComponent extra1 = new TextComponent(items.size() + "");
         extra1.setColor(net.md_5.bungee.api.ChatColor.RED);
         TextComponent extra2 = new TextComponent(" stashed items. If you still wish to continue, click below.");
         extra2.setColor(net.md_5.bungee.api.ChatColor.RED);
         TextComponent extra3 = new TextComponent("\nCLEAR STASH YES I AM SURE");
         extra3.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
         extra3.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new TextComponent[]{new TextComponent(ChatColor.YELLOW + "Click here to clear your stash")}));
         extra3.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/kojiskyblock:clearstash confirm"));
         extra3.setBold(true);
         main.addExtra(extra1);
         main.addExtra(extra2);
         main.addExtra(extra3);
         p.spigot().sendMessage(main);
      }
   }

   public void sendItemStashMessage(Player p) {
      List stash = getPlayerData().getItemStash(p);
      if (!stash.isEmpty()) {
         String plural = stash.size() > 1 ? "items" : "item";
         TextComponent textComponent = new TextComponent(ChatColor.YELLOW + "You have " + ChatColor.GREEN + stash.size() + ChatColor.YELLOW + " " + plural + " stashed away!!!");
         HoverEvent hoverEvent = new HoverEvent(Action.SHOW_TEXT, new TextComponent[]{new TextComponent(ChatColor.YELLOW + "Click to pickup your " + plural + "!")});
         ClickEvent clickEvent = new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/kojiskyblock:pickupstash");
         TextComponent extra1 = new TextComponent("\n" + ChatColor.GOLD + "Click here " + ChatColor.YELLOW + "to pick it up!");
         textComponent.setHoverEvent(hoverEvent);
         textComponent.setClickEvent(clickEvent);
         extra1.setHoverEvent(hoverEvent);
         extra1.setClickEvent(clickEvent);
         textComponent.addExtra(extra1);
         p.spigot().sendMessage(textComponent);
      }

   }

   public abstract void createPlayer(Player var1);

   public abstract boolean doesPlayerDataExist(Player var1);

   public abstract boolean register();

   public StatMap getBaseStats() {
      StatMap map = new StatMap(new Duplet[0]);
      Stats[] var2 = Stats.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Stats stats = var2[var4];
         if (stats != Stats.MANA && stats != Stats.HEALTH) {
            map.put(stats, Config.getBaseValue(stats));
         }
      }

      return map;
   }

   public static PlayerData getPlayerData() {
      return playerData;
   }
}
