package pl.agh.tw.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static short SERVER_PORT = 9999;
    public static int THREADS = 4;

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newCachedThreadPool();

        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            DatagramSocket serverDatagramSocket = new DatagramSocket(SERVER_PORT);

            BroadcastService broadcastService = new BroadcastService();
            threadPool.submit(broadcastService);

            DatagramService datagramService = new DatagramService(serverDatagramSocket, broadcastService);
            threadPool.submit(datagramService);
            boolean exitFlag = false;
            do {
                try {
                    Socket clientSocket = serverSocket.accept();
                    threadPool.submit(new ClientService(clientSocket, broadcastService));
                } catch (IOException e) {
                    exitFlag = true;
                }
            } while (!exitFlag);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
