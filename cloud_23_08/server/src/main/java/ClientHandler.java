import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final DataInputStream is;
    private final DataOutputStream os;
    private final Socket socket;
    private final IOServer server;
    private static int counter = 0;
    private final String name;
    private String serverPath = "server/src/main/resources/";

    public ClientHandler(Socket socket, IOServer ioServer) throws IOException {
        server = ioServer;
        this.socket = socket;
        counter++;
        name = "user#" + counter;
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
        System.out.println("Client handled: ip = " + socket.getInetAddress());
        System.out.println("Nick:" + name);
        serverPath += name;
    }

    public void sendMessage(String message) throws IOException {
        os.writeUTF(message);
        os.flush();
    }

    public void run() {
        while (true) {
            try {
                String message = is.readUTF();
                if (message.startsWith("/upload")) {
                    byte[] buffer = new byte[8192];
                    File file= new File(serverPath);
                    if (!file.exists()) { file.mkdir();}
                    String[] mas = message.split(" ");
                    File to = new File(serverPath + "/" + mas[1].trim());
                    OutputStream fos = new FileOutputStream(to);
                    int count = 0;
                    while ((count = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, count);
                        fos.flush();
                    }
                    fos.close();
                } else {
                    System.out.println("message from" + name + ": " + message);
                    server.broadCastMessage(message);
                    if (message.equals("quit")) {
                        server.kick(this);
                        os.close();
                        is.close();
                        socket.close();
                        System.out.println("client " + name + " disconnected");
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}





