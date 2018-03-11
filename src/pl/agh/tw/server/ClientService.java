package pl.agh.tw.server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientService implements Runnable {
    private final Socket socket;
    private final BroadcastService broadcastService;

    public ClientService(final Socket socket, final BroadcastService broadcastService) {
        this.socket = socket;
        this.broadcastService = broadcastService;
    }

    @Override
    public void run() {
        try {
            System.out.println("Client connected: " + socket.getInetAddress());
            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String registrationLine = reader.readLine();
            String registrationPattern = "^Register: [a-z0-9]+$";

            if (!registrationLine.matches(registrationPattern)) {
                System.out.println("Client incorrect registration command. Closing connection...");
                socket.close();
                return;
            }
            String username = registrationLine.substring(10);
            ClientItem client = new ClientItem(username, socket);
            broadcastService.registerClient(client);
            System.out.println("Client registered: " + socket.getInetAddress() + " -> " + username);

            while(true) {
                String readLine = reader.readLine();
                if (readLine == null)
                    break;
                System.out.println(String.format("%s: %s",
                        username, readLine));
                this.broadcastService.broadcastMessage(client, readLine);
            }

            System.out.println("Client " + username + " disconnected");
            this.broadcastService.unregisterClient(client);
            this.broadcastService.broadcastMessage(client, "<disconnected>");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
