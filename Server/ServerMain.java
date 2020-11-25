package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerMain {
    public static void main(String[] args) {
        ServerSocket server = null;
        Socket socket = null;

        try {
            server = new ServerSocket(8189);
            System.out.println("Сервер запущен");

            socket = server.accept();
            System.out.println("Клиент подключился");

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            /*Scanner scanner = new Scanner(socket.getInputStream()); // с помощью сканера читаем данные из сети


             // Realization of echo server

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);*/

            while (true) {
                String str = in.readUTF();
                System.out.println("Client: " + str);

                if (str.equals("/end")) {
                    break;
                }

                out.writeUTF(str);
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
        }

    }
}
