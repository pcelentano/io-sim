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

/**
 * Created by mgutierrez on 15/5/15.
 */
public class AbsolutePriorityToleranceResumptionStrategy implements SimulationStrategy {

    private Event possibleBExit;

    @Override public void handleArrival(@NotNull Event event, @NotNull Simulation simulation) {
        //si no hay nadie en la cola y hay alguien atendiendose, si es un B y yo soy un A desplazalo, si es un A me mando a la cola
        //si hay alguien en la cola lo encolo y listo

        if(simulation.isQueueEmpty()){
            //Si la cola esta vacia
            if(simulation.getCurrentCustomer() != null){
                //Si hay alguien atendiendose
                if(event.getCustomer().getType() == A && simulation.getCurrentCustomer().getType() == B){
                    //Si se esta atendiendo un B y llega un A
                    //desplazo, quilombo
//                    cageCustomer(event, simulation)

                    cageCustomer(event, simulation);

                    simulation.addCustomertoQueue(event.getCustomer());
                    attendNext(event, simulation, false);
                } else {
                    //Si el que llega es un B o se esta atendiendo un A
                    simulation.addCustomertoQueue(event.getCustomer());
                }
            } else {
                //Si no hay nadie atendiendose
                simulation.addCustomertoQueue(event.getCustomer());
                attendNext(event, simulation, false);
            }
        } else {
            //Si la cola tiene gente, solo encolo
            simulation.addCustomertoQueue(event.getCustomer());
        }
    }

    private void cageCustomer(Event event, Simulation simulation) {
        simulation.setCagedCustomer(simulation.getCurrentCustomer());

        simulation.removeEvent(possibleBExit);

        possibleBExit.setRemainingTime(possibleBExit.getInitTime() - event.getInitTime());
    }

    @Override public void handleDeparture(@NotNull Event event, @NotNull Simulation simulation) {
        //me fijo si viene un A y lo meto, sino
        //si tengo un B desplazado (variable que tengo que generiquizar porque no esta)
        //lo meto, sino que entre el siguiente en la cola

        //CONTEMPLAR, cola vacia, caja vacia, entra un B y el proximo es A, ENCARCELA AL B PAPA

        simulation.setCurrentCusomer(null);

        final Customer customer = simulation.peekCustomerQueue();
        if(customer != null) {
            //Si hay alguien en la cola
            if (customer.getType() == A){
                //Si ese alguien es de tipo A
                attendNext(event, simulation, false);
            } else {
                //Si ese alguien es de tipo B
                if (simulation.getCagedCustomer() != null) {
                    //Si hay alguien encarcelado, lo saco y lo pongo
                    attendNext(event, simulation, true);
                } else {
                    //Si no hay nadie encarcelado
                    attendNext(event, simulation, false);
                }
            }
        } else {
            //Si no hay nadie en la cola esperando (cola vacia)
            if (simulation.getCagedCustomer() != null) {
                //Si hay alguien encarcelado
                attendNext(event, simulation, true);
            }
        }
    }

    private void attendNext(Event event, Simulation simulation, boolean attendCaged) {
        Customer customer;

        if(attendCaged){
            //Sacar al enjaulado y settearlo como null

            customer = simulation.getCagedCustomer();
            simulation.setCagedCustomer(null);

            //Meter el nuevo evento del encarcelado

            possibleBExit = new Event(DEPARTURE, customer, event.getInitTime() + possibleBExit.getRemainingTime(), false);
            simulation.addEventAndSort(possibleBExit);
        } else {
            //Sacar al primero de la cola

            customer = simulation.pollCustomerQueue();

            final Customer checkPeek = simulation.peekCustomerQueue();
            if(checkPeek != null && checkPeek.getType() == A && customer != null && customer.getType() == B){
                //Si el que viene de la cola es un B pero el que sigue es un A, ese A tiene que encarcelar al B
                assert simulation.getCagedCustomer() == null;

                final double mu = Mathematics.getDurationChannel(simulation.getMuB());
                possibleBExit = new Event(DEPARTURE, customer, event.getInitTime() + mu, false);

                cageCustomer(event, simulation);

                customer = checkPeek;
            }
        }

        simulation.setCurrentCusomer(customer);

        if (customer != null){
            final Customer.CustomerType type = customer.getType();
            System.out.println("Atendiendo a " + type.toString());
            event.attentionChanelStatus(OCCUPIED);
            if (!attendCaged) {
                final double mu = Mathematics.getDurationChannel(type == A ? simulation.getMuA() : simulation.getMuB());
                final Event bExit = new Event(DEPARTURE, customer, event.getInitTime() + mu, false);
                if(customer.getType() == B) possibleBExit = bExit;
                simulation.addEventAndSort(bExit);
            }
        } else {
            event.attentionChanelStatus(EMPTY);
        }
    }

    @Override public void handleInitiation(@NotNull Event event, @NotNull Simulation simulation) {
        event.queueLength(0).attentionChanelStatus(EMPTY);
    }
}
