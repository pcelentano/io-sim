package model.strategies;

import model.Event;
import model.Simulation;
import org.jetbrains.annotations.NotNull;

/**
 * Created by chelen on 25/01/15.
 */
public interface SimulationStrategy {

    void handleArrival(@NotNull Event event,@NotNull Simulation simulation);

    void handleDeparture(@NotNull Event event,@NotNull Simulation simulation);

    void handleInitiation(@NotNull Event event,@NotNull Simulation simulation);
}
