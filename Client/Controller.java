package Client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Controller implements Initializable {
    @FXML
    TextArea textArea;
    @FXML
    TextField textField;

    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    final String IP_ADDRESS = "localhost";
    final int PORT = 8189;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String str = in.readUTF(); // сюда приходит сообщение от сервера
                            if (str.equals("/server is closed")) {
                                textArea.appendText("Alert: you have been disconnected from the server");
                                break;
                            }
                            textArea.appendText(str + "\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage() {
        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
