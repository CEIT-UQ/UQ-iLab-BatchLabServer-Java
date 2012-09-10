package uq.ilabs.library.lab.types;

/**
 *
 * @author uqlpayne
 */
public class ExperimentStatus {

    /**
     * Indicates the status of this experiment.
     */
    private StatusCodes statusCode;
    /**
     *
     */
    private WaitEstimate waitEstimate;
    /**
     * Estimated runtime (in seconds) of this experiment. [OPTIONAL, less than 0 if not used].
     */
    private double estRuntime;
    /**
     * Estimated remaining run time (in seconds) of this experiment, if the experiment is
     * currently running. [OPTIONAL, less than 0 if not used].
     */
    private double estRemainingRuntime;

    public double getEstRemainingRuntime() {
        return estRemainingRuntime;
    }

    public void setEstRemainingRuntime(double estRemainingRuntime) {
        this.estRemainingRuntime = estRemainingRuntime;
    }

    public double getEstRuntime() {
        return estRuntime;
    }

    public void setEstRuntime(double estRuntime) {
        this.estRuntime = estRuntime;
    }

    public StatusCodes getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCodes statusCode) {
        this.statusCode = statusCode;
    }

    public WaitEstimate getWaitEstimate() {
        return waitEstimate;
    }

    public void setWaitEstimate(WaitEstimate waitEstimate) {
        this.waitEstimate = waitEstimate;
    }

    public ExperimentStatus() {
        this(StatusCodes.Unknown);
    }

    public ExperimentStatus(StatusCodes statusCode) {
        this.statusCode = statusCode;
        this.waitEstimate = new WaitEstimate();
        this.estRuntime = 0.0;
        this.estRemainingRuntime = 0.0;
    }
}
