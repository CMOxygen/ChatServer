/*
LOCALHOST CHAT VERSION FOR DEVELOPMENT.
NOT FOR COMMON USE.
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SimpleChatClientA {

    String chatHistory = "";

    //два потока для передачи данных.
    PrintWriter writer;
    BufferedReader reader;

    Socket socket;

    JTextArea incoming;
    JTextField outgoing;
    JTextField userName;

    JScrollPane qScroller;

    JDialog test;

    public static void main(String[] args) {
        new SimpleChatClientA().go();
    }

    public void go() {

        JFrame frame = new JFrame("CHAT");
        JPanel panelMain = new JPanel();

        incoming = new JTextArea(15, 50);

        outgoing = new JTextField(20);
        userName = new JTextField(10);

        qScroller = new JScrollPane(incoming);

        JLabel labelUserName = new JLabel("User Name:");

        JButton buttonSend = new JButton("SEND");

        incoming.setLineWrap(true);
        incoming.setWrapStyleWord(true);
        incoming.setEditable(false);

        qScroller.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroller.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        buttonSend.addActionListener(new ListenerButtonSend());
        panelMain.add(qScroller);
        panelMain.add(labelUserName);
        panelMain.add(userName);
        panelMain.add(outgoing);
        panelMain.add(buttonSend);

        setUpNetworking();

        //запускаем отдельный поток для вложенного класса
        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();

        frame.getContentPane().add(BorderLayout.CENTER, panelMain);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    private void setUpNetworking() {
        try {
            socket = new Socket("127.1.0.0", 5000);
            InputStreamReader streamReader
                    = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(streamReader);

            writer = new PrintWriter(socket.getOutputStream());

            System.out.println("NETWORKING ESTABLISHED");

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public class ListenerButtonSend implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ev) {

            try {
                if(outgoing.getText().contains("/clear"))
                    incoming.setText("");

                writer.println("[" + userName.getText()
                        + "]" + outgoing.getText());

                writer.flush();
                System.out.println("WRITE " + outgoing.getText());

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            outgoing.setText("");
            outgoing.requestFocus();
        }
    }

    public class IncomingReader implements Runnable {
        @Override
        public void run() {

            String message;

            JScrollBar vertical = qScroller.getVerticalScrollBar();

            try {

                while ((message = reader.readLine()) != null) {
                    System.out.println("read " + message);
                    incoming.append(message + "\n");
                    vertical.setValue(vertical.getMaximum());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

