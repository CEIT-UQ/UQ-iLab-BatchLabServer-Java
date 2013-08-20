/*
 * LabServer_Add (Name, Guid, ServiceUrl, ContactEmail, CompletedEmail, FailedEmail, Authenticate)
 */
SELECT LabServer_Add ('ACMachine Java','2CD01113C51C4ca997B059531CD9469D','http://localhost:8080/ACMachineLabServer/LabServerWebService', 'labserver@your.email.domain', NULL, NULL, TRUE);

/*
 * LabEquipment_Add (ServiceType, ServiceUrl, Passkey)
 */
SELECT LabEquipment_Add ('SOAP','http://localhost:8087/LabEquipmentService.asmx','fd3cf16cc855484fb06801379f475837',true);
