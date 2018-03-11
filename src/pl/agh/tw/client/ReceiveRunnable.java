package pl.agh.tw.client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReceiveRunnable implements Runnable {
    private final Socket socket;

    public ReceiveRunnable(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            while (true) {
                String receivedLine = bufferedReader.readLine();
                if (receivedLine == null)
                    break;
                System.out.println(receivedLine);
            }

        } catch (IOException e) {
//            e.printStackTrace();
        }
    }
}
