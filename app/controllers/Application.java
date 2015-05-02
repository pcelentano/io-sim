package controllers;

import model.Data;
import play.*;
import play.mvc.*;

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
}
