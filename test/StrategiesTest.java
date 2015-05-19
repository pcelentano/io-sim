import model.simulation.Customer;
import model.simulation.Event;
import model.simulation.Result;
import model.simulation.Simulation;
import model.simulation.strategies.AbsolutePriorityPartialIntoleranceStrategy;
import model.simulation.strategies.SchejtmanStrategy;
import model.simulation.strategies.SimulationStrategy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static model.simulation.Customer.CustomerType.A;
import static model.simulation.Customer.CustomerType.B;
import static model.simulation.Event.EventType.ARRIVAL;
import static model.simulation.Event.EventType.INITIATION;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class StrategiesTest {

    @Test
    public void testArriveBChanelEmpty() {
        final AbsolutePriorityPartialIntoleranceStrategy strategy = new AbsolutePriorityPartialIntoleranceStrategy();
        final Simulation simulation = new Simulation(strategy, 3, 10, 10, 12, 10);

        simulation.addEventAndSort(new Event(INITIATION, null, 0, false));
        simulation.addEventAndSort(new Event(ARRIVAL, new Customer(A, 0, 2), 2, false));
        final Result result = simulation.runManual();

    }

    @Test
    public void myTest() {
        final AbsolutePriorityPartialIntoleranceStrategy strategy = new AbsolutePriorityPartialIntoleranceStrategy();
        final Simulation simulation = new Simulation(strategy, 3, 10, 10, 12, 10);

        simulation.addEventAndSort(new Event(ARRIVAL, new Customer(B, 0, 2), 2, false));
        simulation.addEventAndSort(new Event(ARRIVAL, new Customer(A, 0, 3), 3, false));
        simulation.addEventAndSort(new Event(ARRIVAL, new Customer(B, 1, 10), 10, false));
        simulation.addEventAndSort(new Event(ARRIVAL, new Customer(A, 1, 10.0000000000000001), 10.0000000000000001, false));
        simulation.addEventAndSort(new Event(ARRIVAL, new Customer(B, 2, 10.0000000000000002), 10.0000000000000002, false));
        final Result result = simulation.runManual();
        System.out.println();

    }
}
