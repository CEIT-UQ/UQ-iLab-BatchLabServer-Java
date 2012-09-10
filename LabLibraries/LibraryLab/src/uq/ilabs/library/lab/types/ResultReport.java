package uq.ilabs.library.lab.types;

/**
 *
 * @author uqlpayne
 */
public class ResultReport {

    /**
     * Indicates the status of this experiment.
     */
    private StatusCodes statusCode;
    /**
     * An opaque, domain-dependent set of experiment results. [REQUIRED if experimentStatus == Completed (3), OPTIONAL
     * if experimentStatus == Failed (4)].
     */
    private String xmlExperimentResults;
    /**
     * A transparent XML string that helps to identify this experiment. Used for indexing and querying in generic
     * components which can't understand the opaque experimentSpecification and experimentResults. [OPTIONAL, null if
     * unused].
     */
    private String xmlResultExtension;
    /**
     * A transparent XML string that helps to identify any blobs saved as part of the results of this experiment.
     * [OPTIONAL, null if unused].
     */
    private String xmlBlobExtension;
    /**
     * Domain-dependent human-readable text containing non-fatal warnings about the experiment including runtime
     * warnings.
     */
    private String[] warningMessages;
    /**
     * Domain-dependent human-readable text describing why the experiment terminated abnormally including runtime
     * errors. [REQUIRED if experimentStatus == Failed (4)].
     */
    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public StatusCodes getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCodes statusCode) {
        this.statusCode = statusCode;
    }

    public String[] getWarningMessages() {
        return warningMessages;
    }

    public void setWarningMessages(String[] warningMessages) {
        this.warningMessages = warningMessages;
    }

    public String getXmlBlobExtension() {
        return xmlBlobExtension;
    }

    public void setXmlBlobExtension(String xmlBlobExtension) {
        this.xmlBlobExtension = xmlBlobExtension;
    }

    public String getXmlExperimentResults() {
        return xmlExperimentResults;
    }

    public void setXmlExperimentResults(String xmlExperimentResults) {
        this.xmlExperimentResults = xmlExperimentResults;
    }

    public String getXmlResultExtension() {
        return xmlResultExtension;
    }

    public void setXmlResultExtension(String xmlResultExtension) {
        this.xmlResultExtension = xmlResultExtension;
    }

    public ResultReport() {
    }

    public ResultReport(StatusCodes statusCode) {
        this.statusCode = statusCode;
    }

    public ResultReport(StatusCodes statusCode, String errorMessage) {
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }
}
