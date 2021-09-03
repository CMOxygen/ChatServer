/*
В данном примере реализован клиент для чата.
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

    //два потока для передачи данных.
    PrintWriter writer;
    BufferedReader reader;

    Socket socket;

    JTextArea incoming;
    JTextField outgoing;

    public static void main(String[] args) {
        new SimpleChatClientA().go();
    }

    public void go() {

        JFrame frame = new JFrame("CHAT");
        JPanel panelMain = new JPanel();
        incoming = new JTextArea(15, 50);
        incoming.setLineWrap(true);
        incoming.setWrapStyleWord(true);
        incoming.setEditable(false);

        JScrollPane qScroller = new JScrollPane(incoming);

        qScroller.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroller.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        outgoing = new JTextField(20);

        JButton buttonSend = new JButton("SEND");
        buttonSend.addActionListener(new ListenerButtonSend());
        panelMain.add(qScroller);
        panelMain.add(outgoing);
        panelMain.add(buttonSend);

        setUpNetworking();
        //запускаем отдельный поток для вложенного класса
        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();

        frame.getContentPane().add(BorderLayout.CENTER, panelMain);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setVisible(true);
    }

    private void setUpNetworking() {
        try {
            socket = new Socket("127.0.0.1", 5000);
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
                writer.println(outgoing.getText());
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

            try {

                while ((message = reader.readLine()) != null) {
                    System.out.println("read " + message);
                    incoming.append(message + "\n");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

