package ThiefCatch;

import java.util.Scanner;


public class Player
{
    String name;
    String[] hand;
    String DEL = "X";

    public Player(String name, String[] hand)
    {
        this.name = name;
        this.hand = hand;
    }
    void handSuffle()
    {
        for(int i=0 ;  i < 10000; i ++)
        {
            int ran1 = (int)(Math.random()*hand.length);
            int ran2 = (int)(Math.random()*hand.length);
            
            String tmp = hand[ran1];
            hand[ran1] = hand[ran2];
            hand[ran2] = tmp;      
        }
    }
    void getCard(Player before)
    {
        before.handSuffle();
        String[] befortmp = new String[before.hand.length-1];
        
        String getcard = before.hand[before.hand.length-1];
        
        for(int i = 0 ; i < befortmp.length; i++)
            befortmp[i] = before.hand[i];
        
        ////////////////
        before.setHand(befortmp);

        String[] whotmp = new String[hand.length+1];
        for(int i = 0 ; i < hand.length; i++)
            whotmp[i] = hand[i];      
        whotmp[hand.length] = getcard;
        
       this.hand = whotmp;
        
        System.out.println("ㅡㅡㅡ" + name + "가 " + before.name + "의 카드한장을 뽑은 후 ");
        //before.handPrint();
        //handPrint();
        
        //endChk(before);
    }
    public String[] getHand()
    {
        return hand;
    }
    public void setHand(String[] hand)
    {
        this.hand = hand;
    }
    @SuppressWarnings("resource")
    void playerSelect()
    {
        handPrint();
        Scanner sc = new Scanner(System.in);
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
            
        String[] whotmp = new String[hand.length-2];
        int flag = 0;
        for(int i = 0 ; i<hand.length; i++ )
        {
            if( hand[i].equals(what))
            {
                hand[i] = DEL;
                flag++;
            }
            if( flag == 2)
                break;
        }
        int index = 0;
        for(String x : hand)
            if(!x.equals(DEL))
                whotmp[index++] = x;


        this.hand = whotmp;
        System.out.println("ㅡ" +name + "의 손패에서 " + what + " 한쌍 제거 완료");
    }
    boolean handChk()
    {
        for(int i = 0; i < hand.length; i++)
        {
            int cnt = 0;
            for(int j = 0; j < hand.length; j++)
                if(hand[i].equals(hand[j]))
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
