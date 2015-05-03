package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import model.*;
import model.simulation.Simulation;
import model.simulation.strategies.RelativePriorityTotalAbandonmentStrategy;
import play.libs.Json;
import play.mvc.*;

import play.mvc.Result;
import views.html.*;

import java.util.ArrayList;
import java.util.List;

import static play.libs.Json.toJson;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render());
    }

    public static Result data() {
        final List<Data> data = new ArrayList<Data>();
        for (int i = 0; i < 5; i++) {
            data.add(new Data("Name #" + i));
        }
        try { Thread.sleep(5000); } catch (InterruptedException e) { e.printStackTrace(); }
        return ok(toJson(data));

    }

    public static Result simulation() {
        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
        final InputData data = Json.fromJson(request().body().asJson(), InputData.class);
        final Simulation simulation = new Simulation(new RelativePriorityTotalAbandonmentStrategy(), data.getClientsHourA(), data.getClientsHourB(), data.getMuA(), data.getMuB(), data.getTime());
        final model.Result run = simulation.run();
        return ok(toJson(run));

    }
}
