package MultiThiefCatchServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;

public class MultiThiefCatchServer
{
    HashMap clients; // out, �ּ�
    
    HashMap clientsIn; // 1, ������ �ޱ�����
    
    LinkedList<Player> players = new LinkedList<Player>();
    
    int index = 0;
    
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
        clients = new HashMap();
        clientsIn = new HashMap();
        Collections.synchronizedMap(clientsIn);
        Collections.synchronizedMap(clients);
        
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
                System.out.println("[" + socket.getInetAddress() + ":"
                        + socket.getPort() + "] ���� �����Ͽ����ϴ�.");
                ServerReceiver thread = new ServerReceiver(socket);
                thread.start();
                
                //////
                try
                {
                    thread.sleep(2000);
                }
                catch (InterruptedException e)
                {
                    
                    e.printStackTrace();
                }
                
                if (clients.size() == 4)
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
    }
    
    void turn(Player before, Player who) // ��ͷ� ������ turn
    {
        turnPrint(who.name); // ���� ���� ������ ��ο��� ����ϴ� �޼���
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
    
    void playerSelect(Player who)
    {
        DataInputStream in = (DataInputStream) clientsIn.get(who.name);
        
        try
        {
            
            sendToPlayerMsg(who.name, "[1] ���� ���� ���� ������ / �׿�. PASS");
            
            String select = in.readUTF();
            if (select.length() != 1)
            {
                sendToAll(who.name + "�� �� ����! ");
                allPlayerHandPrint();
                return;
            }
            if (select.equals("1"))
            {
                if (!who.handChk())
                    sendToPlayerMsg(who.name, "���� ���������ʽ��ϴ�.");
                else
                {
                    sendToPlayerMsg(who.name, "���� ���ڽ� ���� (ex: 4)");
                    String pair = in.readUTF();
                    if (pair.length() == 1) sendToAll(who.removeCard(pair));
                }
            }
            sendToAll(who.name + "�� �� ����! ");
            allPlayerHandPrint();
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
    }
    
    void sendToPlayerMsg(String name, String msg) // Ư�� �÷��̾�� �޽��� ���� �޼���
    {
        Iterator it = clients.keySet().iterator();
        while (it.hasNext())
        {
            String toPlayer = (String) it.next();
            if (toPlayer.equals(name))
            {
                try
                {
                    DataOutputStream out = (DataOutputStream) clients
                            .get(toPlayer);
                    out.writeUTF("[Server] : " + msg);
                }
                catch (IOException e)
                {
                    // TODO: handle exception
                }
                break;
            }
        }
    }
    
    void sendToAll(String msg) // ��� �÷��̾�� �޽��� ���� �޼���
    {
        Iterator it = clients.keySet().iterator();
        while (it.hasNext())
        {
            try
            {
                DataOutputStream out = (DataOutputStream) clients
                        .get(it.next());
                out.writeUTF("[Server] : " + msg);
            }
            catch (IOException e)
            {
            }
        }
        System.out.println(msg);
    }
    
    String clinetsPrint() // ���� Ŭ���̾�Ʈ ��ϵ� String������ ��ȯ
    {
        String result = "";
        
        Iterator it = clients.keySet().iterator();
        while (it.hasNext())
            result += (String) it.next() + " ";
        
        return result;
    }
    
    void ServerAllPlayerHandPrint() // server���� ��� �÷��̾� �ڵ� ���
    {
        for (Player x : players)
            System.out.println(x.handPrint());
        System.out.println("�ѤѤѤѤѤѤѤѤѤѤѤ�");
    }
    
    void allPlayerHandPrint() // ��� �÷��̾�� �ڽ��� ���� ���
    {
        Iterator it = clients.keySet().iterator();
        while (it.hasNext())
        {
            String name = (String) it.next();
            Player tmp = find(name);
            try
            {
                DataOutputStream out = (DataOutputStream) clients.get(name);
                out.writeUTF(tmp.handPrint());
            }
            catch (IOException e)
            {
                // TODO: handle exception
            }
        }
    }
    
    void turnPrint(String nowturn) // �� ���� ��Ȳ ��� �÷��̾�� ���
    {
        Iterator it = clients.keySet().iterator();
        while (it.hasNext())
        {
            String name = (String) it.next();
            Player tmp = find(name);
            try
            {
                if (name.equals(nowturn))
                {
                    DataOutputStream out = (DataOutputStream) clients.get(name);
                    out.writeUTF("[Server] : " + nowturn + " ����� ���Դϴ�");
                }
                else
                {
                    DataOutputStream out = (DataOutputStream) clients.get(name);
                    out.writeUTF("[Server] : ����" + nowturn + "�� �� �������Դϴ�.");
                }
                
            }
            catch (IOException e)
            {
                // TODO: handle exception
            }
        }
    }
    
    boolean endChk(Player who) // ���� size üũ
    {
        if (who.hand.size() == 0)
        {
            sendToAll("�ڡڡ� " + who.name + " ���а� ������ϴ�. !!Ŭ����!!");
            players.remove(who);
            clientsIn.remove(who.name);
            clients.remove(who.name);
            if (players.size() == 1)
            {
                sendToAll("�ۡۡۡۡۡۡۡۡۡ� ���� ���� �ۡۡۡۡۡۡۡۡۡ�");
                sendToAll("���� ���� = " + players.getFirst().name + "���Դϴ�.");
                System.exit(0);
            }
        }
        return false;
    }
    
    Player find(String name) // �Է¹��� �̸��� ���� Player ��ü ��ȯ �޼���
    {
        for (Player p : players)
            if (p.name.equals(name)) return p;
        return null;
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
                sendToAll(name + " ���� ����!");
                
                int firstSize = clients.size() * 10;
                int lastSize = clients.size() * 10 + 10;
                ;
                if (clients.size() == 3) lastSize++;
                
                players.add(new Player(name,
                        Arrays.copyOfRange(deck, firstSize, lastSize)));
                clients.put(name, out);
                clientsIn.put(name, in);
                sendToAll("������ �� : " + clients.size() + " ��� : "
                        + clinetsPrint());
                
                while (out != null)
                {
                    if (clients.size() > 4) System.out.println(in.readUTF());
                }
                
            }
            catch (IOException e)
            {
                // TODO: handle exception
            }
            
            finally
            {
                sendToAll(name + " ���� ����.");
                clients.remove(name);
                clientsIn.remove(name);
                System.out.println("[" + socket.getInetAddress() + ":"
                        + socket.getPort() + "] ���� ��������");
                System.out.println("���� ���� ������ ��[" + clients.size() + "] ��� : "
                        + clinetsPrint());
            }
        }
        
    }
    
}
