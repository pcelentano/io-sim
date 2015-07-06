package model.simulation;

import model.simulation.mathematics.Mathematics;
import model.simulation.mathematics.ResultCalculator;
import model.simulation.strategies.SimulationStrategy;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Predicate;

import static java.util.Objects.*;
import static model.simulation.Customer.CustomerType.A;
import static model.simulation.Customer.CustomerType.B;
import static model.simulation.Event.EventType.ARRIBO;
import static model.simulation.Event.EventType.INICIO;

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
    /** When absolute priority is on! */
    private Queue<Customer> priorityQueue;
    private final TreeSet<Event> events;
    private Customer currentCustomer;
    /** When resumption is needed */
    private Customer cagedCustomer;
    private final Result result;
    private int aInQueue;


    /** Creates a Simulation with given Strategy. */
    public Simulation(SimulationStrategy simulationStrategy, double clientsPerHourA, double clientsPerHourB, double muA, double muB, int max_time) {
        this.clientsPerHourA = clientsPerHourA;
        this.clientsPerHourB = clientsPerHourB;
        this.muA = muA;
        this.muB = muB;
        MAX_TIME = max_time;
        aInQueue = 0;
        customerQueue = new LinkedList<Customer>();
        priorityQueue = null;
        events = new TreeSet<Event>(new Comparator<Event>() {
            @Override public int compare(Event o1, Event o2) {
                return o1.compareTo(o2);
            }
        });
        currentCustomer = null;
        cagedCustomer = null;
        this.simulationStrategy = simulationStrategy;
        result = new Result();
    }

    /** Run Simulation. @param withEvents*/
    public Result run(boolean withEvents) {



        addArrivalEvents();

        double time = 0 ;

        addEventAndSort(new Event(Event.EventType.INICIO, null, time, false));

        while (time < MAX_TIME && !events.isEmpty()) {
            final Event event = events.pollFirst();
            handleEvent(event);
            result.addEvent(event);
//            eventPosition++;
            time = event.getInitTime();
        }


//        System.out.println("done!");


        final NumericResults results = result.getResults();

        final ResultCalculator resultCalculator = new ResultCalculator();
        resultCalculator.calculate(result.getEvents(), results);

//        remove events if not needed
        if (!withEvents) result.getEvents().clear();


        return result;

    }

    /** Adds event to list and sorts list by time. */
    public void addEventAndSort(@NotNull Event event) {
        events.add(event);
    }

    /** Remove event from event list */
    public void removeEvent(@NotNull Event event){
        events.remove(event);
    }


    /** Delegates Event Handling to SimulationStrategy. */
    private void handleEvent(Event event) {
        switch (event.getType()){

            case ARRIBO:
                simulationStrategy.handleArrival(event, this);
                break;
            case SALIDA:
                simulationStrategy.handleDeparture(event, this);
                break;
            case INICIO:
                simulationStrategy.handleInitiation(event, this);
                break;
        }

        // Set delta time
        if (event.getType() != INICIO){
            final Event previousEvent = result.getEvents().get(result.getEvents().size()-1);
            previousEvent.deltaTime(event.getInitTime() - previousEvent.getInitTime());
        }

        final Customer customer = event.getCustomer();
        if (customer != null && customer.getCustomerNumber() % 4000 == 0 && customer.getType() == B) System.out.println("Still working.. " + event.getInitTime());

    }


    private void addArrivalEvents() {
        addArrivalsA();
        addArivalsB();
//        Collections.sort(events);
    }

    private void addArrivalsA() {
        double time = 0;
        int customerNumber = 0;
        while (time < MAX_TIME){
            final double iaca = Mathematics.getClientArrivalInterval(clientsPerHourA);
            time = time + iaca;
            if(time < MAX_TIME)
            events.add(new Event(ARRIBO, new Customer(A, customerNumber++, time), time, false));
        }
        result.getResults().setAcustomers(customerNumber);
    }

    private  void addArivalsB() {
        double time = 0;
        int customerNumber = 0;
        while (time < MAX_TIME){
            final double iacb = Mathematics.getClientArrivalInterval(clientsPerHourB);
            time = time + iacb;
            if(time < MAX_TIME)
                events.add(new Event(ARRIBO, new Customer(B, customerNumber++, time), time, false));
        }
        result.getResults().setBcustomers(customerNumber);
    }


    /** Get B attention rate. */
    public double getMuB() { return muB; }

    /** Get A attention rate. */
    public double getMuA() { return muA; }


    public Customer getCagedCustomer() { return cagedCustomer; }

    public void setCagedCustomer(Customer cagedCustomer) { this.cagedCustomer = cagedCustomer; }

    /** Current customer being attended. */
    @Nullable public Customer getCurrentCustomer() { return currentCustomer; }

    /** Set Current customer being attended. */
    public void setCurrentCustomer(@Nullable Customer currentCustomer) { this.currentCustomer = currentCustomer; }

    /** Returns First queued Customer, and removes it from queue, null if queue isEmpty(). */
    @Nullable public Customer pollCustomerQueue() {
        final Customer poll = customerQueue.poll();
        if (poll != null && poll.getType() == A) aInQueue --;
        return poll;
    }

    /** Add customer to customerQueue. */
    public void addCustomertoQueue(Customer customer) {
        customerQueue.add(customer);
        if (customer.getType() == A) aInQueue++;
    }

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

    public int getALength() {
//        for (final Customer customer : customerQueue) {
//            if (customer.getType() == A) aux++;
//        }
        return aInQueue;
    }

    public void absolutePriority() { priorityQueue = new LinkedList<Customer>(); }

    @Nullable public Customer peekPriorityQueue() {
        checkValidPriorityQueue();
        return priorityQueue.peek();
    }

    /** Add customer to priorityQueue. */
    public void addCustomertoPriorityQueue(Customer customer) {
        checkValidPriorityQueue();
        priorityQueue.add(customer);
        if (customer.getType() == A) aInQueue++;
    }

    /** Returns First queued Priority Customer, and removes it from queue, null if queue isEmpty(). */
    @Nullable public Customer pollPriorityQueue() {
        checkValidPriorityQueue();
        final Customer poll = priorityQueue.poll();
        if (poll != null && poll.getType() == A) aInQueue --;
        return poll;
    }

    /** Priority Queue size. */
    public int getPriorityQueueLength() {
        checkValidPriorityQueue();
        return priorityQueue.size();
    }

    /** Priority queue empty */
    public boolean isPriorityQueueEmpty() {
        checkValidPriorityQueue();
        return priorityQueue.isEmpty();
    }

    private void checkValidPriorityQueue() {
        if(isNull(priorityQueue)) throw new IllegalStateException("This is only for priority customers, and needs to be initialized in the strategy's initialization method.");
    }
}
