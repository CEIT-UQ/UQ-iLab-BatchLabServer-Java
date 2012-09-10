/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.lab.types;

/**
 *
 * @author uqlpayne
 */
public class ExecutionStatus {

    public enum Status {

        None(0), Created(1), Initialising(2), Starting(3), Running(4), Stopping(5), Finalising(6), Done(7),
        Completed(8), Failed(9), Cancelled(10);

        private Status(int value) {
            this.value = value;
        }
        private final int value;

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
    private int executionId;
    /**
     * Status of the currently executing driver
     */
    private Status executeStatus;
    /**
     * Result status of the most recent driver execution
     */
    private Status resultStatus;
    /**
     * Time remaining (in seconds) for the currently executing driver
     */
    private int timeRemaining;
    /**
     * Information about driver execution that did not complete successfully
     */
    private String errorMessage;

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
        return executeStatus;
    }

    public void setExecuteStatus(Status executeStatus) {
        this.executeStatus = executeStatus;
    }

    public Status getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(Status resultStatus) {
        this.resultStatus = resultStatus;
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
        this.executeStatus = Status.None;
        this.resultStatus = Status.None;
        this.timeRemaining = -1;
        this.errorMessage = null;
    }
}
