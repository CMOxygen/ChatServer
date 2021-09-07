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

public class VerySimpleChatServer {

    ArrayList<User> clientOutputStream;

    String chatHistory = "";

    InputStreamReader isReader;
    BufferedReader reader;
    Socket socket;

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

                isReader = new InputStreamReader(clientSocket.getInputStream());
                reader = new BufferedReader(isReader);

                newUser.setUserName("USER");
                newUser.setUserSocket(clientSocket);
                newUser.setUserWriter(writer);
                newUser.setUserInputStreamReader(isReader);
                newUser.setUserReader(reader);
                clientOutputStream.add(newUser);

                Thread thread = new Thread(new ClientHandler(newUser));
                thread.start();

                writer.write(chatHistory);
                System.out.println("got a connection");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void tellEveryone(String message) {

        for (User user : clientOutputStream) {
            try {
                PrintWriter writer = user.getUserWriter();
                writer.println(message);
                writer.flush();
                System.out.println("WRITE" + message);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    class ClientHandler implements Runnable {

        User userToTalk;

        public ClientHandler(User user) {

            try {
                socket = user.getUserSocket();
                reader = user.getUserReader();
                //                isReader = user.get;
                userToTalk = user;

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
                    chatHistory = chatHistory.concat(message + "\n");
                    System.out.println("HISTORY:\n" + chatHistory);

                    tellEveryone(message);

//                    if (message.contains("/requestChatHistory")) {
//                        userToTalk.getUserWriter().write(chatHistory);
//                        continue;
//                    } else {
//                        tellEveryone(message);
//                    }
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
    InputStreamReader userInputStreamReader;
    BufferedReader userReader;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Socket getUserSocket() {
        return userSocket;
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

    public void setUserInputStreamReader(InputStreamReader userInputStreamReader) {
        this.userInputStreamReader = userInputStreamReader;
    }

    public BufferedReader getUserReader() {
        return userReader;
    }

    public void setUserReader(BufferedReader userReader) {
        this.userReader = userReader;
    }
}
