package uq.ilabs.library.lab.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author uqlpayne
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExperimentStatus", propOrder = {
    "statusCode",
    "waitEstimate",
    "estRuntime",
    "estRemainingRuntime"
})
public class ExperimentStatus {

    @XmlElement(name = "statusCode")
    protected int statusCode;
    @XmlElement(name = "wait")
    protected WaitEstimate waitEstimate;
    @XmlElement(name = "estRuntime")
    protected double estRuntime;
    @XmlElement(name = "estRemainingRuntime")
    protected double estRemainingRuntime;

    public StatusCodes getStatusCode() {
        return StatusCodes.ToStatusCode(statusCode);
    }

    public void setStatusCode(StatusCodes statusCode) {
        this.statusCode = statusCode.getValue();
    }

    public WaitEstimate getWaitEstimate() {
        return waitEstimate;
    }

    public void setWaitEstimate(WaitEstimate waitEstimate) {
        this.waitEstimate = waitEstimate;
    }

    public double getEstRuntime() {
        return estRuntime;
    }

    public void setEstRuntime(double estRuntime) {
        this.estRuntime = estRuntime;
    }

    public double getEstRemainingRuntime() {
        return estRemainingRuntime;
    }

    public void setEstRemainingRuntime(double estRemainingRuntime) {
        this.estRemainingRuntime = estRemainingRuntime;
    }

    public ExperimentStatus() {
        this(StatusCodes.Unknown);
    }

    public ExperimentStatus(StatusCodes statusCode) {
        this.statusCode = statusCode.getValue();
        this.waitEstimate = new WaitEstimate();
        this.estRuntime = 0.0;
        this.estRemainingRuntime = 0.0;
    }
}
