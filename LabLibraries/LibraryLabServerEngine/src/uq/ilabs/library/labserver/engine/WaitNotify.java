/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.engine;

import uq.ilabs.library.lab.utilities.Logfile;

/**
 *
 * @author uqlpayne
 */
public class WaitNotify {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = WaitNotify.class.getName();
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private boolean signal;
    //</editor-fold>

    /**
     * 
     */
    public WaitNotify() {
        final String methodName = "WaitNotify";
        Logfile.WriteCalled(STR_ClassName, methodName);

        /*
         * Initialise local variables
         */
        this.signal = false;

        Logfile.WriteCompleted(STR_ClassName, methodName);
    }

    /**
     * Wait the specified number of milliseconds with 1000 millisecond resolution
     * @param milliseconds 
     */
    public synchronized boolean Wait(int milliseconds) {
        int seconds = (milliseconds + 500) / 1000;
        
        try {
            do {
                wait(1000);
            } while (this.signal == false && --seconds > 0);
        } catch (InterruptedException ex) {
        }

        return this.signal;
    }

    /**
     * 
     */
    public synchronized void Notify() {
        this.signal = true;
        notifyAll();
    }

    /**
     * 
     */
    public synchronized void Reset() {
        this.signal = false;
    }
}
