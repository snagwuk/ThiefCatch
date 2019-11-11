package ThiefCatch;

import java.util.LinkedList;
import java.util.Scanner;


public class Player
{
    String name;
    //String[] hand;
    LinkedList<String> hand = new LinkedList<String>();

    public Player(String name, String[] hand)
    {
        this.name = name;
        //this.hand = hand;
        for(String x : hand)
            this.hand.add(x);
    }
    public LinkedList<String> getHand()
    {
        return hand;
    }
    public void setHand(LinkedList<String> hand)
    {
        this.hand = hand;
    }
    void handSuffle()
    {
        for(int i=0 ;  i < 10000; i ++)
        {
            int ran1 = (int)(Math.random()*hand.size());
            int ran2 = (int)(Math.random()*hand.size());
            
            String tmp = hand.get(ran1);
            hand.set(ran1, hand.get(ran2));
            hand.set(ran2, tmp);  
        }
    }
   
    void getCard(Player before)
    {
        before.handSuffle();
       
        String getcard = before.hand.get(before.hand.size()-1);
        before.hand.remove(getcard);
        before.setHand(before.hand);
        
        ////////////////
        
        this.hand.add(getcard);
        System.out.println("ㅡㅡㅡ" + name + "가 " + before.name + "의 카드한장을 뽑은 후 ");
        
        //before.handPrint();
        //handPrint();
        //endChk(before);
    }
   

    @SuppressWarnings("resource")
    void playerSelect()
    {
       
        Scanner sc = new Scanner(System.in);
        handPrint();
        System.out.print("ㅡㅡ 1.같은 쌍의 숫자 버리기 / 그외. PASS  >>>");
        int select = sc.nextInt();
        if(select == 1)
        {
            if(!handChk())
            {
                System.out.println("쌍이 존재하지않습니다.");
            }
            else
            {
                System.out.print("버릴 숫자쌍 선택 (ex: 4)  >>>");
                Scanner sc1 = new Scanner(System.in);
                String pair = sc1.nextLine();
                removeCard(pair);
            }
        }
        System.out.println(name + "의 턴 종료! ");
    }
    boolean cardPairChk(String what)
    {
        int cnt = 0 ;
        for(String x : hand)
            if(x.equals(what))
                cnt++;
        if(cnt >= 2)
            return true;
        return false;
    }
    void removeCard(String what)
    {
        if (!cardPairChk(what))
        {
            System.out.println(what + "은 한쌍이 존재하지 않습니다. 턴을 강제 끝냅니다.");
            return;
        }
            
        this.hand.remove(what);
        this.hand.remove(what);
        
        System.out.println("ㅡ" +name + "의 손패에서 " + what + " 한쌍 제거 완료");
    }
    boolean handChk()
    {
        for(int i = 0; i < hand.size(); i++)
        {
            int cnt = 0;
            for(int j = 0; j < hand.size(); j++)
                if(hand.get(i).equals(hand.get(j)))
                    cnt++;
            if(cnt == 2 )
                return true;
        }
        return false;
    }
    void handPrint()
    {
        System.out.print(name + " : ");
        for(String x :hand)
        {
            System.out.print(x +" ");
        }
        System.out.println();
    }

}
