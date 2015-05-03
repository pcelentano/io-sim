package controllers;

import model.*;
import model.simulation.Simulation;
import model.simulation.strategies.RelativePriorityTotalAbandonmentStrategy;
import play.*;
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
        return ok(toJson(data));

    }

    public static Result simulation() {
        final Simulation simulation = new Simulation(new RelativePriorityTotalAbandonmentStrategy());
        final model.Result run = simulation.run();
        return ok(toJson(run));

    }
}
