/*
 * LabServer_Add (Name, Guid, ServiceUrl, ContactEmail, CompletedEmail, FailedEmail, Authenticate)
 */
SELECT LabServer_Add ('TimeOfDay Java','259e49a7578240dca96233bb9472bb1c','http://localhost:8080/TimeOfDayLabServer/LabServerWebService', 'labserver@your.email.domain', NULL, NULL, TRUE);

/*
 * LabEquipment_Add (ServiceType, ServiceUrl, Passkey)
 */
SELECT LabEquipment_Add ('SOAP','http://localhost:8087/LabEquipmentService.asmx','fd3cf16cc855484fb06801379f475837', false);
SELECT LabEquipment_Add ('SOAP','http://localhost:8080/TimeOfDayLabEquipment/LabEquipmentService','fd3cf16cc855484fb06801379f475837', true);
