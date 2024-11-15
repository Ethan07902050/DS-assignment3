import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ElectionServer {
    private ServerSocket serverSocket;
    private Map<String, Socket> clients = new ConcurrentHashMap<>(); // Map to store client ID to socket

    public ElectionServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Election Server started on port " + port);
        new Thread(this::acceptClients).start();
    }

    private void acceptClients() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private String clientId;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                // First message from client should be its ID
                clientId = in.readLine();
                System.out.println("Client connected with ID: " + clientId);
                clients.put(clientId, clientSocket); // Register client

                String message;
                while ((message = in.readLine()) != null) {
                    // System.out.println("Received message from " + clientId + ": " + message);
                    processMessage(message);
                }

            } catch (IOException e) {
                System.out.println("Client " + clientId + " disconnected.");
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                clients.remove(clientId);
            }
        }
    }

    private void processMessage(String messageStr) {
        Message message = Message.toMessage(messageStr);
        Socket targetSocket = clients.get(message.receiver);

        if (targetSocket != null) {
            sendMessage(targetSocket, messageStr);
            // System.out.println("Sent message from " + message.sender + " to " + message.receiver);
        } else {
            System.err.println("Target client " + message.receiver + " not found.");
        }
    }

    private void sendMessage(Socket targetSocket, String messageStr) {
        try {
            PrintWriter out = new PrintWriter(targetSocket.getOutputStream(), true);
            out.println(messageStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new ElectionServer(8080); // Start the server on port 8080
    }
}
