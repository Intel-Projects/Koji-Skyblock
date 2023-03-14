package koji.skyblock.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Set;
import koji.developerkit.utils.xseries.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.java.JavaPlugin;

public final class InventoryUpdate {
   private static final Class CRAFT_PLAYER;
   private static final Class CHAT_MESSAGE;
   private static final Class PACKET_PLAY_OUT_OPEN_WINDOW;
   private static final Class I_CHAT_BASE_COMPONENT;
   private static final Class CONTAINER;
   private static final Class CONTAINERS;
   private static final Class ENTITY_PLAYER;
   private static final Class I_CHAT_MUTABLE_COMPONENT;
   private static final MethodHandle getHandle;
   private static final MethodHandle getBukkitView;
   private static final MethodHandle literal;
   private static final MethodHandle chatMessage;
   private static final MethodHandle packetPlayOutOpenWindow;
   private static final MethodHandle activeContainer;
   private static final MethodHandle windowId;
   private static final Lookup LOOKUP = MethodHandles.lookup();
   private static final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(InventoryUpdate.class);
   private static final boolean supports19 = ReflectionUtils.supports(19);
   private static final boolean TWO_ARGS_CHAT_MESSAGE_CONSTRUCTOR;
   private static final Set UNOPENABLES;
   private static final Object[] DUMMY_COLOR_MODIFIERS;

   public static void updateInventory(Player player, String newTitle) {
      Preconditions.checkArgument(player != null, "Cannot update inventory to null player.");

      try {
         Object craftPlayer = CRAFT_PLAYER.cast(player);
         Object entityPlayer = getHandle.invoke(craftPlayer);
         newTitle = newTitle != null && newTitle.length() > 32 ? newTitle.substring(0, 32) : (newTitle == null ? "" : newTitle);
         Object title = supports19 ? literal.invoke(newTitle) : (TWO_ARGS_CHAT_MESSAGE_CONSTRUCTOR ? chatMessage.invoke(newTitle, DUMMY_COLOR_MODIFIERS) : chatMessage.invoke(newTitle));
         Object activeContainer = InventoryUpdate.activeContainer.invoke(entityPlayer);
         Integer windowId = InventoryUpdate.windowId.invoke(activeContainer);
         Object bukkitView = getBukkitView.invoke(activeContainer);
         if (!(bukkitView instanceof InventoryView)) {
            return;
         }

         InventoryView view = (InventoryView)bukkitView;
         InventoryType type = view.getTopInventory().getType();
         if ((type == InventoryType.WORKBENCH || type == InventoryType.ANVIL) && !useContainers()) {
            return;
         }

         if (UNOPENABLES.contains(type.name())) {
            return;
         }

         int size = view.getTopInventory().getSize();
         InventoryUpdate.Containers container = InventoryUpdate.Containers.getType(type, size);
         if (container == null) {
            return;
         }

         if (container.getContainerVersion() > ReflectionUtils.VER && useContainers()) {
            Bukkit.getLogger().warning(String.format("[%s] This container doesn't work on your current version.", plugin.getDescription().getName()));
            return;
         }

         Object object = !useContainers() && container == InventoryUpdate.Containers.GENERIC_3X3 ? "minecraft:" + type.name().toLowerCase() : container.getObject();
         Object packet = useContainers() ? packetPlayOutOpenWindow.invoke(windowId, object, title) : packetPlayOutOpenWindow.invoke(windowId, object, title, size);
         ReflectionUtils.sendPacketSync(player, new Object[]{packet});
         player.updateInventory();
      } catch (Throwable var14) {
         var14.printStackTrace();
      }

   }

   private static MethodHandle getField(Class refc, Class instc, String name, String... extraNames) {
      MethodHandle handle = getFieldHandle(refc, instc, name);
      if (handle != null) {
         return handle;
      } else if (extraNames != null && extraNames.length > 0) {
         return extraNames.length == 1 ? getField(refc, instc, extraNames[0]) : getField(refc, instc, extraNames[0], removeFirst(extraNames));
      } else {
         return null;
      }
   }

