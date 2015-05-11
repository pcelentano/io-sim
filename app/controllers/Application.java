package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import model.simulation.Data;
import model.simulation.InputData;
import model.simulation.Simulation;
import model.simulation.SimulationParameters;
import model.simulation.strategies.RelativePriorityTotalAbandonmentStrategy;
import model.simulation.strategies.SimulationStrategy;
import play.libs.Json;
import play.mvc.*;

import play.mvc.Result;
import views.html.*;

import java.util.ArrayList;
import java.util.List;

import static model.simulation.SimulationFactory.getStrategy;
import static play.libs.Json.toJson;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render());
    }

    public static Result simulation() {
        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
        final JsonNode json = request().body().asJson();
        final InputData data = Json.fromJson(json.get("simData"), InputData.class);
//        final SimulationParameters parameters = Json.fromJson(json.get("simParam"), SimulationParameters.class);
        final SimulationStrategy strategy = getStrategy(new SimulationParameters());
        if (strategy == null) return internalServerError("Strategy not present");
        final Simulation simulation = new Simulation(strategy, data.getClientsHourA(), data.getClientsHourB(), data.getMuA(), data.getMuB(), data.getTime());
        final model.simulation.Result run = simulation.run();
        return ok(toJson(run));

    }
}
