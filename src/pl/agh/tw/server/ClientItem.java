package pl.agh.tw.server;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class ClientItem {
    private final String username;
    private final Socket clientSocket;
    private final BufferedWriter writer;

    public ClientItem(String username, Socket clientSocket) throws IOException {
        this.username = username;
        this.clientSocket = clientSocket;
        this.writer = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
    }

    public String getUsername() {
        return username;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public BufferedWriter getWriter() {
        return writer;
    }
}
