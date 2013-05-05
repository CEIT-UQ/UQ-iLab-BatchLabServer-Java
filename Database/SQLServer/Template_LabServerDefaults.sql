/*
 * [dbo].[LabServer_Add] Name, ServiceGuid, ServiceUrl, ContactEmail, CompletedEmail, FailedEmail, Authenticate
 */
DECLARE @RC_LabServer int
DECLARE @Name varchar(32)
DECLARE @ServiceGuid varchar(40)
DECLARE @ServiceUrlLS varchar(256)
DECLARE @ContactEmail varchar(128)
DECLARE @CompletedEmail varchar(256)
DECLARE @FailedEmail varchar(256)
DECLARE @Authenticate bit

SET @Name = 'Template'
SET @ServiceGuid = '8C0BA543419E4d4ab340d449acd3e057'
SET @ServiceUrlLS = 'http://localhost:8083/LabServerWebService.asmx'
SET @ContactEmail = 'labserver@your.email.domain'
SET @CompletedEmail = NULL
SET @FailedEmail = NULL
SET @Authenticate = 0

PRINT N'Adding LabServer: Name = ' + @Name + N', ServiceUrl = ' + @ServiceUrlLS
EXECUTE @RC_LabServer = [dbo].[LabServer_Add] @Name, @ServiceGuid, @ServiceUrlLS, @ContactEmail, @CompletedEmail, @FailedEmail, @Authenticate
PRINT N'Id = ' + CAST(@RC_LabServer AS NVARCHAR(4))

/*
 * [dbo].[LabEquipment_Add] ServiceUrl, Passkey, Enabled
 */
DECLARE @RC_LabEquipment int
DECLARE @ServiceUrlLE varchar(256)
DECLARE @Passkey varchar(40)
DECLARE @Enabled bit

SET @ServiceUrlLE = 'http://localhost:8087/LabEquipmentService.asmx'
SET @Passkey = 'fd3cf16cc855484fb06801379f475837'
SET @Enabled = 1

PRINT N'Adding LabEquipment: ServiceUrl = ' + @ServiceUrlLE
EXECUTE @RC_LabEquipment = [dbo].[LabEquipment_Add] @ServiceUrlLE, @Passkey, @Enabled
PRINT N'Id = ' + CAST(@RC_LabEquipment AS NVARCHAR(4))
