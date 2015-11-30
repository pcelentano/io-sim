package model.simulation.strategies;

import model.simulation.Customer;
import model.simulation.Event;
import model.simulation.Simulation;
import model.simulation.mathematics.Mathematics;

import javax.validation.constraints.NotNull;

import static model.simulation.Customer.CustomerType.A;
import static model.simulation.Customer.CustomerType.B;
import static model.simulation.Event.EventType.SALIDA;
import static model.simulation.Event.Status.VACIO;
import static model.simulation.Event.Status.OCUPADO;

/**
 * Created by Megamingus on 16/5/2015.
 */
public class FIFONoPriorityStrategy implements SimulationStrategy {

    @Override public void handleArrival(@NotNull Event event, @NotNull Simulation simulation) {
        addEventCustomer(simulation, event.getCustomer());

        attend(event, simulation);

        //Settear la longitud de la cola en este evento de entrada
        event.queueLength(simulation.getQueueLength() + simulation.getPriorityQueueLength())
                .attentionChanelStatus(OCUPADO)
                .setQueueALength(simulation.getALength());

        if (simulation.getCurrentCustomer() != null)
            event.setAttentionChannelCustomer(simulation.getCurrentCustomer().getType());
    }

    private void addEventCustomer(Simulation simulation, Customer customer) {
        simulation.addCustomertoQueue(customer);
    }

    private void attend(final Event event, final Simulation simulation) {
        if (simulation.getCurrentCustomer() == null) {
            //si no hay nadie atendiendose

            // me fijo si hay alguien en la cola y lo meto
            final Customer normalCustomer = simulation.peekCustomerQueue();
            if(normalCustomer != null){
                attendThis(simulation, event, simulation.pollCustomerQueue());
            }
        }
    }

    private void attendThis(Simulation simulation, Event event, Customer nextCustomer) {
        nextCustomer.waitTime(event.getInitTime() - nextCustomer.getArrivalTime());
        final Customer.CustomerType type = nextCustomer.getType();
        event.attentionChanelStatus(OCUPADO);
        final double mu = Mathematics.getDurationChannel(type == A ? simulation.getMuA() : simulation.getMuB());
        final Event bExit = new Event(SALIDA, nextCustomer, event.getInitTime() + mu, false);

        simulation.addEventAndSort(bExit);
        simulation.setCurrentCustomer(nextCustomer);
    }

    @Override public void handleDeparture(@NotNull Event event, @NotNull Simulation simulation) {

        simulation.setCurrentCustomer(null);

        attend(event, simulation);

        //Settear permanencia del cliente en el sistema
        event.getCustomer().setPermanence(event.getInitTime() - event.getCustomer().getArrivalTime());

        //Settear la longitud de la cola en este evento de salida
        event.queueLength(simulation.getQueueLength() + simulation.getPriorityQueueLength())
                .attentionChanelStatus(simulation.getCurrentCustomer() == null ? VACIO : OCUPADO)
                .setQueueALength(simulation.getALength());

        if (simulation.getCurrentCustomer() != null)
            event.setAttentionChannelCustomer(simulation.getCurrentCustomer().getType());
    }

    @Override public void handleInitiation(@NotNull Event event, @NotNull Simulation simulation) {
        simulation.absolutePriority();
        event.queueLength(0).attentionChanelStatus(VACIO);
    }

}