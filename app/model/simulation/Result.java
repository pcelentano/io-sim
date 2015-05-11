package model.simulation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chelen on 02/05/15.
 */
public class Result {
    private final NumericResults results;
    private final List<Event> events;


    public Result() {
        events = new ArrayList<>();
        results = new NumericResults();
    }

    public void addEvent(Event e) {
        events.add(e);
    }

    public List<Event> getEvents() {
        return events;
    }

    public NumericResults getResults(){
        return results;
    }


}
