package model.simulation.mathematics;

import model.simulation.Customer;
import model.simulation.Event;
import model.simulation.NumericResults;

import java.util.List;

import static model.simulation.Customer.CustomerType.A;
import static model.simulation.Customer.CustomerType.B;
import static model.simulation.Event.EventType.SALIDA;
import static model.simulation.Event.Status.OCUPADO;

/**
 * Created by pablo on 5/14/15.
 */
public class ResultCalculator {


    public void calculate(final List<Event> events, final NumericResults results) {

        double lcT = 0;
        double lcaT = 0;
        double lcbT = 0;
        double lT = 0;
        double laT = 0;
        double lbT = 0;
        double wN  = 0;
        double waN  = 0;
        double wbN  = 0;
        double wcN = 0;
        double wcaN = 0;
        double wcbN = 0;
        double h   = 0;
        double ha   = 0;
        double hb   = 0;
        int abandonment = 0;
        int notEnter = 0;
        int cagedTotal = 0;


        for (final Event event : events) {
            final Customer customer = event.getCustomer();
            lcT += event.getQueueLength() * event.getDeltaTime();
            lT  += (event.getQueueLength() + (event.getAttentionChanelStatus() == OCUPADO ? 1 : 0)) * event.getDeltaTime();

            lcaT += event.getQueueALength() * event.getDeltaTime();
            laT  += (event.getQueueALength() + (event.getAttentionChanelStatus() == OCUPADO && event.getAttentionChannelCustomer() == A ? 1 : 0)) * event.getDeltaTime();

            lcbT += (event.getQueueLength() - event.getQueueALength()) * event.getDeltaTime();
            lbT  += (event.getQueueLength() - event.getQueueALength() + (event.getAttentionChanelStatus() == OCUPADO && event.getAttentionChannelCustomer() == B ? 1 : 0)) * event.getDeltaTime();

            if (customer != null && event.getType() == SALIDA){
                    wN  += customer.getPermanence();
                    wcN  += customer.getWaitTime();
                if(customer.wasCaged()) cagedTotal++;

                if (customer.getType() == A){
                    waN  += customer.getPermanence();
                    wcaN += customer.getWaitTime();
                } else {
                    wbN  += customer.getPermanence();
                    wcbN += customer.getWaitTime();
                }
                    if (!event.getCustomer().isInterrupted()){
//                        user finished service

                        if (customer.getType() == B) hb++;
                        else ha++;
                        h++;
                    } else {
//                        user was interrupted
//                        check if abandonment or notEnter by testing permanence time
                        if (customer.getPermanence() > 0 ) abandonment++;
                        else notEnter++;

                    }
            }


        }


        final double totalTime = events.get(events.size()-1).getInitTime();
        final int customersA = results.getAcustomers();
        final int customersB = results.getBcustomers();
        final int totalCustomers = customersA + customersB;

        results.setLc(lcT / totalTime);
        results.setLcA(lcaT / totalTime);
        results.setLcB(lcbT / totalTime);
        results.setL(lT / totalTime);
        results.setLa(laT / totalTime);
        results.setLb(lbT / totalTime);
        results.setW(totalCustomers!=0?wN / totalCustomers:0);
        results.setWa(customersA!=0?waN / customersA:0);
        results.setWb(customersB!=0?wbN / customersB:0);
        results.setWc(totalCustomers!=0?wcN / totalCustomers:0);
        results.setWcA(customersA!=0?wcaN / customersA:0);
        results.setWcB(customersB!=0?wcbN / customersB:0);
        results.setH(totalCustomers!=0?h/totalCustomers:0);
        results.setHa(customersA!=0?ha/ customersA:0);
        results.setHb(customersB!=0?hb/ customersB:0);
        results.setPorcentajeBAbandono(customersB!=0?(float)abandonment / customersB:0);
        results.setPorcentajeBNoIngresa(customersB!=0?(float)notEnter / customersB:0);
        results.setPorcentajeBEnjaulado(customersB!=0?(float)cagedTotal/customersB:0);



    }



}
