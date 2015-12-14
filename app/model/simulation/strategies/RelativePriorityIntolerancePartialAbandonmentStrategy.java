package model.simulation.strategies;

import model.simulation.Customer;
import model.simulation.Event;
import model.simulation.Simulation;
import model.simulation.mathematics.Mathematics;

import javax.validation.constraints.NotNull;

import static model.simulation.Customer.CustomerType.A;
import static model.simulation.Event.EventType.SALIDA;
import static model.simulation.Event.Status.OCUPADO;
import static model.simulation.Event.Status.VACIO;

/**
 * Created by Tomas Vilaboa on 8/10/2015.
 */
public class RelativePriorityIntolerancePartialAbandonmentStrategy implements SimulationStrategy{

    @Override public void handleArrival(@NotNull Event event, @NotNull Simulation simulation) {
        //Entra A
        // Si no hay nadie, se atiende
        //Si hay un B atendiendose, se atiende apenas termine el B
        //Si hay un A atendiendose, se encola en la cola de prioridad

        //Entra un B
        //Si no hay nadie, se atiende
        //Si hay un B atendiendose, se encola en la cola normal
        //Si hay un A atendiendose, se encola en la cola normal

        final Customer arrivalCustomer = event.getCustomer();
        addCustomerToQueue(simulation, arrivalCustomer);
        attendNext(event, simulation);
        final Customer currentCustomer = simulation.getCurrentCustomer();
        if ( currentCustomer != null)
            event.setAttentionChannelCustomer(currentCustomer.getType());
        event.queueLength(simulation.getQueueLength() + simulation.getPriorityQueueLength())
                .attentionChanelStatus(OCUPADO)
                .setQueueALength(simulation.getALength());
    }




    private void attendNext(final Event event, final Simulation simulation) {
        //Primero me fijo si se termino de atender el ultimo cliente
        //Si es asi, me fijo si hay un A esperando y lo atiendo si es el caso
        //Sino, me fijo si hay un B esperando y lo atiendo si es el caso
        //Sino, sigo esperando a que venga otro cliente

        if (simulation.getCurrentCustomer() == null)
            if (simulation.peekPriorityQueue() != null)
            attendCustomer(simulation, event, simulation.pollPriorityQueue());
        else if (simulation.peekPriorityQueue() != null)
                attendCustomer(simulation, event, simulation.pollCustomerQueue());
    }

    private void attendCustomer(Simulation simulation, Event event, Customer nextCustomer) {
        //Primero, seteo el tiempo de espera del cliente que va a ser atendido
        //Despues, agrego el evento de salida del cliente que termino de atenderse
        //Finalmente, seteo el cliente que acaba de llegar como el cliente que se esta atendiendo
        nextCustomer.waitTime(event.getInitTime() - nextCustomer.getArrivalTime());
        final double mu = Mathematics.getDurationChannel(nextCustomer.getType() == A ? simulation.getMuA() : simulation.getMuB());
        final Event bExit = new Event(SALIDA, nextCustomer, event.getInitTime() + mu, false);
        event.attentionChanelStatus(OCUPADO);
        simulation.addEventAndSort(bExit);
        simulation.setCurrentCustomer(nextCustomer);
    }

    @Override public void handleInitiation(@NotNull Event event, @NotNull Simulation simulation) {
        simulation.absolutePriority();
        event.queueLength(0).attentionChanelStatus(VACIO);
    }

    @Override public void handleDeparture(@NotNull Event event, @NotNull Simulation simulation) {
        simulation.setCurrentCustomer(null);
        attendNext(event, simulation);
        event.getCustomer().setPermanence(event.getInitTime() - event.getCustomer().getArrivalTime());
        event.queueLength(simulation.getQueueLength() + simulation.getPriorityQueueLength())
                .attentionChanelStatus(simulation.getCurrentCustomer() == null ? VACIO : OCUPADO)
                .setQueueALength(simulation.getALength());
        if (simulation.getCurrentCustomer() != null)
            event.setAttentionChannelCustomer(simulation.getCurrentCustomer().getType());
    }



    private void addCustomerToQueue(Simulation simulation, Customer customer) {
        if(customer.getType() == A) simulation.addCustomerToPriorityQueue(customer);
        else simulation.addCustomertoQueue(customer);
    }

}
