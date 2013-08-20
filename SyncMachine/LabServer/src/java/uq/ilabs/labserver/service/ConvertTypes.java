/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labserver.service;

import java.util.Arrays;

/**
 *
 * @author uqlpayne
 */
public class ConvertTypes {

    /**
     *
     * @param strings
     * @return ArrayOfString
     */
    public static edu.mit.ilab.ArrayOfString Convert(String[] strings) {
        edu.mit.ilab.ArrayOfString arrayOfString = null;

        if (strings != null) {
            arrayOfString = new edu.mit.ilab.ArrayOfString();
            arrayOfString.getString().addAll(Arrays.asList(strings));
        }

        return arrayOfString;
    }

    /**
     *
     * @param experimentStatus
     * @return edu.mit.ilab.ExperimentStatus
     */
    public static edu.mit.ilab.ExperimentStatus Convert(uq.ilabs.library.lab.types.ExperimentStatus experimentStatus) {
        edu.mit.ilab.ExperimentStatus proxyExperimentStatus = null;

        if (experimentStatus != null) {
            proxyExperimentStatus = new edu.mit.ilab.ExperimentStatus();
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
     * @return edu.mit.ilab.LabExperimentStatus
     */
    public static edu.mit.ilab.LabExperimentStatus Convert(uq.ilabs.library.lab.types.LabExperimentStatus labExperimentStatus) {
        edu.mit.ilab.LabExperimentStatus proxyLabExperimentStatus = null;

        if (labExperimentStatus != null) {
            proxyLabExperimentStatus = new edu.mit.ilab.LabExperimentStatus();
            proxyLabExperimentStatus.setMinTimetoLive(labExperimentStatus.getMinTimetoLive());
            proxyLabExperimentStatus.setStatusReport(Convert(labExperimentStatus.getExperimentStatus()));
        }

        return proxyLabExperimentStatus;
    }

    /**
     *
     * @param labStatus
     * @return edu.mit.ilab.LabStatus
     */
    public static edu.mit.ilab.LabStatus Convert(uq.ilabs.library.lab.types.LabStatus labStatus) {
        edu.mit.ilab.LabStatus proxyLabStatus = null;

        if (labStatus != null) {
            proxyLabStatus = new edu.mit.ilab.LabStatus();
            proxyLabStatus.setLabStatusMessage(labStatus.getLabStatusMessage());
            proxyLabStatus.setOnline(labStatus.isOnline());
        }

        return proxyLabStatus;
    }

    /**
     *
     * @param resultReport
     * @return edu.mit.ilab.ResultReport
     */
    public static edu.mit.ilab.ResultReport Convert(uq.ilabs.library.lab.types.ResultReport resultReport) {
        edu.mit.ilab.ResultReport proxyResultReport = null;

        if (resultReport != null) {
            proxyResultReport = new edu.mit.ilab.ResultReport();
            proxyResultReport.setErrorMessage(resultReport.getErrorMessage());
            proxyResultReport.setExperimentResults(resultReport.getXmlExperimentResults());
            proxyResultReport.setStatusCode(resultReport.getStatusCode().getValue());
            proxyResultReport.setXmlBlobExtension(resultReport.getXmlBlobExtension());
            proxyResultReport.setXmlResultExtension(resultReport.getXmlResultExtension());
            proxyResultReport.setWarningMessages(Convert(resultReport.getWarningMessages()));
        }

        return proxyResultReport;
    }

    /**
     *
     * @param submissionReport
     * @return edu.mit.ilab.SubmissionReport
     */
    public static edu.mit.ilab.SubmissionReport Convert(uq.ilabs.library.lab.types.SubmissionReport submissionReport) {
        edu.mit.ilab.SubmissionReport proxySubmissionReport = null;

        if (submissionReport != null) {
            proxySubmissionReport = new edu.mit.ilab.SubmissionReport();
            proxySubmissionReport.setExperimentID(submissionReport.getExperimentId());
            proxySubmissionReport.setMinTimeToLive(submissionReport.getMinTimeToLive());
            proxySubmissionReport.setVReport(Convert(submissionReport.getValidationReport()));
            proxySubmissionReport.setWait(Convert(submissionReport.getWaitEstimate()));
        }

        return proxySubmissionReport;
    }

    /**
     *
     * @param validationReport
     * @return edu.mit.ilab.ValidationReport
     */
    public static edu.mit.ilab.ValidationReport Convert(uq.ilabs.library.lab.types.ValidationReport validationReport) {
        edu.mit.ilab.ValidationReport proxyValidationReport = null;

        if (validationReport != null) {
            proxyValidationReport = new edu.mit.ilab.ValidationReport();
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
     * @return edu.mit.ilab.WaitEstimate
     */
    public static edu.mit.ilab.WaitEstimate Convert(uq.ilabs.library.lab.types.WaitEstimate waitEstimate) {
        edu.mit.ilab.WaitEstimate proxyWaitEstimate = null;

        if (waitEstimate != null) {
            proxyWaitEstimate = new edu.mit.ilab.WaitEstimate();
            proxyWaitEstimate.setEffectiveQueueLength(waitEstimate.getEffectiveQueueLength());
            proxyWaitEstimate.setEstWait(waitEstimate.getEstWait());
        }

        return proxyWaitEstimate;
    }
}
