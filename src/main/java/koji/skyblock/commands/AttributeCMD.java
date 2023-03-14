package koji.skyblock.commands;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import koji.developerkit.commands.KCommand;
import koji.skyblock.files.data.PlayerData;
import koji.skyblock.item.enchants.EnchantTableGUI;
import koji.skyblock.player.Stats;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class AttributeCMD extends KCommand implements TabExecutor {
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      try {
         if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.NOT_PLAYER.getMessage());
            return false;
         } else if ((args.length < 3 || !args[0].equalsIgnoreCase("set")) && (args.length != 2 || !args[0].equalsIgnoreCase("get"))) {
            sender.sendMessage(ChatColor.RED + "Usage: /attribute <set | get> <type> <value (if being set)>");
            return false;
         } else {
            Object returnType = null;
            String getMethodName = "";
            String setMethodName = "";
            double statValue = 0.0D;
            String setMessage = "";
            String var11 = args[1].toLowerCase();
            byte var12 = -1;
            switch(var11.hashCode()) {
            case -2012336102:
               if (var11.equals("dropitemalert")) {
                  var12 = 2;
               }
               break;
            case -1249319480:
               if (var11.equals("getsit")) {
                  var12 = 3;
               }
               break;
            case -1096817609:
               if (var11.equals("canseepets")) {
                  var12 = 1;
               }
               break;
            case 3024134:
               if (var11.equals("bits")) {
                  var12 = 0;
               }
               break;
            case 402023631:
               if (var11.equals("enchanttablesorting")) {
                  var12 = 4;
               }
            }

            label181: {
               Stats[] var13;
               int var14;
               int var15;
               switch(var12) {
               case 0:
                  if (args.length >= 3) {
                     try {
                        returnType = Integer.parseInt(args[2]);
                     } catch (NumberFormatException var19) {
                        sender.sendMessage(ChatColor.RED + "Bits must be an whole number!");
                        return false;
                     }

                     setMessage = ChatColor.GREEN + "Set bits to " + returnType;
                  }

                  getMethodName = getMethodName + "Bits";
                  setMethodName = setMethodName + "Bits";
                  break label181;
               case 1:
                  if (args.length >= 3) {
                     if (!args[2].equalsIgnoreCase("false") && !args[2].equalsIgnoreCase("true")) {
                        sender.sendMessage(ChatColor.RED + "CanSeePets must be a true or false!");
                        return false;
                     }

                     returnType = Boolean.parseBoolean(args[2]);
                     setMessage = ChatColor.GREEN + "Set Can See Pets to " + returnType;
                  }

                  getMethodName = getMethodName + "CanSeePets";
                  setMethodName = setMethodName + "CanSeePets";
                  break label181;
               case 2:
                  if (args.length >= 3) {
                     if (!args[2].equalsIgnoreCase("false") && !args[2].equalsIgnoreCase("true")) {
                        sender.sendMessage(ChatColor.RED + "DropItemAlert must be a true or false!");
                        return false;
                     }

                     returnType = Boolean.parseBoolean(args[2]);
                     if ((Boolean)returnType) {
                        setMessage = ChatColor.GREEN + "You will no longer be warned about Skyblock drop rules!";
                     } else {
                        setMessage = ChatColor.GREEN + "Set Drop Item Alert to " + returnType;
                     }
                  }

                  getMethodName = getMethodName + "DropItemAlert";
                  setMethodName = setMethodName + "DropItemAlert";
               case 3:
                  if (args.length >= 3) {
                     if (!args[2].equalsIgnoreCase("false") && !args[2].equalsIgnoreCase("true")) {
                        sender.sendMessage(ChatColor.RED + "GetsIt must be a true or false!");
                        return false;
                     }

                     returnType = Boolean.parseBoolean(args[2]);
                     if ((Boolean)returnType) {
                        setMessage = ChatColor.GREEN + "Items from mob and block drops directly go into your inventory...\n" + ChatColor.GREEN + "But you will no longer be bothered about it!";
                     } else {
                        setMessage = ChatColor.GREEN + "Set Gets It to " + returnType;
                     }
                  }

                  getMethodName = getMethodName + "sIt";
                  setMethodName = setMethodName + "GetsIt";
                  break label181;
               case 4:
                  if (args.length >= 3) {
                     try {
                        returnType = EnchantTableGUI.Sorting.valueOf(args[2].toUpperCase());
                     } catch (IllegalArgumentException var18) {
                        sender.sendMessage(ChatColor.RED + "EnchantTableSorting must be default, missing, az, or za!");
                        return false;
                     }

                     setMessage = ChatColor.GREEN + "Set Enchant Table Sorting to " + returnType;
                  }

                  getMethodName = getMethodName + "EnchantTableSorting";
                  setMethodName = setMethodName + "EnchantTableSorting";
                  break label181;
               default:
                  var13 = Stats.values();
                  var14 = var13.length;
                  var15 = 0;
               }

               while(var15 < var14) {
                  Stats stat = var13[var15];
                  if (stat != Stats.MANA && stat != Stats.HEALTH && stat.getPlaceholderTag().equalsIgnoreCase(args[1])) {
                     if (args.length >= 3) {
                        try {
                           statValue = Double.parseDouble(args[2]);
                           if ((int)statValue == 0 && (stat == Stats.MAX_HEALTH || stat == Stats.MAX_MANA)) {
                              sender.sendMessage(ChatColor.RED + "Health/mana cannot be 0!");
                              return false;
                           }
                        } catch (NumberFormatException var20) {
                           sender.sendMessage(ChatColor.RED + "Stat must be a number!");
                           return false;
                        }

                        setMessage = ChatColor.GREEN + "Set " + args[1] + " to " + statValue;
                     }

                     returnType = stat;
                     getMethodName = getMethodName + "Stat";
                     setMethodName = setMethodName + "Stat";
                     break;
                  }

                  ++var15;
               }
            }

            if (getMethodName.equalsIgnoreCase("")) {
               sender.sendMessage(ChatColor.RED + args[1] + " is not a valid attribute type! Valid types:\n" + ChatColor.RED + "bits, canseepets, dropitemalert, getsit, enchanttablesorting,\n " + ChatColor.RED + "and any stat (ex: crit_chance).");
               return false;
            } else {
               Player p = (Player)sender;
               Object[] objList;
               Method method;
               if (args[0].equalsIgnoreCase("set") && args.length >= 3) {
                  method = this.getMethod(returnType instanceof Stats, setMethodName, true, returnType);
                  objList = new Object[]{p, returnType, statValue};
                  Method getMethod = this.getMethod(returnType instanceof Stats, getMethodName, false, returnType);
                  Object invoked = this.invoke(returnType instanceof Stats, false, objList, getMethod);
                  this.invoke(returnType instanceof Stats, true, objList, method);
                  if (!invoked.toString().equals(returnType.toString()) && (args.length == 3 || !args[3].equalsIgnoreCase("silent"))) {
                     sender.sendMessage(setMessage);
                  }
               } else {
                  method = this.getMethod(returnType instanceof Stats, getMethodName, false, returnType);
                  objList = new Object[]{p, returnType};
                  Object returned = this.invoke(returnType instanceof Stats, false, objList, method);
                  sender.sendMessage(ChatColor.GREEN + args[1] + ": " + returned);
               }

               return true;
            }
         }
      } catch (Throwable var21) {
         throw var21;
      }
   }

   public Object invoke(boolean stat, boolean set, Object[] obj, Method method) {
      try {
         if (obj.length < 3) {
            return null;
         } else if (stat && set) {
            return method.invoke(PlayerData.getPlayerData(), obj[0], obj[1], obj[2]);
         } else {
            return !stat && !set ? method.invoke(PlayerData.getPlayerData(), obj[0]) : method.invoke(PlayerData.getPlayerData(), obj[0], obj[1]);
         }
      } catch (Throwable var6) {
         throw var6;
      }
   }

   public Method getMethod(boolean stat, String methodName, boolean set, Object returnType) {
      try {
         String setString = set ? "set" : "get";
         Method getMethod;
         if (!stat && !set) {
            getMethod = PlayerData.getPlayerData().getClass().getMethod(setString + methodName, Player.class);
         } else if (!stat) {
            getMethod = PlayerData.getPlayerData().getClass().getMethod(setString + methodName, Player.class, returnType.getClass());
         } else if (!set) {
            getMethod = PlayerData.getPlayerData().getClass().getMethod(setString + methodName, Player.class, Stats.class);
         } else {
            getMethod = PlayerData.getPlayerData().getClass().getMethod(setString + methodName, Player.class, Stats.class, Double.class);
         }

         return getMethod;
      } catch (Throwable var7) {
         throw var7;
      }
   }

   public List onTabComplete(CommandSender sender, Command command, String label, String[] args) {
      switch(args.length) {
      case 1:
         return this.partial(args[0], arrayList(new String[]{"set", "get"}));
      case 2:
         List list = Stats.getPlaceholders();
         list.addAll(arrayList(new String[]{"bits", "canseepets", "getsit", "enchanttablesorting"}));
         return this.partial(args[1], list);
      case 3:
         String var6 = args[2].toLowerCase();
         byte var7 = -1;
         switch(var6.hashCode()) {
         case -1249319480:
            if (var6.equals("getsit")) {
               var7 = 2;
            }
            break;
         case -1096817609:
            if (var6.equals("canseepets")) {
               var7 = 1;
            }
            break;
         case 402023631:
            if (var6.equals("enchanttablesorting")) {
               var7 = 3;
            }
            break;
         case 1760088538:
            if (var6.equals("itemDropAlert")) {
               var7 = 0;
            }
         }

         switch(var7) {
         case 0:
         case 1:
         case 2:
            return this.partial(args[2], arrayList(new String[]{"false", "true"}));
         case 3:
            return this.partial(args[2], arrayList(new String[]{"default", "missing", "az", "za"}));
         }
      default:
         return null;
      }
   }

   public List partial(String token, List collection) {
      List list = new ArrayList();
      StringUtil.copyPartialMatches(token, collection, list);
      Collections.sort(list);
      return list;
   }
}
