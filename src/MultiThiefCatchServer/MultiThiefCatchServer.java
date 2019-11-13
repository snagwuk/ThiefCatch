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
        
        // 초기 4인용 덱 생성
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
                ServerReceiver thread = new ServerReceiver(socket);
                thread.start();
                
                thread.sleep(1000);

                if (players.size() == 4)
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
        catch (InterruptedException e)
        {   
            e.printStackTrace();
        }
    }
    
    void turn(Player before, Player who) throws IOException // 재귀로 구현한 turn
    {
        turnPrint(who); // 현재 턴이 누군지 모두에게 출력하는 메서드
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

    void playerSelect(Player who) throws IOException
    { 
        sendToPlayer(who, "[1]. 같은 쌍의 숫자 버리기 / 그 외. PASS");
        
        String select = who.in.readUTF();
        if (select.length() != 1)
        {
            sendToAll(who.name + "의 턴 종료! ");
            allPlayerHandPrint();
            return;
        }
        if (select.equals("1"))
        {
            if (!who.handChk())
                sendToPlayer(who, "쌍이 존재하지않습니다.");
            else
            {
                sendToPlayer(who, "버릴 숫자쌍 선택 (ex: 4)");
                String pair = who.in.readUTF();
                if (pair.length() == 1) sendToAll(who.removeCard(pair));
            }
        }
        sendToAll(who.name + "의 턴 종료! ");
        allPlayerHandPrint();
        
    }
    
    void sendToPlayer(Player who, String msg) throws IOException // 특정 플레이어에게
                                                                 // 메시지 전달 메서드
    {
        who.out.writeUTF("[Server] : " + msg);
    }
    
    void sendToAll(String msg) throws IOException // 모든 플레이어에게 메시지 전달 메서드
    {
        for (Player x : players)
            x.out.writeUTF("[Server] : " + msg);
        System.out.println(msg);
    }
    
    String clinetsPrint() // 현재 클라이언트 목록들 String값으로 반환
    {
        String result = "";
        
        for (Player x : players)
            result += (String) x.name + " ";
        return result;
    }
    
    void ServerAllPlayerHandPrint() // server에게 모든 플레이어 핸드 출력
    {
        for (Player x : players)
            System.out.println(x.handPrint());
        System.out.println("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
    }
    
    void allPlayerHandPrint() throws IOException // 모든 플레이어에게 자신의 손패 출력
    {
        for (Player x : players)
            x.out.writeUTF(x.handPrint());
    }
    
    void turnPrint(Player now) throws IOException // 턴 진행 상황 모든 플레이어에게 출력
    {
        for (Player x : players)
        {
            if (x.equals(now))
                x.out.writeUTF("[Server] : " + now.name + " 당신의 턴입니다");
            else
                x.out.writeUTF("[Server] : 현재" + now.name + "의 턴 진행중입니다.");
        }
        
    }
    
    void endChk(Player who) throws IOException // 손패 size 체크
    {
        if (who.hand.size() == 0)
        {
            sendToAll("★★★ " + who.name + " 클리어 ★★★ ");
            sendToPlayer(who, "당신의 등수는 " + rank++ + "등입니다.");
            players.remove(who);
            if (players.size() == 1)
            {
                for (DataOutputStream x : endouts)
                {
                    x.writeUTF("○○○○○○○○○○ 게임 종료 ○○○○○○○○○○");
                    x.writeUTF("최종 도둑 = " + players.getFirst().name + "님입니다.");
                }
                players.getFirst().out.writeUTF("손당신이 도둑입니다.");
                System.exit(0);
            }
            sendToPlayer(who, "●●●●●●●● 남은 플레이어 기다리는 중 ●●●●●●●●");
        }
    }
    
    Player find(String name) // 입력받은 이름을 가진 Player 객체 반환 메서드
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
                    out.writeUTF("이미 입장한 이름과 중복됩니다.");
                    return;
                }
              
                sendToAll(name + " 님이 입장!");
                
                int firstSize = players.size() * 10;
                int lastSize = players.size() * 10 + 10;
                if (players.size() == 3) lastSize++;
                
                Player player = new Player(name,
                        Arrays.copyOfRange(deck, firstSize, lastSize), in, out);
                players.add(player);
                endouts.add(out);
                
                sendToAll("접속자 수 : " + players.size() + " 목록 : "
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
                // sendToAll(name + " 님이 퇴장.");
                // players.remove(find(name));
                System.out.println("[" + socket.getInetAddress() + ":"
                        + socket.getPort() + "] 에서 접속종료");
            }
        }
        
    }
    
}
