package model.simulation.strategies;

import model.simulation.Customer;
import model.simulation.Event;
import model.simulation.Simulation;
import model.simulation.mathematics.Mathematics;

import javax.validation.constraints.NotNull;

import static model.simulation.Customer.CustomerType.A;
import static model.simulation.Customer.CustomerType.B;
import static model.simulation.Event.EventType.SALIDA;
import static model.simulation.Event.Status.OCUPADO;
import static model.simulation.Event.Status.VACIO;

/**
 * Created by franco on 21/06/2015.
 */
public class AbsolutePriorityIntoleranceTotalAbandonmentStrategy implements SimulationStrategy{

    private Event possibleBExitEvent;

    @Override
    public void handleArrival(@NotNull Event event, @NotNull Simulation simulation) {
        addEventCustomer(simulation, event);
        attend(event, simulation);

        //Set queue length at arrival event
        event.queueLength(simulation.getQueueLength() + simulation.getPriorityQueueLength())
                .attentionChanelStatus(OCUPADO)
                .setQueueALength(simulation.getALength());

        if (simulation.getCurrentCustomer() != null) event.setAttentionChannelCustomer(simulation.getCurrentCustomer().getType());
    }

    @Override
    public void handleDeparture(@NotNull Event event, @NotNull Simulation simulation) {

        simulation.setCurrentCustomer(null);

        attend(event, simulation);

        //Set client's presence within the system
        event.getCustomer().setPermanence(event.getInitTime() - event.getCustomer().getArrivalTime());

        //Set queue length at exit event
        event.queueLength(simulation.getQueueLength() + simulation.getPriorityQueueLength())
                .attentionChanelStatus(simulation.getCurrentCustomer() == null ? VACIO : OCUPADO)
                .setQueueALength(simulation.getALength());

        if (simulation.getCurrentCustomer() != null)
            event.setAttentionChannelCustomer(simulation.getCurrentCustomer().getType());
    }

    @Override
    public void handleInitiation(@NotNull Event event, @NotNull Simulation simulation) {
        simulation.absolutePriority();
        event.queueLength(0).attentionChanelStatus(VACIO);
    }

    /**
     * Adds a customer to a priority queue if is an A or adds it to the regular queue if is a B
     *
     * @param simulation simulation
     * @param event event
     */
    private void addEventCustomer(Simulation simulation, Event event) {
        //Sets event's & current customer
        final Customer customer = event.getCustomer();
        final Customer current = simulation.getCurrentCustomer();

        //If customer is A typed
        if(customer.getType() == A){
            //Check that current is not null
            if(current == null){
                //If there are B typed customers in System, they leave
                if(!simulation.isQueueEmpty()){
                    for (int i = 0; i < simulation.getQueueLength(); i++) {
                        final Customer c = simulation.pollCustomerQueue();
                        if (c != null) {
                            c.interrupted();
                            simulation.addEventAndSort(new Event(SALIDA, c, event.getInitTime(), true).comment("Left because A entered"));
                        }
                    }
                    simulation.addCustomertoPriorityQueue(customer);
                }
                else simulation.addCustomertoPriorityQueue(customer);
            }
            //If current is not null
            else{
                if(current.getType() == A) simulation.addCustomertoPriorityQueue(customer);
                //If current is a B typed customer, he is interrupted
                else{
                    simulation.removeEvent(possibleBExitEvent);
                    current.interrupted();
                    final Event currentExitEvent = new Event(SALIDA, current, event.getInitTime(), true);
                    simulation.addEventAndSort(currentExitEvent.comment("Left because A entered"));
                    //All B typed customers queued leave
                    if(!simulation.isQueueEmpty()){
                        for (int i = 0; i < simulation.getQueueLength(); i++) {
                            final Customer c = simulation.pollCustomerQueue();
                            if (c != null) {
                                c.interrupted();
                                simulation.addEventAndSort(new Event(SALIDA, c, event.getInitTime(), true).comment("Left because A entered"));
                            }
                        }
                    }
                    simulation.addCustomertoPriorityQueue(customer);
                }
            }
        }

        //If customer is B typed
        else if(customer.getType() == B){
            //Check if current is null
            if(current == null){
                //If there are no A typed customers queued, customer is queued
                if(simulation.isPriorityQueueEmpty()) simulation.addCustomertoQueue(customer);
                //Else he leaves without entering
                else {
                    customer.setPermanence(0).interrupted();
                    simulation.addEventAndSort(new Event(SALIDA, customer, event.getInitTime(), true).comment("Left because A in queue"));
                }
            }
            //When current is not null
            else{
                //If current is A typed, customers leaves without entering
                if(current.getType() == A){
                    customer.setPermanence(0).interrupted();
                    simulation.addEventAndSort(new Event(SALIDA, customer, event.getInitTime(), true).comment("Left Because A current"));
                }
                else simulation.addCustomertoQueue(customer);
            }
        }
    }

    /**
     * @param event      event
     * @param simulation simulation
     */
    private void attend(final Event event, final Simulation simulation) {
        //Checks if there is a customer to be attended
        if(simulation.getCurrentCustomer() == null) {
            final Customer priorityCustomer = simulation.peekPriorityQueue();

            //Check if there is an A type customer
            if (priorityCustomer != null) attendThis(simulation, event, simulation.pollPriorityQueue());

            //If there are no A type customers, check for a regular customer and attend him
            else {
                final Customer regularCustomer = simulation.peekCustomerQueue();
                if (regularCustomer != null) attendThis(simulation, event, simulation.pollCustomerQueue());
            }
        }
    }

    /**
     * Attends a customer
     *
     * @param simulation   simulation
     * @param event        event
     * @param nextCustomer customer
     */
    private void attendThis(Simulation simulation, Event event, Customer nextCustomer) {

        nextCustomer.waitTime(event.getInitTime() - nextCustomer.getArrivalTime());
        final Customer.CustomerType type = nextCustomer.getType();
        event.attentionChanelStatus(OCUPADO);
        final double mu = Mathematics.getDurationChannel(type == A ? simulation.getMuA() : simulation.getMuB());
        final Event bExit = new Event(SALIDA, nextCustomer, event.getInitTime() + mu, false);

        // Add event as a possible interruption
        if (nextCustomer.getType() == B) possibleBExitEvent = bExit;
        simulation.addEventAndSort(bExit);
        simulation.setCurrentCustomer(nextCustomer);
    }
}
