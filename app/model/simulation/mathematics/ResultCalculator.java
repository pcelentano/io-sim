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
//        double laT = 0;
//        double lbT = 0;
        double wN  = 0;
        double wcN = 0;
        double H   = 0;


        for (Event event : events) {
            final Customer customer = event.getCustomer();
            lcT += event.getQueueLength() * event.getDeltaTime();
            lT  += (event.getQueueLength() + (event.getAttentionChanelStatus() == OCCUPIED ? 1 : 0)) * event.getDeltaTime();


            if (customer != null && event.getType() == DEPARTURE){
                    wN  += customer.getPermanence();
                    wcN  += customer.getWaitTime();
            }


        }


        final double totalTime = events.get(events.size()-1).getInitTime();
        final int totalCustomers = results.getAcustomers() + results.getBcustomers();

        results.setLc(lcT / totalTime);
        results.setL(lT / totalTime);
        results.setW(wN / totalCustomers);
        results.setWc(wcN / totalCustomers);


    }



}
