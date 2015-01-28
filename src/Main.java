import model.Simulation;
import model.strategies.RelativePriorityTotalAbandonmentStrategy;

public class Main {

    public static void main(String[] args) {
        final Simulation simulation = new Simulation(new RelativePriorityTotalAbandonmentStrategy());
        simulation.run();
    }
}
