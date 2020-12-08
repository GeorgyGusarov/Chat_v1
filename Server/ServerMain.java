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
            server = new ServerSocket(8889);
            System.out.println("Server is online");

            while (true) {
                socket = server.accept();
                System.out.println("Client connected");
                new ClientHandler(socket, this);
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

    /**
     *
     * @param from пришёл пользователь, которому мы собираемся отправить сообщение
     * @param msg перебераем пользователей и если его енту в списке, то отправляем msg
     */
    public void showMsgToServer(ClientHandler from, String msg) {
        for (ClientHandler o : clients) {
            if (!o.checkBlackList(from.getNick())) {
                o.sendMsgBackToClient(msg);
            }
        }
    }

    public void subscribe(ClientHandler client) {
        clients.add(client);
    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client);
    }

    public void sendPersonalMsg(ClientHandler from, String to, String msg) {
        for (ClientHandler o : clients) {
            if (o.getNick().equals(to)) {
                if (!o.checkBlackList(from.getNick())) {
                    o.sendMsgBackToClient("from " + from.getNick() + ": " + msg);
                    from.sendMsgBackToClient("to " + to + ": " + msg);
                    return;
                }
            }
        }
        from.sendMsgBackToClient("Client with nick: " + to + " is not online");
    }

    public boolean isNickTaken(String nick) {
        for (ClientHandler o : clients) {
            if (o.getNick().equals(nick)) {
                return true;
            }
        }
        return false;
    }
}
