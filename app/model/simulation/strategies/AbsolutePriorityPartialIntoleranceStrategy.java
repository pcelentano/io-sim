package model.simulation.strategies;

import model.simulation.Customer;
import model.simulation.Event;
import model.simulation.Simulation;
import model.simulation.mathematics.Mathematics;

import javax.validation.constraints.NotNull;

import static model.simulation.Customer.CustomerType.A;
import static model.simulation.Customer.CustomerType.B;
import static model.simulation.Event.EventType.DEPARTURE;
import static model.simulation.Event.Status.EMPTY;
import static model.simulation.Event.Status.OCCUPIED;

public class AbsolutePriorityPartialIntoleranceStrategy implements SimulationStrategy {
    private Event possibleBExit;

    @Override
    public void handleArrival(@NotNull Event event, @NotNull Simulation simulation) {
        if (simulation.isQueueEmpty()) {
            final Customer currentCustomer = simulation.getCurrentCustomer();
            if (currentCustomer != null) {
                if (event.getCustomer().getType() == A && currentCustomer.getType() == B) {
                    simulation.removeEvent(possibleBExit);
                    currentCustomer.setPermanence(event.getInitTime() - currentCustomer.getArrivalTime()).interrupted();
                    simulation.addEventAndSort(new Event(DEPARTURE, simulation.getCurrentCustomer(), event.getInitTime(), true));
                    simulation.addCustomertoQueue(event.getCustomer());
                    attendNext(event, simulation);
                } else {
                    simulation.addCustomertoQueue(event.getCustomer());
                }
            } else {
                simulation.addCustomertoQueue(event.getCustomer());
                attendNext(event, simulation);
            }
        } else {
            simulation.addCustomertoQueue(event.getCustomer());
        }
        event.queueLength(simulation.getQueueLength()).attentionChanelStatus(OCCUPIED).setQueueALength(simulation.getALength());
        if (simulation.getCurrentCustomer() != null)
            event.setAttentionChannelCustomer(simulation.getCurrentCustomer().getType());

    }

    @Override
    public void handleDeparture(@NotNull Event event, @NotNull Simulation simulation) {
        simulation.setCurrentCusomer(null);
        attendNext(event, simulation);
        event.getCustomer().setPermanence(event.getInitTime() - event.getCustomer().getArrivalTime());
        event.queueLength(simulation.getQueueLength()).attentionChanelStatus(simulation.getCurrentCustomer() == null ? EMPTY : OCCUPIED).setQueueALength(simulation.getALength());
        if (simulation.getCurrentCustomer() != null)
            event.setAttentionChannelCustomer(simulation.getCurrentCustomer().getType());

    }

    private void attendNext(Event event, Simulation simulation) {
        Customer customer = simulation.pollCustomerQueue();
        final Customer checkPeek = simulation.peekCustomerQueue();

        if (checkPeek != null && checkPeek.getType() == A && customer != null && customer.getType() == B) {
            customer.waitTime(event.getInitTime() - customer.getArrivalTime()).setPermanence(event.getInitTime() - customer.getArrivalTime()).interrupted();
            simulation.addEventAndSort(new Event(DEPARTURE, customer, event.getInitTime(), false));
            simulation.setCurrentCusomer(customer);
            customer = simulation.pollCustomerQueue();
        }

        simulation.setCurrentCusomer(customer);

        if (customer != null) {
            customer.waitTime(event.getInitTime() - customer.getArrivalTime());
            final Customer.CustomerType type = customer.getType();
            event.attentionChanelStatus(OCCUPIED);
            final double mu = Mathematics.getDurationChannel(type == A ? simulation.getMuA() : simulation.getMuB());
            final Event bExit = new Event(DEPARTURE, customer, event.getInitTime() + mu, false);
            if (customer.getType() == B) possibleBExit = bExit;
            simulation.addEventAndSort(bExit);
        } else {
            event.attentionChanelStatus(EMPTY);
        }
    }

    @Override
    public void handleInitiation(@NotNull Event event, @NotNull Simulation simulation) {
        event.queueLength(0).attentionChanelStatus(EMPTY);
    }
}
