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
    HashMap clients; // out, 주소
    
    HashMap clientsIn; // 1, 서버가 받기위한
    
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
        
        // 넣은 deck배열을 랜덤으로 섞기
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
            System.out.println("서버가 시작되었습니다.");
            while (true)
            {
                socket = serversocket.accept();
                System.out.println("[" + socket.getInetAddress() + ":"
                        + socket.getPort() + "] 에서 접속하였습니다.");
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
                    sendToAll("도둑잡기 게임 시작하겠습니다.");
                    turn(players.get(players.size() - 1), players.get(0));
                }
                
            }
            
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    void turn(Player before, Player who) // 재귀로 구현한 turn
    {
        turnPrint(who.name); // 현재 턴이 누군지 모두에게 출력하는 메서드
        who.getCard(before); // 현재 플레이어가 이전 플레이어 카드 한장 뽑는 메서드
        sendToAll(who.name + "가 " + before.name + "의 카드를 한장 뽑았습니다.");
        allPlayerHandPrint(); // 모든 플레이어 자신 손패 출력
        
        endChk(before); // 미구현
        
        playerSelect(who);
        
        ServerAllPlayerHandPrint();
        
        // 다음 턴 진행을 위한 turn 메소드 호출
        int x = (players.lastIndexOf(who) + 1) % players.size();
        turn(who, players.get(x));
    }
    
    void playerSelect(Player who)
    {
        DataInputStream in = (DataInputStream) clientsIn.get(who.name);
        
        try
        {
            
            sendToPlayerMsg(who.name, "[1] 같은 쌍의 숫자 버리기 / 그외. PASS");
            
            String select = in.readUTF();
            if (select.length() != 1)
            {
                sendToAll(who.name + "의 턴 종료! ");
                allPlayerHandPrint();
                return;
            }
            if (select.equals("1"))
            {
                if (!who.handChk())
                    sendToPlayerMsg(who.name, "쌍이 존재하지않습니다.");
                else
                {
                    sendToPlayerMsg(who.name, "버릴 숫자쌍 선택 (ex: 4)");
                    String pair = in.readUTF();
                    if (pair.length() == 1) sendToAll(who.removeCard(pair));
                }
            }
            sendToAll(who.name + "의 턴 종료! ");
            allPlayerHandPrint();
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
    }
    
    void sendToPlayerMsg(String name, String msg) // 특정 플레이어에게 메시지 전달 메서드
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
    
    void sendToAll(String msg) // 모든 플레이어에게 메시지 전달 메서드
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
    
    String clinetsPrint() // 현재 클라이언트 목록들 String값으로 반환
    {
        String result = "";
        
        Iterator it = clients.keySet().iterator();
        while (it.hasNext())
            result += (String) it.next() + " ";
        
        return result;
    }
    
    void ServerAllPlayerHandPrint() // server에게 모든 플레이어 핸드 출력
    {
        for (Player x : players)
            System.out.println(x.handPrint());
        System.out.println("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
    }
    
    void allPlayerHandPrint() // 모든 플레이어에게 자신의 손패 출력
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
    
    void turnPrint(String nowturn) // 턴 진행 상황 모든 플레이어에게 출력
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
                    out.writeUTF("[Server] : " + nowturn + " 당신의 턴입니다");
                }
                else
                {
                    DataOutputStream out = (DataOutputStream) clients.get(name);
                    out.writeUTF("[Server] : 현재" + nowturn + "의 턴 진행중입니다.");
                }
                
            }
            catch (IOException e)
            {
                // TODO: handle exception
            }
        }
    }
    
    boolean endChk(Player who) // 손패 size 체크
    {
        if (who.hand.size() == 0)
        {
            sendToAll("★★★ " + who.name + " 손패가 비었습니다. !!클리어!!");
            players.remove(who);
            clientsIn.remove(who.name);
            clients.remove(who.name);
            if (players.size() == 1)
            {
                sendToAll("○○○○○○○○○○ 게임 종료 ○○○○○○○○○○");
                sendToAll("최종 도둑 = " + players.getFirst().name + "님입니다.");
                System.exit(0);
            }
        }
        return false;
    }
    
    Player find(String name) // 입력받은 이름을 가진 Player 객체 반환 메서드
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
                sendToAll(name + " 님이 입장!");
                
                int firstSize = clients.size() * 10;
                int lastSize = clients.size() * 10 + 10;
                ;
                if (clients.size() == 3) lastSize++;
                
                players.add(new Player(name,
                        Arrays.copyOfRange(deck, firstSize, lastSize)));
                clients.put(name, out);
                clientsIn.put(name, in);
                sendToAll("접속자 수 : " + clients.size() + " 목록 : "
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
                sendToAll(name + " 님이 퇴장.");
                clients.remove(name);
                clientsIn.remove(name);
                System.out.println("[" + socket.getInetAddress() + ":"
                        + socket.getPort() + "] 에서 접속종료");
                System.out.println("현재 서버 접속자 수[" + clients.size() + "] 목록 : "
                        + clinetsPrint());
            }
        }
        
    }
    
}
