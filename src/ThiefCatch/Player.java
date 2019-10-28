package ThiefCatch;

public class Player
{
    String name;
    String [] hand;
    Player()
    {
        
    }
    public Player(String name, String[] hand)
    {
        this.name = name;
        this.hand = hand;
    }

    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String[] getHand()
    {
        return hand;
    }
    public void setHand(String[] hand)
    {
        this.hand = hand;
    }
}
