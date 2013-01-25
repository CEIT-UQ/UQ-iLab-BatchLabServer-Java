/*
 * (Name, Guid, ServiceUrl, ContactEmail, CompletedEmail, FailedEmail, Authenticate)
 */
SELECT LabServer_Add ('Radioactivity Java','FF464E3507564f28A64F156282BB912E','http://localhost:8080/RadioactivityLabServer/LabServerWebService', 'you@your.email', NULL, NULL, TRUE);

/*
 * (ServiceUrl, Passkey)
 */
SELECT LabEquipment_Add ('http://localhost:8080/RadioactivityLabEquipment/LabEquipmentService','fd3cf16cc855484fb06801379f475837',true);
