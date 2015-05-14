package model.simulation;

import model.simulation.mathematics.Mathematics;
import model.simulation.strategies.SimulationStrategy;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Predicate;

import static model.simulation.Customer.CustomerType.A;
import static model.simulation.Customer.CustomerType.B;
import static model.simulation.Event.EventType.ARRIVAL;
import static model.simulation.Event.EventType.INITIATION;

/**
 * Simulation.
 */
public class Simulation {


    private final double clientsPerHourA;
    private final double clientsPerHourB;
    private final double muA;
    private final double muB;
    public final int MAX_TIME;

    public final SimulationStrategy simulationStrategy;

    private final Queue<Customer> customerQueue;
    private final List<Event> events;
    private Customer currentCustomer;


    /** Creates a Simulation with given Strategy. */
    public Simulation(SimulationStrategy simulationStrategy, double clientsPerHourA, double clientsPerHourB, double muA, double muB, int max_time) {
        this.clientsPerHourA = clientsPerHourA;
        this.clientsPerHourB = clientsPerHourB;
        this.muA = muA;
        this.muB = muB;
        MAX_TIME = max_time;
        customerQueue = new LinkedList<Customer>();
        events = new ArrayList<>();
        currentCustomer = null;
        this.simulationStrategy = simulationStrategy;
    }

    /** Run Simulation.*/
    public Result run() {

        final Result result = new Result();

        addArrivalEvents();

        double time = 0 ;

        addEventAndSort(new Event(Event.EventType.INITIATION, null, time, false));

        int eventPosition = 0;


        while (time < MAX_TIME) {
            final Event event = events.get(eventPosition);
            handleEvent(event);
            result.addEvent(event);
            eventPosition++;
            time = event.getInitTime();
        }


        System.out.println("done!");


        final NumericResults results = result.getResults();
        results.setLcA(Math.pow(clientsPerHourA, 2) / ((muA - clientsPerHourA) * muA));
        results.setLcB(Math.pow(clientsPerHourB, 2) / ((muB - clientsPerHourB) * muB));
        results.setLa(clientsPerHourA / (muA - clientsPerHourA));
        results.setLb(clientsPerHourB / (muB - clientsPerHourB));
        results.setWcA(results.getLcA()/clientsPerHourA);
        results.setWcB(results.getLcB()/clientsPerHourB);
        results.setWa(results.getLa()/clientsPerHourA);
        results.setWb(results.getLb()/clientsPerHourB);
        results.setHa(results.getLcA()-results.getLa());
        results.setHb(results.getLcB()-results.getLb());

//        if (! withEvents) result.getEvents().clear();

        return result;

    }

    /** Adds event to list and sorts list by time. */
    public void addEventAndSort(@NotNull Event event) {
        events.add(event);
        Collections.sort(events);
    }


    /** Delegates Event Handling to SimulationStrategy. */
    private void handleEvent(Event event) {
        switch (event.getType()){

            case ARRIVAL:
                simulationStrategy.handleArrival(event, this);
                break;
            case DEPARTURE:
                simulationStrategy.handleDeparture(event, this);
                break;
            case INITIATION:
                simulationStrategy.handleInitiation(event, this);
                break;
        }

        // Set delta time
        if (event.getType() != INITIATION){
            final Event previousEvent = events.get(events.indexOf(event) - 1);
            previousEvent.deltaTime(event.getInitTime() - previousEvent.getInitTime());
        }

        System.out.println("event = " + event);

    }


    private void addArrivalEvents() {
        addArrivalsA();
        addArivalsB();
        Collections.sort(events);
    }

    private void addArrivalsA() {
        double time = 0;
        int customerNumber = 0;
        while (time < MAX_TIME){
            final double iaca = Mathematics.getClientArrivalInterval(clientsPerHourA);
            events.add(new Event(ARRIVAL, new Customer(A, customerNumber++, time + iaca), time + iaca, false));
            time = time + iaca;
        }
    }

    private  void addArivalsB() {
        double time = 0;
        int customerNumber = 0;
        while (time < MAX_TIME){
            final double iacb = Mathematics.getClientArrivalInterval(clientsPerHourB);
            events.add(new Event(ARRIVAL, new Customer(B, customerNumber++, time + iacb), time + iacb, false));
            time = time + iacb;
        }
    }


    /** Get B attention rate. */
    public double getMuB() { return muB; }

    /** Get A attention rate. */
    public double getMuA() { return muA; }





    /** Current customer being attended. */
    @Nullable public Customer getCurrentCustomer() { return currentCustomer; }

    /** Set Current customer being attended. */
    public void setCurrentCusomer(@Nullable Customer currentCustomer) { this.currentCustomer = currentCustomer; }

    /** Returns First queued Customer, and removes it from queue, null if queue isEmpty(). */
    @Nullable public Customer pollCustomerQueue() { return customerQueue.poll(); }

    /** Add customer to customerQueue. */
    public void addCustomertoQueue(Customer customer) { customerQueue.add(customer); }

    /** Customer queue empty */
    public boolean isQueueEmpty() { return customerQueue.isEmpty(); }

    /** Returns First queued Customer, without removing it from queue, null if queue isEmpty(). */
    @Nullable public Customer peekCustomerQueue() { return customerQueue.peek(); }

    /** Removes all matching customers from queue. */
    public void removeFromQueue(Predicate<Customer> predicate) { customerQueue.removeIf(predicate); }

    /** Queue size. */
    public int getQueueLength() {
        return customerQueue.size();
    }
}
