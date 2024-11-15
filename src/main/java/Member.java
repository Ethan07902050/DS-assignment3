import java.io.*;
import java.net.Socket;
import java.util.List;

public class Member {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Acceptor acceptor;
    private Proposer proposer;
    private Learner learner;
    private String myId;
    private boolean running;
    private boolean responding;
    private String responseType;

    public Member(String myId, List<String> memberIds, String host,
                  int port, String proposedValue, String responseType) {
        try {
            this.running = true;
            this.responding = true;
            this.socket = new Socket(host, port);
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.myId = myId;
            this.responseType = responseType;

            // Send client ID to server
            this.out.println(myId);

            // Initialize proposer
            this.proposer = null;
            if (proposedValue != null)
                this.proposer = new Proposer(proposedValue, memberIds, myId);

            // Initialize acceptor and learner
            this.acceptor = new Acceptor(memberIds, myId);
            this.learner = new Learner(myId, memberIds.size());
        } catch (Exception e) {
            System.err.println("Cannot initiate member: " + e.getMessage());
        }
    }

    public void run() {
        if (proposer != null) {
            List<Message> messages = proposer.propose();
            messages.forEach(message -> out.println(message.toJsonString()));
        }

        while (running) {
            try {
                String messageStr = in.readLine();
                // System.out.println(myId + " receives " + messageStr);
                Message inMessage = Message.toMessage(messageStr);

                // Add delay or ignore based on response type
                applyResponseDelay();

                Message outMessage;
                List<Message> outMessages;

                // Simulate the member is not responding
                if (!responding)
                    continue;

                switch (inMessage.type) {
                    case "prepare":
                        outMessage = acceptor.onPrepare(inMessage);
                        if (outMessage != null)
                            out.println(outMessage.toJsonString());
                        break;
                    case "promise":
                        outMessages = proposer.onPromise(inMessage);
                        if (outMessages != null)
                            outMessages.forEach(message -> out.println(message.toJsonString()));
                        break;
                    case "accept":
                        outMessages = acceptor.onAccept(inMessage);
                        if (outMessages != null)
                            outMessages.forEach(message -> out.println(message.toJsonString()));
                        break;
                    case "accepted":
                        learner.onAccepted(inMessage);
                        break;
                }
            } catch (IOException e) {
                System.err.println("An error happened when reading message: " + e.getMessage());
            }
        }

        closeResources();
    }

    // Method to introduce delay or ignore response based on responseType
    private void applyResponseDelay() {
        try {
            switch (responseType) {
                case "immediate":
                    // No delay
                    break;
                case "small_delay":
                    Thread.sleep(100); // 100ms delay
                    break;
                case "large_delay":
                    Thread.sleep(2000); // 2000ms delay
                    break;
                case "no_response":
                    responding = false; // Stop the member from responding
                    break;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted during delay: " + e.getMessage());
        }
    }


    // Method to close resources when needed
    public void closeResources() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
}
