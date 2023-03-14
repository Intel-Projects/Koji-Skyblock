package koji.skyblock.item.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import koji.developerkit.KBase;
import koji.developerkit.utils.xseries.XMaterial;
import koji.skyblock.Skyblock;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class SignMenuFactory extends KBase {
   private static final SignMenuFactory factory = new SignMenuFactory(Skyblock.getPlugin());
   private static final int ACTION_INDEX = 9;
   private static final int SIGN_LINES = 4;
   private static final String NBT_FORMAT = "{\"text\":\"%s\"}";
   private static final String NBT_BLOCK_ID = "minecraft:sign";
   private final Plugin plugin;
   private final Map inputs;

   public SignMenuFactory(Plugin plugin) {
      this.plugin = plugin;
      this.inputs = new HashMap();
      this.listen();
   }

   public SignMenuFactory.Menu newMenu(List text) {
      return new SignMenuFactory.Menu(text);
   }

   private void listen() {
      ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.plugin, new PacketType[]{Client.UPDATE_SIGN}) {
         public void onPacketReceiving(PacketEvent event) {
            Player player = event.getPlayer();
            SignMenuFactory.Menu menu = (SignMenuFactory.Menu)SignMenuFactory.this.inputs.remove(player);
            if (menu != null) {
               event.setCancelled(true);
               boolean success = menu.response.test(player, SignMenuFactory.this.unwrap(event));
               if (!success && menu.reopenIfFail && !menu.forceClose) {
                  Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                     menu.open(player);
                  }, 2L);
               }

               Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                  if (player.isOnline()) {
                     Location location = menu.position.toLocation(player.getWorld());
                     player.getWorld().getBlockAt(location).setType(XMaterial.AIR.parseMaterial());
                     player.sendBlockChange(location, XMaterial.AIR.parseMaterial(), (byte)0);
                  }

               }, 2L);
            }
         }
      });
   }

   private String[] unwrap(PacketEvent event) {
      return !XMaterial.supports(9) ? (String[])Stream.of((Object[])event.getPacket().getChatComponentArrays().read(0)).map((component) -> {
         return ComponentSerializer.parse(component.getJson());
      }).map((text) -> {
         String decrypted = ComponentSerializer.toString(text);
         return color(decode(decrypted.substring(11, decrypted.length() - 13)));
      }).toArray((x$0) -> {
         return new String[x$0];
      }) : (String[])event.getPacket().getStringArrays().read(0);
   }

   private static String decode(String escaped) {
      if (!escaped.contains("\\u")) {
         return escaped;
      } else {
         StringBuilder processed = new StringBuilder();

         for(int position = escaped.indexOf("\\u"); position != -1; position = escaped.indexOf("\\u")) {
            if (position != 0) {
               processed.append(escaped, 0, position);
            }

            String token = escaped.substring(position + 2, position + 6);
            escaped = escaped.substring(position + 6);
            processed.append((char)Integer.parseInt(token, 16));
         }

         processed.append(escaped);
         return processed.toString();
      }
   }

   public static SignMenuFactory getFactory() {
      return factory;
   }

   public final class Menu {
      private final List text;
      private BiPredicate response;
      private boolean reopenIfFail;
      private BlockPosition position;
      private boolean forceClose;

      Menu(List text) {
         this.text = text;
      }

      public SignMenuFactory.Menu reopenIfFail(boolean value) {
         this.reopenIfFail = value;
         return this;
      }

      public SignMenuFactory.Menu response(BiPredicate response) {
         this.response = response;
         return this;
      }

      public void open(Player player) {
         Objects.requireNonNull(player, "player is null");
         if (player.isOnline()) {
            Location location = player.getLocation();
            this.position = new BlockPosition(location.getBlockX(), location.getBlockY() + (255 - location.getBlockY()), location.getBlockZ());
            player.sendBlockChange(this.position.toLocation(location.getWorld()), XMaterial.OAK_WALL_SIGN.parseMaterial(), (byte)0);
            player.sendSignChange(this.position.toLocation(location.getWorld()), (String[])this.text.toArray(new String[0]));
            PacketContainer openSign = ProtocolLibrary.getProtocolManager().createPacket(Server.OPEN_SIGN_EDITOR);
            PacketContainer signData = ProtocolLibrary.getProtocolManager().createPacket(Server.TILE_ENTITY_DATA);
            openSign.getBlockPositionModifier().write(0, this.position);
            NbtCompound signNBT = (NbtCompound)signData.getNbtModifier().read(0);

            for(int line = 0; line < 4; ++line) {
               signNBT.put("Text" + (line + 1), this.text.size() > line ? String.format("{\"text\":\"%s\"}", SignMenuFactory.color((String)this.text.get(line))) : "");
            }

            signNBT.put("x", this.position.getX());
            signNBT.put("y", this.position.getY());
            signNBT.put("z", this.position.getZ());
            signNBT.put("id", "minecraft:sign");
            signData.getBlockPositionModifier().write(0, this.position);
            if (!XMaterial.supports(18)) {
               signData.getIntegers().write(0, 9);
            }

            signData.getNbtModifier().write(0, signNBT);

            try {
               ProtocolLibrary.getProtocolManager().sendServerPacket(player, signData);
               ProtocolLibrary.getProtocolManager().sendServerPacket(player, openSign);
            } catch (InvocationTargetException var7) {
               var7.printStackTrace();
            }

            SignMenuFactory.this.inputs.put(player, this);
         }
      }

      public void close(Player player, boolean force) {
         this.forceClose = force;
         if (player.isOnline()) {
            player.closeInventory();
         }

      }

      public void close(Player player) {
         this.close(player, false);
      }
   }
}
