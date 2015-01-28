package model.strategies;

import model.Customer;
import model.Event;
import model.Simulation;
import model.mathematics.Mathematics;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

import static model.Customer.CustomerType.A;
import static model.Customer.CustomerType.B;
import static model.Event.EventType.DEPARTURE;

/**
 * Relative Priority, Total Abandonment Simulation Strategy by: Pablo Celentano.
 */
public class RelativePriorityTotalAbandonmentStrategy implements SimulationStrategy {

    private int bLeftBecauseACurrent = 0;
    private int bLeftBecauseAInQueue = 0;
    private int bLeftBecauseAArrival = 0;
    private int bAttendance = 0;
    private int aAttendance = 0;

    private final Predicate<Customer> ALL_CUSTOMERS = customer -> {
        bLeftBecauseAArrival ++;
        return true;
    };

    @Override public void handleArrival(@NotNull Event event, @NotNull  Simulation simulation) {

        // no hay alguien siendo atendido -->  pasa
            // entro A
                // Atendido A --> se encola
                // Atendido B
                    // hay cola ?
                        // Cola a --> encolo A
                        // Cola de B --> limpio cola y encolo A

        // entra B
            // Atendido A --> Se va
            // Atendido B
                // Hay cola?
                    // Cola A --> Se va
                    // Cola B --> Se encola


        final Customer customer = event.getCustomer();
        final Customer currentCustomer = simulation.getCurrentCustomer();

        if (currentCustomer == null) {
            simulation.addCustomertoQueue(customer);
            attendNext(event, simulation);
        }

        else if (customer.getType() == A){
            if (currentCustomer.getType() == A) simulation.addCustomertoQueue(customer);
            else {
                if (queueType(simulation) != B) simulation.addCustomertoQueue(customer);
                else {
                    // bLeftBecauseAArrival ++
                    simulation.removeFromQueue(ALL_CUSTOMERS);
//                    customerQueue.clear(); // limpio la cola tengo que generar eventos de que se fueron y los por ques
                    System.out.println("removed all B from queue");
                    simulation.addCustomertoQueue(customer);
                }
            }
        } else {
            if (currentCustomer.getType() == A){
                // bLeftBecauseACurrent
                bLeftBecauseACurrent ++;
                System.out.println("B left because A current");
            }
            else {
                if (queueType(simulation) == A){
                    // bLeftBeacuaseAinQueue
                    bLeftBecauseAInQueue ++;
                    System.out.println("B left because A in queue");
                }

                else simulation.addCustomertoQueue(customer);
            }
        }


    }

    @Override public void handleDeparture(@NotNull Event event, @NotNull Simulation simulation) { attendNext(event, simulation); }

    @Override public void handleInitiation(@NotNull Event event, @NotNull Simulation simulation) { }


    private void attendNext(Event event, Simulation simulation) {
        final Customer customer = simulation.pollCustomerQueue();
        simulation.setCurrentCusomer(customer);

        if (customer != null){
            final Customer.CustomerType type = customer.getType();
            System.out.println("Atendiendo a " + type.toString());

            if (type == A) aAttendance ++;
            else if (type == B) bAttendance++;

            final double mu = Mathematics.getDurationChannel(type == A ? simulation.getMuA() : simulation.getMuB());
            simulation.addEventAndSort(new Event(DEPARTURE, customer, event.getTime() + mu));
        }
    }

    private Customer.CustomerType queueType(Simulation simulation) {
        // La cola solo puede estar vacia, ser toda de A o toda de B
        if (simulation.isQueueEmpty()) return null;
        //noinspection ConstantConditions
        return simulation.peekCustomerQueue().getType(); //queue is not empty
    }


}
