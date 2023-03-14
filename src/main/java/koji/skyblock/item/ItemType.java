package koji.skyblock.item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import koji.developerkit.KBase;
import koji.developerkit.utils.xseries.XMaterial;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public abstract class ItemType extends KBase {
   private static final ArrayList added = new ArrayList();
   public static ItemType ARMOR = new ItemType() {
      public String getName() {
         return "ARMOR";
      }

      public String getDisplayName() {
         return "Armor";
      }

      public boolean includes(CustomItem ci) {
         return HELMET.includes(ci) || CHESTPLATE.includes(ci) || LEGGINGS.includes(ci) || BOOTS.includes(ci);
      }

      public ItemStack getItem() {
         return XMaterial.DIAMOND_CHESTPLATE.parseItem();
      }

      public boolean isBroadType() {
         return true;
      }
   };
   public static ItemType HELMET = new ItemType() {
      public String getName() {
         return "HELMET";
      }

      public String getDisplayName() {
         return "Helmet";
      }

      public boolean includes(CustomItem ci) {
         return ci.getType().equals("HELMET") || EnchantmentTarget.ARMOR_HEAD.includes(ci.build());
      }

      public ItemStack getItem() {
         return XMaterial.IRON_HELMET.parseItem();
      }
   };
   public static ItemType CHESTPLATE = new ItemType() {
      public String getName() {
         return "CHESTPLATE";
      }

      public String getDisplayName() {
         return "Chestplate";
      }

      public boolean includes(CustomItem ci) {
         return ci.getType().equals("CHESTPLATE") || EnchantmentTarget.ARMOR_TORSO.includes(ci.build());
      }
   };
   public static ItemType LEGGINGS = new ItemType() {
      public String getName() {
         return "LEGGINGS";
      }

      public String getDisplayName() {
         return "Leggings";
      }

      public boolean includes(CustomItem ci) {
         return ci.getType().equals("LEGGINGS") || EnchantmentTarget.ARMOR_LEGS.includes(ci.build());
      }

      public ItemStack getItem() {
         return XMaterial.IRON_LEGGINGS.parseItem();
      }
   };
   public static ItemType BOOTS = new ItemType() {
      public String getName() {
         return "BOOTS";
      }

      public String getDisplayName() {
         return "Boots";
      }

      public boolean includes(CustomItem ci) {
         return ci.getType().equals("BOOTS") || EnchantmentTarget.ARMOR_FEET.includes(ci.build());
      }

      public ItemStack getItem() {
         return XMaterial.IRON_BOOTS.parseItem();
      }
   };
   public static ItemType FISHING_ROD = new ItemType() {
      public String getName() {
         return "FISHING ROD";
      }

      public String getDisplayName() {
         return "Fishing Rod";
      }

      public boolean includes(CustomItem ci) {
         return ci.getType().equals("FISHING ROD") || ci.build().getType().toString().endsWith("FISHING_ROD");
      }

      public ItemStack getItem() {
         return XMaterial.FISHING_ROD.parseItem();
      }
   };
   public static ItemType TOOL = new ItemType() {
      public String getName() {
         return "TOOL";
      }

      public String getDisplayName() {
         return "Tools";
      }

      public boolean includes(CustomItem ci) {
         return ci.getType().equals("TOOL") || AXE.includes(ci) || PICKAXE.includes(ci) || HOE.includes(ci) || EnchantmentTarget.TOOL.includes(ci.build());
      }

      public ItemStack getItem() {
         return XMaterial.DIAMOND_PICKAXE.parseItem();
      }

      public boolean isBroadType() {
         return true;
      }
   };
   public static ItemType SHOVEL = new ItemType() {
      public String getName() {
         return "SHOVEL";
      }

      public String getDisplayName() {
         return "Shovel";
      }

      public boolean includes(CustomItem ci) {
         return ci.getType().equals("SHOVEL") || ci.build().getType().toString().endsWith("SHOVEL");
      }

      public ItemStack getItem() {
         return XMaterial.IRON_SHOVEL.parseItem();
      }
   };
   public static ItemType PICKAXE = new ItemType() {
      public String getName() {
         return "PICKAXE";
      }

      public String getDisplayName() {
         return "Pickaxe";
      }

      public boolean includes(CustomItem ci) {
         return ci.getType().equals("PICKAXE") || ci.build().getType().toString().endsWith("PICKAXE");
      }

      public ItemStack getItem() {
         return XMaterial.IRON_PICKAXE.parseItem();
      }
   };
   public static ItemType HOE = new ItemType() {
      public String getName() {
         return "HOE";
      }

      public String getDisplayName() {
         return "Hoe";
      }

      public boolean includes(CustomItem ci) {
         return ci.getType().equals("HOE") || ci.build().getType().toString().endsWith("HOE");
      }

      public ItemStack getItem() {
         return XMaterial.IRON_HOE.parseItem();
      }
   };
   public static ItemType SWORD = new ItemType() {
      public String getName() {
         return "SWORD";
      }

      public String getDisplayName() {
         return "Sword";
      }

      public boolean includes(CustomItem ci) {
         return ci.getType().equals("SWORD") || ci.build().getType().toString().endsWith("SWORD");
      }

      public ItemStack getItem() {
         return XMaterial.IRON_SWORD.parseItem();
      }
   };
   public static ItemType FISHING_WEAPON = new ItemType() {
      public String getName() {
         return "FISHING WEAPON";
      }

      public String getDisplayName() {
         return "Fishing Weapon";
      }

      public boolean includes(CustomItem ci) {
         return ci.getType().equals("FISHING WEAPON");
      }

      public ItemStack getItem() {
         return (new CustomItem(XMaterial.FISHING_ROD)).addEnchantGlow().build();
      }
   };
   public static ItemType AXE = new ItemType() {
      public String getName() {
         return "AXE";
      }

      public String getDisplayName() {
         return "Axe";
      }

      public boolean includes(CustomItem ci) {
         return ci.getType().equals("AXE") || ci.build().getType().toString().endsWith("AXE");
      }

      public ItemStack getItem() {
         return XMaterial.IRON_AXE.parseItem();
      }
   };
   public static ItemType BOW = new ItemType() {
      public String getName() {
         return "BOW";
      }

      public String getDisplayName() {
         return "Bow";
      }

      public boolean includes(CustomItem ci) {
         return ci.getType().equals("BOW") || ci.build().getType().toString().endsWith("BOW");
      }

      public ItemStack getItem() {
         return XMaterial.BOW.parseItem();
      }
   };
   public static ItemType BOOK = new ItemType() {
      public String getName() {
         return "BOOK";
      }

      public String getDisplayName() {
         return "Book";
      }

      public boolean includes(CustomItem ci) {
         return false;
      }

      public boolean includeInList() {
         return false;
      }
   };

   public static List getValuesFullList() {
      List types = (List)added.stream().distinct().collect(Collectors.toList());
      types.removeIf((type) -> {
         return !type.includeInList();
      });
      return types;
   }

   private static List getFullListIncludeNon() {
      return added;
   }

   public static void addItemType(ItemType target) {
      if (!added.contains(target)) {
         added.add(target);
      }
   }

   public abstract String getName();

   public abstract String getDisplayName();

   public ItemStack getItem() {
      return XMaterial.IRON_CHESTPLATE.parseItem();
   }

   public abstract boolean includes(CustomItem var1);

   public boolean includeInList() {
      return true;
   }

   public boolean isBroadType() {
      return false;
   }

   public static ItemType parse(String type) {
      List clone = new ArrayList(getFullListIncludeNon());
      Iterator var2 = clone.iterator();

      ItemType types;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         types = (ItemType)var2.next();
      } while(!types.getName().equalsIgnoreCase(type));

      return types;
   }

   public static boolean isIncludedAtAll(CustomItem is) {
      Iterator var1 = getFullListIncludeNon().iterator();

      ItemType types;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         types = (ItemType)var1.next();
      } while(!types.includes(is));

      return true;
   }

   static {
      added.addAll(arrayList(new ItemType[]{ARMOR, HELMET, CHESTPLATE, LEGGINGS, BOOTS, FISHING_ROD, TOOL, SHOVEL, PICKAXE, HOE, SWORD, FISHING_WEAPON, BOW, BOOK}));
   }
}
