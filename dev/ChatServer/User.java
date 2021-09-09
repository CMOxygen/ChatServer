import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;

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