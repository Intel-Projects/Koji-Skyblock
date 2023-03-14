package koji.skyblock.item.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import koji.developerkit.KBase;
import koji.developerkit.utils.xseries.XMaterial;
import koji.skyblock.item.CustomItem;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ItemStackSerializer extends KBase {
   public static String serialize(ItemStack item) {
      StringBuilder builder = new StringBuilder();
      builder.append(item.getType().toString());
      if (item.getDurability() != 0) {
         builder.append(":").append(item.getDurability());
      }

      builder.append("~itemstash--attribute~").append(item.getAmount());
      Iterator var2 = item.getEnchantments().keySet().iterator();

      while(var2.hasNext()) {
         Enchantment enchant = (Enchantment)var2.next();
         builder.append("~itemstash--attribute~").append(enchant.getName()).append(":").append(item.getEnchantments().get(enchant));
      }

      String name = getName(item);
      if (name != null) {
         builder.append("~itemstash--attribute~name:").append(name);
      }

      String lore = getLore(item);
      if (lore != null) {
         builder.append("~itemstash--attribute~lore:").append(lore);
      }

      Color color = getArmorColor(item);
      if (color != null) {
         builder.append("~itemstash--attribute~rgb:").append(color.getRed()).append("|").append(color.getGreen()).append("|").append(color.getBlue());
      }

      String compound = getCompound(item);
      if (compound != null) {
         builder.append("~itemstash--attribute~compound:").append(compound);
      }

      return builder.toString();
   }

   public static CustomItem deserialize(String serializedItem) {
      String[] strings = serializedItem.split("~itemstash--attribute~");
      Map enchants = new HashMap();

      assert XMaterial.AIR.parseMaterial() != null;

      ItemStack item = new ItemStack(XMaterial.AIR.parseMaterial());
      String[] var5 = strings;
      int var6 = strings.length;

      String[] args;
      int var7;
      for(var7 = 0; var7 < var6; ++var7) {
         String str = var5[var7];
         args = str.split(":");
         if (Material.matchMaterial(args[0]) != null && item.getType() == XMaterial.AIR.parseMaterial()) {
            item.setType(Material.matchMaterial(args[0]));
            if (args.length == 2) {
               item.setDurability(Short.parseShort(args[1]));
            }
            break;
         }
      }

      if (item.getType() == XMaterial.AIR.parseMaterial()) {
         return null;
      } else {
         item.addUnsafeEnchantments(enchants);
         CustomItem itemBuilder = new CustomItem(item);
         String[] var11 = strings;
         var7 = strings.length;

         for(int var12 = 0; var12 < var7; ++var12) {
            String str = var11[var12];
            args = str.split(":", 2);
            if (isNumber(args[0])) {
               item.setAmount(Integer.parseInt(args[0]));
            }

            if (args.length != 1) {
               if (args[0].equalsIgnoreCase("name")) {
                  itemBuilder.setName(color(args[1].replace("```", " ")));
               } else if (args[0].equalsIgnoreCase("lore")) {
                  setLore(itemBuilder, color(args[1]));
               } else if (args[0].equalsIgnoreCase("rgb")) {
                  setArmorColor(itemBuilder, args[1]);
               } else if (Enchantment.getByName(args[0].toUpperCase()) != null) {
                  enchants.put(Enchantment.getByName(args[0].toUpperCase()), Integer.parseInt(args[1]));
               } else if (args[0].equalsIgnoreCase("compound")) {
                  itemBuilder.applyCompoundFromString(args[1], true);
               }
            }
         }

         return itemBuilder;
      }
   }

   private static String getName(ItemStack item) {
      if (!item.hasItemMeta()) {
         return null;
      } else {
         return !item.getItemMeta().hasDisplayName() ? null : item.getItemMeta().getDisplayName().replace(" ", "```").replace('§', '&');
      }
   }

   private static String getLore(ItemStack item) {
      if (!item.hasItemMeta()) {
         return null;
      } else if (!item.getItemMeta().hasLore()) {
         return null;
      } else {
         StringBuilder builder = new StringBuilder();
         List lore = item.getItemMeta().getLore();

         for(int ind = 0; ind < lore.size(); ++ind) {
            builder.append(ind > 0 ? "|" : "").append(((String)lore.get(ind)).replace(" ", "_").replace('§', '&'));
         }

         return builder.toString();
      }
   }

   private static void setLore(CustomItem item, String lore) {
      lore = lore.replace("_", " ");
      item.setLore((List)(new ArrayList(Arrays.asList(lore.split("\\|")))));
   }

   private static Color getArmorColor(ItemStack item) {
      return !(item.getItemMeta() instanceof LeatherArmorMeta) ? null : ((LeatherArmorMeta)item.getItemMeta()).getColor();
   }

   private static void setArmorColor(CustomItem item, String str) {
      try {
         String[] colors = str.split("\\|");
         int red = Integer.parseInt(colors[0]);
         int green = Integer.parseInt(colors[1]);
         int blue = Integer.parseInt(colors[2]);
         item.setColor(Color.fromRGB(red, green, blue));
      } catch (Exception var6) {
      }

   }

   private static String getCompound(ItemStack item) {
      String compound = null;
      if (item != null) {
         CustomItem ci = new CustomItem(item);
         compound = ci.getStringFromCompound();
      }

      return compound;
   }

   private static boolean isNumber(String str) {
      try {
         Integer.parseInt(str);
         return true;
      } catch (NumberFormatException var2) {
         return false;
      }
   }
}
