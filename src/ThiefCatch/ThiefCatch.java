package ThiefCatch;

import java.util.Arrays;
import java.util.LinkedList;

public class ThiefCatch
{
    int MAX_DECK_LENGTH = 40 + 1;
    
    String joker = "★ ";
    
    String[] deck = new String[MAX_DECK_LENGTH];
    
    LinkedList<Player> players = new LinkedList<Player>();
    
    public static void main(String[] args)
    {
        new ThiefCatch().start();
    }
    
    public void start()
    {
        deckSetting();
        allPlayerHandPrint();
        tern(players.get(players.size() - 1), players.get(0));
    }
    
    void deckSetting()
    {
        for (int i = 0; i < 10; i++)
            deck[i] = deck[i + 10] = deck[i + 20] = deck[i + 30] = i + "";
        deck[MAX_DECK_LENGTH - 1] = joker;
        
        // 넣은 deck배열을 랜덤으로 섞기
        for (int i = 0; i < 10000; i++)
        {
            int ran1 = (int) (Math.random() * MAX_DECK_LENGTH);
            int ran2 = (int) (Math.random() * MAX_DECK_LENGTH);
            
            String tmp = deck[ran1];
            deck[ran1] = deck[ran2];
            deck[ran2] = tmp;
        }
        players.add(new Player("A", Arrays.copyOfRange(deck, 0, 10)));
        players.add(new Player("B", Arrays.copyOfRange(deck, 10, 20)));
        players.add(new Player("C", Arrays.copyOfRange(deck, 20, 30)));
        players.add(new Player("D", Arrays.copyOfRange(deck, 30, 41)));
        
        /*
         * //test용 짧은 덱구성 players.add(new Player("A",new String[]{"1","4"}));
         * players.add(new Player("B",new String[]{"3","2"})); players.add(new
         * Player("C",new String[]{"3","1"})); players.add(new Player("D",new
         * String[]{"4","2",joker}));
         */
    }
    
    void allPlayerHandPrint()
    {
        for (Player x : players)
            x.handPrint();
        System.out.println("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
    }
    
    void tern(Player before, Player who)
    {
        
        System.out.println("ㅡㅡㅡㅡ 현재턴 : " + who.name);
        who.getCard(before);
        endChk(before); // 카드 뽑은이후 뽑힌 before의 핸드 사이즈가 0이면 게임에서 제외 + 게임종료까지
        allPlayerHandPrint();
        who.playerSelect();
        
        allPlayerHandPrint();
        int x = (players.lastIndexOf(who) + 1) % players.size();
        tern(who, players.get(x));
    }
    
    void endChk(Player who)
    {
        if (who.hand.size() == 0)
        {
            System.out.println("★★★ " + who.name + " 손패가 비었습니다. !!클리어!!");
            players.remove(who); // 손패가0인 플레이어 목록(players)에서 제거
            
            if (players.size() == 1)
            {
                System.out.println("○○○○○○○○○○ 게임 종료 ○○○○○○○○○○");
                System.out.println("최종 도둑 = " + players.getFirst().name + "님입니다."); // 혼자남은 플레이어 가져와서 출력
                System.exit(0); // main 시스템 종료
            }
        }
    }
    Player find(String name)
    {
        for (Player p : players)
            if (p.name.equals(name)) 
                return p;
        return null;
    }
    
}
