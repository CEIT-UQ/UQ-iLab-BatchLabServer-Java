/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labclient;

import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.lab.utilities.XmlUtilitiesException;
import uq.ilabs.library.labclient.engine.LabExperimentResult;

/**
 *
 * @author uqlpayne
 */
public class ExperimentResult extends LabExperimentResult {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ExperimentResult.class.getName();
    private static final Level logLevel = Level.FINER;
    /*
     * String constants
     */
    private static final String STR_SomeParameter = "SomeParameter";
    private static final String STR_SomeResult = "SomeResult";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    /*
     * Specification
     */
    private int someParameter;
    /*
     * Results
     */
    private int someResult;

    public int getSomeParameter() {
        return someParameter;
    }

    public void setSomeParameter(int someParameter) {
        this.someParameter = someParameter;
    }

    public int getSomeResult() {
        return someResult;
    }

    public void setSomeResult(int someResult) {
        this.someResult = someResult;
    }
    //</editor-fold>

    public ExperimentResult(String xmlExperimentResult) {
        super(xmlExperimentResult);

        final String methodName = "ExperimentResult";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Get specification information
             */
            this.someParameter = XmlUtilities.GetChildValueAsInt(this.nodeExperimentResult, Consts.STRXML_SomeParameter);
            /*
             * Get result information
             */
            this.someResult = XmlUtilities.GetChildValueAsInt(this.nodeExperimentResult, Consts.STRXML_SomeResult);
        } catch (XmlUtilitiesException ex) {
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @throws IOException
     */
    @Override
    public void CreateHtmlResultInfo() throws IOException {
        super.CreateHtmlResultInfo();

        /*
         * Experiment setup
         */
        StringWriter sw = new StringWriter();
        sw.write(String.format(STRTBL_Row_arg2, STR_SomeParameter, this.someParameter));
        this.tblSpecification += sw.toString();

        /*
         * Experiment results
         */
        sw = new StringWriter();
        sw.write(String.format(STRTBL_Row_arg2, STR_SomeResult, this.someResult));
        this.tblResults += sw.toString();
    }

    /**
     *
     * @throws IOException
     */
    @Override
    public void CreateCsvResultInfo() throws IOException {
        super.CreateCsvResultInfo();

        /*
         * Experiment setup
         */
        StringWriter sw = new StringWriter();
        sw.write(String.format(STRCSV_Format_arg2, STR_SomeParameter, this.someParameter));
        this.csvSpecification += sw.toString();

        /*
         * Experiment results
         */
        sw = new StringWriter();
        sw.write(String.format(STRCSV_Format_arg2, STR_SomeResult, this.someResult));
        this.csvResults += sw.toString();
    }
}
