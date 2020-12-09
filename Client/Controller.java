package Client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import java.io.*;
import java.net.*;
import java.util.*;

public class Controller {
    @FXML
    TextArea textArea;
    @FXML
    TextField textField;
    @FXML
    HBox upperPanel;
    @FXML
    HBox bottomPanel;
    @FXML
    TextField loginField;
    @FXML
    PasswordField passwordField;
    @FXML
    ListView<String> usersList; // список юзеров онлайн

    private boolean isLoggedIn;

    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    final String IP_ADDRESS = "localhost";
    final int PORT = 8889;

    public void connect() {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        /**
                         * первый цикл
                         */
                        while (true) {
                            String msg = in.readUTF(); // сюда приходит сообщение от сервера
                            if (msg.startsWith("/login successful")) { // лучше использовать метод startsWith
                                setLoggedIn(true);
                                break;
                            } else {
                                textArea.appendText(msg + "\n");
                            }
                        }

                        /**
                         * второй цикл отвечающий за работу
                         */
                        while (true) {
                            String msg = in.readUTF(); // сюда приходит сообщение от сервера
                            if (msg.equals("/server is closed")) {
                                textArea.appendText("WARNING!: you've been disconnected from the server" + "\n");
                                break;
                            }
                            if (msg.startsWith("/usersList")) {
                                String[] usersArray = msg.split(" ");
                                Platform.runLater(new Runnable() { // нужен для синхронизации работы при изменении клиентов. Обновление листа происходит тут
                                    @Override
                                    public void run() {
                                        usersList.getItems().clear();
                                        for (int i = 1; i < usersArray.length; i++) {
                                            usersList.getItems().add(usersArray[i]);
                                        }
                                    }
                                });
                            } else {
                                textArea.appendText(msg + "\n");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        setLoggedIn(false);
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

    public void tryToAuthentication(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        try {
            out.writeUTF("/login " + loginField.getText() + " " + passwordField.getText());
            loginField.clear();
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
        if (!isLoggedIn) {
            upperPanel.setVisible(true);
            upperPanel.setManaged(true);
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
            usersList.setVisible(false);
            usersList.setManaged(false);
        } else {
            upperPanel.setVisible(false);
            upperPanel.setManaged(false);
            bottomPanel.setVisible(true);
            bottomPanel.setManaged(true);
            usersList.setVisible(true);
            usersList.setManaged(true);
        }
    }

    public void selectUser(MouseEvent mouseEvent) {

    }
}
