package ThiefCatch;

import java.time.Period;
import java.util.Arrays;
import java.util.Scanner;
class Player
{
    Player()
    {
        
    }
    public Player(String a, String[] b)
    {
        // TODO Auto-generated constructor stub
        name = a;
        hand = b;
    }
    String name;
    String [] hand;
}
public class Deck
{
    
    int MAX_DECK_LENGTH = 40+1;
    int PEOPLE = 4;
    String DELWORD = "X";
    String joker = "★ ";
    String[] deck = new String[MAX_DECK_LENGTH];     
    //  (0=A / 1=B / 2=C / 3=D)\
    int nowTern = -1;
    String [] cycle;
    Player[] players;
    Player[] tempPlayers;
    

    
    void init()
    {
        // 0~9 4개셋트 + 조커 deck배열에 넣기
        for(int i = 0; i < 10; i++)
        {
            deck[i] = deck[i+10] = deck[i+20] = deck[i+30]  = i+"";
        }
        deck[MAX_DECK_LENGTH-1] = joker;
        
        // 넣은 deck 배열 랜덤섞기
        for(int i=0 ;  i < 10000; i ++)
        {
            int ran1 = (int)(Math.random()*MAX_DECK_LENGTH);
            int ran2 = (int)(Math.random()*MAX_DECK_LENGTH);
            
            String tmp = deck[ran1];
            deck[ran1] = deck[ran2];
            deck[ran2] = tmp;      
        }
        
        players  = new Player[4];
        for(int i = 0; i < 4; i ++)
            players[i] = new Player();
        players[0].name = "A";
        players[0].hand = new String[10];
        players[1].name = "B";
        players[1].hand = new String[10];
        players[2].name = "C";
        players[2].hand = new String[10];
        players[3].name = "D";
        players[3].hand = new String[11];
        
       players[0].hand =  Arrays.copyOfRange(deck, 0,10);
       players[1].hand =  Arrays.copyOfRange(deck, 10,20);
       players[2].hand =  Arrays.copyOfRange(deck, 20,30);
       players[3].hand =  Arrays.copyOfRange(deck, 30,41);
        
    }
    
    void initTest()
    {
        players  = new Player[4];
        String[] a = {"1","4"};
        String[] b =  {"3","2"};
        String[] c = {"3","1"};
        String[] d = {"4","2",joker};
        
       players[0] = new Player("A",a);
       players[1] = new Player("B",b);
       players[2] = new Player("C",c);
       players[3] = new Player("D",d);

       

    }
    
       
    void handShuffle(Player who)
    {
        for(int i=0 ;  i < 10000; i ++)
        {
            int ran1 = (int)(Math.random()*who.hand.length);
            int ran2 = (int)(Math.random()*who.hand.length);
            
            String tmp = who.hand[ran1];
            who.hand[ran1] = who.hand[ran2];
            who.hand[ran2] = tmp;      
        }
        
        for(Player x : players)
        {
            if(x.name.equals(who.name))
                x.hand =  who.hand;
        }
        
    }
    public void printHand()
    {
        /*  System.out.print("플레이어 순서 : ");
        for(char x : players)
        {
            if(x == 'D')
                System.out.print(x + "\n");
            else
                System.out.print(x + "->");
        }*/
        for(int i =0; i< PEOPLE; i++)
        {
            System.out.print(players[i].name + " : ");
            for(String x : players[i].hand)
            {
                System.out.print(x +" ");
            }
            System.out.println();
        }
        

    }
    void printplayerHand(Player who)
    {
        System.out.print(who.name + " : ");
        for(String x : who.hand)
        {
            System.out.print(x +" ");
        }
        System.out.println();
    }
    
    void start()
    {
        printHand();
        nowTern = 0;
        Tern(players[3],players[0]);
       

    }
    
    void Tern(Player before, Player who)
    {
        System.out.println("ㅡㅡㅡㅡ 현재턴 : " +who.name);
        System.out.println("ㅡㅡㅡ " + before.name +"의 카드를 하나 뽑습니다.");
        getCard(before,who);
        playerSelect(who);
        
        nowTern++;
        nowTern = nowTern % PEOPLE;

        Player nextwho = players[nowTern];
        Tern(who, nextwho);
    }
    
    void getCard(Player before, Player who)
    {
        handShuffle(before);
        String[] befortmp = new String[before.hand.length-1];
        
        String getcard = before.hand[before.hand.length-1];
        
        for(int i = 0 ; i < befortmp.length; i++)
            befortmp[i] = before.hand[i];
        
        for(Player x : players)
        {
            if(x.name.equals(before.name))
                x.hand =  befortmp;
        }

        
        String[] whotmp = new String[who.hand.length+1];
        for(int i = 0 ; i < who.hand.length; i++)
            whotmp[i] = who.hand[i];      
        whotmp[who.hand.length] = getcard;
        
        for(Player y : players)
        {
            if(y.name.equals(who.name))
                y.hand =  whotmp;
        }
        
        System.out.println("ㅡㅡㅡ" + who.name + "가 " + before.name + "의 카드한장을 뽑은 후 ");
        printHand();
       
        endChk(before);
        
    }
    void removeCard(Player who,String what)
    {
        String[] whotmp = new String[who.hand.length-2];
        int flag = 0;
        for(int i = 0 ; i<who.hand.length; i++ )
        {
            if( who.hand[i].equals(what))
            {
                who.hand[i] = DELWORD;
                flag++;
            }
            if( flag == 2)
                break;
        }
        int index = 0;
        for(String x : who.hand)
            if(!x.equals(DELWORD))
                whotmp[index++] = x;  

        for(Player y : players)
        {
            if(y.name.equals(who.name))
                y.hand =  whotmp;
        }
        System.out.println(who.name + "의 손패에서 " + what + " 한쌍 제거 완료");
        printHand();
    }
    
    public void playerSelect(Player who)
    {
        Scanner sc = new Scanner(System.in);
        System.out.print("1. 같은 쌍의 숫자 버리기 / 그외. PASS  >>>");
        int select = sc.nextInt();
        if(select == 1)
        {
            if(!jungbok(who))
            {
                System.out.println("쌍이 존재하지않습니다.");
            }
            else{
                printplayerHand(who);
                System.out.print("버릴 숫자쌍 선택 (ex: 4)  >>>");
                Scanner sc1 = new Scanner(System.in);
                String pair = sc1.nextLine();
                removeCard(who,pair);
            }
        }
        System.out.println(who.name + "의 턴 종료! ");
        endChk(who);
    }
    
    boolean endChk(Player who)
    {
        if(who.hand.length == 0)
        {
            System.out.println("★★★★★★★★★★★★★★★★");
            System.out.println(who.name + " 손패가 비었습니다. !!클리어!!");
            PEOPLE--;
            tempPlayers = new Player[PEOPLE];
            int index = 0;
            for(int i = 0; i <PEOPLE+1; i++ )
            {
                if(!players[i].name.equals(who.name))
                    tempPlayers[index++] = players[i];
            }
            
            if(PEOPLE == 1)
            {
                for(Player x : players)
                {
                    
                    System.out.println("최종 도둑 = " +  x.name +"님입니다.");
                    System.exit(0);
                   
                }
            }
            
        }
        return false;
    }
    
    boolean jungbok(Player who)
    {
        for(int i = 0; i < who.hand.length; i++)
        {
            int cnt = 0 ;
            for(int j = 0; j < who.hand.length; j++)
                if(who.hand[i].equals(who.hand[j]))
                    cnt++;
            if(cnt == 2 )
                return true;
        }
        return false;
    }
}
