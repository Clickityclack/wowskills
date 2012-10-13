package wowskills;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class WowClass
{
    public Player player;
    public String className;
    public boolean combatOn = true;
    
    //Debuff
    public float slowAmount = 1;
    public CoolDown slowedTime = new CoolDown();
    
    public void setPlayer(Player player)
    {
        this.player = player;
    }
    
    public void slowMovement(PlayerMoveEvent event)
    {
        if(!slowedTime.offCD())
        {
            Vector velocity = player.getVelocity();
            velocity.setX(velocity.getX() * slowAmount);
            velocity.setY(velocity.getY() * slowAmount);
            player.setVelocity(velocity);
        }else{
            slowAmount = 1;
        }
    }
    
    public void slow(float amount, long time)
    {
        slowAmount = amount;
        slowedTime.setCD(time);
    }
     
}
