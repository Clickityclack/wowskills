package wowskills;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class Warrior extends WowClass
{

    public static int CHARGE_CD = 0;
    public static int CHARGE_SPEED = 0;
    public static int HEROIC_STRIKE_CD = 0;
    public static float HEROIC_STRIKE_DAMAGE_MODIFIER = 0f;
    public static int HAMSTRING_CD = 0;
    public static float HAMSTRING_SLOW_MODIFIER = 0f;
    public static long HAMSTRING_SLOW_TIME = 0;
    public static float DEFENSIVE_STANCE_MODIFIER = 0f;
    public CoolDown charge = new CoolDown(CHARGE_CD);
    public CoolDown heroicStrike = new CoolDown(HEROIC_STRIKE_CD);
    public CoolDown hamstring = new CoolDown(HAMSTRING_CD);

    public Warrior()
    {
        className = "Warrior";
    }

    //Does a charge foward
    public void charge()
    {
        if (charge.offCD())
        {
            player.setVelocity(player.getLocation().getDirection().setY(0).multiply(CHARGE_SPEED));
            charge.setCD();
        } else
        {
            player.sendMessage("Charge is on cooldown for " + charge.offTime() + " seconds.");
        }
    }

    public void heroicStrike(EntityDamageByEntityEvent damageEvent)
    {
        if (!player.isSneaking())
        {
            if (heroicStrike.offCD())
            {
                int damage = (int) Math.ceil(damageEvent.getDamage() * HEROIC_STRIKE_DAMAGE_MODIFIER);
                player.sendMessage("Heroic Strike for " + damage + ".");
                damageEvent.setDamage(damage);
                heroicStrike.setCD();
            } else
            {
                player.sendMessage("Heroic Strike is on cooldown for " + heroicStrike.offTime() + " seconds.");
            }
        }
    }

    public void hamstring(Player targetPlayer, WowClass targetPlayerClass)
    {
        if (hamstring.offCD())
        {
            targetPlayerClass.slow(HAMSTRING_SLOW_MODIFIER, HAMSTRING_SLOW_TIME);
            player.sendMessage("You slowed " + targetPlayer.getName() + " by " + HAMSTRING_SLOW_MODIFIER * 100 + "%.");
            targetPlayer.sendMessage(player.getName() + " slowed you by " + HAMSTRING_SLOW_MODIFIER * 100 + "%.");
            hamstring.setCD();
        } else
        {
            player.sendMessage("Hamstring is on cooldown for " + hamstring.offTime() + " seconds.");
        }
    }

    public void defensiveStance(EntityDamageByEntityEvent damageEvent)
    {
        if (player.isSneaking())
        {
            damageEvent.setDamage((int) Math.ceil(damageEvent.getDamage() * DEFENSIVE_STANCE_MODIFIER));
        }
    }
}
