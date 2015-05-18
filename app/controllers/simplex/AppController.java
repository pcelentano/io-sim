package controllers.simplex;

import models.WebConnection;
import org.codehaus.jackson.JsonNode;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.simplex;

import java.util.UUID;

public class AppController extends Controller {

    /**
     * Home page
     */
    public static Result index() {
        return ok(simplex.render());
    }



    /**
     * Handle the game webSocket.
     */
    public static WebSocket<JsonNode> socket() {
        final String id = UUID.randomUUID().toString();
        return new WebSocket<JsonNode>() {
            // Called when the WebSocket Handshake is done.
            public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) {
                // Join the user to the page.
                try {
                    WebConnection.join(id, in, out);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    }

}