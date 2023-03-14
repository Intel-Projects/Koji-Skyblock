package koji.skyblock.item.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import koji.developerkit.listener.KListener;
import koji.developerkit.utils.xseries.XMaterial;
import koji.skyblock.Skyblock;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.Plugin;

public class EntityHider extends KListener {
   protected Table observerEntityMap = HashBasedTable.create();
   private static final PacketType[] ENTITY_PACKETS;
   private ProtocolManager manager;
   private final Listener bukkitListener;
   private final PacketAdapter protocolListener;
   protected final EntityHider.Policy policy;

   @EventHandler
   public void onItemPickup(PlayerPickupItemEvent e) {
      if (!Skyblock.getEntityHider().canSee(e.getPlayer(), e.getItem())) {
         e.setCancelled(true);
      }

   }

   public EntityHider(Plugin plugin, EntityHider.Policy policy) {
      Preconditions.checkNotNull(plugin, "plugin cannot be NULL.");
      this.policy = policy;
      this.manager = ProtocolLibrary.getProtocolManager();
      plugin.getServer().getPluginManager().registerEvents(this.bukkitListener = this.constructBukkit(), plugin);
      this.manager.addPacketListener(this.protocolListener = this.constructProtocol(plugin));
   }

   protected boolean setVisibility(Player observer, int entityID, boolean visible) {
      switch(this.policy) {
      case BLACKLIST:
         return !this.setMembership(observer, entityID, !visible);
      case WHITELIST:
         return this.setMembership(observer, entityID, visible);
      default:
         throw new IllegalArgumentException("Unknown policy: " + this.policy);
      }
   }

   protected boolean setMembership(Player observer, int entityID, boolean member) {
      if (member) {
         return this.observerEntityMap.put(observer.getEntityId(), entityID, true) != null;
      } else {
         return this.observerEntityMap.remove(observer.getEntityId(), entityID) != null;
      }
   }

   protected boolean getMembership(Player observer, int entityID) {
      return this.observerEntityMap.contains(observer.getEntityId(), entityID);
   }

   protected boolean isVisible(Player observer, int entityID) {
      boolean presence = this.getMembership(observer, entityID);
      return this.policy == EntityHider.Policy.WHITELIST == presence;
   }

   protected void removeEntity(Entity entity) {
      int entityID = entity.getEntityId();
      Iterator var3 = this.observerEntityMap.rowMap().values().iterator();

      while(var3.hasNext()) {
         Map maps = (Map)var3.next();
         maps.remove(entityID);
      }

   }

   protected void removePlayer(Player p) {
      this.observerEntityMap.rowMap().remove(p.getEntityId());
   }

   private Listener constructBukkit() {
      return new Listener() {
         @EventHandler
         public void onEntityDeath(EntityDeathEvent e) {
            EntityHider.this.removeEntity(e.getEntity());
         }

         @EventHandler
         public void onChunkUnload(ChunkUnloadEvent e) {
            Entity[] var2 = e.getChunk().getEntities();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Entity entity = var2[var4];
               EntityHider.this.removeEntity(entity);
            }

         }

         @EventHandler
         public void onPlayerQuit(PlayerQuitEvent e) {
            EntityHider.this.removePlayer(e.getPlayer());
         }
      };
   }

   private PacketAdapter constructProtocol(Plugin plugin) {
      return new PacketAdapter(plugin, ENTITY_PACKETS) {
         public void onPacketSending(PacketEvent event) {
            int entityID = (Integer)event.getPacket().getIntegers().read(0);
            if (!EntityHider.this.isVisible(event.getPlayer(), entityID)) {
               event.setCancelled(true);
            }

         }
      };
   }

   public final boolean toggleEntity(Player observer, Entity entity) {
      if (this.isVisible(observer, entity.getEntityId())) {
         return this.hideEntity(observer, entity);
      } else {
         return !this.showEntity(observer, entity);
      }
   }

   public final boolean setHide(Player observer, Entity entity, boolean willSee) {
      return willSee ? this.showEntity(observer, entity) : this.hideEntity(observer, entity);
   }

   public final boolean showEntity(Player observer, Entity entity) {
      this.validate(observer, entity);
      boolean hiddenBefore = !this.setVisibility(observer, entity.getEntityId(), true);
      if (this.manager != null && hiddenBefore) {
         this.manager.updateEntity(entity, arrayList(new Player[]{observer}));
      }

      return hiddenBefore;
   }

   public final boolean hideEntity(Player observer, Entity entity) {
      this.validate(observer, entity);
      boolean visibleBefore = this.setVisibility(observer, entity.getEntityId(), false);
      if (visibleBefore) {
         PacketContainer destroyEntity = new PacketContainer(Server.ENTITY_DESTROY);
         if (XMaterial.supports(17)) {
            destroyEntity.getIntLists().write(0, new ArrayList(Collections.singletonList(entity.getEntityId())));
         } else {
            destroyEntity.getIntegerArrays().write(0, new int[]{entity.getEntityId()});
         }

         try {
            this.manager.sendServerPacket(observer, destroyEntity);
         } catch (InvocationTargetException var6) {
            throw new RuntimeException("Cannot send server packet.", var6);
         }
      }

      return visibleBefore;
   }

   public final boolean canSee(Player observer, Entity entity) {
      this.validate(observer, entity);
      return this.isVisible(observer, entity.getEntityId());
   }

   private void validate(Player observer, Entity entity) {
      Preconditions.checkNotNull(observer, "observer cannot be NULL.");
      Preconditions.checkNotNull(entity, "entity cannot be NULL.");
   }

   public EntityHider.Policy getPolicy() {
      return this.policy;
   }

   public void close() {
      if (this.manager != null) {
         HandlerList.unregisterAll(this.bukkitListener);
         this.manager.removePacketListener(this.protocolListener);
         this.manager = null;
      }

   }

   static {
      ENTITY_PACKETS = new PacketType[]{Server.ENTITY_EQUIPMENT, Server.BED, Server.ANIMATION, Server.NAMED_ENTITY_SPAWN, Server.COLLECT, Server.SPAWN_ENTITY, Server.SPAWN_ENTITY_LIVING, Server.SPAWN_ENTITY_PAINTING, Server.SPAWN_ENTITY_EXPERIENCE_ORB, Server.ENTITY_VELOCITY, Server.REL_ENTITY_MOVE, Server.ENTITY_LOOK, Server.ENTITY_MOVE_LOOK, Server.ENTITY_MOVE_LOOK, Server.ENTITY_TELEPORT, Server.ENTITY_HEAD_ROTATION, Server.ENTITY_STATUS, Server.ATTACH_ENTITY, Server.ENTITY_METADATA, Server.ENTITY_EFFECT, Server.REMOVE_ENTITY_EFFECT, Server.BLOCK_BREAK_ANIMATION};
   }

   public static enum Policy {
      WHITELIST,
      BLACKLIST;
   }
}
