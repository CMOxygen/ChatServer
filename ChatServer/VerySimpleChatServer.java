import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class VerySimpleChatServer {

    ArrayList clientOutputStream;

    public static void main(String[] args) {
        new VerySimpleChatServer().go();
    }

    public void go() {

        clientOutputStream = new ArrayList();

        try {
            ServerSocket serverSocket = new ServerSocket(5000);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                PrintWriter writer
                        = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStream.add(writer);

                Thread thread = new Thread(new ClientHandler(clientSocket));
                thread.start();
                System.out.println("got a connection");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void tellEveryone(String message) {

        Iterator iterator = clientOutputStream.iterator();

        while (iterator.hasNext()) {

            try {
                PrintWriter writer = (PrintWriter) iterator.next();
                writer.println(message);
                writer.flush();
                System.out.println("WRITE" + message);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
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
                    tellEveryone(message);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
