package MultiTC;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;

public class TcpIpMultichatServer
{
    HashMap clients;
    public static void main(String[] args)
    {
        // TODO Auto-generated method stub
    	System.out.println(">> Server");
        new TcpIpMultichatServer().start();
    }
    void start()
    {
        // TODO Auto-generated method stub
        ServerSocket serversocket = null;
        Socket socket = null;
        
        try
        {
            serversocket = new ServerSocket(7777);
            System.out.println("������ ���۵Ǿ����ϴ�.");
           while(true)
           {
               socket = serversocket.accept();
               System.out.println("[" + socket.getInetAddress() + ":" + socket.getPort() + "] ���� �����Ͽ����ϴ�." );
               ServerReceiver thread = new ServerReceiver(socket);
               thread.start();
           }
           
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
   
    void sendToPlayerMsg(String name, String msg)
    {
    	Iterator it = clients.keySet().iterator();
        while(it.hasNext())
        {
        	String toPlayer = (String) it.next();
        	if(toPlayer.equals(name))
        	{
	            try
	            {
	                DataOutputStream out = (DataOutputStream) clients.get(toPlayer);
	                out.writeUTF(msg);
	            }
	            catch (IOException e)
	            {
	                // TODO: handle exception
	            }
	            break;
        	}
        }
    }
    void sendToAll(String msg)
    {
        Iterator it = clients.keySet().iterator();
        while(it.hasNext())
        {
            try
            {
                DataOutputStream out = (DataOutputStream) clients.get(it.next());
                out.writeUTF(msg);
            }
            catch (IOException e)
            {
                // TODO: handle exception
            }
        }
    }
    TcpIpMultichatServer()
    {
        clients = new HashMap();
        Collections.synchronizedMap(clients);    
    }
    
    class ServerReceiver extends Thread
    {
        Socket socket;
        
        DataInputStream in;
        DataOutputStream out;
        ServerReceiver(  Socket socket)
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
                
                
                if(clients.size() > 4)	// 4���̻� ���� �����κ�
                {	
                	out.writeUTF("�ִ� ������ 4���Դϴ�. ������ȸ��");
                	return;
                }
                
                sendToAll("#" + name + " ���� ����!");
                clients.put(name,out);
                
                players.add(new Player(name));
                System.out.println("���� ���� ������ ��["+clients.size() + "] ��� : " + clinetsPrint());
                
                //if(clients.size() == 4)
                	ThiefCatchStart();
                //else
                //	System.out.println("���� �÷��̾� ������");
                	
                while(in != null)
                {
                   	
                    	
                }
            } catch (IOException e)
            {
                // TODO: handle exception
            }
            
            
            
            finally {
                sendToAll("#" + name + " ���� ����.");
                clients.remove(name);
                System.out.println("[" + socket.getInetAddress() + ":" + socket.getPort() + "] ���� ��������"  );
                System.out.println("���� ���� ������ ��["+clients.size() + "] ��� : " + clinetsPrint());
                
            }
        }
        
        void playerSelect(Player who)
        {
        	PlayerHandPrint(who.name);
        	sendToPlayerMsg(who.name, "1.���� ���� ���� ������ / �׿�. PASS  >>>");
        	String select;
        	String pair;
			try {
				select = in.readUTF();
				if(select.equals("1"))
	            {
					sendToPlayerMsg(who.name, who.handPrint());
	                if(!who.handChk())
	                {
	                	sendToPlayerMsg(who.name,"���� ���������ʽ��ϴ�.");
	                }
	                else
	                {
	                    
	                	sendToPlayerMsg(who.name,"���� ���ڽ� ���� (ex: 4)  >>>");
	                    pair =  in.readUTF();
	                    who.removeCard(pair);
	                }
	            }
	            sendToAll(who.name + "�� �� ���� ");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        }
        
    }
    LinkedList<Player> players = new LinkedList<Player>();
    
    String clinetsPrint()
    {
    	String result = "";
    	
    	Iterator it = clients.keySet().iterator();
        while(it.hasNext())
        	result += (String) it.next() +" ";

    	return result;
    }
    
     void ThiefCatchStart()
    {
    	sendToAll("ThiefCatch ������ �����ϰڽ��ϴ�.");
        deckSetting();
        sendToAll("�ʱⵦ ���� ���� �й� �Ϸ�");
        allPlayerHandPrint();
        tern(players.get(players.size()-1),players.get(0));
    }
    
    void deckSetting()
    {
    	 int MAX_DECK_LENGTH = 40+1;
    	    String joker = "�� ";
    	    String[] deck = new String[MAX_DECK_LENGTH];   
       /* for(int i = 0; i < 10; i++)
            deck[i] = deck[i+10] = deck[i+20] = deck[i+30]  = i+"";
        deck[MAX_DECK_LENGTH-1] = joker;
        
        // ���� deck�迭�� �������� ����
        for(int i=0 ;  i < 10000; i ++)
        {
            int ran1 = (int)(Math.random()*MAX_DECK_LENGTH);
            int ran2 = (int)(Math.random()*MAX_DECK_LENGTH);
            
            String tmp = deck[ran1];
            deck[ran1] = deck[ran2];
            deck[ran2] = tmp;      
        }
        players.add(new Player("A",Arrays.copyOfRange(deck, 0,10)));
        players.add(new Player("B",Arrays.copyOfRange(deck, 10,20)));
        players.add(new Player("C",Arrays.copyOfRange(deck, 20,30)));
        players.add(new Player("D",Arrays.copyOfRange(deck, 30,41)));
        */
        /*
        //test�� ª�� ������
    	players.add(new Player(clients.,new String[]{"1","4"}));
        players.add(new Player("B",new String[]{"3","2"}));
        players.add(new Player("C",new String[]{"3","1"}));
        players.add(new Player("D",new String[]{"4","2",joker}));*/
    	    
    	//players.get(0).setHand(new String[]{"1","4"});
      //  players.get(1).setHand(new String[]{"3","2"});
       // players.get(2).setHand(new String[]{"3","1"});
      //  players.get(3).setHand(new String[]{"4","2",joker});
    	
    	    players.get(0).setHand(new String[]{"1","1","4"});
    	 players.add(new Player("B",new String[]{"3","2"}));
         players.add(new Player("C",new String[]{"3","1"}));
         players.add(new Player("D",new String[]{"4","2",joker}));
            
       
        
    }
    void ServerAllPlayerHandPrint()
    {
        for(Player x : players)
            x.handPrint();
        System.out.println("�ѤѤѤѤѤѤѤѤѤѤѤ�");
    }
    void allPlayerHandPrint()
    {
    	   Iterator it = clients.keySet().iterator();
           while(it.hasNext())
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
    void PlayerHandPrint(String nowname)
    {
    	   Iterator it = clients.keySet().iterator();
           while(it.hasNext())
           {
        	   String name = (String) it.next();
        	   Player tmp = find(name);
               try
               {
            	   if(name.equals(nowname))
            	   {
	                   DataOutputStream out = (DataOutputStream) clients.get(name);
	                   out.writeUTF(tmp.handPrint());
            	   }
               }
               catch (IOException e)
               {
                   // TODO: handle exception
               }
           }
    }
    void ternPrint(String nowtern)
    {
    	   Iterator it = clients.keySet().iterator();
           while(it.hasNext())
           {
        	   String name = (String) it.next();
        	   Player tmp = find(name);
               try
               {
            	   if(name.equals(nowtern))
            	   {
	                   DataOutputStream out = (DataOutputStream) clients.get(name);
	                   out.writeUTF(nowtern + " ����� ���Դϴ�");
            	   }
            	   else
            	   {
	                   DataOutputStream out = (DataOutputStream) clients.get(name);
	                   out.writeUTF("����" +nowtern+ "�� �� �������Դϴ�.");
            	   }
            		   
               }
               catch (IOException e)
               {
                   // TODO: handle exception
               }
           }
    }
    
    void tern(Player before, Player who)
    {
    	ternPrint(who.name);
        who.getCard(before);
        endChk(before);
        who.playerSelect();

        int x = (players.lastIndexOf(who)+1) % players.size();
        tern(who, players.get(x));
    }
    
    boolean endChk(Player who)
    {
        if(who.hand.size() == 0)
        {
            System.out.println("�ڡڡ� "+who.name + " ���а� ������ϴ�. !!Ŭ����!!");
           
            //Player tmp = find(who.name);
            players.remove(who);
            if(players.size() == 1)
            {
                System.out.println("�ۡۡۡۡۡۡۡۡۡ� ���� ���� �ۡۡۡۡۡۡۡۡۡ�");
                System.out.println("���� ���� = " +  players.getFirst().name +"���Դϴ�.");
                System.exit(0);
            }  
        }
        return false;
    }
    Player find(String name)
    {
        for(Player p : players)
            if(p.name.equals(name))
                    return p;
        return null;
    }
    
    
}
