package MultiThiefCatchClient;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javafx.scene.input.KeyCode;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JScrollPane;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MultiThiefCatchClient extends JFrame
{
    Socket socket;
    
    DataOutputStream out;
    
    DataInputStream in;
    
    private JPanel contentPane;
    
    private JTextField nameTf;
    
    private JTextField handTf;
    
    private JTextField sendTf;
    
    private JTextArea textArea;
    
    private JScrollPane scrollPane;
    
    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable() {
            public void run()
            {
                try
                {
                    MultiThiefCatchClient frame = new MultiThiefCatchClient();
                    frame.setVisible(true);
                    System.out.println("client 실행");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        
    }
    
    public MultiThiefCatchClient()
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e)
            {
                System.out.println("client 종료");
                System.exit(0);
            }
        });
        setTitle("Mulit Thief Catch Client");
        
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        JLabel lblNewLabel = new JLabel("IP");
        lblNewLabel.setBounds(12, 10, 57, 15);
        contentPane.add(lblNewLabel);
        
        JLabel lblNewLabel_1 = new JLabel("Name");
        lblNewLabel_1.setBounds(176, 10, 57, 15);
        contentPane.add(lblNewLabel_1);
        
        JTextField IPTf = new JTextField();
        IPTf.setText("211.63.89.98");
        IPTf.setBounds(12, 23, 116, 21);
        contentPane.add(IPTf);
        IPTf.setColumns(10);
        
        nameTf = new JTextField();
        nameTf.setBounds(176, 23, 116, 21);
        contentPane.add(nameTf);
        nameTf.setColumns(10);
        
        handTf = new JTextField();
        handTf.setToolTipText("");
        handTf.setEditable(false);
        handTf.setBounds(109, 195, 313, 21);
        contentPane.add(handTf);
        handTf.setColumns(10);
        
        sendTf = new JTextField();
        sendTf.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == 10) sendToServer();
            }
        });
        sendTf.setBounds(12, 226, 274, 21);
        contentPane.add(sendTf);
        sendTf.setColumns(10);
        
        textArea = new JTextArea();
        
        scrollPane = new JScrollPane(textArea);
        contentPane.add(scrollPane);
        scrollPane.setBounds(12, 54, 410, 131);
        
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        
        JButton connectBtn = new JButton("Connect");
        connectBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                if (nameTf.getText().length() < 1) return;
                try
                {
                    serverConnect(IPTf.getText(), nameTf.getText());
                    connectBtn.setEnabled(false);
                }
                catch (UnknownHostException e1)
                {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                catch (IOException e1)
                {
                    // TODO Auto-generated catch block
                    textArea.append("서버가 열려있지 않습니다.\n");
                }
                catch (Exception e1)
                {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                
            }
        });
        connectBtn.setBounds(325, 22, 97, 23);
        contentPane.add(connectBtn);
        
        JButton sendBtn = new JButton("Send");
        sendBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                sendToServer();
                
            }
        });
        sendBtn.setBounds(325, 225, 97, 23);
        contentPane.add(sendBtn);
        JLabel lblNewLabel_2 = new JLabel("My Hand : ");
        lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel_2.setBounds(12, 198, 97, 15);
        contentPane.add(lblNewLabel_2);
    }
    
    void sendToServer()
    {
        if (sendTf.getText().length() != 1)
        {
            sendTf.setText("");
            return;
        }
        if (socket == null) return;
        textArea.append(sendTf.getText() + "\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
        try
        {
            out.writeUTF(sendTf.getText());
            sendTf.setText("");
        }
        catch (IOException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
    
    public void serverConnect(String serverIp, String name) throws Exception
    {
        socket = new Socket(serverIp, 7777);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
        Thread receiver = new Thread(new ClientReceiver());
        receiver.start();
        
        out.writeUTF(name);
    }
    
    class ClientReceiver extends Thread
    {
        
        public void run()
        {
            
            try
            {
                while (in != null)
                {
                    String tmp = in.readUTF();
                    
                    // if (tmp.substring(0, 1).equals("손"))
                    if (tmp.contains("손"))
                        handTf.setText(tmp.substring(1));
                    else
                    {
                        textArea.append(tmp + "\n");
                        textArea.setCaretPosition(textArea.getDocument().getLength());
                    }
                }
                
            }
            catch (IOException e)
            {
                // TODO: handle exception
            }
            
        }
    }
}
