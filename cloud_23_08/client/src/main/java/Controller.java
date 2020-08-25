import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public ListView<String> listView;
    public TextField text;
    public Button send;
    private Socket socket;
    private static DataOutputStream os;
    private static DataInputStream is;
    private String clientPath = "client/ClientStorage/";


    public static void stop() {
        try {
            os.writeUTF("quit");
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(ActionEvent actionEvent) {
        String message = text.getText();
        try {
            if (message.startsWith("/upload")) {
                byte[] buffer = new byte[8192];
                os.writeUTF(message);
                String[] mas = message.split(" ");
                File file = new File(clientPath + mas[1].trim());
                InputStream fis = new FileInputStream(file);
                int count = 0;
                while ((count = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, count);
                    os.flush();
                }
                fis.close();
            } else {
                os.writeUTF(message);
                os.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        text.clear();
    }

    public void initialize(URL location, ResourceBundle resources) {
        text.setOnAction(this::sendMessage);
        File dir = new File(clientPath);
        for (File file : dir.listFiles()) {
            listView.getItems().add(file.getName() + "     |     " + file.length() + " bytes");
        }
        try {
            socket = new Socket("localhost", 8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                while (true) {
                    try {
                        String message = is.readUTF();
                        if (message.equals("quit")) {
                            break;
                        }
                        Platform.runLater(() -> listView.getItems().add(message));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onMousePresset(javafx.scene.input.MouseEvent mouseEvent) {
        String selectedItem = listView.getSelectionModel().getSelectedItem();
        String[] mas = selectedItem.split(" ");
        text.setText("/upload " + mas[0]);
    }
}
