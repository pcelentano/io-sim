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
 * Created by mgutierrez on 15/5/15.
 */
public class AbsolutePriorityToleranceResumptionStrategy implements SimulationStrategy {
    private Event possibleBExit;

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
        if(customer.getType() == A) simulation.addCustomertoPriorityQueue(customer);
        else simulation.addCustomertoQueue(customer);
    }

    private void attend(final Event event, final Simulation simulation) {
        if (simulation.getCurrentCustomer() == null) {
            //si no hay nadie atendiendose
            final Customer priorityCustomer = simulation.peekPriorityQueue();
            if(priorityCustomer != null){
                //me fijo si hay un A
                attendThis(simulation, event, simulation.pollPriorityQueue());
            } else if(simulation.getCagedCustomer() != null) {
                //me fijo si hay alguien enjaulado y lo devuelvo
                resumeCaged(simulation, event);
            } else{
                //sino me fijo si hay alguien en la cola y lo meto
                final Customer normalCustomer = simulation.peekCustomerQueue();
                if(normalCustomer != null){
                    attendThis(simulation, event, simulation.pollCustomerQueue());
                }
            }
        } else {
            //si hay alguien atendiendose
            if(!simulation.isPriorityQueueEmpty() && simulation.getCurrentCustomer().getType() == B){
                //si el que se atiende es un B y hay algun A, lo enjaulo y mando al A
                cageCurrent(event, simulation);
                attend(event, simulation);
            }
        }
    }

    private void cageCurrent(Event event, Simulation simulation) {
        simulation.setCagedCustomer(simulation.getCurrentCustomer());
        simulation.getCagedCustomer().caged();

        simulation.removeEvent(possibleBExit);
        possibleBExit.setRemainingTime(possibleBExit.getInitTime() - event.getInitTime());
        simulation.setCurrentCusomer(null);
    }

    private void attendThis(Simulation simulation, Event event, Customer nextCustomer) {
        nextCustomer.waitTime(event.getInitTime() - nextCustomer.getArrivalTime());
        final Customer.CustomerType type = nextCustomer.getType();
        event.attentionChanelStatus(OCUPADO);
        final double mu = Mathematics.getDurationChannel(type == A ? simulation.getMuA() : simulation.getMuB());
        final Event bExit = new Event(SALIDA, nextCustomer, event.getInitTime() + mu, false);
        if (nextCustomer.getType() == B) possibleBExit = bExit;
        simulation.addEventAndSort(bExit);
        simulation.setCurrentCusomer(nextCustomer);
    }

    private void resumeCaged(Simulation simulation, Event event) {
        Customer customer = simulation.getCagedCustomer();
        simulation.setCagedCustomer(null);

        //Meter el nuevo evento del encarcelado

        possibleBExit = new Event(SALIDA, possibleBExit.getCustomer(), event.getInitTime() + possibleBExit.getRemainingTime(), false);
        simulation.addEventAndSort(possibleBExit);

        simulation.setCurrentCusomer(customer);
    }

    @Override public void handleDeparture(@NotNull Event event, @NotNull Simulation simulation) {

        simulation.setCurrentCusomer(null);

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
