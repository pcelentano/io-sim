package model.simulation.mathematics;

import model.simulation.Customer;
import model.simulation.Event;
import model.simulation.NumericResults;

import java.util.List;

import static model.simulation.Event.EventType.DEPARTURE;
import static model.simulation.Event.Status.OCCUPIED;

/**
 * Created by pablo on 5/14/15.
 */
public class ResultCalculator {


    public void calculate(final List<Event> events, final NumericResults results) {

        double lcT = 0;
//        double lcaT = 0;
//        double lcbT = 0;
        double lT = 0;
        double laT = 0;
        double lbT = 0;
        double wN  = 0;
        double wcN = 0;
        double h   = 0;
        double ha   = 0;
        double hb   = 0;
        int abandonment = 0;
        int notEnter = 0;


        for (Event event : events) {
            final Customer customer = event.getCustomer();
            lcT += event.getQueueLength() * event.getDeltaTime();
            lT  += (event.getQueueLength() + (event.getAttentionChanelStatus() == OCCUPIED ? 1 : 0)) * event.getDeltaTime();


            if (customer != null && event.getType() == DEPARTURE){
                    wN  += customer.getPermanence();
                    wcN  += customer.getWaitTime();
                    if (!event.getCustomer().isInterrupted()){
//                        user finished service

                        if (customer.getType() == Customer.CustomerType.B) hb++;
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
        final int totalCustomers = results.getAcustomers() + results.getBcustomers();

        results.setLc(lcT / totalTime);
        results.setL(lT / totalTime);
        results.setW(wN / totalCustomers);
        results.setWc(wcN / totalCustomers);
        results.setH(h/totalCustomers);
        results.setHa(ha/results.getAcustomers());
        results.setHb(hb/results.getBcustomers());
        results.setPorcentajeBAbandono((float)abandonment / results.getBcustomers());
        results.setPorcentajeBNoIngresa((float)notEnter / results.getBcustomers());



    }



}