   private static String[] removeFirst(String[] array) {
      int length = array.length;
      String[] result = new String[length - 1];
      System.arraycopy(array, 1, result, 0, length - 1);
      return result;
   }

   private static MethodHandle getFieldHandle(Class refc, Class inscofc, String name) {
      try {
         Field[] var3 = refc.getFields();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Field field = var3[var5];
            field.setAccessible(true);
            if (field.getName().equalsIgnoreCase(name) && (field.getType().isInstance(inscofc) || field.getType().isAssignableFrom(inscofc))) {
               return LOOKUP.unreflectGetter(field);
            }
         }

         return null;
      } catch (ReflectiveOperationException var7) {
         return null;
      }
   }

   private static MethodHandle getConstructor(Class refc, Class... types) {
      try {
         Constructor constructor = refc.getDeclaredConstructor(types);
         constructor.setAccessible(true);
         return LOOKUP.unreflectConstructor(constructor);
      } catch (ReflectiveOperationException var3) {
         var3.printStackTrace();
         return null;
      }
   }

   private static MethodHandle getMethod(Class refc, String name, MethodType type) {
      return getMethod(refc, name, type, false);
   }

   private static MethodHandle getMethod(Class refc, String name, MethodType type, boolean isStatic) {
      try {
         return isStatic ? LOOKUP.findStatic(refc, name, type) : LOOKUP.findVirtual(refc, name, type);
      } catch (ReflectiveOperationException var5) {
         var5.printStackTrace();
         return null;
      }
   }

   private static boolean useContainers() {
      return ReflectionUtils.VER > 13;
   }

   static {
      TWO_ARGS_CHAT_MESSAGE_CONSTRUCTOR = ReflectionUtils.supports(7) && ReflectionUtils.VER < 16;
      UNOPENABLES = Sets.newHashSet(new String[]{"CRAFTING", "CREATIVE", "PLAYER"});
      DUMMY_COLOR_MODIFIERS = new Object[0];
      CRAFT_PLAYER = ReflectionUtils.getCraftClass("entity.CraftPlayer");
      CHAT_MESSAGE = supports19 ? null : ReflectionUtils.getNMSClass("network.chat", "ChatMessage");
      PACKET_PLAY_OUT_OPEN_WINDOW = ReflectionUtils.getNMSClass("network.protocol.game", "PacketPlayOutOpenWindow");
      I_CHAT_BASE_COMPONENT = ReflectionUtils.getNMSClass("network.chat", "IChatBaseComponent");
      CONTAINERS = useContainers() ? ReflectionUtils.getNMSClass("world.inventory", "Containers") : null;
      ENTITY_PLAYER = ReflectionUtils.getNMSClass("server.level", "EntityPlayer");
      CONTAINER = ReflectionUtils.getNMSClass("world.inventory", "Container");
      I_CHAT_MUTABLE_COMPONENT = supports19 ? ReflectionUtils.getNMSClass("network.chat", "IChatMutableComponent") : null;
      getHandle = getMethod(CRAFT_PLAYER, "getHandle", MethodType.methodType(ENTITY_PLAYER));
      getBukkitView = getMethod(CONTAINER, "getBukkitView", MethodType.methodType(InventoryView.class));
      literal = supports19 ? getMethod(I_CHAT_BASE_COMPONENT, "b", MethodType.methodType(I_CHAT_MUTABLE_COMPONENT, String.class), true) : null;
      chatMessage = supports19 ? null : (TWO_ARGS_CHAT_MESSAGE_CONSTRUCTOR ? getConstructor(CHAT_MESSAGE, String.class, Object[].class) : getConstructor(CHAT_MESSAGE, String.class));
      packetPlayOutOpenWindow = useContainers() ? getConstructor(PACKET_PLAY_OUT_OPEN_WINDOW, Integer.TYPE, CONTAINERS, I_CHAT_BASE_COMPONENT) : getConstructor(PACKET_PLAY_OUT_OPEN_WINDOW, Integer.TYPE, String.class, I_CHAT_BASE_COMPONENT, Integer.TYPE);
      activeContainer = getField(ENTITY_PLAYER, CONTAINER, "activeContainer", "bV", "bW", "bU", "containerMenu");
      windowId = getField(CONTAINER, Integer.TYPE, "windowId", "j", "containerId");
   }

   private static enum Containers {
      GENERIC_9X1(14, "minecraft:chest", new String[]{"CHEST"}),
      GENERIC_9X2(14, "minecraft:chest", new String[]{"CHEST"}),
      GENERIC_9X3(14, "minecraft:chest", new String[]{"CHEST", "ENDER_CHEST", "BARREL"}),
      GENERIC_9X4(14, "minecraft:chest", new String[]{"CHEST"}),
      GENERIC_9X5(14, "minecraft:chest", new String[]{"CHEST"}),
      GENERIC_9X6(14, "minecraft:chest", new String[]{"CHEST"}),
      GENERIC_3X3(14, (String)null, new String[]{"DISPENSER", "DROPPER"}),
      ANVIL(14, "minecraft:anvil", new String[]{"ANVIL"}),
      BEACON(14, "minecraft:beacon", new String[]{"BEACON"}),
      BREWING_STAND(14, "minecraft:brewing_stand", new String[]{"BREWING"}),
      ENCHANTMENT(14, "minecraft:enchanting_table", new String[]{"ENCHANTING"}),
      FURNACE(14, "minecraft:furnace", new String[]{"FURNACE"}),
      HOPPER(14, "minecraft:hopper", new String[]{"HOPPER"}),
      MERCHANT(14, "minecraft:villager", new String[]{"MERCHANT"}),
      SHULKER_BOX(14, "minecraft:blue_shulker_box", new String[]{"SHULKER_BOX"}),
      BLAST_FURNACE(14, (String)null, new String[]{"BLAST_FURNACE"}),
      CRAFTING(14, (String)null, new String[]{"WORKBENCH"}),
      GRINDSTONE(14, (String)null, new String[]{"GRINDSTONE"}),
      LECTERN(14, (String)null, new String[]{"LECTERN"}),
      LOOM(14, (String)null, new String[]{"LOOM"}),
      SMOKER(14, (String)null, new String[]{"SMOKER"}),
      CARTOGRAPHY_TABLE(14, (String)null, new String[]{"CARTOGRAPHY"}),
      STONECUTTER(14, (String)null, new String[]{"STONECUTTER"}),
      SMITHING(16, (String)null, new String[]{"SMITHING"});

      private final int containerVersion;
      private final String minecraftName;
      private final String[] inventoryTypesNames;
      private static final char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();

      private Containers(int containerVersion, String minecraftName, String... inventoryTypesNames) {
         this.containerVersion = containerVersion;
         this.minecraftName = minecraftName;
         this.inventoryTypesNames = inventoryTypesNames;
      }

      public static InventoryUpdate.Containers getType(InventoryType type, int size) {
         if (type == InventoryType.CHEST) {
            return valueOf("GENERIC_9X" + size / 9);
         } else {
            InventoryUpdate.Containers[] var2 = values();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               InventoryUpdate.Containers container = var2[var4];
               String[] var6 = container.getInventoryTypesNames();
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  String bukkitName = var6[var8];
                  if (bukkitName.equalsIgnoreCase(type.toString())) {
                     return container;
                  }
               }
            }

            return null;
         }
      }

      public Object getObject() {
         try {
            if (!InventoryUpdate.useContainers()) {
               return this.getMinecraftName();
            } else {
               int version = ReflectionUtils.VER;
               String name = version == 14 && this == CARTOGRAPHY_TABLE ? "CARTOGRAPHY" : this.name();
               if (version > 16) {
                  name = String.valueOf(alphabet[this.ordinal()]);
               }

               Field field = InventoryUpdate.CONTAINERS.getField(name);
               return field.get((Object)null);
            }
         } catch (ReflectiveOperationException var4) {
            var4.printStackTrace();
            return null;
         }
      }

      public int getContainerVersion() {
         return this.containerVersion;
      }

      public String getMinecraftName() {
         return this.minecraftName;
      }

      public String[] getInventoryTypesNames() {
         return this.inventoryTypesNames;
      }
   }
}
