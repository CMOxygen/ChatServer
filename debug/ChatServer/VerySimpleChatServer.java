/*
 * LOCALHOST VERSION FOR DEVELOPMENT.
 * NOT FOR COMMON USE
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class VerySimpleChatServer {

//    ArrayList clientOutputStream;

    ArrayList<User> clientOutputStream;

    String chatHistory = "";


    public static void main(String[] args) {
        new VerySimpleChatServer().go();
    }

    public void go() {

        clientOutputStream = new ArrayList<User>();
        chatHistory.concat("test");

        try {
            ServerSocket serverSocket = new ServerSocket(5000);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                User newUser = new User();

                PrintWriter writer
                        = new PrintWriter(clientSocket.getOutputStream());

                newUser.setUserName("USER");
                newUser.setUserSocket(clientSocket);
                newUser.setUserWriter(writer);

                clientOutputStream.add(newUser);

                Thread thread = new Thread(new ClientHandler(clientSocket));
                thread.start();
                System.out.println("got a connection");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void tellEveryone(String message) {

//        Iterator iterator = clientOutputStream.iterator();
       chatHistory = chatHistory.concat(message);

        for (User user : clientOutputStream) {
            try {
                PrintWriter writer = user.getUserWriter();
                writer.println(message);
                writer.flush();
                System.out.println("WRITE" + chatHistory);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

//        while (iterator.hasNext()) {
//
//            try {
//                PrintWriter writer = (/*PrintWriter*/) iterator.next();
//                writer.println(message);
//                writer.flush();
//                System.out.println("WRITE" + message);
//
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
    }

    class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket socket;

        public ClientHandler(Socket clientSocket) {
            try {
                socket = clientSocket;
                InputStreamReader isReader
                        = new InputStreamReader(socket.getInputStream());
                reader = new BufferedReader(isReader);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void run() {
            String message;

            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println("read " + message);
//                    chatHistory.concat(message + "\n");
                    tellEveryone(message);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

class User implements Serializable {
    String userName = "NULL";
    Socket userSocket;
    PrintWriter userWriter;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserSocket(Socket userSocket) {
        this.userSocket = userSocket;
    }

    public PrintWriter getUserWriter() {
        return userWriter;
    }

    public void setUserWriter(PrintWriter userWriter) {
        this.userWriter = userWriter;
    }
}
