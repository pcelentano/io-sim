package model.simulation.strategies;

import model.simulation.Customer;
import model.simulation.Event;
import model.simulation.Simulation;
import model.simulation.mathematics.Mathematics;

import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Predicate;

import static model.simulation.Customer.CustomerType.A;
import static model.simulation.Customer.CustomerType.B;
import static model.simulation.Event.EventType.SALIDA;
import static model.simulation.Event.Status.OCUPADO;
import static model.simulation.Event.Status.VACIO;

/**
 * Created by lucas on 09/07/15.
 */
public class RelativePriorityToleranceReinitiationStrategy implements SimulationStrategy {

    private Event possibleBExit;


    @Override public void handleArrival(@NotNull Event event, @NotNull  Simulation simulation) {

        final Customer customer = event.getCustomer(); //Cliente nuevo
        final Customer currentCustomer = simulation.getCurrentCustomer(); //Cliente siendo atendido

        if (currentCustomer == null) {
            //Si no hay nadie siendo atendido
            simulation.addCustomertoQueue(customer);
            attendNext(event, simulation);
        }
        else if (customer.getType() == A){
            //Si hay un cliente siendo atendido y el cliente nuevo es tipo A
            //Agrego cliente nuevo primero para que sea atendido luego del actual

            Queue<Customer> auxCustomerQueue;
            auxCustomerQueue = simulation.getCustomerQueue();

            simulation.clearCustomerQueue();
            simulation.addCustomertoQueue(customer);
            for (int i = 0; i < auxCustomerQueue.size(); i++) {
                simulation.addCustomertoQueue(auxCustomerQueue.poll());
            }
            simulation.addAinQueue();
        }
        else {
            //Si hay un cliente siendo atendido y el cliente nuevo es tipo B
            if (currentCustomer.getType() == A){
                // Si el cliente acutal es tipo A, agrego cliente B a la cola
                simulation.addCustomertoQueue(customer);
            }
            else {
                //Si el cliente actual es tipo B, agrego cliente B a la cola y reinicio atencion a cliente actual
                simulation.addCustomertoQueue(customer);

                simulation.removeEvent(possibleBExit);
                Queue<Customer> auxCustomerQueue;
                auxCustomerQueue = simulation.getCustomerQueue();

                simulation.clearCustomerQueue();
                simulation.addCustomertoQueue(currentCustomer);
                for (int i = 0; i < auxCustomerQueue.size(); i++) {
                    simulation.addCustomertoQueue(auxCustomerQueue.poll());
                }
                simulation.setCurrentCusomer(null);
                handleArrival(event, simulation);
            }
        }
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
            simulation.setCurrentCusomer(null);
            attendNext(event, simulation);
        }

        //Settear permanencia del cliente en el sistema
        final Customer customer = event.getCustomer();
        customer.setPermanence(event.getInitTime() - customer.getArrivalTime());

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
        final Customer customer = simulation.pollCustomerQueue(); //Elijo al proximo cliente de la cola
        simulation.setCurrentCusomer(customer);

        if (customer != null) {
            //Si hay alguien en la cola, lo atiendo, ocupando el canal
            customer.waitTime(event.getInitTime() - customer.getArrivalTime());
            final Customer.CustomerType type = customer.getType();
            event.attentionChanelStatus(OCUPADO);
            final double mu = Mathematics.getDurationChannel(type == A ? simulation.getMuA() : simulation.getMuB());
            final Event exitEvent = new Event(SALIDA, customer, event.getInitTime() + mu, false);
            if (customer.getType() == B) possibleBExit = exitEvent;
            simulation.addEventAndSort(exitEvent);
        }
        else {
            //Si no hay nadie en la cola, el canal queda vacio
            event.attentionChanelStatus(VACIO);
        }
    }
}
