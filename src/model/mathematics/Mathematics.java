package model.mathematics;

/**
 * Math Utility for random time intervals.
 */
public class Mathematics {

    private static final double MIN_PROBABILITY = 0.001;

    private Mathematics() { }

    /** Client arrival interval for given lambda. */
    public static double getClientArrivalInterval(final double clientsPerHour) {
        return poissonLikeFunction(clientsPerHour);
    }


    /** Client attention interval for given mu. */
    public static double getDurationChannel(double attentionAverageSpeed) {
        return poissonLikeFunction(attentionAverageSpeed);
    }


    /** Random function seems to tell next ocurrence of an event given a ocurrence rate By : Laura Lopez Bukovac, check her code. */
    private static double poissonLikeFunction(double rate) {
        final double iac = getThreeDecimals((1 / rate) * (Math.log(1 / Math.random())));
        if (iac <= MIN_PROBABILITY) {
            return poissonLikeFunction(rate);
        } else {
            return iac;
        }
    }

    private static double getThreeDecimals(double n) {
        final int nInt = (int) (n * 1000);
        return ((double) nInt) / 1000;
    }
}
