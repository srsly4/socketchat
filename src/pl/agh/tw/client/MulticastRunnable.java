package pl.agh.tw.client;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class MulticastRunnable implements Runnable {

    private final MulticastSocket socket;

    public MulticastRunnable(MulticastSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        byte[] buff = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buff, buff.length);

        while (true) {
            try {
                socket.receive(packet);

                System.out.println("Received multicast packet from " + packet.getAddress() + ":\n");
                System.out.println("===========");
                System.out.println(new String(packet.getData(), "UTF-8"));
                System.out.println("===========");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
