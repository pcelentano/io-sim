package model.simulation.strategies;

import model.simulation.Customer;
import model.simulation.Event;
import model.simulation.Simulation;
import model.simulation.mathematics.Mathematics;

import javax.validation.constraints.NotNull;
import java.util.function.Predicate;

import static model.simulation.Customer.CustomerType.A;
import static model.simulation.Customer.CustomerType.B;
import static model.simulation.Event.EventType.SALIDA;
import static model.simulation.Event.Status.VACIO;
import static model.simulation.Event.Status.OCUPADO;

/**
 * Relative Priority, Total Abandonment Simulation Strategy by: Pablo Celentano.
 */
public class RelativePriorityTotalAbandonmentStrategy implements SimulationStrategy {


    private final Predicate<Customer> ALL_CUSTOMERS = new Predicate<Customer>() {
        @Override public boolean test(Customer customer) {
            return true;
        }
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

                    for (int i = 0; i < simulation.getQueueLength(); i++) {
                        final Customer c = simulation.pollCustomerQueue();
                        if (c != null) {
                            c.interrupted();
                            simulation.addEventAndSort(new Event(SALIDA, c, event.getInitTime(), true).comment("Left because A entered"));
                        }
                    }

//                    simulation.removeFromQueue(ALL_CUSTOMERS);
//                    customerQueue.clear(); // limpio la cola tengo que generar eventos de que se fueron y los por ques

//                    System.out.println("removed all B from queue");
                    simulation.addCustomertoQueue(customer);
                }
            }
        } else {
            if (currentCustomer.getType() == A){
                // bLeftBecauseACurrent
                customer.setPermanence(0).interrupted();
                simulation.addEventAndSort(new Event(SALIDA, customer, event.getInitTime(), true).comment("Left Because A current"));
//                System.out.println("B left because A current");
            }
            else {
                if (queueType(simulation) == A){
                    // bLeftBeacuaseAinQueue
                    customer.interrupted();
                    simulation.addEventAndSort(new Event(SALIDA, customer, event.getInitTime(), true).comment("Left because A in queue"));
//                    System.out.println("B left because A in queue");
                }

                else simulation.addCustomertoQueue(customer);
            }
        }

//        event.queueLength(simulation.getQueueLength()).attentionChanelStatus(OCUPADO).setQueueALength(simulation.getALength());
//        if (currentCustomer != null) event.setAttentionChannelCustomer(currentCustomer.getType());

// event.queueLength(simulation.getQueueLength()).attentionChanelStatus(OCUPADO).setQueueALength(simulation.getALength());
// if (currentCustomer != null) event.setAttentionChannelCustomer(currentCustomer.getType());

//Settear la longitud de la cola en este evento de entrada
        event.queueLength(simulation.getQueueLength())
                .attentionChanelStatus(OCUPADO)
                .setQueueALength(simulation.getALength());

        if (simulation.getCurrentCustomer() != null)
            event.setAttentionChannelCustomer(simulation.getCurrentCustomer().getType());


    }

    @Override public void handleDeparture(@NotNull Event event, @NotNull Simulation simulation) {
        if (!event.isSilent()){
            event.comment("Attended Client");
            attendNext(event, simulation);
        }

        final Customer customer = event.getCustomer();
        customer.setPermanence(event.getInitTime() - customer.getArrivalTime());

//        event.queueLength(simulation.getQueueLength()).attentionChanelStatus(simulation.getCurrentCustomer() == null ? VACIO : OCUPADO).setQueueALength(simulation.getALength());
//        if (simulation.getCurrentCustomer() != null) event.setAttentionChannelCustomer(simulation.getCurrentCustomer().getType());

        event.getCustomer().setPermanence(event.getInitTime() - event.getCustomer().getArrivalTime());

//Settear la longitud de la cola en este evento de salida
        event.queueLength(simulation.getQueueLength())
                .attentionChanelStatus(simulation.getCurrentCustomer() == null ? VACIO : OCUPADO)
                .setQueueALength(simulation.getALength());

        if (simulation.getCurrentCustomer() != null)
            event.setAttentionChannelCustomer(simulation.getCurrentCustomer().getType());

    }

    @Override public void handleInitiation(@NotNull Event event, @NotNull Simulation simulation) {
        event.queueLength(0).attentionChanelStatus(VACIO).setQueueALength(simulation.getALength());
    }


    private void attendNext(Event event, Simulation simulation) {
        final Customer customer = simulation.pollCustomerQueue();
        simulation.setCurrentCustomer(customer);

        if (customer != null){
            customer.waitTime(event.getInitTime() - customer.getArrivalTime());
            final Customer.CustomerType type = customer.getType();
//            System.out.println("Atendiendo a " + type.toString());
            event.attentionChanelStatus(OCUPADO);
            final double mu = Mathematics.getDurationChannel(type == A ? simulation.getMuA() : simulation.getMuB());
            simulation.addEventAndSort(new Event(SALIDA, customer, event.getInitTime() + mu, false));
        } else {
            event.attentionChanelStatus(VACIO);
        }
    }

    private Customer.CustomerType queueType(Simulation simulation) {
        // La cola solo puede estar vacia, ser toda de A o toda de B
        if (simulation.isQueueEmpty()) return null;
        //noinspection ConstantConditions
        return simulation.peekCustomerQueue().getType(); //queue is not empty
    }


}
