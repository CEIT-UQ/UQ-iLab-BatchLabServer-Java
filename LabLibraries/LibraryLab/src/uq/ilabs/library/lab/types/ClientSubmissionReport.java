package uq.ilabs.library.lab.types;

/**
 *
 * @author uqlpayne
 */
public class ClientSubmissionReport {

    /**
     * A copy of the ValidationReport returned by the LabServer
     */
    private ValidationReport validationReport;
    /**
     * A number greater than 0 that identifies the experiment.
     */
    private int experimentId;
    /**
     * Guaranteed minimum time (in seconds, starting now) before this experimentID and associated data will be purged
     * from the LabServer.
     */
    private double minTimeToLive;
    /**
     * The estimated time to wait before this experiment will execute.
     */
    private WaitEstimate waitEstimate;

    public int getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(int experimentId) {
        this.experimentId = experimentId;
    }

    public double getMinTimeToLive() {
        return minTimeToLive;
    }

    public void setMinTimeToLive(double minTimeToLive) {
        this.minTimeToLive = minTimeToLive;
    }

    public ValidationReport getValidationReport() {
        return validationReport;
    }

    public void setValidationReport(ValidationReport validationReport) {
        this.validationReport = validationReport;
    }

    public WaitEstimate getWaitEstimate() {
        return waitEstimate;
    }

    public void setWaitEstimate(WaitEstimate waitEstimate) {
        this.waitEstimate = waitEstimate;
    }

    public ClientSubmissionReport() {
        this.validationReport = new ValidationReport();
        this.experimentId = -1;
        this.minTimeToLive = 0.0;
        this.waitEstimate = new WaitEstimate();
    }

    public ClientSubmissionReport(int experimentId) {
        this.validationReport = new ValidationReport();
        this.experimentId = experimentId;
        this.minTimeToLive = 0.0;
        this.waitEstimate = new WaitEstimate();
    }
}
