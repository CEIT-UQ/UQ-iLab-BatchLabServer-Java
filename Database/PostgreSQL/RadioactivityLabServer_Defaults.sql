/*
 * LabServer_Add (Name, Guid, ServiceUrl, ContactEmail, CompletedEmail, FailedEmail, Authenticate)
 */
SELECT LabServer_Add ('Radioactivity Java','FF464E3507564f28A64F156282BB912E','http://localhost:8080/RadioactivityLabServer/LabServerWebService', 'labserver@your.email.domain', NULL, NULL, TRUE);

/*
 * LabEquipment_Add (ServiceUrl, Passkey, Enabled)
 */
SELECT LabEquipment_Add ('http://localhost:8087/LabEquipmentService.asmx', 'fd3cf16cc855484fb06801379f475837', false);
SELECT LabEquipment_Add ('http://localhost:8080/RadioactivityLabEquipment/LabEquipmentService', 'fd3cf16cc855484fb06801379f475837', true);
