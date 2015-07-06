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

        //If customer is an A type customer
        if(customer.getType() == A){
            //Evaluates if priority queue is empty
            if(simulation.isPriorityQueueEmpty()){
                //If regular queue is empty, customers queues into priority queue
                if(simulation.isQueueEmpty()){
                    //If there is no current or it's type equals to A
                    if(current == null || current.getType() == A) simulation.addCustomertoPriorityQueue(customer);
                    //If type is B
                    else{
                        simulation.removeEvent(possibleBExitEvent);
                        current.interrupted();
                        final Event currentExitEvent = new Event(SALIDA, current, event.getInitTime(), true);
                        simulation.addEventAndSort(currentExitEvent.comment("Left because A entered"));
                        simulation.addCustomertoPriorityQueue(customer);
                    }
                }
                //Else all B type customers queued are interrupted and leave the system
                else{
                    if(current != null && current.getType() == B){
                        simulation.removeEvent(possibleBExitEvent);
                        current.interrupted();
                        final Event currentExitEvent = new Event(SALIDA, current, event.getInitTime(), true);
                        simulation.addEventAndSort(currentExitEvent.comment("Left because A entered"));
                    }
                    for (int i = 0; i < simulation.getQueueLength(); i++) {
                        final Customer c = simulation.pollCustomerQueue();
                        if (c != null) {
                            c.interrupted();
                            simulation.addEventAndSort(new Event(SALIDA, c, event.getInitTime(), true).comment("Left because A entered"));
                        }
                    }
                    simulation.addCustomertoPriorityQueue(customer);
                }
            }
            //If priority queue is not empty, customer is enqueued
            else simulation.addCustomertoPriorityQueue(customer);
        }

        //If customer is a B type customer
        else{
            //Evaluates if current customer being attended is an A type customer and leaves without queueing
            if(current != null && current.getType() == A){
                customer.setPermanence(0).interrupted();
                simulation.addEventAndSort(new Event(SALIDA, customer, event.getInitTime(), true).comment("Left Because A current"));
            }
            //Or if there are A type customers in queue, customer also leaves without queueing
            else if(!simulation.isPriorityQueueEmpty()){
                customer.setPermanence(0).interrupted();
                simulation.addEventAndSort(new Event(SALIDA, customer, event.getInitTime(), true).comment("Left because A in queue"));
            }
            //If there are no A type customers in system, customers queues
            else simulation.addCustomertoQueue(customer);
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
