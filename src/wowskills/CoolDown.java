package wowskills;

public class CoolDown
{
    public long lastUsed = 0;
    public double cdTime = 0;

    public CoolDown()
    {
    }
    
    public CoolDown(double seconds)
    {
        cdTime = seconds;
    }
    
    public boolean offCD()
    {
        if(System.currentTimeMillis() > (lastUsed + Math.round(cdTime * 1000)))
        {
            return true;
        }else{
            return false;
        }
    }
    
    public void setCD()
    {
        lastUsed = System.currentTimeMillis();
    }
    
    public void setCD(double seconds)
    {
        lastUsed = System.currentTimeMillis();
        cdTime = seconds;
    }
    
    public int offTime()
    {
        return (int)Math.ceil(cdTime - (System.currentTimeMillis() - lastUsed) / 1000);
    }
}
