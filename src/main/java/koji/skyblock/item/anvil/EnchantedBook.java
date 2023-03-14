package koji.skyblock.item.anvil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import koji.developerkit.utils.xseries.XMaterial;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.ItemType;
import koji.skyblock.item.Rarity;
import koji.skyblock.item.enchants.Enchant;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class EnchantedBook extends CustomItem {
   public EnchantedBook(XMaterial material) {
      super(material);
      this.init();
   }

   public EnchantedBook(ItemStack item) {
      super(item);
      this.init();
   }

   public EnchantedBook(CustomItem ci) {
      super(ci.build());
      this.init();
   }

   public void init() {
      if (this.im.getType() != XMaterial.ENCHANTED_BOOK.parseMaterial()) {
         throw new NullPointerException("Material must be an enchanted book!");
      } else if (this.getItemType() != ItemType.BOOK) {
         this.HideFlags(63);
         this.setAllowEnchants(false);
         this.setType(ItemType.BOOK);
         EnchantmentStorageMeta meta = (EnchantmentStorageMeta)this.im.getItemMeta();
         Map enchants = new HashMap(meta.getStoredEnchants());
         enchants.forEach((a, b) -> {
            meta.removeStoredEnchant(a);
         });
         this.im.setItemMeta(meta);
         Iterator var3 = enchants.keySet().iterator();

         while(var3.hasNext()) {
            Enchantment enchant = (Enchantment)var3.next();
            Enchant matching = matchingEnchant((Class)Enchant.getVanillaEquivalent().get(enchant));
            if (matching != null) {
               this.addEnchant(matching, (Integer)enchants.get(enchant));
            }
         }

         Rarity rarity = Rarity.COMMON;
         switch(this.getHighestLevelEnchant()) {
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

         this.setRarity(rarity);
         this.setLore(this.getLore());
         this.setName(rarity.getColor() + "Enchanted Book");
      }
   }

   public int getHighestLevelEnchant() {
      List values = new ArrayList(this.getEnchants().values());
      Collections.sort(values);
      Collections.reverse(values);
      return (Integer)getOrDefault(values, 0, 0);
   }

   public String getName() {
      return this.getRarity().getColor() + "Enchanted Book";
   }

   public List getLore() {
      List lore = new ArrayList(this.getEnchantLore());
      lore.addAll(arrayList(new String[]{"", ChatColor.GRAY + "Apply Cost: " + ChatColor.DARK_AQUA + this.getApplyCost() + " Exp Levels", "", ChatColor.GRAY + "Use this on an item in an Anvil", ChatColor.GRAY + "to apply it!", "", this.getRarity().getDisplay()}));
      return lore;
   }
}
