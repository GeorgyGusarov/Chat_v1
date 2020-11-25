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

    public void sendMessage() {
        textArea.appendText(textField.getText() + "\n");
        textField.clear();
        textField.requestFocus();
    }

}
