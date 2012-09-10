package uq.ilabs.library.lab.types;

/**
 *
 * @author uqlpayne
 */
public class LabExperimentStatus {

    private ExperimentStatus experimentStatus;
    /**
     * Guaranteed minimum remaining time (in seconds) before this experimentID and
     * associated data will be purged from the LabServer.
     */
    private double minTimetoLive;

    public double getMinTimetoLive() {
        return minTimetoLive;
    }

    public void setMinTimetoLive(double minTimetoLive) {
        this.minTimetoLive = minTimetoLive;
    }

    public ExperimentStatus getExperimentStatus() {
        return experimentStatus;
    }

    public void setExperimentStatus(ExperimentStatus experimentStatus) {
        this.experimentStatus = experimentStatus;
    }

    public LabExperimentStatus() {
        this.experimentStatus = new ExperimentStatus();
        this.minTimetoLive = 0.0;
    }

    public LabExperimentStatus(ExperimentStatus experimentStatus) {
        this.experimentStatus = experimentStatus;
        this.minTimetoLive = 0.0;
    }
}
