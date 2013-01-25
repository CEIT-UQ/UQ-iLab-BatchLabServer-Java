/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver;

import java.util.logging.Level;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.lab.utilities.XmlUtilitiesException;
import uq.ilabs.library.labserver.engine.LabExperimentSpecification;

/**
 *
 * @author uqlpayne
 */
public class ExperimentSpecification extends LabExperimentSpecification {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ExperimentSpecification.class.getName();
    private static final Level logLevel = Level.FINE;
    /*
     * String constants for exception messages
     */
    private static final String STRERR_SomeParameter = "SomeParameter";
    private static final String STRERR_ValueNotSpecified_arg = "%s: Not specified!";
    private static final String STRERR_ValueNotNumber_arg = "%s: Not a number!";
    private static final String STRERR_ValueNotInteger_arg = "%s: Not an integer!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private int someParameter;

    public int getSomeParameter() {
        return someParameter;
    }
    //</editor-fold>

    /**
     *
     * @param xmlSpecification
     * @throws Exception
     */
    public ExperimentSpecification(String xmlSpecification) throws Exception {
        super(xmlSpecification);

        final String methodName = "ExperimentSpecification";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Get the experiment parameters from the specification
             */
            try {
                this.someParameter = XmlUtilities.GetChildValueAsInt(this.nodeSpecification, Consts.STRXML_SomeParameter);
            } catch (XmlUtilitiesException ex) {
                throw new RuntimeException(String.format(STRERR_ValueNotSpecified_arg, STRERR_SomeParameter));
            } catch (NumberFormatException ex) {
                try {
                    XmlUtilities.GetChildValueAsDouble(this.nodeSpecification, Consts.STRXML_SomeParameter);
                    throw new RuntimeException(String.format(STRERR_ValueNotInteger_arg, STRERR_SomeParameter));
                } catch (NumberFormatException ex1) {
                    throw new RuntimeException(String.format(STRERR_ValueNotNumber_arg, STRERR_SomeParameter));
                }
            }
        } catch (RuntimeException | XmlUtilitiesException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }
}
