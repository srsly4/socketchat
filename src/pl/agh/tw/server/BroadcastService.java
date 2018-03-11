package pl.agh.tw.server;


import java.io.BufferedWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BroadcastService implements Runnable {

    private List<ClientItem> clients = new CopyOnWriteArrayList<>();
    private Queue<MessageItem> messageQueue = new ConcurrentLinkedQueue<>();
    private ReentrantLock sendLock = new ReentrantLock();
    private Condition sendCondition = sendLock.newCondition();

    private boolean exitFlag = false;
    public void registerClient(ClientItem clientItem) {
        clients.add(clientItem);
    }

    public void unregisterClient(ClientItem clientItem) {
        clients.remove(clientItem);
    }

    public void broadcastMessage(ClientItem clientItem, String message) {
        messageQueue.add(new MessageItem(clientItem, message));
        sendLock.lock();
        sendCondition.signalAll();
        sendLock.unlock();
    }

    public ClientItem findClientByAddress(InetAddress address, int port) {
        ClientItem found = null;
        for (ClientItem ci : this.clients) {
            if (ci.getClientSocket().getInetAddress().equals(address)
                    && ci.getClientSocket().getPort() == port)
                found = ci;
        }
        return found;
    }

    public void broadcastDatagram(ClientItem clientItem, byte[] data, DatagramSocket socket) {

        for (ClientItem ci : this.clients) {
            if (ci != clientItem) {
                DatagramPacket packet = new DatagramPacket(data, data.length,
                        ci.getClientSocket().getInetAddress(),
                        ci.getClientSocket().getPort());
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void close() {
        exitFlag = true;
    }

    @Override
    public void run() {
        while(!exitFlag) {
            sendLock.lock();
            try {
                while (this.messageQueue.size() <= 0){
                    sendCondition.await();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                exitFlag = true;
            }

            while (this.messageQueue.size() > 0) {
                MessageItem msg = this.messageQueue.poll();
                String msgString = String.format("[%s] %s: %s",
                        msg.getClientItem().getClientSocket().getInetAddress(),
                        msg.getClientItem().getUsername(),
                        msg.getMessage());

                for (ClientItem ci : this.clients) {
                    if (ci != msg.getClientItem()) {
                        try {
                            BufferedWriter writer = ci.getWriter();
                            writer.write(msgString);
                            writer.newLine();
                            writer.flush();
                        } catch (IOException e) {
                            System.out.println("Could not send message: ");
                            e.printStackTrace();
                        }
                    }
                }
            }

            sendLock.unlock();
        }
    }
}
