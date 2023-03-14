package koji.skyblock.item.reforges;

import koji.skyblock.item.CustomItem;
import koji.skyblock.item.ItemType;

public enum ReforgeType {
   MELEE_WEAPON {
      public boolean includes(CustomItem mat) {
         return ItemType.SWORD.includes(mat) || ItemType.FISHING_WEAPON.includes(mat) || ItemType.FISHING_ROD.includes(mat);
      }
   },
   RANGED_WEAPON {
      public boolean includes(CustomItem mat) {
         return ItemType.BOW.includes(mat);
      }
   },
   ARMOR {
      public boolean includes(CustomItem mat) {
         return ItemType.ARMOR.includes(mat);
      }
   },
   AXE {
      public boolean includes(CustomItem mat) {
         return ItemType.AXE.includes(mat);
      }
   },
   HOE {
      public boolean includes(CustomItem mat) {
         return ItemType.HOE.includes(mat);
      }
   },
   PICKAXE {
      public boolean includes(CustomItem mat) {
         return ItemType.PICKAXE.includes(mat);
      }
   };

   private ReforgeType() {
   }

   public abstract boolean includes(CustomItem var1);

   public static ReforgeType getApplicableType(CustomItem item) {
      ReforgeType[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ReforgeType types = var1[var3];
         if (types.includes(item)) {
            return types;
         }
      }

      return null;
   }

   public static boolean hasApplicableType(CustomItem item) {
      return getApplicableType(item) != null;
   }

   // $FF: synthetic method
   ReforgeType(Object x2) {
      this();
   }
}
