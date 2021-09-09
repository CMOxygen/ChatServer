/*
 * LOCALHOST VERSION FOR DEVELOPMENT.
 * NOT FOR COMMON USE
 */

import java.io.*;
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
        System.out.println("Server is running...");
        new VerySimpleChatServer().go();
    }

    public void go() {
        try {
            chatHistory = getChatHistory();

        } catch (Exception ex) {
            System.out.println("ERROR OPENING FILE chat_history");
        }
        clientOutputStream = new ArrayList<User>();

        try {
            ServerSocket serverSocket = new ServerSocket(5000);

            while (true) {

                Socket clientSocket = serverSocket.accept();

                User newUser = new User();

                PrintWriter writer
                        = new PrintWriter(clientSocket.getOutputStream());

                isReader = new InputStreamReader(clientSocket.getInputStream());
                reader = new BufferedReader(isReader);

                newUser.setUserName("defaultUserName");
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

    public void saveChatHistory(String history) {
        try {
            FileOutputStream chatFileStream = new FileOutputStream("chat_history.txt");
            ObjectOutputStream chatObjectStream = new ObjectOutputStream(chatFileStream);
            chatObjectStream.writeObject(history);
            chatObjectStream.close();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void clearChatHistory() {
        try {
            FileOutputStream chatFileStream = new FileOutputStream("chat_history.txt");
            ObjectOutputStream chatObjectStream = new ObjectOutputStream(chatFileStream);
            chatObjectStream.writeObject("");
            chatObjectStream.close();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public String getChatHistory() throws Exception {

        FileInputStream chatFileStream = new FileInputStream("chat_history.txt");
        ObjectInputStream chatObjectStream = new ObjectInputStream(chatFileStream);
        return (String) chatObjectStream.readObject();
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

    class ClientHandler implements Runnable, Serializable {

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

                    if (message.contains("/clear"))
                        chatHistory = "";

                    tellEveryone(message);
                    saveChatHistory(chatHistory);
                }
                System.out.println("HISTORY2:\n" + chatHistory);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
