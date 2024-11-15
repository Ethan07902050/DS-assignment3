import java.util.*;

public class Proposer {
    private String proposalValue;
    private int proposalNumber;

    private List<Message> promises;
    private List<String> memberIds;
    private String myId;

    public Proposer(String proposalValue, List<String> memberIds, String myId) {
        this.proposalNumber = 0;
        this.proposalValue = proposalValue;
        this.promises = new ArrayList<>();
        this.memberIds = memberIds;
        this.myId = myId;
    }

    public List<Message> propose() {
        this.proposalNumber++;
        this.promises.clear();
        List<Message> messages = new ArrayList<>();

        for (String receiver : memberIds) {
            Message message = new Message(myId, receiver, "prepare",
                    proposalNumber,null, 0, null);
            messages.add(message);
        }

        return messages;
    }

    public List<Message> onPromise(Message inMessage) {
        promises.add(inMessage);
        if (promises.size() == (memberIds.size() + 1) / 2) {
            int maxAcceptedNumber = 0;
            String acceptedValue = null;
            List<Message> messages = new ArrayList<>();

            // Find the accepted value with the highest accepted number
            for (Message promise : promises) {
                if (promise.acceptedNumber > maxAcceptedNumber) {
                    acceptedValue = promise.acceptedValue;
                    maxAcceptedNumber = promise.acceptedNumber;
                }
            }

            // Propose the accepted value with the highest proposal number
            // Otherwise propose the initialized value
            if (acceptedValue != null) proposalValue = acceptedValue;
            for (String receiver : memberIds) {
                Message message = new Message(myId, receiver, "accept",
                        proposalNumber, proposalValue, 0, null);
                messages.add(message);
            }
            return messages;
        } else return null;
    }

    private void abort() {
        this.proposalNumber = 0;
    }
}
