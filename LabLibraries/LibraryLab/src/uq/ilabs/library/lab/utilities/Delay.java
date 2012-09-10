/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.lab.utilities;

/**
 *
 * @author uqlpayne
 */
public class Delay {

    /**
     * MilliSeconds
     * @param value The number of milliseconds to delay execution.
     */
    public static void MilliSeconds(int value) {
        try {
            Thread.sleep(value);
        } catch (InterruptedException ex) {
        }
    }
}
