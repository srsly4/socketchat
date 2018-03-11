package pl.agh.tw.client;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    private static int APP_PORT = 9999;
    private static String MULTICAST_ADDRESS = "228.5.6.7";
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String username, addr;
        if (args.length == 2) {
            username = args[1];
            addr = args[0];
            System.out.println("Connecting to " + addr + " with username " + username);
        } else {
            System.out.println("Connect to: ");
            addr = sc.nextLine();
            System.out.println("Your username: ");
            username = sc.nextLine();
        }
        try {
            Socket socket = new Socket(InetAddress.getByName(addr), APP_PORT);
            DatagramSocket datagramSocket = new DatagramSocket(socket.getLocalPort());
            MulticastSocket multicastSocket = new MulticastSocket(APP_PORT+1);
//            multicastSocket.setReuseAddress(true);
//            multicastSocket.bind(new InetSocketAddress(APP_PORT+1));
            multicastSocket.joinGroup(InetAddress.getByName(MULTICAST_ADDRESS));


            Thread readThread = new Thread(new ReceiveRunnable(socket));
            readThread.start();

            Thread datagramThread = new Thread(new DatagramRunnable(datagramSocket));
            datagramThread.start();

            Thread multicastThread = new Thread(new MulticastRunnable(multicastSocket));
            multicastThread.start();

            System.out.println("Connected.");
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write("Register: " + username + "\n");
            writer.flush();

            System.out.println("Registered");
            while (true) {
                String line = sc.nextLine();
                if (line.equals("/U")) {
                    System.out.println("Enter datagram data line by line finishing with /END:\n");

                    byte[] buff = getMultilineData(sc).getBytes("UTF-8");
                    DatagramPacket packet = new DatagramPacket(buff, buff.length, socket.getInetAddress(), APP_PORT);
                    datagramSocket.send(packet);
                    continue;
                }
                if (line.equals("/M")) {
                    System.out.println("Enter multicast datagram data line by line finishing with /END:\n");

                    byte[] buff = getMultilineData(sc).getBytes("UTF-8");
                    DatagramPacket packet = new DatagramPacket(buff, buff.length,
                            InetAddress.getByName(MULTICAST_ADDRESS), APP_PORT+1);
                    multicastSocket.send(packet);
                    continue;
                }
                if (line.equals("/q")) {
                    socket.close();
                    break;
                }
                writer.write(line);
                writer.newLine();
                writer.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getMultilineData(Scanner sc) {
        StringBuilder builder = new StringBuilder();
        String partial = "";

        while (!partial.equals("/END")) {
            partial = sc.nextLine();
            if (!partial.equals("/END")) {
                builder.append(partial);
                builder.append("\n");
            }
        }
        return builder.toString();
    }
}
