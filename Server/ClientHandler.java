package Server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private ServerMain serverMain;
    private String nick;
    private List<String> blackList;

    public List<String> getBlackList() {
        return blackList;
    }

    public String getNick() {
        return nick;
    }

    public ClientHandler(Socket socket, ServerMain serverMain) {
        try {
            this.socket = socket;
            this.serverMain = serverMain;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.blackList = new ArrayList<>();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        /**
                         * первый цикл отвечающий за авторизацию
                         */
                        while (true) {
                            String msg = in.readUTF(); // считываем сообщение
                            if (msg.startsWith("/login")) { // лучше использовать метод startsWith
                                String[] loginAndPassArray = msg.split(" ");
                                String newNick = AuthenticationService.getNickByLoginAndPass(loginAndPassArray[1], loginAndPassArray[2]);
                                if (newNick != null) {
                                    if (!serverMain.isNickTaken(newNick)) {
                                        sendMsgBackToClient("/login successful");
                                        nick = newNick;
                                        sendMsgBackToClient("/Welcome: " + nick + "\n" +
                                                            "Available commands:" + "\n" +
                                                            "/end - disconnect from the server" + "\n" +
                                                            "/w - send personal message to user. Start typing: /w nick1 'your message'" + "\n" +
                                                            "/block - block selected user. Start typing: /block nick1");
                                        serverMain.subscribe(ClientHandler.this);
                                        break;
                                    } else {
                                        sendMsgBackToClient("WARNING!: login is already in use");
                                    }
                                } else {
                                    sendMsgBackToClient("WARNING!: Incorrect login or password");
                                }
                            }
                        }

                        /**
                         * второй цикл отвечающий за отключение
                         * технический блок
                         */
                        while (true) {
                            String msg = in.readUTF(); // считываем сообщение
                            if (msg.startsWith("/")) {
                                if (msg.equalsIgnoreCase("/end")) {
                                    out.writeUTF("/server is closed");
                                    break;
                                }
                                if (msg.startsWith("/w")) {
                                    String[] personalMsg = msg.split(" ", 3);
                                    serverMain.sendPersonalMsg(ClientHandler.this, personalMsg[1], personalMsg[2]);
                                }
                                if (msg.startsWith("/block")) {
                                    String[] blockedUsers = msg.split(" ");
                                    blackList.add(blockedUsers[1]);
                                    sendMsgBackToClient("User " + blockedUsers[1] + " added to blacklist.");
                                }
                            } else {
                                serverMain.showMsgToServer(ClientHandler.this, nick + ": " + msg);
                            }
                            System.out.println(nick + ": " + msg);
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

    public boolean checkBlackList(String nick) {
        return blackList.contains(nick);
    }
}
