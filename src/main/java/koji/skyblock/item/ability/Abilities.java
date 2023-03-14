package koji.skyblock.item.ability;

import com.google.common.util.concurrent.AtomicDouble;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import koji.developerkit.listener.KListener;
import koji.developerkit.runnable.KRunnable;
import koji.developerkit.runnable.KRunnable.CancellationActivationType;
import koji.developerkit.utils.ItemBuilder;
import koji.developerkit.utils.duplet.Tuple;
import koji.developerkit.utils.xseries.ReflectionUtils;
import koji.developerkit.utils.xseries.XMaterial;
import koji.developerkit.utils.xseries.XSound;
import koji.skyblock.Skyblock;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.enchants.enchants.DragonTracer;
import koji.skyblock.player.MiscListeners;
import koji.skyblock.player.PClass;
import koji.skyblock.player.Stats;
import koji.skyblock.player.api.RightClickAbilityCastEvent;
import koji.skyblock.player.damage.DamageListener;
import koji.skyblock.player.events.PlaceholderChangeRequest;
import koji.skyblock.player.events.SkyblockDamageEvent;
import koji.skyblock.player.events.SkyblockMagicDamageEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class Abilities extends KListener {
   private static final HashMap econ = new HashMap();
   private static final List balloons = new ArrayList(Arrays.asList("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTJkZDExZGEwNDI1MmY3NmI2OTM0YmMyNjYxMmY1NGYyNjRmMzBlZWQ3NGRmODk5NDEyMDllMTkxYmViYzBhMiJ9fX0=", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzYzODdmYzI0Njg5M2Q5MmE2ZGQ5ZWExYjUyZGNkNTgxZTk5MWVlZWUyZTI2M2IyN2ZmZjFiY2YxYjE1NGViNyJ9fX0=", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjg2OGU2YTVjNGE0NDVkNjBhMzA1MGI1YmVjMWQzN2FmMWIyNTk0Mzc0NWQyZDQ3OTgwMGM4NDM2NDg4MDY1YSJ9fX0=", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjA1MmJlMWMwNmE0YTMyNTEyOWQ2ZjQxYmI4NGYwZWExY2E2ZjlmNjllYmRmZmY0MzE2ZTc0MjQ1MWM3OWMyMSJ9fX0=", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGY4NTUyMmVlODE1ZDExMDU4N2ZmZmM3NDExM2Y0MTlkOTI5NTk4ZTI0NjNiOGNlOWQzOWNhYTlmYjZmZjVhYiJ9fX0=", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTI2ZWM3Y2QzYjZhZTI0OTk5NzEzN2MxYjk0ODY3YzY2ZTk3NDk5ZGEwNzFiZjUwYWRmZDM3MDM0MTMyZmEwMyJ9fX0=", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWVmMTYyZGVmODQ1YWEzZGM3ZDQ2Y2QwOGE3YmY5NWJiZGZkMzJkMzgxMjE1YWE0MWJmZmFkNTIyNDI5ODcyOCJ9fX0="));
   private static final List colors;

   public static Ability getEmeraldBlade() {
      if (CustomItem.getAllAbilities().containsKey("emeraldBlade")) {
         final Ability emeraldBlade = CustomItem.getAbility("emeraldBlade");
         (new KRunnable((task) -> {
            Iterator var1 = Bukkit.getServer().getOnlinePlayers().iterator();

            while(var1.hasNext()) {
               Player p = (Player)var1.next();
               if (isValidItem(p.getItemInHand()) && (new CustomItem(p.getItemInHand())).hasAbility("emeraldBlade")) {
                  double balance = Skyblock.getPlugin().getEconomy().getBalance(p);
                  if ((Double)econ.getOrDefault(p, -1.7976931348623157E308D) != balance) {
                     econ.put(p, balance);
                     p.setItemInHand(emeraldBlade(p, new CustomItem(p.getItemInHand())));
                  }
               }
            }

         })).runTaskTimer(Skyblock.getPlugin(), 1L, 3L);
         return new OverwriteAbility() {
            public Ability getOriginalAbility() {
               return emeraldBlade;
            }

            @EventHandler
            public void onItemHold(PlayerItemHeldEvent e) {
               Player p = e.getPlayer();
               ItemStack item = p.getInventory().getItem(e.getNewSlot());
               CustomItem ci = new CustomItem(item);
               p.getInventory().setItem(e.getNewSlot(), Abilities.emeraldBlade(p, ci));
            }

            public File getFile() {
               return emeraldBlade.getFile();
            }
         };
      } else {
         return null;
      }
   }

   public static void putAbilities() {
      if (CustomItem.getAbility("emeraldBlade") != null) {
         CustomItem.registerAbility("emeraldBlade", getEmeraldBlade());
      }

      if (CustomItem.getAbility("radiantOrb") != null) {
         CustomItem.registerAbility("radiantOrb", new OverwriteAbility() {
            final HashMap tasks = new HashMap();

            public Ability getOriginalAbility() {
               return CustomItem.getAbility("radiantOrb");
            }

            @EventHandler
            public void onRightClick(PlayerInteractEvent e) {
               try {
                  Player p = e.getPlayer();
                  PClass pS = PClass.getPlayer(p);
                  if (pS.getStat(Stats.MANA) >= pS.getStat(Stats.MAX_MANA) / 2.0D) {
                     pS.removeStat(Stats.MANA, pS.getStat(Stats.MAX_MANA) / 2.0D);
                     if (this.tasks.containsKey(p)) {
                        KRunnable task = (KRunnable)this.tasks.get(p);
                        task.cancel();
                        String name = ChatColor.GREEN + "Radiant Power Orb";
                        p.sendMessage(ChatColor.YELLOW + "Your previous " + name + ChatColor.YELLOW + " was removed!");
                     }

                     Location aLoc = e.getPlayer().getLocation().add(e.getPlayer().getLocation().getDirection().normalize());
                     aLoc.setY((double)e.getPlayer().getLocation().getBlockY() + 0.5D);
                     final ArmorStand a = (ArmorStand)Skyblock.getArmorStand(aLoc);
                     a.setHelmet((new ItemBuilder(XMaterial.PLAYER_HEAD)).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2FiNGM0ZDZlZTY5YmMyNGJiYTJiOGZhZjY3YjlmNzA0YTA2YjAxYWE5M2YzZWZhNmFlZjdhOTY5NmM0ZmVlZiJ9fX0=").build());
                     a.setSmall(true);
                     a.setGravity(false);
                     a.setRemoveWhenFarAway(false);
                     final ArmorStand nameTag = (ArmorStand)Skyblock.getArmorStand(a.getLocation().add(0.0D, 1.0D, 0.0D));
                     nameTag.setCustomNameVisible(true);
                     nameTag.setGravity(false);
                     nameTag.setSmall(true);
                     nameTag.setRemoveWhenFarAway(false);
                     AtomicInteger i = new AtomicInteger(30);
                     Skyblock.getAbilityParticleManager().radiantOrbSpawn(nameTag);
                     playSound(p, XSound.BLOCK_WOODEN_BUTTON_CLICK_OFF, 1.0F);
                     BukkitTask bRunnable = (new BukkitRunnable() {
                        double degree = 0.0D;
                        double lastY;

                        {
                           this.lastY = Math.sin(Math.toRadians(this.degree * 4.5D));
                        }

                        public void run() {
                           try {
                              double radians = Math.toRadians(this.degree * 4.5D);
                              double y = Math.sin(radians);
                              a.setVelocity(new Vector(0.0D, y - this.lastY, 0.0D));
                              nameTag.setVelocity(new Vector(0.0D, y - this.lastY, 0.0D));
                              this.lastY = y;
                              Location aLoc = a.getLocation();
                              aLoc.setYaw(aLoc.getYaw() + 18.0F);
                              a.teleport(aLoc);
                              Skyblock.getAbilityParticleManager().radiantOrbRotate(a, nameTag);
                              ++this.degree;
                           } catch (Throwable var6) {
                              throw var6;
                           }
                        }
                     }).runTaskTimer(Skyblock.getPlugin(), 0L, 1L);
                     KRunnable run = (new KRunnable((taskx) -> {
                        nameTag.setCustomName(ChatColor.GREEN + "Radiant " + ChatColor.YELLOW + i.get() + "s");
                        i.getAndDecrement();
                        Collection entityList = a.getWorld().getNearbyEntities(a.getLocation(), 18.0D, 18.0D, 18.0D);
                        entityList.removeIf((entity) -> {
                           return !(entity instanceof Player);
                        });
                        Iterator var5 = ((List)entityList.stream().map((entity) -> {
                           return (Player)entity;
                        }).collect(Collectors.toList())).subList(0, Math.min(5, entityList.size())).iterator();

                        while(var5.hasNext()) {
                           Player player = (Player)var5.next();
                           PClass playerClass = PClass.getPlayer(player);
                           playerClass.addStat(Stats.HEALTH, playerClass.getStat(Stats.MAX_HEALTH) / 100.0D);
                           Skyblock.getAbilityParticleManager().radiantOrbPlayer(player);
                        }

                     }, 600L)).cancelTask((taskx) -> {
                        bRunnable.cancel();
                        a.remove();
                        nameTag.remove();
                        this.tasks.remove(p);
                     }, CancellationActivationType.BOTH).cancelTask((taskx) -> {
                        Skyblock.getAbilityParticleManager().radiantOrbDespawn(nameTag);
                     }, CancellationActivationType.TIME);
                     this.tasks.put(p, run);
                     run.runTaskTimer(Skyblock.getPlugin(), 0L, 20L);
                  } else {
                     p.sendMessage(ChatColor.RED + "Not enough mana! " + ChatColor.GRAY + "(Costs " + num(Math.floor(pS.getStat(Stats.MAX_MANA) / 2.0D)) + " mana)");
                  }

               } catch (Throwable var10) {
                  throw var10;
               }
            }

            public boolean sendMessage() {
               return false;
            }

            public File getFile() {
               return CustomItem.getAbility("radiantOrb").getFile();
            }
         });
      }

      if (CustomItem.getAbility("witherImpact") != null) {
         CustomItem.registerAbility("witherImpact", new OverwriteAbility() {
            public Ability getOriginalAbility() {
               return CustomItem.getAbility("witherImpact");
            }

            @EventHandler
            public void onRightClick(RightClickAbilityCastEvent e) {
               Player p = e.getPlayer();
               PClass pS = PClass.getPlayer(p);
               if (pS.getDamageReduction("witherImpact") == 0.0D) {
                  double statAdded = pS.getStat(Stats.CRIT_DAMAGE) * 1.5D;
                  pS.addAbsorption("witherImpact", statAdded);
                  pS.addDamageReduction("witherImpact", 10.0D, false);
                  (new KRunnable((task) -> {
                     pS.resetAbsorption("witherImpact");
                     pS.resetDamageReduction("witherImpact");
                     pS.addStat(Stats.HEALTH, pS.getAbsorption("witherImpact"));
                  })).runTaskLater(Skyblock.getPlugin(), 100L);
               }

               if (Abilities.teleport(p, this.getInt("range"))) {
                  playSound(p, XSound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0F);
                  Skyblock.getAbilityParticleManager().witherImpact(p);
                  Bukkit.getPluginManager().callEvent(new SkyblockMagicDamageEvent(p, p.getLocation(), 10000.0D, 0.3D, 6.0D, 6.0D, 6.0D, "Implosion", true));
               }

            }

            @EventHandler
            public void onPlaceholder(PlaceholderChangeRequest e) {
               e.setItem(e.getItem().changePlaceholder("witherImpact", "damage", num(PClass.format(PClass.getPlayer(e.getPlayer()).getAbilityDamage(10000.0D, 0.3D)))));
            }

            public File getFile() {
               return CustomItem.getAbility("witherImpact").getFile();
            }
         });
      }

      if (CustomItem.getAbility("instantTransmission") != null) {
         CustomItem.registerAbility("instantTransmission", new OverwriteAbility() {
            public Ability getOriginalAbility() {
               return CustomItem.getAbility("instantTransmission");
            }

            @EventHandler
            public void onRightClick(RightClickAbilityCastEvent e) {
               Player p = e.getPlayer();
               if (Abilities.teleport(p, this.getInt("range"))) {
                  playSound(p, XSound.ENTITY_ENDERMAN_TELEPORT, 1.0F);
               }

            }

            public File getFile() {
               return CustomItem.getAbility("instantTransmission").getFile();
            }
         });
      }

      if (CustomItem.getAbility("meSmashHead") != null) {
         CustomItem.registerAbility("meSmashHead", new OverwriteAbility() {
            final List entities = new ArrayList();
            final List players = new ArrayList();

            public Ability getOriginalAbility() {
               return CustomItem.getAbility("meSmashHead");
            }

            @EventHandler
            public void onRightClick(RightClickAbilityCastEvent e) {
               this.players.add(e.getPlayer());
            }

            @EventHandler
            public void onSkyblockDamage(SkyblockDamageEvent e) {
               if (!e.isPlayerReceivingDamage() && e.getDamager() instanceof Player) {
                  Player p = (Player)e.getDamager();
                  if (this.players.contains(p)) {
                     this.players.remove(p);
                     if (isMobType(e.getEntity(), new EntityType[]{EntityType.COW, EntityType.MUSHROOM_COW, EntityType.CHICKEN, EntityType.PIG, EntityType.SHEEP, EntityType.WOLF})) {
                        this.entities.add(e.getEntity());
                     }

                     (new KRunnable((task) -> {
                        this.entities.remove(e.getEntity());
                     })).runTaskLater(Skyblock.getPlugin(), 600L);
                     e.setFinalDamageMultiplier(e.getFinalDamageMultiplier() + 1.0D);
                  }
               } else if (e.isPlayerReceivingDamage() && e.getEntity() instanceof Player && this.entities.contains(e.getDamager())) {
                  e.setDamageReduction(e.getDamageReduction() + 65.0D);
               }

            }

            public File getFile() {
               return CustomItem.getAbility("meSmashHead").getFile();
            }
         });
      }

      if (CustomItem.getAbility("leap") != null) {
         CustomItem.registerAbility("leap", new OverwriteAbility() {
            public Ability getOriginalAbility() {
               return CustomItem.getAbility("leap");
            }

            @EventHandler
            public void onRightClick(RightClickAbilityCastEvent e) {
               Player p = e.getPlayer();
               Location loc = p.getLocation().clone();
               loc.setPitch(loc.getPitch() > -25.0F ? -25.0F : loc.getPitch());
               Vector vector = loc.getDirection().multiply(this.getDouble("power"));
               p.setVelocity(vector);
               double abilityDamage = PClass.getPlayer(p).getAbilityDamage(this.getDouble("damage"), 1.0D);
               p.setItemInHand(e.getItem().changePlaceholder("leap", "damage", num(commaify(abilityDamage))).buildWithAbilities());
               (new KRunnable((task) -> {
                  if (e.getPlayer().isOnGround() || !e.getPlayer().getLocation().add(0.0D, -0.3751D, 0.0D).getBlock().getType().isTransparent()) {
                     Skyblock.getAbilityParticleManager().leap(p);
                     Bukkit.getPluginManager().callEvent(new SkyblockMagicDamageEvent(p, p.getLocation(), 350.0D, 1.0D, 4.0D, 4.0D, 4.0D, "Leap", false, (entity) -> {
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, Integer.MAX_VALUE, false, false));
                        (new KRunnable((thing) -> {
                           entity.removePotionEffect(PotionEffectType.SLOW);
                        })).runTaskLater(Skyblock.getPlugin(), (long)(this.getDouble("frozen") * 20.0D));
                     }));
                     task.cancel();
                  }

               })).runTaskTimer(Skyblock.getPlugin(), 5L, 1L);
            }

            @EventHandler
            public void onPlaceholderRequest(PlaceholderChangeRequest e) {
               PClass pS = PClass.getPlayer(e.getPlayer());
               double abilityDamage = pS.getAbilityDamage(this.getDouble("damage"), 1.0D);
               e.setItem(e.getItem().changePlaceholder("leap", "damage", num(commaify(abilityDamage))));
            }

            public File getFile() {
               return CustomItem.getAbility("leap").getFile();
            }
         });
      }

      if (CustomItem.getAbility("heatSeekingRose") != null) {
         CustomItem.registerAbility("heatSeekingRose", new OverwriteAbility() {
            public Ability getOriginalAbility() {
               return CustomItem.getAbility("heatSeekingRose");
            }

            @EventHandler
            public void onCast(final RightClickAbilityCastEvent e) {
               final Player p = e.getPlayer();
               final ArmorStand ar = (ArmorStand)Skyblock.getArmorStand(p.getEyeLocation().subtract(0.0D, 1.0D, 0.0D));
               ar.setGravity(false);
               ar.setArms(true);
               ar.setItemInHand((new ItemBuilder(XMaterial.POPPY)).build());
               ar.teleport(ar.getLocation().setDirection(p.getLocation().getDirection()));
               (new BukkitRunnable() {
                  final Location startLocation = ar.getLocation();
                  int timesLockedOn = 0;
                  Entity lockedEntity = null;
                  final List entitiesAlreadyLocked = new ArrayList();
                  final double maxDistance = Math.pow(getDouble("max-distance"), 2.0D);

                  public void run() {
                     if (!ar.isDead()) {
                        if (this.timesLockedOn > 3 || ar.getLocation().distanceSquared(this.startLocation) > this.maxDistance) {
                           ar.remove();
                           this.cancel();
                           return;
                        }

                        if (this.lockedEntity == null && (this.timesLockedOn > 0 || ar.getLocation().distanceSquared(this.startLocation) > Math.pow(getDouble("minimum-travel"), 2.0D))) {
                           List entities = (List)ar.getNearbyEntities(5.0D, 5.0D, 5.0D).stream().filter((entityx) -> {
                              boolean bool = !this.entitiesAlreadyLocked.contains(entityx) && ar.hasLineOfSight(entityx) && !entityx.isDead();
                              if (!(entityx instanceof LivingEntity)) {
                                 return false;
                              } else if (entityx instanceof ArmorStand) {
                                 return bool && ((ArmorStand)entityx).isVisible() && !((ArmorStand)entityx).isMarker();
                              } else {
                                 return bool && !(entityx instanceof Player);
                              }
                           }).sorted(Comparator.comparingDouble((entityx) -> {
                              return entityx.getLocation().distanceSquared(ar.getLocation());
                           })).collect(Collectors.toList());
                           if (!entities.isEmpty()) {
                              this.lockedEntity = (Entity)entities.get(0);
                              ++this.timesLockedOn;
                              this.entitiesAlreadyLocked.add(this.lockedEntity);
                           }
                        }

                        if (this.lockedEntity != null && ar.getNearbyEntities(1.0D, 1.0D, 1.0D).contains(this.lockedEntity)) {
                           LivingEntity entity = (LivingEntity)this.lockedEntity;
                           entity.setNoDamageTicks(0);
                           entity.setLastDamageCause(new SkyblockDamageEvent(p, entity, false, SkyblockDamageEvent.Type.UNSPECIFIED, 0.0D, e.getItem()));
                           entity.damage(0.0D, p);
                           Skyblock.getAbilityParticleManager().heatSeekingRose(this.lockedEntity.getLocation());
                           this.lockedEntity = null;
                           return;
                        }

                        Location loc = ar.getLocation().add(0.0D, 1.0D, 0.0D);
                        if (!loc.getBlock().getType().isTransparent()) {
                           Skyblock.getAbilityParticleManager().heatSeekingRose(loc);
                           ar.remove();
                           this.cancel();
                           return;
                        }

                        Location arLoc = ar.getLocation();
                        Location setDirection = ar.getLocation().clone();
                        if (this.lockedEntity != null) {
                           setDirection.setDirection(this.lockedEntity.getLocation().subtract(ar.getLocation().toVector()).toVector());
                        }

                        setDirection.add(arLoc.getDirection()).multiply(1.0D);
                        ar.teleport(setDirection);
                     } else if (ar.isDead()) {
                        this.cancel();
                     }

                  }
               }).runTaskTimer(Skyblock.getPlugin(), 0L, 1L);
            }

            public double getManaCost(Player p) {
               return PClass.getPlayer(p).getStat(Stats.MAX_MANA) / 10.0D;
            }

            public File getFile() {
               return CustomItem.getAbility("heatSeekingRose").getFile();
            }
         });
      }

      if (CustomItem.getAbility("tripleShot") != null) {
         CustomItem.registerAbility("tripleShot", new OverwriteAbility() {
            public Ability getOriginalAbility() {
               return CustomItem.getAbility("tripleShot");
            }

            @EventHandler
            public void onArrowFire(EntityShootBowEvent e) {
               if (e.getEntity() instanceof Player) {
                  Arrow arrowRight = (Arrow)e.getEntity().launchProjectile(Arrow.class, e.getProjectile().getVelocity());
                  arrowRight.setShooter(e.getEntity());
                  arrowRight.setVelocity(this.rotateVectorAroundY(arrowRight.getVelocity(), 12.5D));
                  MiscListeners.getArrows().put(arrowRight, Tuple.of((Player)e.getEntity(), new CustomItem(((Player)e.getEntity()).getItemInHand()), 0.4D));
                  Arrow arrowLeft = (Arrow)e.getEntity().launchProjectile(Arrow.class, e.getProjectile().getVelocity());
                  arrowLeft.setShooter(e.getEntity());
                  arrowLeft.setVelocity(this.rotateVectorAroundY(arrowLeft.getVelocity(), -12.5D));
                  MiscListeners.getArrows().put(arrowLeft, Tuple.of((Player)e.getEntity(), new CustomItem(((Player)e.getEntity()).getItemInHand()), 0.4D));
                  (new DragonTracer.HomingArrowRunnable(arrowRight, 10.0D)).runTaskTimer(Skyblock.getPlugin(), 5L, 1L);
                  (new DragonTracer.HomingArrowRunnable(arrowLeft, 10.0D)).runTaskTimer(Skyblock.getPlugin(), 5L, 1L);
               }

            }

            public Vector rotateVectorAroundY(Vector vector, double degrees) {
               double rad = Math.toRadians(degrees);
               double currentX = vector.getX();
               double currentZ = vector.getZ();
               double cosine = Math.cos(rad);
               double sine = Math.sin(rad);
               return new Vector(cosine * currentX - sine * currentZ, vector.getY(), sine * currentX + cosine * currentZ);
            }

            public File getFile() {
               return CustomItem.getAbility("tripleShot").getFile();
            }
         });
      }

      if (CustomItem.getAbility("burningSouls") != null) {
         CustomItem.registerAbility("burningSouls", new OverwriteAbility() {
            public Ability getOriginalAbility() {
               return CustomItem.getAbility("burningSouls");
            }

            public BiPredicate getExtraConditions() {
               return (player, item) -> {
                  Stream entities = player.getNearbyEntities(6.0D, 6.0D, 6.0D).stream().filter((entity) -> {
                     return !(entity instanceof Player) && !(entity instanceof ArmorStand) && entity instanceof LivingEntity;
                  });
                  if (entities.findAny().isPresent()) {
                     return true;
                  } else {
                     player.sendMessage(ChatColor.RED + "No enemies within range!");
                     return false;
                  }
               };
            }

            @EventHandler
            public void onSpellCast(RightClickAbilityCastEvent e) {
               Player p = e.getPlayer();
               PClass.getPlayer(p).addBonusStat("burningSouls", Stats.DEFENSE, 300.0D);
               (new KRunnable((task) -> {
                  PClass.getPlayer(p).resetBonusStats("burningSouls");
               })).runTaskLater(Skyblock.getPlugin(), 99L);
               playSound(p, XSound.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, 1.0F);
               playSound(p, XSound.ITEM_FLINTANDSTEEL_USE, 0.0F);
               (new KRunnable((task) -> {
                  playSound(p, XSound.ENTITY_BLAZE_SHOOT, 1.0F);
               })).runTaskLater(Skyblock.getPlugin(), 10L);
               List entities = (List)p.getNearbyEntities(6.0D, 6.0D, 6.0D).stream().filter((entityx) -> {
                  return !(entityx instanceof Player) && !(entityx instanceof ArmorStand) && entityx instanceof LivingEntity;
               }).collect(Collectors.toList());
               Iterator var4 = entities.iterator();

               while(var4.hasNext()) {
                  Entity entity = (Entity)var4.next();
                  Location loc = this.faceLocation(p, entity.getLocation());
                  loc = loc.add(entity.getLocation().toVector().subtract(loc.toVector()).normalize());
                  ArmorStand stand = (ArmorStand)Skyblock.getArmorStand(loc);
                  stand.setGravity(false);
                  AtomicDouble degree = new AtomicDouble(0.0D);
                  (new KRunnable((task) -> {
                     Location location = this.faceLocation(stand, entity.getLocation());
                     double x;
                     if (stand.getLocation().distanceSquared(entity.getLocation()) <= 0.5D) {
                        task.cancel();
                        x = PClass.getPlayer(p).getAbilityDamage(30000.0D, 0.1D);
                        (new KRunnable((nextTask) -> {
                           if (entity.isDead()) {
                              nextTask.cancel();
                           } else {
                              double damage = x / 5.0D;
                              Skyblock.getAbilityParticleManager().pigmanDamage(entity.getLocation());
                              entity.setLastDamageCause(new SkyblockDamageEvent(p, entity, false, SkyblockDamageEvent.Type.MAGIC, damage, e.getItem()));
                              ((LivingEntity)entity).damage(damage, p);
                              DamageListener.setDamageIndicator(entity.getLocation(), DamageListener.DamageType.NORMAL, damage);
                              PClass.getPlayer(p).setLastAttackedEntity(entity);
                           }
                        }, 100L)).runTaskTimer(Skyblock.getPlugin(), 0L, 20L);
                     } else {
                        stand.teleport(location);
                        stand.setVelocity(entity.getLocation().toVector().subtract(stand.getLocation().toVector()).normalize().multiply(0.1D));
                        x = 0.4D * Math.cos(Math.toRadians(degree.get()));
                        double y = 0.4D * Math.sin(Math.toRadians(degree.get()));
                        Vector vec = new Vector(x, 0.0D, y);
                        vec = this.rotateVectorAroundX(vec, (double)location.getPitch());
                        vec = this.rotateVectorAroundY(vec, (double)location.getYaw());
                        Skyblock.getAbilityParticleManager().pigmanSwordSpiral(stand.getLocation().add(vec).add(0.0D, 1.5D, 0.0D));
                        degree.getAndAdd(36.0D);
                     }
                  }, 100L)).cancelTask((task) -> {
                     stand.remove();
                  }, CancellationActivationType.BOTH).runTaskTimer(Skyblock.getPlugin(), 0L, 1L);
               }

            }

            public Location faceLocation(Entity entity, Location to) {
               if (entity.getWorld() != to.getWorld()) {
                  return entity.getLocation();
               } else {
                  Location fromLocation = entity.getLocation();
                  double xDiff = to.getX() - fromLocation.getX();
                  double yDiff = to.getY() - fromLocation.getY();
                  double zDiff = to.getZ() - fromLocation.getZ();
                  double distanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
                  double distanceY = Math.sqrt(distanceXZ * distanceXZ + yDiff * yDiff);
                  double yaw = Math.toDegrees(Math.acos(xDiff / distanceXZ));
                  double pitch = Math.toDegrees(Math.acos(yDiff / distanceY)) - 90.0D;
                  if (zDiff < 0.0D) {
                     yaw += Math.abs(180.0D - yaw) * 2.0D;
                  }

                  Location loc = entity.getLocation();
                  loc.setYaw((float)(yaw - 90.0D));
                  loc.setPitch((float)(pitch - 90.0D));
                  return loc;
               }
            }

            public Vector rotateVectorAroundX(Vector vector, double degrees) {
               double rad = Math.toRadians(degrees);
               double currentY = vector.getY();
               double currentZ = vector.getZ();
               double cosine = Math.cos(rad);
               double sine = Math.sin(rad);
               return new Vector(vector.getX(), currentY * cosine - currentZ * sine, currentY * sine + currentZ * cosine);
            }

            public Vector rotateVectorAroundY(Vector vector, double degrees) {
               double rad = Math.toRadians(degrees);
               double currentX = vector.getX();
               double currentZ = vector.getZ();
               double cosine = Math.cos(rad);
               double sine = Math.sin(rad);
               return new Vector(cosine * currentX - sine * currentZ, vector.getY(), sine * currentX + cosine * currentZ);
            }

            public File getFile() {
               return CustomItem.getAbility("burningSouls").getFile();
            }
         });
      }

      if (CustomItem.getAbility("dragonRage") != null) {
         CustomItem.registerAbility("dragonRage", new OverwriteAbility() {
            public Ability getOriginalAbility() {
               return CustomItem.getAbility("dragonRage");
            }

            @EventHandler
            public void onAbilityCast(RightClickAbilityCastEvent e) {
               final Player p = e.getPlayer();
               playSound(p, XSound.ENTITY_ENDER_DRAGON_GROWL, 1.0F);
               BukkitTask task = (new BukkitRunnable() {
                  final Location location = p.getEyeLocation();
                  final Vector dir = p.getLocation().getDirection().normalize().multiply(0.035D);
                  final CustomItem item = new CustomItem(p.getItemInHand());
                  final double damage = PClass.getPlayer(p).getAbilityDamage(12000.0D, 0.1D);
                  final double pitch;
                  final double yaw;
                  double degree;
                  double radius;
                  final double increaseDegree;
                  final double increaseRadius;

                  {
                     this.pitch = (double)(this.location.getPitch() + 90.0F);
                     this.yaw = (double)(-this.location.getYaw());
                     this.degree = 0.0D;
                     this.radius = 0.3D;
                     this.increaseDegree = 18.0D;
                     this.increaseRadius = 0.01125D;
                  }

                  public void run() {
                     for(int i = 0; i < 4; ++i) {
                        double x = this.radius * Math.cos(Math.toRadians(this.degree));
                        double y = this.radius * Math.sin(Math.toRadians(this.degree));
                        Vector vec = new Vector(x, 0.0D, y);
                        vec = this.rotateVectorAroundX(vec, this.pitch);
                        vec = this.rotateVectorAroundY(vec, this.yaw);
                        Location clone = this.location.clone().add(vec).add(0.0D, 0.15D, 0.0D);
                        Skyblock.getAbilityParticleManager().dragonRage(clone);
                        this.location.add(this.dir);
                        this.location.getWorld().getNearbyEntities(this.location, this.radius / 2.0D, this.radius / 2.0D, this.radius / 2.0D).stream().filter((entity) -> {
                           return !(entity instanceof Player) && !(entity instanceof ArmorStand) && entity instanceof LivingEntity;
                        }).forEach((e) -> {
                           Vector highestKnockback = this.dir.normalize().multiply(50);
                           e.setVelocity(new Vector(Math.max(-3.9D, Math.min(3.9D, highestKnockback.getX())), Math.max(-3.9D, Math.min(3.9D, highestKnockback.getY())), Math.max(-3.9D, Math.min(3.9D, highestKnockback.getZ()))));
                           e.setLastDamageCause(new SkyblockDamageEvent(p, e, false, SkyblockDamageEvent.Type.MAGIC, this.damage, this.item));
                           ((LivingEntity)e).damage(this.damage, p);
                           DamageListener.setDamageIndicator(e.getLocation(), DamageListener.DamageType.NORMAL, this.damage);
                        });
                        this.degree += 18.0D;
                        this.radius += 0.01125D;
                     }

                  }

                  public Vector rotateVectorAroundX(Vector vector, double degrees) {
                     double rad = Math.toRadians(degrees);
                     double currentY = vector.getY();
                     double currentZ = vector.getZ();
                     double cosine = Math.cos(rad);
                     double sine = Math.sin(rad);
                     return new Vector(vector.getX(), currentY * cosine - currentZ * sine, currentY * sine + currentZ * cosine);
                  }

                  public Vector rotateVectorAroundY(Vector vector, double degrees) {
                     double rad = Math.toRadians(degrees);
                     double currentX = vector.getX();
                     double currentZ = vector.getZ();
                     double cosine = Math.cos(rad);
                     double sine = Math.sin(rad);
                     return new Vector(currentX * cosine + currentZ * sine, vector.getY(), currentX * -sine + currentZ * cosine);
                  }
               }).runTaskTimer(Skyblock.getPlugin(), 0L, 1L);
               (new KRunnable((current) -> {
                  task.cancel();
               })).runTaskLater(Skyblock.getPlugin(), 20L);
            }

            public File getFile() {
               return CustomItem.getAbility("dragonRage").getFile();
            }
         });
      }

      if (CustomItem.getAbility("showtime") != null) {
         CustomItem.registerAbility("showtime", new OverwriteAbility() {
            public Ability getOriginalAbility() {
               return CustomItem.getAbility("showtime");
            }

            @EventHandler
            public void onCast(RightClickAbilityCastEvent e) {
               Player p = e.getPlayer();
               ArmorStand stand = (ArmorStand)Skyblock.getArmorStand(p.getLocation().add(p.getLocation().getDirection().normalize()).add(0.0D, 0.5D, 0.0D));
               stand.setSmall(true);
               stand.setGravity(false);
               stand.setHelmet((new ItemBuilder(XMaterial.PLAYER_HEAD)).setTexture((String)getRandom(Abilities.balloons)).build());
               Vector direction = stand.getLocation().getDirection();
               (new KRunnable((task) -> {
                  Location loc = stand.getLocation().add(0.0D, 1.0D, 0.0D);
                  if (loc.getBlock().getType().isTransparent() && !loc.getWorld().getNearbyEntities(loc, 0.5D, 0.5D, 0.5D).stream().anyMatch((entity) -> {
                     return !(entity instanceof Player) && !(entity instanceof ArmorStand) && entity instanceof LivingEntity;
                  })) {
                     Location rotate = stand.getLocation().clone();
                     rotate.setYaw(rotate.getYaw() + 18.0F);
                     stand.teleport(rotate);
                     stand.setVelocity(direction.normalize());
                  } else {
                     Color[] colors = Abilities.getColors();
                     FireworkEffect fireworkEffect = FireworkEffect.builder().flicker(true).withColor(new Color[]{colors[0], colors[1]}).build();
                     new Abilities.InstantFirework(fireworkEffect, loc);
                     if (loc.getWorld().getNearbyEntities(loc, 2.0D, 2.0D, 2.0D).contains(p)) {
                        Vector defaultVelocity = p.getLocation().toVector().subtract(loc.toVector()).normalize();
                        defaultVelocity.setY(defaultVelocity.getY() * 0.5D);
                        defaultVelocity.setX(defaultVelocity.getX() * 1.5D);
                        defaultVelocity.setZ(defaultVelocity.getZ() * 1.5D);
                        p.setVelocity(defaultVelocity);
                     }

                     Bukkit.getPluginManager().callEvent(new SkyblockMagicDamageEvent(p, loc, 1000.0D, 0.2D, 2.0D, 2.0D, 2.0D, "Bongo Staff", false));
                     task.cancel();
                  }
               }, 400L)).cancelTask((task) -> {
                  stand.remove();
               }, CancellationActivationType.BOTH).runTaskTimer(Skyblock.getPlugin(), 0L, 1L);
            }

            public File getFile() {
               return CustomItem.getAbility("showtime").getFile();
            }
         });
      }

      if (CustomItem.getAbility("doubleJump") != null) {
         CustomItem.registerAbility("doubleJump", new OverwriteAbility() {
            public Ability getOriginalAbility() {
               return CustomItem.getAbility("doubleJump");
            }

            @EventHandler
            public void onDoubleJump(PlayerToggleSneakEvent e) {
               Player p = e.getPlayer();
               String display = this.contains("display-name.message") ? this.getString("display-name.message") : capitalize(space(this.getIdentifier()).replace("_", " "));
               if (!p.isOnGround() && e.isSneaking() && PClass.getPlayer(p).subtractMana(new CustomItem(p.getInventory().getBoots()), this.getIdentifier(), display, this.getActualManaCost(p))) {
                  p.setVelocity(p.getLocation().getDirection().multiply(this.getDouble("power")).setY(1));
                  Skyblock.getAbilityParticleManager().doubleJump(p.getLocation());
               }

            }

            public File getFile() {
               return CustomItem.getAbility("doubleJump").getFile();
            }
         });
      }

   }

   private static Color[] getColors() {
      Color color1 = (Color)getRandom(colors);

      Color color2;
      for(color2 = (Color)getRandom(colors); color1.equals(color2); color2 = (Color)getRandom(colors)) {
      }

      return new Color[]{color1, color2};
   }

   private static boolean teleport(Player p, int range) {
      if (p.getLocation().getY() < 255.0D) {
         ArrayList blocks = (ArrayList)p.getLineOfSight((Set)null, range);
         Vector vec = p.getLocation().getDirection();
         Location playerLoc = p.getLocation().add(vec);
         boolean solidBlockFound = false;

         Location loc;
         for(int i = 0; i < range; ++i) {
            if (((Block)blocks.get(i)).getType().isSolid() || ((Block)blocks.get(i)).getRelative(BlockFace.DOWN).getType().isSolid()) {
               if (i - 1 >= 0) {
                  loc = ((Block)blocks.get(i - 1)).getLocation();
                  if (!((Block)blocks.get(i)).getRelative(BlockFace.UP).getType().isSolid()) {
                     loc = ((Block)blocks.get(i)).getLocation();
                  } else {
                     loc.setY(loc.getY() - 1.0D);
                  }

                  loc.setYaw(playerLoc.getYaw());
                  loc.setPitch(playerLoc.getPitch());
                  loc.setX(loc.getX() + 0.5D);
                  loc.setZ(loc.getZ() + 0.5D);
                  p.teleport(loc);
                  p.setFallDistance(0.0F);
                  p.sendMessage(ChatColor.RED + "There are blocks in the way!");
                  return true;
               }

               solidBlockFound = true;
               break;
            }
         }

         if (!solidBlockFound) {
            loc = ((Block)blocks.get(range - 1)).getLocation();
            loc.setYaw(playerLoc.getYaw());
            loc.setPitch(playerLoc.getPitch());
            loc.setX(loc.getX() + 0.5D);
            loc.setY(loc.getY() - 1.0D);
            loc.setZ(loc.getZ() + 0.5D);
            p.teleport(loc);
            return true;
         }
      } else {
         p.sendMessage(ChatColor.RED + "There are blocks in the way!");
      }

      return false;
   }

   private static ItemStack emeraldBlade(Player p, CustomItem item) {
      double currentMoney = Skyblock.getPlugin().getEconomy().getBalance(p);
      double currentDamage = 2.5D * Math.pow(currentMoney, 0.25D);
      return item.changePlaceholder("emeraldBlade", "damage", String.valueOf(round(currentDamage, 1))).removeStat(Stats.DAMAGE, item.getDouble("previousEmeraldBladeDamage"), false).addStat(Stats.DAMAGE, currentDamage, false).setDouble("previousEmeraldBladeDamage", currentDamage).buildWithAbilities();
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void onAbility(PlayerInteractEvent e) {
      if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK && !e.isCancelled()) && isValidItem(e.getItem())) {
         CustomItem item = new CustomItem(e.getItem());
         if (item.getAbilities().stream().anyMatch((a) -> {
            return a.hasEvent(RightClickAbilityCastEvent.class);
         })) {
            RightClickAbilityCastEvent rCACE = new RightClickAbilityCastEvent(e.getPlayer(), item, e);
            Bukkit.getPluginManager().callEvent(rCACE);
            PlaceholderChangeRequest pCR = new PlaceholderChangeRequest(e.getPlayer(), item);
            Bukkit.getPluginManager().callEvent(pCR);
            if (!XMaterial.supports(9)) {
               e.getPlayer().setItemInHand(pCR.getItem().buildWithAbilities());
            } else {
               try {
                  Method getOffhand = e.getPlayer().getInventory().getClass().getDeclaredMethod("getItemInOffHand");
                  ItemStack offItem = (ItemStack)getOffhand.invoke(e.getPlayer().getInventory());
                  if (offItem.equals(e.getItem())) {
                     Method setOffhand = e.getPlayer().getInventory().getClass().getDeclaredMethod("setItemInOffHand", ItemStack.class);
                     setOffhand.invoke(e.getPlayer().getInventory(), pCR.getItem().buildWithAbilities());
                  }

                  if (e.getPlayer().getInventory().getItemInHand().equals(e.getItem())) {
                     e.getPlayer().setItemInHand(pCR.getItem().buildWithAbilities());
                  }
               } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException var8) {
               }
            }
         }
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void onMagic(SkyblockMagicDamageEvent e) {
      List nearby = new ArrayList(e.getLocation().getWorld().getNearbyEntities(e.getLocation(), e.getNearbyX(), e.getNearbyY(), e.getNearbyZ()));
      nearby.removeIf((entityx) -> {
         return !(entityx instanceof LivingEntity) || entityx instanceof ArmorStand || entityx.hasMetadata("NPC") || entityx instanceof Player || entityx instanceof EnderDragon;
      });
      PClass pS = PClass.getPlayer(e.getPlayer());
      double damage = pS.getAbilityDamage(e.getBaseAbilityDamage(), e.getAbilityScaling());
      Iterator var6 = nearby.iterator();

      while(var6.hasNext()) {
         Entity entity = (Entity)var6.next();
         DamageListener.setDamageIndicator(entity.getLocation(), DamageListener.DamageType.NORMAL, damage);
         pS.setLastAttackedEntity(entity);
         entity.setLastDamageCause(new SkyblockDamageEvent(e.getPlayer(), entity, false, SkyblockDamageEvent.Type.MAGIC, damage, e.getPlayer().getItemInHand() == null ? null : new CustomItem(e.getPlayer().getItemInHand())));
         ((LivingEntity)entity).damage(damage, e.getPlayer());
         if (e.getTask() != null) {
            e.getTask().accept((LivingEntity)entity);
         }
      }

      if (nearby.size() != 0 && e.isSendMessage()) {
         e.getPlayer().sendMessage(ChatColor.GRAY + "Your " + e.getName() + " hit " + ChatColor.RED + nearby.size() + ChatColor.GRAY + " enemies for " + ChatColor.RED + PClass.format(pS.getAbilityDamage(e.getBaseAbilityDamage(), e.getAbilityScaling())) + ChatColor.GRAY + " damage.");
      }
   }

   static {
      colors = new ArrayList(Arrays.asList(Color.YELLOW, Color.LIME, Color.FUCHSIA, Color.PURPLE, Color.GREEN, Color.RED, Color.BLUE, Color.ORANGE, Color.AQUA, Color.BLUE, Color.NAVY, Color.OLIVE, Color.GRAY, Color.SILVER));
   }

   public static class InstantFirework {
      public InstantFirework(FireworkEffect fe, Location loc) {
         Firework f = (Firework)loc.getWorld().spawn(loc, Firework.class);
         FireworkMeta fm = f.getFireworkMeta();
         fm.addEffect(fe);
         f.setFireworkMeta(fm);

         try {
            Class entityFireworkClass = ReflectionUtils.getNMSClass("world.entity.projectile", "EntityFireworks");
            Class craftFireworkClass = ReflectionUtils.getCraftClass("entity.CraftFirework");
            Object firework = craftFireworkClass.cast(f);
            Method handle = firework.getClass().getMethod("getHandle");
            Object entityFirework = handle.invoke(firework);
            Field expectedLifespan = XMaterial.getVersion() < 17 ? entityFireworkClass.getDeclaredField("expectedLifespan") : entityFireworkClass.getDeclaredField("f");
            Field ticksFlown = XMaterial.getVersion() < 17 ? entityFireworkClass.getDeclaredField("ticksFlown") : entityFireworkClass.getDeclaredField("e");
            ticksFlown.setAccessible(true);
            ticksFlown.setInt(entityFirework, expectedLifespan.getInt(entityFirework) - 1);
            ticksFlown.setAccessible(false);
         } catch (Exception var12) {
            var12.printStackTrace();
         }

      }
   }
}
