import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PaxosSimulation {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        ElectionServer server = new ElectionServer(port);

        try {
            // Load JSON content from config.json in resources using getResourceAsStream
            InputStream inputStream = PaxosSimulation.class.getClassLoader().getResourceAsStream(args[0]);
            if (inputStream == null) {
                throw new IllegalArgumentException("config.json not found in resources");
            }

            // Read the file content as a string
            String content = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            JSONObject config = new JSONObject(content);

            // Extract members array
            JSONArray membersArray = config.getJSONArray("members");

            // Create memberIds list
            List<String> memberIds = new ArrayList<>();
            for (int i = 0; i < membersArray.length(); i++) {
                JSONObject memberConfig = membersArray.getJSONObject(i);
                memberIds.add(memberConfig.getString("id"));
            }

            // Initialize each member based on configuration
            List<Member> members = new ArrayList<>();
            for (int i = 0; i < membersArray.length(); i++) {
                JSONObject memberConfig = membersArray.getJSONObject(i);

                String myId = memberConfig.getString("id");
                String proposedValue = memberConfig.optString("proposedValue", null);
                if ("null".equals(proposedValue)) proposedValue = null; // handle null explicitly

                String responseType = memberConfig.getString("responseType");

                // Initialize and start each member
                Member member = new Member(myId, memberIds, "localhost", port, proposedValue, responseType);
                members.add(member);
            }

            // Start each member
            for (Member member : members) {
                new Thread(member::run).start();
            }
        } catch (Exception e) {
            System.err.println("Error reading JSON configuration: " + e.getMessage());
        }
    }
}
