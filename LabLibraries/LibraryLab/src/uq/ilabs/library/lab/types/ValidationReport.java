package uq.ilabs.library.lab.types;

/**
 *
 * @author uqlpayne
 */
public class ValidationReport {

    /**
     * True if the experiment specification is acceptable for execution.
     */
    private boolean accepted;
    /**
     * Domain-dependent human-readable text containing non-fatal warnings about the experiment.
     */
    private String[] warningMessages;
    /**
     * Domain-dependent human-readable text describing why the experiment specification would not be accepted (if
     * accepted == false).
     */
    private String errorMessage;
    /**
     * Estimated runtime (in seconds) of this experiment. [OPTIONAL, &lt; 0 if not supported].
     */
    private double estRuntime;

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public double getEstRuntime() {
        return estRuntime;
    }

    public void setEstRuntime(double estRuntime) {
        this.estRuntime = estRuntime;
    }

    public String[] getWarningMessages() {
        return warningMessages;
    }

    public void setWarningMessages(String[] warningMessages) {
        this.warningMessages = warningMessages;
    }

    public ValidationReport() {
        this.accepted = false;
    }

    public ValidationReport(boolean accepted, double estRuntime) {
        this.accepted = accepted;
        this.estRuntime = estRuntime;
    }

    public ValidationReport(String errorMessage) {
        this.accepted = false;
        this.estRuntime = -1.0;
        this.errorMessage = errorMessage;
    }
}
