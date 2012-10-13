package wowskills;

public class Warrior extends WowClass
{
    public CoolDown charge = new CoolDown(8);
    public CoolDown heroicStrike = new CoolDown(6);
    
    public Warrior()
    {
        className = "Warrior";
    }
    
    public void charge()
    {
        if(charge.offCD())
        {
            player.setVelocity(player.getLocation().getDirection().multiply(8));
            charge.setCD();
        }else{
            player.sendMessage("Charge is on cooldown for "+charge.offTime()+" seconds.");
        }
    }
    
    public void heroicStrike()
    {
        
    }
}
