import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NIOClient implements Runnable {

    @Override
    public void run() {
        try {
            int port = 8189;
            InetAddress hostIP= InetAddress.getLocalHost();
            InetSocketAddress myAddress = new InetSocketAddress(hostIP, port);
            SocketChannel channel = SocketChannel.open(myAddress);
            Scanner in = new Scanner(System.in);
            ByteBuffer buffer = ByteBuffer.allocate(256);
            while (channel.isOpen()) {
                String message = in.next();
                buffer.put(message.getBytes());
                buffer.flip();
                System.out.println("Written " + channel.write(buffer) + " bytes");
                buffer.clear();
            }
            } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Thread(new NIOClient()).start();
    }
}