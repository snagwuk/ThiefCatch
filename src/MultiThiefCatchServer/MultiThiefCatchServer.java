package MultiThiefCatchServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class MultiThiefCatchServer
{
    LinkedList<DataOutputStream> endouts = new LinkedList<DataOutputStream>();
    
    LinkedList<Player> players = new LinkedList<Player>();
    
    int rank = 1;
    
    int MAX_DECK_LENGTH = 40 + 1;
    
    String[] deck = new String[MAX_DECK_LENGTH];
    
    public static void main(String[] args)
    {
        // TODO Auto-generated method stub
        System.out.println(">> Server");
        new MultiThiefCatchServer().start();
    }
    
    MultiThiefCatchServer()
    {
        Collections.synchronizedCollection(players);
        
        // �ʱ� 4�ο� �� ����
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
                socket = serversocket.accept();
                ServerReceiver thread = new ServerReceiver(socket);
                thread.start();
                
                thread.sleep(1000);

                if (players.size() == 4)
                {
                    allPlayerHandPrint();
                    sendToAll("������� ���� �����ϰڽ��ϴ�.");
                    turn(players.get(players.size() - 1), players.get(0));
                }
                
            }
            
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
        sendToAll(who.name + "�� " + before.name + "�� ī�带 ���� �̾ҽ��ϴ�.");
        allPlayerHandPrint(); // ��� �÷��̾� �ڽ� ���� ���
        
        endChk(before); // �̱���
        
        playerSelect(who);
        
        ServerAllPlayerHandPrint();
        
        // ���� �� ������ ���� turn �޼ҵ� ȣ��
        int x = (players.lastIndexOf(who) + 1) % players.size();
        turn(who, players.get(x));
    }

    void playerSelect(Player who) throws IOException
    { 
        sendToPlayer(who, "[1]. ���� ���� ���� ������ / �� ��. PASS");
        
        String select = who.in.readUTF();
        if (select.length() != 1)
        {
            sendToAll(who.name + "�� �� ����! ");
            allPlayerHandPrint();
            return;
        }
        if (select.equals("1"))
        {
            if (!who.handChk())
                sendToPlayer(who, "���� ���������ʽ��ϴ�.");
            else
            {
                sendToPlayer(who, "���� ���ڽ� ���� (ex: 4)");
                String pair = who.in.readUTF();
                if (pair.length() == 1) sendToAll(who.removeCard(pair));
            }
        }
        sendToAll(who.name + "�� �� ����! ");
        allPlayerHandPrint();
        
    }
    
    void sendToPlayer(Player who, String msg) throws IOException // Ư�� �÷��̾��
                                                                 // �޽��� ���� �޼���
    {
        who.out.writeUTF("[Server] : " + msg);
    }
    
    void sendToAll(String msg) throws IOException // ��� �÷��̾�� �޽��� ���� �޼���
    {
        for (Player x : players)
            x.out.writeUTF("[Server] : " + msg);
        System.out.println(msg);
    }
    
    String clinetsPrint() // ���� Ŭ���̾�Ʈ ��ϵ� String������ ��ȯ
    {
        String result = "";
        
        for (Player x : players)
            result += (String) x.name + " ";
        return result;
    }
    
    void ServerAllPlayerHandPrint() // server���� ��� �÷��̾� �ڵ� ���
    {
        for (Player x : players)
            System.out.println(x.handPrint());
        System.out.println("�ѤѤѤѤѤѤѤѤѤѤѤ�");
    }
    
    void allPlayerHandPrint() throws IOException // ��� �÷��̾�� �ڽ��� ���� ���
    {
        for (Player x : players)
            x.out.writeUTF(x.handPrint());
    }
    
    void turnPrint(Player now) throws IOException // �� ���� ��Ȳ ��� �÷��̾�� ���
    {
        for (Player x : players)
        {
            if (x.equals(now))
                x.out.writeUTF("[Server] : " + now.name + " ����� ���Դϴ�");
            else
                x.out.writeUTF("[Server] : ����" + now.name + "�� �� �������Դϴ�.");
        }
        
    }
    
    void endChk(Player who) throws IOException // ���� size üũ
    {
        if (who.hand.size() == 0)
        {
            sendToAll("�ڡڡ� " + who.name + " Ŭ���� �ڡڡ� ");
            sendToPlayer(who, "����� ����� " + rank++ + "���Դϴ�.");
            players.remove(who);
            if (players.size() == 1)
            {
                for (DataOutputStream x : endouts)
                {
                    x.writeUTF("�ۡۡۡۡۡۡۡۡۡ� ���� ���� �ۡۡۡۡۡۡۡۡۡ�");
                    x.writeUTF("���� ���� = " + players.getFirst().name + "���Դϴ�.");
                }
                players.getFirst().out.writeUTF("�մ���� �����Դϴ�.");
                System.exit(0);
            }
            sendToPlayer(who, "�ܡܡܡܡܡܡܡ� ���� �÷��̾� ��ٸ��� �� �ܡܡܡܡܡܡܡ�");
        }
    }
    
    Player find(String name) // �Է¹��� �̸��� ���� Player ��ü ��ȯ �޼���
    {
        for (Player p : players)
            if (p.name.equals(name)) return p;
        return null;
    }
    
    boolean nameChk(String name)
    {
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
                // TODO: handle exception
            }
        }
        
        @Override
        public void run()
        
        {
            // TODO Auto-generated method stub
            String name = "";
            
            try
            {
                name = in.readUTF();

                if(nameChk(name))
                {
                    out.writeUTF("�̹� ������ �̸��� �ߺ��˴ϴ�.");
                    return;
                }
              
                sendToAll(name + " ���� ����!");
                
                int firstSize = players.size() * 10;
                int lastSize = players.size() * 10 + 10;
                if (players.size() == 3) lastSize++;
                
                Player player = new Player(name,
                        Arrays.copyOfRange(deck, firstSize, lastSize), in, out);
                players.add(player);
                endouts.add(out);
                
                sendToAll("������ �� : " + players.size() + " ��� : "
                        + clinetsPrint());
                
                while (out != null)
                {
                    if (players.size() > 4) System.out.println(in.readUTF());
                }
                
            }
            catch (IOException e)
            {
                // TODO: handle exception
            }
            
            finally
            {
                // sendToAll(name + " ���� ����.");
                // players.remove(find(name));
                System.out.println("[" + socket.getInetAddress() + ":"
                        + socket.getPort() + "] ���� ��������");
            }
        }
        
    }
    
}
