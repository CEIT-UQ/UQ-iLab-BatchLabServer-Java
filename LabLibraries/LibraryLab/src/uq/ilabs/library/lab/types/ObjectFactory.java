/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.lab.types;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

/**
 *
 * @author uqlpayne
 */
public class ObjectFactory {

    private final static String STR_NamespaceURI = "http://ilab.mit.edu";
    private final static QName QNAME_ClientSubmissionReport = new QName(STR_NamespaceURI, "ClientSubmissionReport");
    private final static QName QNAME_LabExperimentStatus = new QName(STR_NamespaceURI, "LabExperimentStatus");
    private final static QName QNAME_LabStatus = new QName(STR_NamespaceURI, "LabStatus");
    private final static QName QNAME_ResultReport = new QName(STR_NamespaceURI, "ResultReport");
    private final static QName QNAME_SubmissionReport = new QName(STR_NamespaceURI, "SubmissionReport");
    private final static QName QNAME_ValidationReport = new QName(STR_NamespaceURI, "ValidationReport");
    private final static QName QNAME_WaitEstimate = new QName(STR_NamespaceURI, "WaitEstimate");
    private final static QName QNAME_LabEquipmentStatus = new QName(STR_NamespaceURI, "LabEquipmentStatus");
    private final static QName QNAME_ExecutionStatus = new QName(STR_NamespaceURI, "ExecutionStatus");
    private final static QName QNAME_Validation = new QName(STR_NamespaceURI, "Validation");

    public JAXBElement<ClientSubmissionReport> createClientSubmissionReport(ClientSubmissionReport value) {
        return new JAXBElement<>(QNAME_ClientSubmissionReport, ClientSubmissionReport.class, null, value);
    }

    public JAXBElement<LabExperimentStatus> createLabExperimentStatus(LabExperimentStatus value) {
        return new JAXBElement<>(QNAME_LabExperimentStatus, LabExperimentStatus.class, null, value);
    }

    public JAXBElement<LabStatus> createLabStatus(LabStatus value) {
        return new JAXBElement<>(QNAME_LabStatus, LabStatus.class, null, value);
    }

    public JAXBElement<ResultReport> createResultReport(ResultReport value) {
        return new JAXBElement<>(QNAME_ResultReport, ResultReport.class, null, value);
    }

    public JAXBElement<SubmissionReport> createSubmissionReport(SubmissionReport value) {
        return new JAXBElement<>(QNAME_SubmissionReport, SubmissionReport.class, null, value);
    }

    public JAXBElement<ValidationReport> createValidationReport(ValidationReport value) {
        return new JAXBElement<>(QNAME_ValidationReport, ValidationReport.class, null, value);
    }

    public JAXBElement<WaitEstimate> createWaitEstimate(WaitEstimate value) {
        return new JAXBElement<>(QNAME_WaitEstimate, WaitEstimate.class, null, value);
    }

    public JAXBElement<LabEquipmentStatus> createLabEquipmentStatus(LabEquipmentStatus value) {
        return new JAXBElement<>(QNAME_LabEquipmentStatus, LabEquipmentStatus.class, null, value);
    }

    public JAXBElement<ExecutionStatus> createExecutionStatus(ExecutionStatus value) {
        return new JAXBElement<>(QNAME_ExecutionStatus, ExecutionStatus.class, null, value);
    }

    public JAXBElement<Validation> createValidation(Validation value) {
        return new JAXBElement<>(QNAME_Validation, Validation.class, null, value);
    }
}
