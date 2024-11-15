import org.json.JSONObject;

public class Message {
    public String sender;
    public String receiver;
    public String type;
    public int proposalNumber;
    public String proposalValue;
    public int acceptedNumber;
    public String acceptedValue;

    // Default constructor
    public Message() {}

    // Parameterized constructor
    public Message(String sender, String receiver, String type, int proposalNumber,
                   String proposalValue, int acceptedNumber, String acceptedValue) {
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
        this.proposalNumber = proposalNumber;
        this.proposalValue = proposalValue;
        this.acceptedNumber = acceptedNumber;
        this.acceptedValue = acceptedValue;
    }

    // Converts a Message object to a JSON string
    public String toJsonString() {
        JSONObject json = new JSONObject();
        json.put("sender", sender);
        json.put("receiver", receiver);
        json.put("type", type);
        json.put("proposalNumber", proposalNumber);
        json.put("proposalValue", proposalValue);
        json.put("acceptedNumber", acceptedNumber);
        json.put("acceptedValue", acceptedValue);
        return json.toString();
    }

    // Extracts a Message object from a JSON string
    public static Message toMessage(String jsonString) {
        JSONObject json = new JSONObject(jsonString);
        Message message = new Message();
        message.sender = json.optString("sender", null);
        message.receiver = json.optString("receiver", null);
        message.type = json.optString("type", null);
        message.proposalNumber = json.optInt("proposalNumber", 0);
        message.proposalValue = json.optString("proposalValue", null);
        message.acceptedNumber = json.optInt("acceptedNumber", 0);
        message.acceptedValue = json.optString("acceptedValue", null);
        return message;
    }
}
