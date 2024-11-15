import java.util.concurrent.atomic.AtomicInteger;

public class Learner {
    private String decidedValue;
    private AtomicInteger acks;
    private String myId;
    private int numMembers;

    public Learner(String myId, int numMembers) {
        this.acks = new AtomicInteger(0);
        this.myId = myId;
        this.numMembers = numMembers;
    }

    public void onAccepted(Message inMessage) {
        if (acks.incrementAndGet() == (numMembers + 1) / 2) {
            System.out.println(myId + " learns value " + inMessage.acceptedValue);
        }
    }
}
