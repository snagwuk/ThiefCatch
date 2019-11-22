package ThiefCatch;

import java.util.Arrays;
import java.util.LinkedList;

public class ThiefCatch
{
    int MAX_DECK_LENGTH = 40 + 1;
    
    String joker = "�� ";
    
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
        
        // ���� deck�迭�� �������� ����
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
         * //test�� ª�� ������ players.add(new Player("A",new String[]{"1","4"}));
         * players.add(new Player("B",new String[]{"3","2"})); players.add(new
         * Player("C",new String[]{"3","1"})); players.add(new Player("D",new
         * String[]{"4","2",joker}));
         */
    }
    
    void allPlayerHandPrint()
    {
        for (Player x : players)
            x.handPrint();
        System.out.println("�ѤѤѤѤѤѤѤѤѤѤѤ�");
    }
    
    void tern(Player before, Player who)
    {
        
        System.out.println("�ѤѤѤ� ������ : " + who.name);
        who.getCard(before);
        endChk(before); // ī�� �������� ���� before�� �ڵ� ����� 0�̸� ���ӿ��� ���� + �����������
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
            System.out.println("�ڡڡ� " + who.name + " ���а� ������ϴ�. !!Ŭ����!!");
            players.remove(who); // ���а�0�� �÷��̾� ���(players)���� ����
            
            if (players.size() == 1)
            {
                System.out.println("�ۡۡۡۡۡۡۡۡۡ� ���� ���� �ۡۡۡۡۡۡۡۡۡ�");
                System.out.println("���� ���� = " + players.getFirst().name + "���Դϴ�."); // ȥ�ڳ��� �÷��̾� �����ͼ� ���
                System.exit(0); // main �ý��� ����
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
