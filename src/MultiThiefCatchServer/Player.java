package MultiThiefCatchServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Player
{
    String name;
    
    LinkedList<String> hand = new LinkedList<String>();
    
    DataInputStream in;
    
    DataOutputStream out;
    
    Player(String name, String[] hand, DataInputStream in,DataOutputStream out)
    {
        this.name = name;
        for (String x : hand)
            this.hand.add(x);
        this.in = in;
        this.out = out;
    }
   
    public void setHand(String[] hand)
    {
        for (String x : hand)
            this.hand.add(x);
    }
    
   

    public void setHand(LinkedList<String> hand)
    {
        this.hand = hand;
    }
    
    void handSuffle()
    {
        for (int i = 0; i < 10000; i++)
        {
            int ran1 = (int) (Math.random() * hand.size());
            int ran2 = (int) (Math.random() * hand.size());
            
            String tmp = hand.get(ran1);
            hand.set(ran1, hand.get(ran2));
            hand.set(ran2, tmp);
        }
    }
    

    void getCard(Player before)
    {
        before.handSuffle();
        
        String getcard = before.hand.get(before.hand.size() - 1);
        before.hand.remove(getcard);
        before.setHand(before.hand);
        
        this.hand.add(getcard);
        //System.out.println("ㅡㅡㅡ" + name + "가 " + before.name + "의 카드한장을 뽑은 후 ");

    }
    
    boolean cardPairChk(String what)
    {
        int cnt = 0;
        for (String x : hand)
            if (x.equals(what)) cnt++;
        if (cnt >= 2) return true;
        return false;
    }
    
    String removeCard(String what)
    {
        if (!cardPairChk(what))
            return (what + "은 한쌍이 존재하지 않습니다. 턴을 강제 끝냅니다."); 
        
        this.hand.remove(what);
        this.hand.remove(what);
        
        return (name + "의 손패에서 " + what + " 한쌍 제거 완료");
    }
    
    boolean handChk()
    {
        // 한 쌍이라도 존재하는지 확인해서 카드 버릴 거 확인하는 거
        Set<String> duplicateCheckSet = new HashSet<>(hand);
        
        if (hand.size() > duplicateCheckSet.size()) { return true; }
        return false;
    }
    
    String handPrint()
    {
        String result = "";
        
        result += "손";
        for (String x : hand)
            result += x + " ";
        
        return result;
    }
    
}
