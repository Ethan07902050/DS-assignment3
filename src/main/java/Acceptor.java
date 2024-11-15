import java.io.*;
import java.net.*;
import java.util.*;

public class Acceptor {
    private int acceptedNum;
    private int promisedNum;
    private String acceptedValue;
    private String myId;
    private List<String> memberIds;

    public Acceptor(List<String> memberIds, String myId) {
        this.acceptedNum = 0;
        this.promisedNum = 0;
        this.acceptedValue = null;
        this.myId = myId;
        this.memberIds = memberIds;
    }

    public Message onPrepare(Message inMessage) {
        Message outMessage = null;

        // Another proposal has already been accepted by the acceptor
        if (acceptedNum != 0) {
            outMessage = new Message(myId, inMessage.sender, "promise",
                    inMessage.proposalNumber, null, acceptedNum, acceptedValue);
        } else if (promisedNum < inMessage.proposalNumber) {
            promisedNum = inMessage.proposalNumber;
            outMessage = new Message(myId, inMessage.sender, "promise",
                    inMessage.proposalNumber, null, 0, null);
        }
        return outMessage;
    }

    public List<Message> onAccept(Message inMessage) {
        if (promisedNum <= inMessage.proposalNumber) {
            promisedNum = inMessage.proposalNumber;
            acceptedNum = inMessage.proposalNumber;
            acceptedValue = inMessage.proposalValue;

            List<Message> messages = new ArrayList<>();
            for (String receiver : memberIds) {
                Message message = new Message(myId, receiver, "accepted",
                        0, null, acceptedNum, acceptedValue);
                messages.add(message);
            }

            return messages;
        }
        return null;
    }

    private void persistState() {
        // Implementation of state persistence (e.g., file or database)
    }
}
