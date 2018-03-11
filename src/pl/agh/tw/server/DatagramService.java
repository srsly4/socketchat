package pl.agh.tw.server;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

public class DatagramService implements Runnable {
    private final DatagramSocket socket;
    private final BroadcastService broadcastService;

    public DatagramService(DatagramSocket socket, BroadcastService broadcastService) {
        this.socket = socket;
        this.broadcastService = broadcastService;
    }

    @Override
    public void run() {
        byte[] buff = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buff, 1024);

        while (true){
            try {
                this.socket.receive(packet);

                ClientItem clientItem = broadcastService.findClientByAddress(
                        packet.getAddress(),
                        packet.getPort()
                );

                if (clientItem == null) {
                    System.out.println("Could not found the client. Aborting!");
                    continue;
                }
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                byteBuffer.put((clientItem.getUsername() + "(" + packet.getAddress() + "): ")
                        .getBytes("UTF-8"));
                byteBuffer.put(packet.getData(), 0, packet.getLength());

                System.out.println("Datagram packet from: " + clientItem.getUsername());

                broadcastService.broadcastDatagram(clientItem, byteBuffer.array(), socket);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
