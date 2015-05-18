package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import model.simulation.InputData;
import model.simulation.Simulation;
import model.simulation.strategies.AbsolutePriorityToleranceResumptionStrategy;
import model.simulation.strategies.RelativePriorityTotalAbandonmentStrategy;
import model.simulation.strategies.SimulationStrategy;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import javax.validation.constraints.NotNull;

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
}
