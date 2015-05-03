package model;

import model.simulation.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chelen on 02/05/15.
 */
public class Result {
    private final double resultA;
    private final double resultB;
    private final List<Event> events;
//    private final double lc;
//    private final double la;
//    private final double lb;
//    private final double l;
//    private final double wcA;
//    private final double wcB;
//    private final double wC;


    public Result() {
        events = new ArrayList<>();
        resultA = 0;
        resultB = 0;
    }
    public void addEvent(Event e) {
        events.add(e);
    }

    public List<Event> getEvents() {
        return events;
    }

    public double getResultB() {
        return resultB;
    }

    public double getResultA() {
        return resultA;
    }
}
