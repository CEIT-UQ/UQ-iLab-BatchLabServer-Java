package uq.ilabs.library.lab.types;

/**
 *
 * @author uqlpayne
 */
public class WaitEstimate {

    /**
     * Number of experiments currently in the execution queue that would run before the
     * hypothetical new experiment.
     */
    private int effectiveQueueLength;
    /**
     * Estimated wait time (in seconds) until the hypothetical new experiment would begin,
     * based on the other experiments currently in the execution queue.
     * [OPTIONAL, less than 0 if not supported].
     */
    private double estWait;

    public int getEffectiveQueueLength() {
        return effectiveQueueLength;
    }

    public void setEffectiveQueueLength(int effectiveQueueLength) {
        this.effectiveQueueLength = effectiveQueueLength;
    }

    public double getEstWait() {
        return estWait;
    }

    public void setEstWait(double estWait) {
        this.estWait = estWait;
    }

    public WaitEstimate() {
        this.effectiveQueueLength = 0;
        this.estWait = 0;
    }
}
