package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import model.simplex.WebConnection;
import model.simulation.InputData;
import model.simulation.Simulation;
import model.simulation.strategies.AbsolutePriorityToleranceResumptionStrategy;
import model.simulation.strategies.FIFONoPriorityStrategy;
import model.simulation.strategies.RelativePriorityTotalAbandonmentStrategy;
import model.simulation.strategies.SimulationStrategy;
import model.simulation.strategies.*;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.index;

import javax.validation.constraints.NotNull;

import java.util.UUID;

import static play.libs.Json.toJson;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render());
    }

    public static Result chelenSimulation() {
        return runSimulationWithStrategy(new RelativePriorityTotalAbandonmentStrategy());
    }

    @NotNull private static Result runSimulationWithStrategy(SimulationStrategy strategy) {
        final JsonNode json = request().body().asJson();
        final InputData data = Json.fromJson(json.get("simData"), InputData.class);
        final Simulation simulation = new Simulation(strategy, data.getClientsHourA(), data.getClientsHourB(), data.getMuA(), data.getMuB(), data.getTime());
        final model.simulation.Result run = simulation.run(data.isWithEvents());
        return ok(toJson(run));
    }

    public static Result guteSimulation() {
        return runSimulationWithStrategy(new AbsolutePriorityToleranceResumptionStrategy());
    }

    public static Result mingoSimulation() {
        return runSimulationWithStrategy(new FIFONoPriorityStrategy());
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

    public static Result schejtmanSimulation(){
        return runSimulationWithStrategy(new AbsolutePriorityPartialIntoleranceStrategy());
    }

    public static Result testoriSimulation(){
        return runSimulationWithStrategy(new AbsolutePriorityIntoleranceTotalAbandonmentStrategy());
    }
}
