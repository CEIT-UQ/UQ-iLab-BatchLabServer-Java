/*
 * [dbo].[LabServer_Add] Name, Guid, ServiceUrl, ContactEmail, CompletedEmail, FailedEmail, Authenticate
 */
DECLARE @RC_LabServer int
DECLARE @Name varchar(32)
DECLARE @Guid varchar(40)
DECLARE @ServiceUrlLS varchar(256)
DECLARE @ContactEmail varchar(128)
DECLARE @CompletedEmail varchar(256)
DECLARE @FailedEmail varchar(256)
DECLARE @Authenticate bit

SET @Name = 'Radioactivity Java'
SET @Guid = 'FF464E3507564f28A64F156282BB912E'
SET @ServiceUrlLS = 'http://localhost:8080/RadioactivityLabServer/LabServerWebService'
SET @ContactEmail = 'labserver@your.email.domain'
SET @CompletedEmail = NULL
SET @FailedEmail = NULL
SET @Authenticate = 1

PRINT N'Adding LabServer: Name = ' + @Name + N', ServiceUrl = ' + @ServiceUrlLS
EXECUTE @RC_LabServer = [dbo].[LabServer_Add] @Name, @Guid, @ServiceUrlLS, @ContactEmail, @CompletedEmail, @FailedEmail, @Authenticate
PRINT N'Id = ' + CAST(@RC_LabServer AS NVARCHAR(4))

/*
 * [dbo].[LabEquipment_Add] ServiceUrl, Passkey, Enabled
 */
DECLARE @RC_LabEquipment int
DECLARE @ServiceUrlLE varchar(256)
DECLARE @Passkey varchar(40)
DECLARE @Enabled bit

SET @ServiceType = 'SOAP'
SET @ServiceUrlLE = 'http://localhost:8087/LabEquipmentService.asmx'
SET @Passkey = 'fd3cf16cc855484fb06801379f475837'
SET @Enabled = 0

PRINT N'Adding LabEquipment: ServiceUrl = ' + @ServiceUrlLE
EXECUTE @RC_LabEquipment = [dbo].[LabEquipment_Add] @ServiceType, @ServiceUrlLE, @Passkey, @Enabled
PRINT N'Id = ' + CAST(@RC_LabEquipment AS NVARCHAR(4))

SET @ServiceUrlLE = 'http://localhost:8080/RadioactivityLabEquipment/LabEquipmentService'
SET @Passkey = 'fd3cf16cc855484fb06801379f475837'
SET @Enabled = 1

PRINT N'Adding LabEquipment: ServiceUrl = ' + @ServiceUrlLE
EXECUTE @RC_LabEquipment = [dbo].[LabEquipment_Add] @ServiceType, @ServiceUrlLE, @Passkey, @Enabled
PRINT N'Id = ' + CAST(@RC_LabEquipment AS NVARCHAR(4))
