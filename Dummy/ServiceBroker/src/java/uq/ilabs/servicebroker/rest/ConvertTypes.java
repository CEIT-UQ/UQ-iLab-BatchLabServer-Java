/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.servicebroker.rest;

import java.util.Arrays;
import uq.ilabs.library.lab.types.ExperimentStatus;
import uq.ilabs.library.lab.types.LabExperimentStatus;
import uq.ilabs.library.lab.types.LabStatus;
import uq.ilabs.library.lab.types.ResultReport;
import uq.ilabs.library.lab.types.SubmissionReport;
import uq.ilabs.library.lab.types.ValidationReport;
import uq.ilabs.library.lab.types.WaitEstimate;

/**
 *
 * @author uqlpayne
 */
public class ConvertTypes {

    /**
     *
     * @param strings
     * @return edu.mit.ilab.rest.ArrayOfString
     */
    public static edu.mit.ilab.rest.ArrayOfString Convert(String[] strings) {
        edu.mit.ilab.rest.ArrayOfString arrayOfString = null;

        if (strings != null) {
            arrayOfString = new edu.mit.ilab.rest.ArrayOfString();
            arrayOfString.getStringList().addAll(Arrays.asList(strings));
        }

        return arrayOfString;
    }

    /**
     *
     * @param experimentStatus
     * @return edu.mit.ilab.rest.ExperimentStatus
     */
    public static edu.mit.ilab.rest.ExperimentStatus Convert(ExperimentStatus experimentStatus) {
        edu.mit.ilab.rest.ExperimentStatus proxyExperimentStatus = null;

        if (experimentStatus != null) {
            proxyExperimentStatus = new edu.mit.ilab.rest.ExperimentStatus();
            proxyExperimentStatus.setEstRemainingRuntime(experimentStatus.getEstRemainingRuntime());
            proxyExperimentStatus.setEstRuntime(experimentStatus.getEstRuntime());
            proxyExperimentStatus.setStatusCode(experimentStatus.getStatusCode().getValue());
            proxyExperimentStatus.setWait(Convert(experimentStatus.getWaitEstimate()));
        }

        return proxyExperimentStatus;
    }

    /**
     *
     * @param labExperimentStatus
     * @return edu.mit.ilab.rest.LabExperimentStatus
     */
    public static edu.mit.ilab.rest.LabExperimentStatus Convert(LabExperimentStatus labExperimentStatus) {
        edu.mit.ilab.rest.LabExperimentStatus proxyLabExperimentStatus = null;

        if (labExperimentStatus != null) {
            proxyLabExperimentStatus = new edu.mit.ilab.rest.LabExperimentStatus();
            proxyLabExperimentStatus.setMinTimetoLive(labExperimentStatus.getMinTimetoLive());
            proxyLabExperimentStatus.setStatusReport(Convert(labExperimentStatus.getExperimentStatus()));
        }

        return proxyLabExperimentStatus;
    }

    /**
     *
     * @param labStatus
     * @return edu.mit.ilab.rest.LabStatus
     */
    public static edu.mit.ilab.rest.LabStatus Convert(LabStatus labStatus) {
        edu.mit.ilab.rest.LabStatus proxyLabStatus = null;

        if (labStatus != null) {
            proxyLabStatus = new edu.mit.ilab.rest.LabStatus();
            proxyLabStatus.setOnline(labStatus.isOnline());
            proxyLabStatus.setLabStatusMessage(labStatus.getLabStatusMessage());
        }

        return proxyLabStatus;
    }

    /**
     *
     * @param resultReport
     * @return edu.mit.ilab.rest.ResultReport
     */
    public static edu.mit.ilab.rest.ResultReport Convert(ResultReport resultReport) {
        edu.mit.ilab.rest.ResultReport proxyResultReport = null;

        if (resultReport != null) {
            proxyResultReport = new edu.mit.ilab.rest.ResultReport();
            proxyResultReport.setErrorMessage(resultReport.getErrorMessage());
            proxyResultReport.setExperimentResults(resultReport.getXmlExperimentResults());
            proxyResultReport.setStatusCode(resultReport.getStatusCode().getValue());
            proxyResultReport.setWarningMessages(Convert(resultReport.getWarningMessages()));
            proxyResultReport.setXmlBlobExtension(resultReport.getXmlBlobExtension());
            proxyResultReport.setXmlResultExtension(resultReport.getXmlResultExtension());
        }

        return proxyResultReport;
    }

    /**
     *
     * @param submissionReport
     * @return edu.mit.ilab.rest.ClientSubmissionReport
     */
    public static edu.mit.ilab.rest.ClientSubmissionReport Convert(SubmissionReport submissionReport) {
        edu.mit.ilab.rest.ClientSubmissionReport proxyClientSubmissionReport = null;

        if (submissionReport != null) {
            /*
             * Convert to the return type
             */
            proxyClientSubmissionReport = new edu.mit.ilab.rest.ClientSubmissionReport();
            proxyClientSubmissionReport.setExperimentID((int) submissionReport.getExperimentId());
            proxyClientSubmissionReport.setMinTimeToLive(submissionReport.getMinTimeToLive());
            proxyClientSubmissionReport.setVReport(Convert(submissionReport.getValidationReport()));
            proxyClientSubmissionReport.setWait(Convert(submissionReport.getWaitEstimate()));
        }

        return proxyClientSubmissionReport;
    }

    /**
     *
     * @param validationReport
     * @return edu.mit.ilab.rest.ValidationReport
     */
    public static edu.mit.ilab.rest.ValidationReport Convert(ValidationReport validationReport) {
        edu.mit.ilab.rest.ValidationReport proxyValidationReport = null;

        if (validationReport != null) {
            proxyValidationReport = new edu.mit.ilab.rest.ValidationReport();
            proxyValidationReport.setAccepted(validationReport.isAccepted());
            proxyValidationReport.setErrorMessage(validationReport.getErrorMessage());
            proxyValidationReport.setEstRuntime(validationReport.getEstRuntime());
            proxyValidationReport.setWarningMessages(Convert(validationReport.getWarningMessages()));
        }

        return proxyValidationReport;
    }

    /**
     *
     * @param waitEstimate
     * @return edu.mit.ilab.rest.WaitEstimate
     */
    public static edu.mit.ilab.rest.WaitEstimate Convert(WaitEstimate waitEstimate) {
        edu.mit.ilab.rest.WaitEstimate proxyWaitEstimate = null;

        if (waitEstimate != null) {
            proxyWaitEstimate = new edu.mit.ilab.rest.WaitEstimate();
            proxyWaitEstimate.setEffectiveQueueLength(waitEstimate.getEffectiveQueueLength());
            proxyWaitEstimate.setEstWait(waitEstimate.getEstWait());
        }

        return proxyWaitEstimate;
    }
}
