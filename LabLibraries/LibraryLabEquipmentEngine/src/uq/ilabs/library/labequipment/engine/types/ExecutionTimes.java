/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment.engine.types;

/**
 *
 * @author uqlpayne
 */
public class ExecutionTimes {

    private int initialise;
    private int start;
    private int run;
    private int stop;
    private int finalise;

    public int getInitialise() {
        return initialise;
    }

    public void setInitialise(int initialise) {
        this.initialise = initialise;
    }

    public int getFinalise() {
        return finalise;
    }

    public void setFinalise(int finalise) {
        this.finalise = finalise;
    }

    public int getRun() {
        return run;
    }

    public void setRun(int run) {
        this.run = run;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStop() {
        return stop;
    }

    public void setStop(int stop) {
        this.stop = stop;
    }
    
    public int getTotalExecutionTime() {
        return (initialise + start + run + stop + finalise);
    }
}
