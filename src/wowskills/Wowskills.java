package wowskills;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import java.util.List;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import java.io.*;
import java.util.Properties;

public class Wowskills extends JavaPlugin implements Listener
{

    @Override
    public void onEnable()
    {
        getLogger().info("Wowskills enabled.");

        //Configuration
        File configFile = new File("plugins/wowskills.properties");
        if (!configFile.exists())
        {
            getLogger().info("Creating properties file.");
            try
            {
                configFile.createNewFile();
                PrintWriter writer = new PrintWriter(configFile);

                //Config Header
                writer.println("# Wow Skills Config File");
                writer.println("# Note: Cooldowns are in seconds");
                writer.println("");
                writer.println("");

                //Warrior defaults                
                writer.println("# Warrior Abilities");
                writer.println("# Charge: Quickly runs foward");
                writer.println("WARRIOR_CHARGE_CD=8");
                writer.println("WARRIOR_CHARGE_SPEED=8");
                writer.println("");
                writer.println("# Heroic Strike: Does extra damage based on base damage");
                writer.println("WARRIOR_HEROIC_STRIKE_CD=8");
                writer.println("# Damage = Normal Damage * Damage Modifier");
                writer.println("WARRIOR_HEROIC_STRIKE_DAMAGE_MODIFIER=1.5");
                writer.println("");
                writer.println("# Hamstring: Slows enemy");
                writer.println("WARRIOR_HAMSTRING_CD=8");
                writer.println("# Slow = Normal Speed * Slow Modifier");
                writer.println("WARRIOR_HAMSTRING_SLOW_MODIFIER=0.5");
                writer.println("WARRIOR_HAMSTRING_SLOW_TIME=5");
                writer.println("");
                writer.println("# Defensive Stance: Reduces damage");
                writer.println("WARRIOR_DEFENSIVE_STANCE_MODIFIER=0.5");

                writer.flush();
                writer.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        //Load config file
        loadConfigFile();

        getServer().getPluginManager().registerEvents(this, this);
    }

    public void loadConfigFile()
    {
        try
        {
            getLogger().info("Loading config file into program.");

            Properties config = new Properties();
            config.load(new FileInputStream("plugins/wowskills.properties"));

            //Warrior configuration
            Warrior.CHARGE_CD = Integer.parseInt(config.getProperty("WARRIOR_CHARGE_CD"));
            Warrior.CHARGE_SPEED = Integer.parseInt(config.getProperty("WARRIOR_CHARGE_SPEED"));
            Warrior.HEROIC_STRIKE_CD = Integer.parseInt(config.getProperty("WARRIOR_HEROIC_STRIKE_CD"));
            Warrior.HEROIC_STRIKE_DAMAGE_MODIFIER = Float.parseFloat(config.getProperty("WARRIOR_HEROIC_STRIKE_DAMAGE_MODIFIER"));
            Warrior.HAMSTRING_CD = Integer.parseInt(config.getProperty("WARRIOR_HAMSTRING_CD"));
            Warrior.HAMSTRING_SLOW_MODIFIER = Float.parseFloat(config.getProperty("WARRIOR_HAMSTRING_SLOW_MODIFIER"));
            Warrior.HAMSTRING_SLOW_TIME = Long.parseLong(config.getProperty("WARRIOR_HAMSTRING_SLOW_TIME"));
            Warrior.DEFENSIVE_STANCE_MODIFIER = Float.parseFloat(config.getProperty("WARRIOR_DEFENSIVE_STANCE_MODIFIER"));

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable()
    {
        getLogger().info("Wowskills disabled.");
    }

    // SET META DATA FOR PLAYER
    public void setMetadata(Player player, String key, Object value)
    {
        player.setMetadata(key, new FixedMetadataValue(this, value));
    }

    // GET META DATA FOR PLAYER
    public Object getMetadata(Player player, String key)
    {
        List<MetadataValue> values = player.getMetadata(key);
        for (MetadataValue value : values)
        {
            if (value.getOwningPlugin().getDescription().getName().equals(this.getDescription().getName()))
            {
                return value.value();
            }
        }
        return false;
    }

    public WowClass getPlayerClass(Player player)
    {
        Object obj = getMetadata(player, "class");
        WowClass wowClass;
        if (obj instanceof WowClass)
        {
            wowClass = (WowClass) obj;
        } else
        {
            return null;
        }
        wowClass.player = player;
        return wowClass;
    }

    //EVENTS EVENTS EVENTS
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        Player player = sender.getServer().getPlayer(sender.getName());

        // command /wow [CLASS] 
        if (cmd.getName().equalsIgnoreCase("wow"))
        {
            getLogger().info("Wowskills detected that " + sender.getName()
                    + " wants class " + args[0] + ".");

            //YOUR A WARRIOR
            if (args[0].equalsIgnoreCase("warrior"))
            {
                getLogger().info(sender.getName() + " is now a Warrior.");
                sender.sendMessage("You are now a Warrior.");
                setMetadata(player, "class", new Warrior());
            }

            return true;
        }

        // command /wowinfo 
        if (cmd.getName().equalsIgnoreCase("wowinfo"))
        {
            WowClass wowClass = getPlayerClass(player);
            if (wowClass != null)
            {
                player.sendMessage("You are a " + wowClass.className + ".");
            } else
            {
                player.sendMessage("You have no class.");
            }
            return true;
        }

        // command /wowcombat
        if (cmd.getName().equalsIgnoreCase("wowcombat"))
        {
            WowClass wowClass = getPlayerClass(player);
            if (wowClass != null)
            {
                if (wowClass.combatOn)
                {
                    wowClass.combatOn = false;
                    player.sendMessage("Combat off.");
                } else
                {
                    wowClass.combatOn = true;
                    player.sendMessage("Combat on.");
                }
            } else
            {
                player.sendMessage("You have no class.");
            }
            return true;
        }

        // command /wowslow
        if (cmd.getName().equalsIgnoreCase("wowslow"))
        {
            WowClass wowClass = getPlayerClass(player);
            if (wowClass != null)
            {
                wowClass.slow(Float.parseFloat(args[0]), 10);
            } else
            {
                player.sendMessage("You have no class.");
            }
            return true;
        }

        // command /wowreload
        if (cmd.getName().equalsIgnoreCase("wowreload"))
        {
            loadConfigFile();
            return true;
        }

        return false;
    }

    //ON LOGIN
    @EventHandler
    public void onLogin(PlayerLoginEvent event)
    {
        getLogger().info("Wowskills detected that " + event.getPlayer().getName() + " has logged in.");
        event.getPlayer().sendMessage("Type \"/wow [class]\" to select your class.");
    }

    //ON DAMAGE FROM LEFT CLICK - DAMAGER SIDE MODIFIERS
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event)
    {
        if (event.getDamager().getType() == EntityType.PLAYER)
        {
            WowClass wowClass = getPlayerClass((Player) event.getDamager());
            if (wowClass != null && wowClass.combatOn)
            {
                if (wowClass instanceof Warrior)
                {
                    Warrior warrior = (Warrior) wowClass;
                    warrior.heroicStrike(event);
                    
                    
                }
            }
        }
    }
    
    //ON DAMAGE FROM LEFT CLICK - DAMAGED SIDE MODIFIERS
    @EventHandler
    public void onDamaged(EntityDamageByEntityEvent event)
    {
        if (event.getEntity().getType() == EntityType.PLAYER)
        {
            WowClass wowClass = getPlayerClass((Player) event.getEntity());
            if (wowClass != null && wowClass.combatOn)
            {
                if (wowClass instanceof Warrior)
                {
                    Warrior warrior = (Warrior) wowClass;
                    warrior.defensiveStance(event);
                }
            }
        }
    }    

    //ON RIGHT CLICK ENTITY
    @EventHandler
    public void onRightClickEntity(PlayerInteractEntityEvent event)
    {
        WowClass wowClass = getPlayerClass(event.getPlayer());
        if (wowClass != null && wowClass.combatOn)
        {
            if (wowClass instanceof Warrior)
            {
                // only effects players
                if (event.getRightClicked().getType() == EntityType.PLAYER)
                {
                    Player target = (Player) event.getRightClicked();
                    WowClass targetClass = getPlayerClass(target);
                    if (targetClass != null)
                    {
                        Warrior warrior = (Warrior) wowClass;
                        warrior.hamstring(target, targetClass);
                    }
                }
            }
        }
    }

    //ON SPRINT
    @EventHandler
    public void onSprint(PlayerToggleSprintEvent event)
    {
        Player player = event.getPlayer();
        WowClass wowClass = getPlayerClass(player);
        if (wowClass != null && wowClass.combatOn)
        {
            if (wowClass instanceof Warrior)
            {
                if (!player.isSprinting())
                {
                    Warrior warrior = (Warrior) wowClass;
                    warrior.charge();
                }
            }
        }

    }

    //ON SNEAK
    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event)
    {
        Player player = event.getPlayer();
        WowClass wowClass = getPlayerClass(player);
        if (wowClass != null && wowClass.combatOn)
        {
            if (wowClass instanceof Warrior)
            {
                Warrior warrior = (Warrior) wowClass;
                if(player.isSneaking())
                {
                    player.sendMessage("You are now in offensive stance.");
                }else{
                    player.sendMessage("You are now in defensive stance.");
                }
            }
        }
    }

    //ON PLAYER MOVE
    @EventHandler
    public void onMove(PlayerMoveEvent event)
    {
        WowClass wowClass = getPlayerClass(event.getPlayer());
        if (wowClass != null)
        {
            if (wowClass.slowAmount != 1)
            {
                wowClass.slowMovement(event);
            }
        }
    }
}