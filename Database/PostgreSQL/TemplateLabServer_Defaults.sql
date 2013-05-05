/*
 * LabServer_Add (Name, Guid, ServiceUrl, ContactEmail, CompletedEmail, FailedEmail, Authenticate)
 */
SELECT LabServer_Add ('Template Java','2CD01113C51C4ca997B059531CD9469D','http://localhost:8080/TemplateLabServer/LabServerWebService', 'labserver@your.email.domain', NULL, NULL, TRUE);

/*
 * LabEquipment_Add (ServiceUrl, Passkey, Enabled)
 */
SELECT LabEquipment_Add ('http://localhost:8087/LabEquipmentService.asmx','fd3cf16cc855484fb06801379f475837', false);
SELECT LabEquipment_Add ('http://localhost:8080/TemplateLabEquipment/LabEquipmentService','fd3cf16cc855484fb06801379f475837', true);
