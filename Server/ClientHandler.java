package Server;

import java.io.*;
import java.net.*;

public class ClientHandler {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private ServerMain serverMain;

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
                        while (true) {
                            String msg = in.readUTF(); // считываем сообщение
                            System.out.println("Client: " + msg);

                            if (msg.equalsIgnoreCase("/end")) {
                                out.writeUTF("/server is closed");
                                break;
                            }
                            serverMain.showMsgToServer(msg);
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
