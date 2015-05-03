package model.simulation.strategies;

import model.simulation.Event;
import model.simulation.Simulation;

import javax.validation.constraints.NotNull;

/**
 * Created by chelen on 25/01/15.
 */
public interface SimulationStrategy {

    void handleArrival(@NotNull Event event, @NotNull Simulation simulation);

    void handleDeparture(@NotNull Event event, @NotNull Simulation simulation);

    void handleInitiation(@NotNull Event event, @NotNull Simulation simulation);
}
