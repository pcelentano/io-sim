package model.simplex;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.F;
import play.libs.Json;
import play.mvc.WebSocket;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mart0
 * Date: 4/24/12
 */
public class WebConnection {
    private static List<User> userList = new ArrayList<User>();

    public static void join(String id, WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) {
        initOutSocket(out, id);
        initInSocket(in, id);
        System.out.println("User Connected ( Online: " + userList.size() + " )");
    }

    private static void initOutSocket(WebSocket.Out<JsonNode> out, String id) {
        final User user = new User(id, out);
        userList.add(user);
    }

    private static void initInSocket(WebSocket.In<JsonNode> in, final String id) {
        in.onMessage(new F.Callback<JsonNode>() {
            public void invoke(JsonNode jsonNode) throws Throwable {
                try {
                    String messageType = jsonNode.get("type").asText();
                    System.out.println("Event Received: " + messageType + " - User: " + id.substring(0, 5));
                    User user = getUserById(id);

                    if (messageType.equals("init")) {
                        String objective = jsonNode.get("objective").asText();
                        String mValue = jsonNode.get("mValue").asText();
                        String tableauCode = jsonNode.get("tableau").asText();
                        String[][] tableau = getTableau(tableauCode);

                        user.initSolver(tableau, mValue, objective);
                        sendToUser(user, "received");
                    } else if (messageType.equals("next")) {
                        user.doNextTableau();
                        sendToUser(user, "received");
                    } else if (messageType.equals("previous")) {
                        user.doPreviousTableau();
                        sendToUser(user, "received");
                    } else if (messageType.equals("optimal")) {
                        user.doOptimalTableau();
                        sendToUser(user, "received");
                    } else if (messageType.equals("initial")) {
                        user.doInitialTableau();
                        sendToUser(user, "received");
                    } else {
                        System.out.println("ERROR: Incompatible type of message received ");
                    }

                } catch (Exception e) {
                    System.out.println("ERROR: Bug Find, The application Crash");
                    e.printStackTrace();
                }

            }
        });

        in.onClose(new F.Callback0() {
            public void invoke() throws Throwable {
                userList.remove(getUserById(id));
                System.out.println("User Disconnected ( Online: " + userList.size() + " )");
            }
        });
    }

    private static void sendToUser(User user, String type) {
        final ObjectNode json = Json.newObject();
        json.put("type", type);
        json.put("msg", "Tableau Received OK");
        json.put("iteration", user.getCurrentIteration());
        json.put("optimal", user.getOptimalIteration());
        json.put("pivotIndex", user.getOptimalIndex());
        json.put("alternativeSolution", user.isAlternativeSolution());
        json.put("thetaTie", user.getThetaTie());
        json.put("openPolyhedron", user.isOpenPolyhedron());
        json.put("incompatibleSolution", user.isIncompatibleSolution());

        String tableauCode = getTableauCode(user.getCurrentTableau());
        json.put("tableau", tableauCode);

        user.getChannel().write(json);
    }

    private static void sendToUser(String id, int currentIteration, String[][] currentTableau) {
        final ObjectNode json = Json.newObject();
        json.put("type", "iteration");
        json.put("iteration", currentIteration);

        String tableauCode = getTableauCode(currentTableau);
        json.put("tableau", tableauCode);

        getUserById(id).getChannel().write(json);
    }

    private static String getTableauCode(String[][] tableau) {
        String result = "";
        for (int i = 0; i < tableau.length; i++) {
            for (int j = 0; j < tableau[i].length; j++) {
                if (j + 1 == tableau[i].length) {
                    result += tableau[i][j];
                } else {
                    result += tableau[i][j] + ",";
                }
            }
            if (i + 1 != tableau.length) {
                result += ";";
            }
        }
        return result;
    }

    private static String[][] getTableau(String tableauCode) {
        String[] columns = tableauCode.split(";");
        int cols = columns.length;
        int rows = columns[0].split(",").length;
        String[][] matrix = new String[cols][rows];
        for (int i = 0; i < columns.length; i++) {
            String column = columns[i];
            String[] cells = column.split(",");
            for (int j = 0; j < cells.length; j++) {
                String cell = cells[j];
                matrix[i][j] = cell;
            }
        }
        return matrix;
    }

    private static User getUserById(String id) {
        for (User user : userList) {
            if (user.getId().equalsIgnoreCase(id)) {
                return user;
            }
        }
        System.out.println("ERROR: cant find that user id ( " + id.substring(0, 5) + " )");
        return null;
    }

}
