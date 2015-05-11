package model.simulation;

import model.simulation.strategies.RelativePriorityTotalAbandonmentStrategy;
import model.simulation.strategies.SimulationStrategy;

/**
 * Created by chelen on 09/05/15.
 */
public class SimulationFactory {

    public static SimulationStrategy getStrategy(final SimulationParameters parameters){
//        if (parameters.getPriority().equals("Relative") && parameters.getTolerance().equals("Intolerant")
//                && parameters.getIntolerance().equals("Total"))
            return new RelativePriorityTotalAbandonmentStrategy();

//        else return null;
    }
}
