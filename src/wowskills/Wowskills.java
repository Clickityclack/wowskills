package wowskills;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class Wowskills extends JavaPlugin implements Listener
{

    @Override
    public void onEnable()
    {
        getLogger().info("Wowskills enabled.");
        getServer().getPluginManager().registerEvents(this, this);
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

        return false;
    }

    //ON LOGIN
    @EventHandler
    public void onLogin(PlayerLoginEvent event)
    {
        getLogger().info("Wowskills detected that " + event.getPlayer().getName() + " has logged in.");
        event.getPlayer().sendMessage("Type \"/wow [class]\" to select your class.");
    }

    //ON SPRINT
    @EventHandler
    public void onSprint(PlayerToggleSprintEvent event)
    {
        Player player = event.getPlayer();
        if (player.isSprinting())
        {
            WowClass wowClass = getPlayerClass(player);

            if (wowClass instanceof Warrior)
            {
                Warrior warrior = (Warrior) wowClass;
                warrior.charge();
            }
        }
    }
}