/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.lab.types;

/**
 *
 * @author uqlpayne
 */
public class Validation {

    private boolean accepted;
    private String errorMessage;
    private int executionTime;

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

    public int getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(int executionTime) {
        this.executionTime = executionTime;
    }

    public Validation() {
    }

    public Validation(boolean accepted, String errorMessage) {
        this.accepted = accepted;
        this.errorMessage = errorMessage;
    }
}
