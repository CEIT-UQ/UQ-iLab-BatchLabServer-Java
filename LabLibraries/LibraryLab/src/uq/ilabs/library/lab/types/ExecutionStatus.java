/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.lab.types;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author uqlpayne
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExecutionStatus", propOrder = {
    "executionId",
    "executeStatus",
    "resultStatus",
    "timeRemaining",
    "errorMessage"
})
public class ExecutionStatus {

    public enum Status {

        None(0),
        Created(1),
        Initialising(2),
        Starting(3),
        Running(4),
        Stopping(5),
        Finalising(6),
        Done(7),
        Completed(8),
        Failed(9),
        Cancelled(10);
        //
        //<editor-fold defaultstate="collapsed" desc="Properties">
        private final int value;

        public int getValue() {
            return value;
        }
        //</editor-fold>

        private Status(int value) {
            this.value = value;
        }

        public static Status ToStatus(int value) {
            switch (value) {
                case 1:
                    return Created;
                case 2:
                    return Initialising;
                case 3:
                    return Starting;
                case 4:
                    return Running;
                case 5:
                    return Stopping;
                case 6:
                    return Finalising;
                case 7:
                    return Done;
                case 8:
                    return Completed;
                case 9:
                    return Failed;
                case 10:
                    return Cancelled;
                default:
                    return None;
            }
        }
    }
    /**
     * The identification of the currently executing driver
     */
    @XmlElement(name = "ExecutionId")
    protected int executionId;
    /**
     * Status of the currently executing driver
     */
    @XmlElement(name = "ExecuteStatus")
    protected int executeStatus;
    /**
     * Result status of the most recent driver execution
     */
    @XmlElement(name = "ResultStatus")
    protected int resultStatus;
    /**
     * Time remaining (in seconds) for the currently executing driver
     */
    @XmlElement(name = "TimeRemaining")
    protected int timeRemaining;
    /**
     * Information about driver execution that did not complete successfully
     */
    @XmlElement(name = "ErrorMessage")
    protected String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getExecutionId() {
        return executionId;
    }

    public void setExecutionId(int executionId) {
        this.executionId = executionId;
    }

    public Status getExecuteStatus() {
        return Status.ToStatus(executeStatus);
    }

    public void setExecuteStatus(Status executeStatus) {
        this.executeStatus = executeStatus.getValue();
    }

    public Status getResultStatus() {
        return Status.ToStatus(resultStatus);
    }

    public void setResultStatus(Status resultStatus) {
        this.resultStatus = resultStatus.getValue();
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(int timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    /**
     * Default constructor
     */
    public ExecutionStatus() {
        this.executeStatus = Status.None.getValue();
        this.resultStatus = Status.None.getValue();
        this.timeRemaining = -1;
        this.errorMessage = null;
    }

    /**
     *
     * @return String
     */
    public String ToXmlString() {
        String xmlString = null;

        try {
            Marshaller marshaller = JAXBContext.newInstance(this.getClass()).createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            JAXBElement<ExecutionStatus> jaxbElement = (new ObjectFactory()).createExecutionStatus(this);
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(jaxbElement, stringWriter);
            xmlString = stringWriter.toString();
        } catch (JAXBException ex) {
            System.out.println(ex.toString());
        }

        return xmlString;
    }

    /**
     *
     * @param xmlString
     * @return LabExperimentStatus
     */
    public static ExecutionStatus XmlParse(String xmlString) {
        ExecutionStatus executionStatus = null;

        try {
            Unmarshaller unmarshaller = JAXBContext.newInstance(ExecutionStatus.class).createUnmarshaller();
            StreamSource streamSource = new StreamSource(new StringReader(xmlString));
            JAXBElement<ExecutionStatus> jaxbElement = (JAXBElement<ExecutionStatus>) unmarshaller.unmarshal(streamSource, ExecutionStatus.class);
            executionStatus = jaxbElement.getValue();
        } catch (JAXBException ex) {
            System.out.println(ex.toString());
        }

        return executionStatus;
    }
}
