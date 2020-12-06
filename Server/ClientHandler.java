package Server;

import java.io.*;
import java.net.*;

public class ClientHandler {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private ServerMain serverMain;
    private String nick;

    public ClientHandler(Socket socket, ServerMain serverMain) {
        try {
            this.socket = socket;
            this.serverMain = serverMain;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        /**
                         * первый цикл отвечающий за авторизацию
                         */
                        while (true) {
                            String msg = in.readUTF(); // считываем сообщение
                            if (msg.startsWith("/login")) {
                                String[] loginAndPassArray = msg.split(" ");
                                String newNick = AuthenticationService.getNickByLoginAndPass(loginAndPassArray[1], loginAndPassArray[2]);
                                if (newNick != null) {
                                    sendMsgBackToClient("/login successful");
                                    nick = newNick;
                                    sendMsgBackToClient("/Welcome " + nick);
                                    serverMain.subscribe(ClientHandler.this);
                                    break;
                                } else {
                                    sendMsgBackToClient("/Incorrect login or password");
                                }
                            }
                        }

                        /**
                         * второй цикл отвечающий за отключение
                         */
                        while (true) {
                            String msg = in.readUTF(); // считываем сообщение
                            if (msg.equalsIgnoreCase("/end")) {
                                out.writeUTF("/server is closed");
                                break;
                            }
                            System.out.println(nick + ": " + msg);
                            serverMain.showMsgToServer(nick + ": " + msg);
                        }

                    } catch (IOException ex) {
                        ex.printStackTrace();
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
                        serverMain.unsubscribe(ClientHandler.this);
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsgBackToClient(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
