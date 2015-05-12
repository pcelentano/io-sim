package model.simulation;

/**
 * Simulation Events.
 */
public class Event implements Comparable<Event> {

    private final EventType type;
    private final Customer customer;
    private final double initTime;
    private int queueLength;
    private Status attentionChanelStatus;
    private  double deltaTime;
    private final boolean silent;

    /** Event Type. */
    public EventType getType() { return type; }

    /** Event Customer. */
    public Customer getCustomer() { return customer; }

    /** Event start initTime. */
    public double getInitTime() { return initTime; }

    /** Event for type customer and start initTime. */
    public Event(EventType t, Customer c, double time, boolean silent) {
        type = t;
        customer = c;
        initTime = time;
        this.silent = silent;
    }

    @Override public int compareTo(Event o) {
        if (initTime > o.getInitTime()) return 1;
        else if (initTime == o.getInitTime()) return 0;
        else return -1;
    }

    @Override public String toString() {
        return initTime + " : " + type.name + "  " + (customer != null ? customer.getType().toString()  : "none" ) + " L(n) : " + queueLength + " Status : " + attentionChanelStatus;
    }

    public int getQueueLength() {
        return queueLength;
    }

    public Status getAttentionChanelStatus() {
        return attentionChanelStatus;
    }

    public double getDeltaTime() {
        return deltaTime;
    }

    public Event queueLength(int queueLength) {
        this.queueLength = queueLength;
        return this;
    }

    public Event attentionChanelStatus(Status attentionChanelStatus) {
        this.attentionChanelStatus = attentionChanelStatus;
        return this;

    }

    public Event deltaTime(double deltaTime) {
        this.deltaTime = deltaTime;
        return this;
    }

    public boolean isSilent() { return silent; }

    public enum EventType {

        INITIATION("INICIO"), ARRIVAL("ARRIBO"), DEPARTURE("SALIDA");

        private final String name;

        EventType(String name) {
            this.name = name;
        }

        @Override public String toString() {
            return name;
        }

    }


    public enum Status {
        EMPTY, OCCUPIED;
    }
}
