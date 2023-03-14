package koji.skyblock.player;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import koji.developerkit.listener.KListener;
import koji.skyblock.api.armorevents.event.ArmorEquipEvent;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.Rarity;
import koji.skyblock.item.enchants.Enchant;
import koji.skyblock.item.enchants.EnchantPriority;
import koji.skyblock.item.enchants.Priority;
import koji.skyblock.pets.Pet;
import koji.skyblock.pets.PetInstance;
import koji.skyblock.pets.api.PetDequipEvent;
import koji.skyblock.pets.api.PetEquipEvent;
import koji.skyblock.pets.api.PetEvent;
import koji.skyblock.player.api.RightClickAbilityCastEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityPortalExitEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.HorseJumpEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PigZapEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.event.entity.SheepRegrowWoolEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

public class GenericListeners extends KListener {
   public static Player parsePlayer(EntityEvent e) {
      Player p = null;
      if (e.getEntity() instanceof Player) {
         p = (Player)e.getEntity();
      }

      if (e.getEntity() instanceof Projectile && ((Projectile)e.getEntity()).getShooter() instanceof Player) {
         p = (Player)((Projectile)e.getEntity()).getShooter();
      }

      if (e instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent)e).getDamager() instanceof Player) {
         p = (Player)((EntityDamageByEntityEvent)e).getDamager();
      }

      if (e instanceof EntityDeathEvent) {
         p = ((EntityDeathEvent)e).getEntity().getKiller();
      }

      return p;
   }

   public static void runFunction(EntityEvent e) {
      Player p = parsePlayer(e);
      ItemStack item = null;
      if (p != null) {
         item = p.getItemInHand();
      }

      runFunction(e, item);
   }

   public static void runFunction(EntityEvent e, ItemStack heldItem) {
      try {
         Player p = parsePlayer(e);
         if (p != null) {
            ItemStack[] armorContents = parsePlayerEvent(e) instanceof ArmorEquipEvent ? ((ArmorEquipEvent)parsePlayerEvent(e)).getNewArmorContents() : p.getInventory().getArmorContents();
            ItemStack[] armorAndStuff = new ItemStack[]{armorContents[0], armorContents[1], armorContents[2], armorContents[3], heldItem};
            ItemStack[] var5 = armorAndStuff;
            int var6 = armorAndStuff.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               ItemStack item = var5[var7];
               if (isValidItem(item)) {
                  CustomItem ci = new CustomItem(item);
                  HashMap ordered = new HashMap();
                  Iterator var11 = ci.getEnchants().keySet().iterator();

                  while(var11.hasNext()) {
                     Enchant enchant = (Enchant)var11.next();
                     Method[] var13 = enchant.getClass().getMethods();
                     int var14 = var13.length;

                     for(int var15 = 0; var15 < var14; ++var15) {
                        Method method = var13[var15];
                        if (method.getParameterTypes().length > 0 && method.getParameterTypes()[0] == e.getClass()) {
                           Priority priority = method.getAnnotation(EnchantPriority.class) != null ? ((EnchantPriority)method.getAnnotation(EnchantPriority.class)).priority() : (Priority)EnchantPriority.class.getMethod("priority").getDefaultValue();
                           ordered.put(enchant, priority);
                        }
                     }
                  }

                  List enchants = ci.enchantByPriority(ordered);
                  if (!ordered.isEmpty()) {
                     enchants.forEach((ench) -> {
                        if (p.hasPermission("koji.skyblock.enchants." + ench.getNameNoSpace().toLowerCase())) {
                           ench.runEvent(parsePlayerEvent(e));
                        }

                     });
                  }

                  try {
                     ci.getAbilities().forEach((thing) -> {
                        if (parsePlayerEvent(e) instanceof RightClickAbilityCastEvent) {
                           String display = thing.contains("display-name.message") ? thing.getString("display-name.message") : capitalize(space(thing.getIdentifier()).replace("_", " "));
                           if (thing.hasEvent(parsePlayerEvent(e)) && PClass.hasPlayer(p) && (thing.getExtraConditions() == null || thing.getExtraConditions().test(p, ci)) && PClass.getPlayer(p).subtractMana(ci, thing.getIdentifier(), display, thing.getActualManaCost(p))) {
                              thing.runEvent(parsePlayerEvent(e));
                           }
                        } else if (thing.hasEvent(parsePlayerEvent(e))) {
                           thing.runEvent(parsePlayerEvent(e));
                        }

                     });
                  } catch (Exception var18) {
                  }
               }
            }

            if (PClass.hasPlayer(p)) {
               PetInstance petInstance = PClass.getPlayer(p).getPetInstance();
               Pet pet = petInstance.getPet();
               Rarity rarity = petInstance.getRarity();
               if (e instanceof PetEvent) {
                  pet = ((PetEvent)e).getPet();
                  if (e instanceof PetEquipEvent) {
                     rarity = ((PetEquipEvent)e).getRarity();
                  } else if (e instanceof PetDequipEvent) {
                     rarity = ((PetDequipEvent)e).getRarity();
                  }
               }

               if (pet != null) {
                  pet.getValidAbilities(rarity).forEach((petAbility) -> {
                     petAbility.runEvent(parsePlayerEvent(e));
                  });
               }
            }

         }
      } catch (Throwable var19) {
         throw var19;
      }
   }

   public static Event parsePlayerEvent(EntityEvent e) {
      return (Event)(e instanceof GenericListeners.PlayerEventToEntity ? ((GenericListeners.PlayerEventToEntity)e).getPlayerEventCounterpart() : e);
   }

   public void onEvent(EntityEvent e) {
      runFunction(e);
   }

   @EventHandler
   public void onCreeperPowerEvent(CreeperPowerEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onEntityBreakDoorEvent(EntityBreakDoorEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onEntityChangeBlockEvent(EntityChangeBlockEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onEntityCombustByBlockEvent(EntityCombustByBlockEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onEntityCombustByEntityEvent(EntityCombustByEntityEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onEntityDamageByBlockEvent(EntityDamageByBlockEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onEntityDeathEvent(EntityDeathEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onEntityExplodeEvent(EntityExplodeEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onEntityInteractEvent(EntityInteractEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onEntityPortalEnterEvent(EntityPortalEnterEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onEntityPortalEvent(EntityPortalEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onEntityPortalExitEvent(EntityPortalExitEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onEntityRegainHealthEvent(EntityRegainHealthEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onEntityShootBowEvent(EntityShootBowEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onEntityTameEvent(EntityTameEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onEntityTargetEvent(EntityTargetEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onEntityTargetLivingEntityEvent(EntityTargetLivingEntityEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onEntityTeleportEvent(EntityTeleportEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onEntityUnleashEvent(EntityUnleashEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onExpBottleEvent(ExpBottleEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onExplosionPrimeEvent(ExplosionPrimeEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onFireworkExplodeEvent(FireworkExplodeEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onHorseJumpEvent(HorseJumpEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onItemDespawnEvent(ItemDespawnEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onItemMergeEvent(ItemMergeEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onItemSpawnEvent(ItemSpawnEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onPigZapEvent(PigZapEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onPlayerDeathEvent(PlayerDeathEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onPotionSplashEvent(PotionSplashEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onProjectileHitEvent(ProjectileHitEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onSheepDyeWoolEvent(SheepDyeWoolEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onSheepRegrowWoolEvent(SheepRegrowWoolEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onSlimeSplitEvent(SlimeSplitEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onSpawnerSpawnEvent(SpawnerSpawnEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onPlayerUnleashEntityEvent(PlayerUnleashEntityEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onEntityDismountEvent(EntityDismountEvent event) {
      this.onEvent(event);
   }

   @EventHandler
   public void onEntityMountEvent(EntityMountEvent event) {
      this.onEvent(event);
   }

   public static class PlayerEventToEntity extends EntityEvent {
      private static final HandlerList handlers = new HandlerList();
      private final PlayerEvent playerEventCounterpart;

      public PlayerEventToEntity(Entity what, PlayerEvent playerEventCounterpart) {
         super(what);
         this.playerEventCounterpart = playerEventCounterpart;
      }

      public HandlerList getHandlers() {
         return handlers;
      }

      public static HandlerList getHandlerList() {
         return handlers;
      }

      public PlayerEvent getPlayerEventCounterpart() {
         return this.playerEventCounterpart;
      }
   }
}
