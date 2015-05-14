package model.simulation.mathematics;

import model.simulation.Customer;
import model.simulation.Event;
import model.simulation.NumericResults;

import java.util.List;

/**
 * Created by pablo on 5/14/15.
 */
public class ResultCalculator {


    public void calculate(final List<Event> events, final NumericResults results) {

        double lcT = 0;
        double wN  = 0;
        double wcN = 0;
        double H   = 0;


        for (Event event : events) {
            final Customer customer = event.getCustomer();
            lcT += event.getQueueLength() * event.getDeltaTime();



            if (customer != null){
                    wN  += customer.getPermanence();
            }


        }


    }



}
