package model;

import model.mathematics.Mathematics;
import model.strategies.SimulationStrategy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

import static model.Customer.CustomerType.A;
import static model.Customer.CustomerType.B;
import static model.Event.EventType.ARRIVAL;

/**
 * Simulation.
 */
public class Simulation {


    private static final double clientsPerHourA = 3;
    private static final double clientsPerHourB = 10;
    private static final double muA = 12;
    private static final double muB = 20;
    public static final int MAX_TIME = 20;

    public final SimulationStrategy simulationStrategy;

    private final Queue<Customer> customerQueue;
    private final List<Event> events;
    private Customer currentCustomer;


    /** Creates a Simulation with given Strategy. */
    public Simulation(SimulationStrategy simulationStrategy) {
        customerQueue = new LinkedList<Customer>();
        events = new ArrayList<>();
        currentCustomer = null;
        this.simulationStrategy = simulationStrategy;
    }

    /** Run Simulation. */
    public void run () {

        addArrivalEvents();

        double time = 0 ;

        addEventAndSort(new Event(Event.EventType.INITIATION, null, time));

        int eventPosition = 0;


        while (time < MAX_TIME) {
            final Event event = events.get(eventPosition);
            handleEvent(event);
            eventPosition++;
            time = event.getTime();
        }


        System.out.println("done!");

    }

    /** Adds event to list and sorts list by time. */
    public void addEventAndSort(@NotNull Event event) {
        events.add(event);
        Collections.sort(events);
    }


    /** Delegates Event Handling to SimulationStrategy. */
    private void handleEvent(Event event) {
        System.out.println("event = " + event);

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
    }


    private void addArrivalEvents() {
        addArrivalsA();
        addArivalsB();
        Collections.sort(events);
    }

    private void addArrivalsA() {
        double time = 0;
        while (time < MAX_TIME){
            final double iaca = Mathematics.getClientArrivalInterval(clientsPerHourA);
            events.add(new Event(ARRIVAL, new Customer(A), time + iaca));
            time = time + iaca;
        }
    }

    private  void addArivalsB() {
        double time = 0;
        while (time < MAX_TIME){
            final double iacb = Mathematics.getClientArrivalInterval(clientsPerHourB);
            events.add(new Event(ARRIVAL, new Customer(B), time + iacb));
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
}
