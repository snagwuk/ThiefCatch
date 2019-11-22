package MultiThiefCatchServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class MultiThiefCatchServer
{
    LinkedList<DataOutputStream> endouts;
    // ����� ��� �����ڿ��� �������Ḧ �˸��� �뵵�� DataOutputStream �������
    
    LinkedList<Player> players;
    // ������ ��� �÷��̾� ����
    
    int rank = 1;
    // ��� ������ ���� ����
    
    int MAX_DECK_LENGTH = 40 + 1;
    // �� ���� ���� ����
    
    String[] deck;
    
    public static void main(String[] args)
    {
        
        System.out.println(">> Server");
        new MultiThiefCatchServer().start();
    }
    
    MultiThiefCatchServer()
    {
        endouts = new LinkedList<DataOutputStream>();
        players = new LinkedList<Player>();
        
        Collections.synchronizedCollection(players); // �÷��̾� ����ȭ
        deck = new String[MAX_DECK_LENGTH]; // �⺻ ī����� �� ����
        
        // �ʱ� 4�ο� ī�嵦 ����
        String joker = "X";
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
    }
    void start()
    {
        ServerSocket serversocket = null;
        Socket socket = null;
        
        try
        {
            serversocket = new ServerSocket(7777);
            System.out.println("������ ���۵Ǿ����ϴ�.");
            while (true)
            {
                socket = serversocket.accept(); // �����ٸ�
                ServerReceiver thread = new ServerReceiver(socket);
                thread.start();
                
                thread.sleep(1000);
                
                if (players.size() == 4)
                {

                    sendToAll(" 4���� �÷��̾ �����Ͽ����ϴ�.");
                    sendToAll("\t");
                    sendToAll(" ������ ���������� ����! ������");
                    sendToAll(" [���� ������ ���� ����˴ϴ�.]");
                    sendToAll(" [��� ī��� �������� �����ɴϴ�.]");
                    allPlayerHandPrint();
                    sendToAll(" ���� ����  : " + "[" + players.get(0).getName()
                            + " -> " + players.get(1).getName() + " -> "
                            + players.get(2).getName() + " -> "
                            + players.get(3).getName() + "]\n");
                    
                    
                    thread.sleep(2000);
                    
                    turn(players.get(players.size() - 1), players.get(0));
                }
                
            }
            
        }
        catch (SocketException e)
        {
            try
            {
                sendToAll(" �������� ���������� ����� ������ �����մϴ�. ������");
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
            System.exit(0);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    void turn(Player before, Player who) throws IOException // ��ͷ� ������ turn
    {
        turnPrint(who); // ���� ���� ������ ��ο��� ����ϴ� �޼���
        who.getCard(before); // ���� �÷��̾ ���� �÷��̾� ī�� ���� �̴� �޼���
        sendToAll(" �� " + who.name + "�� " + before.name + "�� ī�带 ���� �̾ҽ��ϴ�. ��");
        allPlayerHandPrint(); // ��� �÷��̾� �ڽ� ���� ���
        
        endChk(before); 
        
        playerSelect(who);
        
        ServerAllPlayerHandPrint();
        
        // ���� �� ������ ���� turn �޼ҵ� ȣ��
        int x = (players.lastIndexOf(who) + 1) % players.size();
        turn(who, players.get(x));
    }
    
    void playerSelect(Player who) throws IOException
    {
        sendToPlayer(who, " [1] �� ���� ���� ī�� ���� / [Any Key] ���� ī�尡 ����");
        
        String select = who.in.readUTF();
        
        if (select.length() != 1)
        {
            sendToAll(" " + who.name + "�� �� ����!");
            allPlayerHandPrint();
            return;
        }
        if (select.equals("1"))
        {
            if (!who.handChk())
                sendToPlayer(who, " �� ���� ��ȣ�� ���� �� �� ī�尡 ���������ʽ��ϴ�.��");
            else
            {
                sendToPlayer(who, " ���� ī�� ��ȣ�� �����ϼ���.");
                String pair = who.in.readUTF();
                if (pair.length() == 1) sendToAll(who.removeCard(pair));
            }
        }
        sendToAll("\n");
        sendToAll(" �� " + who.name + "�� �� ����! ��");
        allPlayerHandPrint();
        
    }
    
    void sendToPlayer(Player who, String msg) throws IOException 
    {   // Ư�� �÷��̾�� �޽��� ���� �޼���
        who.out.writeUTF(msg);
    }
    
    void sendToAll(String msg) throws IOException 
    {   // ��� �÷��̾�� �޽��� ���� �޼���
        for (Player x : players)
            x.out.writeUTF(msg);
        System.out.println(msg);
    }
    
    String clinetsPrint() 
    {   // ���� Ŭ���̾�Ʈ ��ϵ� String������ ��ȯ
        String result = "";
        
        for (Player x : players)
            result += (String) x.name + " ";
        return result;
    }
    
    void ServerAllPlayerHandPrint() 
    {   // server �ܼ�â�� ��� �÷��̾� �ڵ� ���
        for (Player x : players)
            System.out.println(x.handPrint());
        System.out.println("�ѤѤѤѤѤѤѤѤѤѤѤ�");
    }
    
    void allPlayerHandPrint() throws IOException 
    {   // ��� �÷��̾�� �ڽ��� ���� ���
        for (Player x : players)
            x.out.writeUTF(x.handPrint());
    }
    
    void turnPrint(Player now) throws IOException 
    {   // �� ���� ��Ȳ ��� �÷��̾�� ���
        for (Player x : players)
        {
            if (x.equals(now))
                x.out.writeUTF(" �� [" + now.name + "]" + " ����� ���Դϴ�. ��");
            else
                x.out.writeUTF(" �� ����  [" + now.name + "]" + "�� �� ���� ���Դϴ�. ��");
        }
    }
    void endChk(Player who) throws IOException 
    {   // ���� size üũ
        if (who.hand.size() == 0)
        {
            sendToAll("\n");
            sendToAll("\t            �ڡڡ� [" + who.name + "] Ŭ���� �ڡڡ�\n");
            sendToPlayer(who, "����� ����� " + rank++ + "���Դϴ�.");
            players.remove(who);
            if (players.size() == 1)
            {
                for (DataOutputStream x : endouts)
                {
                    
                    x.writeUTF("           ���������������������� ���� ���� ������������������������\n");
                    sendToAll("\n");
                    
                    x.writeUTF("\t    ������ ������ [" + players.getFirst().name
                            + "] �ʿ����� ������ ");
                }
                players.getFirst().out.writeUTF("�մ���� �����Դϴ�.");
                System.exit(0);
            }
            
            sendToPlayer(who, "           �������������� ���� �÷��̾� ��ٸ��� �� ����������������\n");
        }
    }
    
    Player find(String name) 
    {   // �Է¹��� �̸��� ���� Player ��ü ��ȯ �޼���
        for (Player p : players)
            if (p.name.equals(name)) return p;
        return null;
    }
    
    boolean nameChk(String name)
    {   // �ߺ��� �̸��� ���� ����ڰ� �ִ��� ���� ��ȯ �޼���
        for (Player p : players)
            if (p.name.equals(name)) 
                return true;
        return false;
    }
    class ServerReceiver extends Thread
    {
        Socket socket;
        
        DataInputStream in;
        
        DataOutputStream out;
        
        ServerReceiver(Socket socket)
        {
            this.socket = socket;
            try
            {
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        
        @Override
        public void run()
        {
            String name = "";
            try
            {
                name = in.readUTF();
                
                if (nameChk(name))
                {
                    out.writeUTF(" �̹� ������ �̸��� �ߺ��˴ϴ�.");
                    return;
                }
                
                sendToAll(" ������" + name + " ���� ����! ������");
                
                int firstSize = players.size() * 10;
                int lastSize = players.size() * 10 + 10;
                if (players.size() == 3) lastSize++;
                
                Player player = new Player(name,
                        Arrays.copyOfRange(deck, firstSize, lastSize), in, out);
                players.add(player);
                endouts.add(out);
                
                sendToAll(" ���� �ο��� 4���� ��, ������ ���۵˴ϴ�. -> ���� �ο� ������");
                sendToAll(" ������ �� : " + players.size());
                sendToAll(" ������ �÷��̾� ��� -> " + clinetsPrint());
                sendToAll("\n");
                
                while (out != null)
                {
                    if (players.size() > 4) System.out.println(in.readUTF());
                }
                
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
            finally
            {
                players.remove(find(name));
                try
                {
                    sendToAll(" " + name + " ���� �����մϴ�.");
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                System.out.println(" [" + socket.getInetAddress() + ":"
                        + socket.getPort() + "] ���� ��������");
            }
        }
    }
}