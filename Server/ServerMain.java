package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ServerMain {
    private Vector<ClientHandler> clients;

    public ServerMain() {
        clients = new Vector<>();
        ServerSocket server = null;
        Socket socket = null;

        try {
            AuthenticationService.connect();
            String str = AuthenticationService.getNickByLoginAndPass("login1", "pass1");
            System.out.println(str);

            server = new ServerSocket(2204);
            System.out.println("Server is online");

            while (true) {
                socket = server.accept();
                System.out.println("Client connected");
                subscribe(new ClientHandler(socket, this));
                // clients.add(new ClientHandler(socket, this));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            AuthenticationService.disconnect();
        }
    }

    public void showMsgToServer(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsgBackToClient(msg);
        }
    }

    public void subscribe(ClientHandler client) {
        clients.add(client);
    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client);
    }
}
