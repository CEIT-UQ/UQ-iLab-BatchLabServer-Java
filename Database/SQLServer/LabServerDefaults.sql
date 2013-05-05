/*
 * [dbo].[ServiceBrokers_Add] Name, ServiceGuid, OutPasskey, InPasskey, ServiceUrl, Permitted
 */
DECLARE @RC_ServiceBrokers int
DECLARE @Name varchar(32)
DECLARE @Guid varchar(40)
DECLARE @OutPasskey varchar(40)
DECLARE @InPasskey varchar(40)
DECLARE @ServiceUrl varchar(256)
DECLARE @Permitted bit

SET @Name = 'localhost'
SET @Guid = '196495303F294B13856D7E48872E51CC'
SET @OutPasskey = 'FD3CF16CC855484FB06801379F475837'
SET @InPasskey = NULL
SET @ServiceUrl = 'http://localhost:8080/DummyServiceBroker/ServiceBrokerService'
SET @Permitted = 1

PRINT N'Adding ServiceBroker: ' + @Name + N', Permitted = ' + CAST(@Permitted AS NVARCHAR(1))
EXECUTE @RC_ServiceBrokers = [dbo].[ServiceBrokers_Add] @Name, @Guid, @OutPasskey, @InPasskey, @ServiceUrl, @Permitted
PRINT N'Id = ' + CAST(@RC_ServiceBrokers AS NVARCHAR(4))

/*
 * [dbo].[Users_Add] Username, FirstName, LastName, ContactEmail, UserGroup, Password  (Default password is: ilab)
 */
DECLARE @RC_Users int
DECLARE @Username varchar(32)
DECLARE @FirstName varchar(64)
DECLARE @LastName varchar(64)
DECLARE @ContactEmail varchar(128)
DECLARE @UserGroup varchar(32)
DECLARE @Password varchar(64)

SET @Username = 'manager'
SET @FirstName = 'LabServer'
SET @LastName = 'Manager'
SET @ContactEmail = 'manager@your.email.domain'
SET @UserGroup = 'Manager'
SET @Password = '3759F4FF14D8494DF3B58671FF9251A9D0C41D54'

PRINT N'Adding User: ' + @Username
EXECUTE @RC_Users = [dbo].[Users_Add] @Username, @FirstName, @LastName, @ContactEmail, @UserGroup, @Password
PRINT N'Id = ' + CAST(@RC_Users AS NVARCHAR(4))
