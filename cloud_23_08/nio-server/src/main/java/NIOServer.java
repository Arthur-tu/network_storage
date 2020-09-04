import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NIOServer implements Runnable {

    private final ByteBuffer buffer = ByteBuffer.allocate(256);
    private static int cnt = 0;

    @Override
    public void run() {
        try {
            ServerSocketChannel server = ServerSocketChannel.open();
           // ServerSocket serverSocket = server.socket();
            server.bind(new InetSocketAddress(8189));
            System.out.println("Server started on 8189");
            Selector selector = Selector.open();
            server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_ACCEPT);
            while (server.isOpen()) {
                selector.select();
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isAcceptable()) {
                        cnt++;
                        SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ, "user#" + cnt);
                        System.out.println("Accepted connection: " + "user#" + cnt);
                    }
                    if (key.isReadable()) {
                        System.out.println("Handled read operation");
                        SocketChannel channel = (SocketChannel) key.channel();
                        channel.read(buffer);
                        String data = new String(buffer.array()).trim();
                        if (data.length() > 0) {
                            System.out.println(data);
                            buffer.clear();
                        }
                    }
                    keyIterator.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Thread(new NIOServer()).start();
    }
}
