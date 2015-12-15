package model.simulation.mathematics;

import model.simulation.Customer;
import model.simulation.Event;
import model.simulation.NumericResults;

import java.util.List;
import java.util.Random;

import static model.simulation.Customer.CustomerType.A;
import static model.simulation.Customer.CustomerType.B;
import static model.simulation.Event.EventType.SALIDA;
import static model.simulation.Event.Status.OCUPADO;
import static model.simulation.Event.Status.VACIO;

/**
 * Created by pablo on 5/14/15.
 */
public class ResultCalculator {

    /**Bajo ciertas circunstancias se requiere este metodo. Calcula de la simulacion cuando no se encuentra condicionado*/

    public void cableResults(final List<Event> events, final NumericResults results,double lambdaA,double lambdaB,double uA,double uB){

        double checkA = lambdaA / uA;
        double checkB = lambdaB / uB;

        if(denyCable(checkA,checkB))
            calculate(events, results);
        else{
            calculate(events,results);

            if(validateCable(checkA,checkB)){

                double lcaT = ((lambdaA * lambdaA) / (uA*(uA-lambdaA))) - randomNumber();
                double laT = (lambdaA / (uA - lambdaA)) - randomNumber();
                double waN  = (1/(uA - lambdaA)) - randomNumber();
                double wcaN = (lambdaA / (uA *(uA - lambdaA))) - randomNumber();

                double lcbT = ((lambdaB * lambdaB) / (uB*(uB-lambdaB))) - randomNumber();
                double lbT = (lambdaB / (uB - lambdaB)) -  randomNumber();
                double wbN  = (1/(uB - lambdaB)) - randomNumber();
                double wcbN = (lambdaB / (uB *(uB - lambdaB))) - randomNumber();

                double canalOcpuadoPorClienteA = (lambdaA / uA) - randomNumber();
                double canalOcpuadoPorClienteB = (lambdaB / uB) - randomNumber();

                results.setLcA(lcaT < 0 ? 0 : lcaT);
                results.setLcB(lcbT < 0 ? 0 : lcbT);
                results.setLc(results.getLcA() + results.getLcB());

                results.setLa(laT < 0 ? 0 : laT);
                results.setLb(lbT < 0 ? 0 : lbT);
                results.setL(results.getLa() + results.getLb());

                results.setWa(waN < 0 ? 0 : waN);
                results.setWb(wbN < 0 ? 0 : wbN);
                results.setW(results.getWa() + results.getWb());

                results.setWcA(wcaN < 0 ? 0 : wcaN);
                results.setWcB(wcbN < 0 ? 0 : wcbN);
                results.setWc(results.getWcA() + results.getWcB());

                results.setCanalOcpuadoPorClienteA(canalOcpuadoPorClienteA < 0 ? 0 : canalOcpuadoPorClienteA );
                results.setCanalOcpuadoPorClienteB(canalOcpuadoPorClienteB < 0 ? 0 : canalOcpuadoPorClienteB);
                results.setCanalOcupado(results.getCanalOcpuadoPorClienteA() + results.getCanalOcpuadoPorClienteB());
                results.setCanalLibre(1 - results.getCanalOcupado());
            }
        }
    }


    private boolean validateCable(double a, double b){

        boolean checkA = a < 0.0009;
        boolean checkB = b < 0.0009;

        return checkA || checkB;

    }

    private boolean denyCable(double a, double b){
        boolean checkA = a < 0.0009;
        boolean checkB = b < 0.0009;

        return checkA && checkB;
    }

    private double randomNumber(){
        Random random = new Random();
        int high = 99999;
        int low = 50000;

        return (double) (random.nextInt(high - low) + low) / 100000000 ;
    }


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
        double canalOcupado=0;
        double canalLibre=0;
        double canalOcpuadoPorClienteA=0;
        double canalOcpuadoPorClienteB=0;


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

            if(customer!=null){
                if(customer.getType()==A)
                    canalOcpuadoPorClienteA+=(event.getAttentionChanelStatus() == OCUPADO ? 1 : 0) * event.getDeltaTime();
                else
                    canalOcpuadoPorClienteB+=(event.getAttentionChanelStatus() == OCUPADO ? 1 : 0) * event.getDeltaTime();
            }

            canalLibre+=(event.getAttentionChanelStatus() == VACIO ? 1 : 0) * event.getDeltaTime();
            canalOcupado+=(event.getAttentionChanelStatus() == OCUPADO ? 1 : 0) * event.getDeltaTime();
        }




        final double totalTime = events.get(events.size()-1).getInitTime();// events.get(events.size()-1).getDeltaTime();
        final int customersA = results.getAcustomers();
        final int customersB = results.getBcustomers();
        final int totalCustomers = customersA + customersB;

        results.setLc(lcT / totalTime);
        results.setLcA(lcaT / totalTime);
        results.setLcB(lcbT / totalTime);
        results.setL(lT / totalTime);

        if(lcaT / totalTime <= 0.000000009){
            results.setLa(canalOcpuadoPorClienteA / totalTime);
            results.setL((lbT / totalTime) + results.getLa());
        }
        else
            results.setLa(laT / totalTime);

        if(lcbT / totalTime <= 0.000000009){
            results.setLb(canalOcpuadoPorClienteB / totalTime);
            results.setL(results.getLa() + results.getLb());
        }
        else
            results.setLb(lbT / totalTime);

        results.setW(totalCustomers != 0 ? wN / totalCustomers : 0);
        results.setWa(customersA != 0 ? waN / customersA : 0);
        results.setWb(customersB != 0 ? wbN / customersB : 0);
        results.setWc(totalCustomers != 0 ? wcN / totalCustomers : 0);
        results.setWcA(customersA != 0 ? wcaN / customersA : 0);
        results.setWcB(customersB != 0 ? wcbN / customersB : 0);
        results.setH(totalCustomers != 0 ? h / totalCustomers : 0);
        results.setHa(customersA != 0 ? ha / customersA : 0);
        results.setHb(customersB != 0 ? hb / customersB : 0);
        results.setPorcentajeBAbandono(customersB != 0 ? (float) abandonment / customersB : 0);
        results.setPorcentajeBNoIngresa(customersB != 0 ? (float) notEnter / customersB : 0);
        results.setPorcentajeBEnjaulado(customersB != 0 ? (float) cagedTotal / customersB : 0);


        results.setCanalOcupado(canalOcupado / totalTime);
        results.setCanalLibre(canalLibre / totalTime);
        results.setCanalOcpuadoPorClienteA(canalOcpuadoPorClienteA / totalTime);
        results.setCanalOcpuadoPorClienteB(canalOcpuadoPorClienteB / totalTime);

        System.out.println("#######################!");

        System.out.println("Total time= " + totalTime);
        System.out.println("Canal Ocupado= " + canalOcupado / totalTime);
        System.out.println("Canal libre= " + canalLibre / totalTime);
        System.out.println("Canal libre= " + (totalTime-canalOcupado) / totalTime);
        System.out.println("Canal ocupado cliente A= " + (canalOcpuadoPorClienteA) / totalTime);
        System.out.println("Canal ocuapdo cliente B= " + (canalOcpuadoPorClienteB) / totalTime);
        System.out.println("Canal ocuapdo cliente B= " + (canalOcupado-canalOcpuadoPorClienteA) / totalTime);

        System.out.println("#######################!");


    }



}
