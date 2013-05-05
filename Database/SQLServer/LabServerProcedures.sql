/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[Queue_Add]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[Queue_Add]'
    DROP PROCEDURE [dbo].[Queue_Add];
END
PRINT N'Creating procedure [dbo].[Queue_Add]'
GO
CREATE PROCEDURE [dbo].[Queue_Add]
	@ExperimentId int,
	@SbName varchar(256),
	@UserGroup varchar(256),
	@PriorityHint int,
	@XmlSpecification varchar(max),
	@EstimatedExecTime int,
	@StatusCode varchar(16)
AS
BEGIN
	INSERT INTO [dbo].[Queue] (
		ExperimentId, SbName, UserGroup, PriorityHint, XmlSpecification, EstimatedExecTime, StatusCode
	)
	VALUES (
		@ExperimentId, @SbName, @UserGroup, @PriorityHint, @XmlSpecification, @EstimatedExecTime, @StatusCode
	)
	SELECT CAST(@@IDENTITY AS int)
	RETURN @@IDENTITY
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[Queue_Delete]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[Queue_Delete]'
    DROP PROCEDURE [dbo].[Queue_Delete];
END
PRINT N'Creating procedure [dbo].[Queue_Delete]'
GO
CREATE PROCEDURE [dbo].[Queue_Delete]
    @Id integer
AS
BEGIN
	DECLARE @Retval integer
	
    DELETE FROM [dbo].[Queue]
    WHERE Id = @Id
	IF @@ERROR <> 0 OR @@ROWCOUNT = 0
		SET @Retval = 0
	ELSE
		SET @Retval = @Id
		
	SELECT @Retval
	RETURN @Retval
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[Queue_GetCountBy]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[Queue_GetCountBy]'
    DROP PROCEDURE [dbo].[Queue_GetCountBy];
END
PRINT N'Creating procedure [dbo].[Queue_GetCountBy]'
GO
CREATE PROCEDURE [dbo].[Queue_GetCountBy]
    @ColumnName varchar(16) = NULL,
    @IntValue integer = 0,
    @StrValue varchar(256) = NULL
AS
BEGIN
	DECLARE @Count bigint
	
	IF @ColumnName IS NULL
		SET @Count = (
			SELECT COUNT(*) FROM [dbo].[Queue]
		)
	ELSE IF LOWER(@ColumnName) = 'statuscode'
		SET @Count = (
			SELECT COUNT(*) FROM [dbo].[Queue]
			WHERE StatusCode = @StrValue
		)
		
	SELECT @Count
	RETURN @Count
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[Queue_RetrieveBy]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[Queue_RetrieveBy]'
    DROP PROCEDURE [dbo].[Queue_RetrieveBy];
END
PRINT N'Creating procedure [dbo].[Queue_RetrieveBy]'
GO
CREATE PROCEDURE [dbo].[Queue_RetrieveBy]
    @ColumnName varchar(16) = NULL,
    @IntValue integer = 0,
    @StrValue varchar(256) = NULL
AS
BEGIN
	IF @ColumnName IS NULL
		SELECT * FROM [dbo].[Queue]
		ORDER BY Id ASC
	ELSE IF LOWER(@ColumnName) = 'id'
		SELECT * FROM [dbo].[Queue]
		WHERE Id = @IntValue
	ELSE IF LOWER(@ColumnName) = 'experimentid'
		SELECT * FROM [dbo].[Queue]
		WHERE ExperimentId = @IntValue AND SbName = @StrValue
	ELSE IF LOWER(@ColumnName) = 'statuscode'
		SELECT * FROM [dbo].[Queue]
		WHERE StatusCode = @StrValue
		ORDER BY Id ASC
	return @@ROWCOUNT
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[Queue_UpdateStatus]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[Queue_UpdateStatus]'
    DROP PROCEDURE [dbo].[Queue_UpdateStatus];
END
PRINT N'Creating procedure [dbo].[Queue_UpdateStatus]'
GO
CREATE PROCEDURE [dbo].[Queue_UpdateStatus]
	@Id int,
	@StatusCode varchar(16)
AS
BEGIN
	DECLARE @Retval integer
	
	UPDATE [dbo].[Queue]
	SET StatusCode = @StatusCode
	WHERE Id = @Id
	IF @@ERROR <> 0 OR @@ROWCOUNT = 0
		SET @Retval = 0
	ELSE
		SET @Retval = @Id
		
	SELECT @Retval
	RETURN @Retval
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[Queue_UpdateStatusUnitId]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[Queue_UpdateStatusUnitId]'
    DROP PROCEDURE [dbo].[Queue_UpdateStatusUnitId];
END
PRINT N'Creating procedure [dbo].[Queue_UpdateStatusUnitId]'
GO
CREATE PROCEDURE [dbo].[Queue_UpdateStatusUnitId]
	@Id int,
	@StatusCode varchar(16),
	@UnitId int
AS
BEGIN
	DECLARE @Retval integer
	
	UPDATE [dbo].[Queue]
	SET StatusCode = @StatusCode, UnitId = @UnitId
	WHERE Id = @Id
	IF @@ERROR <> 0 OR @@ROWCOUNT = 0
		SET @Retval = 0
	ELSE
		SET @Retval = @Id
		
	SELECT @Retval
	RETURN @Retval
END
GO

/********************************************************************************************************************
 ********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[Results_Add]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[Results_Add]'
    DROP PROCEDURE [dbo].[Results_Add];
END
PRINT N'Creating procedure [dbo].[Results_Add]'
GO
CREATE PROCEDURE [dbo].[Results_Add]
	@ExperimentId int,
	@SbName varchar(256),
	@UserGroup varchar(256),
	@PriorityHint int,
	@StatusCode varchar(16),
	@XmlExperimentResult varchar(max) = NULL,
	@XmlResultExtension varchar(2048) = NULL,
	@XmlBlobExtension varchar(2048) = NULL,
	@WarningMessages varchar(2048) = NULL,
	@ErrorMessage varchar(2048) = NULL
AS
BEGIN
	INSERT INTO [dbo].[Results] (
		ExperimentId, SbName, UserGroup, PriorityHint, StatusCode, XmlExperimentResult, XmlResultExtension, XmlBlobExtension, WarningMessages, ErrorMessage
	)
	VALUES (
		@ExperimentId, @SbName, @UserGroup, @PriorityHint, @StatusCode, @XmlExperimentResult, @XmlResultExtension, @XmlBlobExtension, @WarningMessages, @ErrorMessage
	)
	SELECT CAST(@@IDENTITY AS int)
	RETURN @@IDENTITY
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[Results_Delete]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[Results_Delete]'
    DROP PROCEDURE [dbo].[Results_Delete];
END
PRINT N'Creating procedure [dbo].[Results_Delete]'
GO
CREATE PROCEDURE [dbo].[Results_Delete]
    @Id integer
AS
BEGIN
	DECLARE @Retval integer
	
    DELETE FROM [dbo].[Results]
    WHERE Id = @Id
	IF @@ERROR <> 0 OR @@ROWCOUNT = 0
		SET @Retval = 0
	ELSE
		SET @Retval = @Id
		
	SELECT @Retval
	RETURN @Retval
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[Results_RetrieveBy]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[Results_RetrieveBy]'
    DROP PROCEDURE [dbo].[Results_RetrieveBy];
END
PRINT N'Creating procedure [dbo].[Results_RetrieveBy]'
GO
CREATE PROCEDURE [dbo].[Results_RetrieveBy]
    @ColumnName varchar(16) = NULL,
    @IntValue integer = 0,
    @StrValue varchar(256) = NULL
AS
BEGIN
	IF @ColumnName IS NULL
		SELECT * FROM [dbo].[Results]
		ORDER BY Id ASC
	ELSE IF LOWER(@ColumnName) = 'id'
		SELECT * FROM [dbo].[Results]
		WHERE Id = @IntValue
	ELSE IF LOWER(@ColumnName) = 'experimentid'
		SELECT * FROM [dbo].[Results]
		WHERE ExperimentId = @IntValue AND SbName = @StrValue
	ELSE IF LOWER(@ColumnName) = 'notified'
		SELECT * FROM [dbo].[Results]
		WHERE Notified = @IntValue
		ORDER BY Id ASC
	return @@ROWCOUNT
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[Results_UpdateNotified]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[Results_UpdateNotified]'
    DROP PROCEDURE [dbo].[Results_UpdateNotified];
END
PRINT N'Creating procedure [dbo].[Results_UpdateNotified]'
GO
CREATE PROCEDURE [dbo].[Results_UpdateNotified]
	@ExperimentId int,
	@SbName varchar(256)
AS
BEGIN
	DECLARE @Retval integer
	
	UPDATE [dbo].[Results]
	SET Notified = 1
	WHERE ExperimentId = @ExperimentId AND SbName = @SbName
	IF @@ERROR <> 0 OR @@ROWCOUNT = 0
		SET @Retval = 0
	ELSE
		SET @Retval = 1
		
	SELECT @Retval
	RETURN @Retval
END
GO

/********************************************************************************************************************
 ********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[Statistics_Add]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[Statistics_Add]'
    DROP PROCEDURE [dbo].[Statistics_Add];
END
PRINT N'Creating procedure [dbo].[Statistics_Add]'
GO
CREATE PROCEDURE [dbo].[Statistics_Add]
	@ExperimentId int,
	@SbName varchar(256),
	@UserGroup varchar(256),
	@PriorityHint int,
	@EstimatedExecTime int,
	@QueueLength int,
	@EstimatedWaitTime int
AS
BEGIN
	INSERT INTO [dbo].[Statistics] (
		ExperimentId, SbName, UserGroup, PriorityHint, EstimatedExecTime, QueueLength, EstimatedWaitTime, TimeSubmitted
	)
	VALUES (
		@ExperimentId, @SbName, @UserGroup, @PriorityHint, @EstimatedExecTime, @QueueLength, @EstimatedWaitTime, CURRENT_TIMESTAMP
 
	)
	SELECT CAST(@@IDENTITY AS int)
	RETURN @@IDENTITY
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[Statistics_Delete]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[Statistics_Delete]'
    DROP PROCEDURE [dbo].[Statistics_Delete];
END
PRINT N'Creating procedure [dbo].[Statistics_Delete]'
GO
CREATE PROCEDURE [dbo].[Statistics_Delete]
    @Id integer
AS
BEGIN
	DECLARE @Retval integer
	
    DELETE FROM [dbo].[Statistics]
    WHERE Id = @Id
	IF @@ERROR <> 0 OR @@ROWCOUNT = 0
		SET @Retval = 0
	ELSE
		SET @Retval = @Id
		
	SELECT @Retval
	RETURN @Retval
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[Statistics_RetrieveBy]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[Statistics_RetrieveBy]'
    DROP PROCEDURE [dbo].[Statistics_RetrieveBy];
END
PRINT N'Creating procedure [dbo].[Statistics_RetrieveBy]'
GO
CREATE PROCEDURE [dbo].[Statistics_RetrieveBy]
    @ColumnName varchar(16) = NULL,
    @IntValue integer = 0,
    @StrValue varchar(256) = NULL
AS
BEGIN
	IF @ColumnName IS NULL
		SELECT * FROM [dbo].[Statistics]
		ORDER BY Id ASC
	ELSE IF LOWER(@ColumnName) = 'id'
		SELECT * FROM [dbo].[Statistics]
		WHERE Id = @IntValue
	ELSE IF LOWER(@ColumnName) = 'experimentid'
		SELECT * FROM [dbo].[Statistics]
		WHERE ExperimentId = @IntValue AND SbName = @StrValue
	ELSE IF LOWER(@ColumnName) = 'usergroup'
		SELECT * FROM [dbo].[Statistics]
		WHERE UserGroup = @StrValue
		ORDER BY Id ASC
	return @@ROWCOUNT
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[Statistics_UpdateCancelled]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[Statistics_UpdateCancelled]'
    DROP PROCEDURE [dbo].[Statistics_UpdateCancelled];
END
PRINT N'Creating procedure [dbo].[Statistics_UpdateCancelled]'
GO
CREATE PROCEDURE [dbo].[Statistics_UpdateCancelled]
	@ExperimentId int,
	@SbName varchar(256)
AS
BEGIN
	DECLARE @Retval integer
	
	UPDATE [dbo].[Statistics]
	SET Cancelled = 1, TimeCompleted = CURRENT_TIMESTAMP
	WHERE ExperimentId = @ExperimentId AND SbName = @SbName
	IF @@ERROR <> 0 OR @@ROWCOUNT = 0
		SET @Retval = 0
	ELSE
		SET @Retval = 1
		
	SELECT @Retval
	RETURN @Retval
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[Statistics_UpdateCompleted]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[Statistics_UpdateCompleted]'
    DROP PROCEDURE [dbo].[Statistics_UpdateCompleted];
END
PRINT N'Creating procedure [dbo].[Statistics_UpdateCompleted]'
GO
CREATE PROCEDURE [dbo].[Statistics_UpdateCompleted]
	@ExperimentId int,
	@SbName varchar(256)
AS
BEGIN
	DECLARE @Retval integer
	
	UPDATE [dbo].[Statistics]
	SET TimeCompleted = CURRENT_TIMESTAMP
	WHERE ExperimentId = @ExperimentId AND SbName = @SbName
	IF @@ERROR <> 0 OR @@ROWCOUNT = 0
		SET @Retval = 0
	ELSE
		SET @Retval = 1
		
	SELECT @Retval
	RETURN @Retval
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[Statistics_UpdateStarted]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[Statistics_UpdateStarted]'
    DROP PROCEDURE [dbo].[Statistics_UpdateStarted];
END
PRINT N'Creating procedure [dbo].[Statistics_UpdateStarted]'
GO
CREATE PROCEDURE [dbo].[Statistics_UpdateStarted]
	@ExperimentId int,
	@SbName varchar(256),
	@UnitId int
AS
BEGIN
	DECLARE @Retval integer
	
	UPDATE [dbo].[Statistics]
	SET UnitId = @UnitId, TimeStarted = CURRENT_TIMESTAMP
	WHERE ExperimentId = @ExperimentId AND SbName = @SbName
	IF @@ERROR <> 0 OR @@ROWCOUNT = 0
		SET @Retval = 0
	ELSE
		SET @Retval = 1
		
	SELECT @Retval
	RETURN @Retval
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[LabEquipment_Add]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[LabEquipment_Add]'
    DROP PROCEDURE [dbo].[LabEquipment_Add];
END
PRINT N'Creating procedure [dbo].[LabEquipment_Add]'
GO
CREATE PROCEDURE [dbo].[LabEquipment_Add]
    @ServiceUrl varchar(256),
    @Passkey varchar(40) = NULL,
    @Enabled bit
AS
BEGIN
	INSERT INTO [dbo].[LabEquipment] (
		ServiceUrl, Passkey, Enabled
	)
	VALUES (
		@ServiceUrl, @Passkey, @Enabled
	)
	SELECT CAST(@@IDENTITY AS int)
	RETURN @@IDENTITY
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[LabEquipment_Delete]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[LabEquipment_Delete]'
    DROP PROCEDURE [dbo].[LabEquipment_Delete];
END
PRINT N'Creating procedure [dbo].[LabEquipment_Delete]'
GO
CREATE PROCEDURE [dbo].[LabEquipment_Delete]
    @Id integer
AS
BEGIN
	DECLARE @Retval integer
	
    DELETE FROM [dbo].[LabEquipment]
    WHERE Id = @Id
	IF @@ERROR <> 0 OR @@ROWCOUNT = 0
		SET @Retval = 0
	ELSE
		SET @Retval = @Id
		
	SELECT @Retval
	RETURN @Retval
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[LabEquipment_RetrieveBy]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[LabEquipment_RetrieveBy]'
    DROP PROCEDURE [dbo].[LabEquipment_RetrieveBy];
END
PRINT N'Creating procedure [dbo].[LabEquipment_RetrieveBy]'
GO
CREATE PROCEDURE [dbo].[LabEquipment_RetrieveBy]
    @ColumnName varchar(16) = NULL,
    @IntValue integer = 0,
    @StrValue varchar(256) = NULL
AS
BEGIN
	IF @ColumnName IS NULL
		SELECT * FROM [dbo].[LabEquipment]
		ORDER BY Id ASC
	ELSE IF LOWER(@ColumnName) = 'id'
		SELECT * FROM [dbo].[LabEquipment]
		WHERE Id = @IntValue
	ELSE IF LOWER(@ColumnName) = 'serviceurl'
		SELECT * FROM [dbo].[LabEquipment]
		WHERE ServiceUrl = @StrValue
	return @@ROWCOUNT
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[LabEquipment_Update]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[LabEquipment_Update]'
    DROP PROCEDURE [dbo].[LabEquipment_Update];
END
PRINT N'Creating procedure [dbo].[LabEquipment_Update]'
GO
CREATE PROCEDURE [dbo].[LabEquipment_Update]
    @Id integer,
    @ServiceUrl varchar(256),
    @Passkey varchar(40) = NULL,
    @Enabled bit
AS
BEGIN
	DECLARE @Retval integer
	
	UPDATE [dbo].[LabEquipment]
	SET ServiceUrl = @ServiceUrl, Passkey = @Passkey, Enabled = @Enabled, DateModified = CURRENT_TIMESTAMP
	WHERE Id = @Id
	IF @@ERROR <> 0 OR @@ROWCOUNT = 0
		SET @Retval = 0
	ELSE
		SET @Retval = @Id
		
	SELECT @Retval
	RETURN @Retval
END
GO

/********************************************************************************************************************
 ********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[LabServer_Add]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[LabServer_Add]'
    DROP PROCEDURE [dbo].[LabServer_Add];
END
PRINT N'Creating procedure [dbo].[LabServer_Add]'
GO
CREATE PROCEDURE [dbo].[LabServer_Add]
	@Name varchar(32),
	@Guid varchar(40),
	@ServiceUrl varchar(256) = NULL,
	@ContactEmail varchar(128) = NULL,
	@CompletedEmail varchar(256) = NULL,
	@FailedEmail varchar(256) = NULL,
	@Authenticate bit
AS
BEGIN
	INSERT INTO [dbo].[LabServer] (
		Name, Guid, ServiceUrl, ContactEmail, CompletedEmail, FailedEmail, Authenticate
	)
	VALUES (
		@Name, @Guid, @ServiceUrl, @ContactEmail, @CompletedEmail, @FailedEmail, @Authenticate
	)
	SELECT CAST(@@IDENTITY AS int)
	RETURN @@IDENTITY
END
GO
	
/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[LabServer_Delete]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[LabServer_Delete]'
    DROP PROCEDURE [dbo].[LabServer_Delete];
END
PRINT N'Creating procedure [dbo].[LabServer_Delete]'
GO
CREATE PROCEDURE [dbo].[LabServer_Delete]
	@Id int
AS
BEGIN
	DECLARE @Retval integer
	
	DELETE FROM [dbo].[LabServer]
	WHERE Id = @Id
	IF @@ERROR <> 0 OR @@ROWCOUNT = 0
		SET @Retval = 0
	ELSE
		SET @Retval = @Id
		
	SELECT @Retval
	RETURN @Retval
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[LabServer_GetList]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[LabServer_GetList]'
    DROP PROCEDURE [dbo].[LabServer_GetList];
END
PRINT N'Creating procedure [dbo].[LabServer_GetList]'
GO
CREATE PROCEDURE [dbo].[LabServer_GetList]
    @ColumnName varchar(16),
    @StrValue varchar(40) = NULL
AS
BEGIN
	IF LOWER(@ColumnName) = 'name'
		SELECT Name FROM [dbo].[LabServer]
		ORDER BY Name ASC
	ELSE IF LOWER(@ColumnName) = 'guid'
		SELECT * FROM [dbo].[LabServer]
		WHERE Guid = @StrValue
		ORDER BY Name ASC
	RETURN @@ROWCOUNT
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[LabServer_RetrieveBy]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[LabServer_RetrieveBy]'
    DROP PROCEDURE [dbo].[LabServer_RetrieveBy];
END
PRINT N'Creating procedure [dbo].[LabServer_RetrieveBy]'
GO
CREATE PROCEDURE [dbo].[LabServer_RetrieveBy]
    @ColumnName varchar(16) = NULL,
    @IntValue integer = 0,
    @StrValue varchar(32) = NULL
AS
BEGIN
	IF @ColumnName IS NULL
		SELECT * FROM [dbo].[LabServer]
		ORDER BY Id ASC
	ELSE IF LOWER(@ColumnName) = 'id'
		SELECT * FROM [dbo].[LabServer]
		WHERE Id = @IntValue
	ELSE IF LOWER(@ColumnName) = 'name'
		SELECT * FROM [dbo].[LabServer]
		WHERE Name = @StrValue
	return @@ROWCOUNT
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[LabServer_Update]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[LabServer_Update]'
    DROP PROCEDURE [dbo].[LabServer_Update];
END
PRINT N'Creating procedure [dbo].[LabServer_Update]'
GO
CREATE PROCEDURE [dbo].[LabServer_Update]
    @Id integer,
	@Name varchar(32),
	@Guid varchar(40),
	@ServiceUrl varchar(256) = NULL,
	@ContactEmail varchar(128) = NULL,
	@CompletedEmail varchar(256) = NULL,
	@FailedEmail varchar(256) = NULL,
	@Authenticate bit
AS
BEGIN
	DECLARE @Retval integer
	
	UPDATE [dbo].[LabServer]
	SET Name = @Name, Guid = @Guid, ServiceUrl = @ServiceUrl, ContactEmail = @ContactEmail, CompletedEmail = @CompletedEmail,
		FailedEmail = @FailedEmail, Authenticate = @Authenticate, DateModified = CURRENT_TIMESTAMP
	WHERE Id = @Id
	IF @@ERROR <> 0 OR @@ROWCOUNT = 0
		SET @Retval = 0
	ELSE
		SET @Retval = @Id
		
	SELECT @Retval
	RETURN @Retval
END
GO

/********************************************************************************************************************
 ********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[ServiceBrokers_Add]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[ServiceBrokers_Add]'
    DROP PROCEDURE [dbo].[ServiceBrokers_Add];
END
PRINT N'Creating procedure [dbo].[ServiceBrokers_Add]'
GO
CREATE PROCEDURE [dbo].[ServiceBrokers_Add]
	@Name varchar(32),
	@Guid varchar(40),
	@OutPasskey varchar(40),
	@InPasskey varchar(40) = NULL,
	@ServiceUrl varchar(256) = NULL,
	@Permitted bit
AS
BEGIN
	INSERT INTO [dbo].[ServiceBrokers] (
		Name, Guid, OutPasskey, InPasskey, ServiceUrl, Permitted
	)
	VALUES (
		@Name, @Guid, @OutPasskey, @InPasskey, @ServiceUrl, @Permitted
	)
	SELECT CAST(@@IDENTITY AS int)
	RETURN @@IDENTITY
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[ServiceBrokers_Delete]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[ServiceBrokers_Delete]'
    DROP PROCEDURE [dbo].[ServiceBrokers_Delete];
END
PRINT N'Creating procedure [dbo].[ServiceBrokers_Delete]'
GO
CREATE PROCEDURE [dbo].[ServiceBrokers_Delete]
	@Id int
AS
BEGIN
	DECLARE @Retval integer
	
	DELETE FROM [dbo].[ServiceBrokers]
	WHERE Id = @Id
	IF @@ERROR <> 0 OR @@ROWCOUNT = 0
		SET @Retval = 0
	ELSE
		SET @Retval = @Id
		
	SELECT @Retval
	RETURN @Retval
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[ServiceBrokers_GetList]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[ServiceBrokers_GetList]'
    DROP PROCEDURE [dbo].[ServiceBrokers_GetList];
END
PRINT N'Creating procedure [dbo].[ServiceBrokers_GetList]'
GO
CREATE PROCEDURE [dbo].[ServiceBrokers_GetList]
    @ColumnName varchar(16),
    @StrValue varchar(32) = NULL
AS
BEGIN
	IF LOWER(@ColumnName) = 'name'
		SELECT Name FROM [dbo].[ServiceBrokers]
		ORDER BY Name ASC
	RETURN @@ROWCOUNT
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[ServiceBrokers_RetrieveBy]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[ServiceBrokers_RetrieveBy]'
    DROP PROCEDURE [dbo].[ServiceBrokers_RetrieveBy];
END
PRINT N'Creating procedure [dbo].[ServiceBrokers_RetrieveBy]'
GO
CREATE PROCEDURE [dbo].[ServiceBrokers_RetrieveBy]
    @ColumnName varchar(16) = NULL,
    @IntValue integer,
    @StrValue varchar(32) = NULL
AS
BEGIN
	IF @ColumnName IS NULL
		SELECT * FROM [dbo].[ServiceBrokers]
		ORDER BY Id ASC
	ELSE IF LOWER(@ColumnName) = 'id'
		SELECT * FROM [dbo].[ServiceBrokers]
		WHERE Id = @IntValue
	ELSE IF LOWER(@ColumnName) = 'name'
		SELECT * FROM [dbo].[ServiceBrokers]
		WHERE Name = @StrValue
	ELSE IF LOWER(@ColumnName) = 'guid'
		SELECT * FROM [dbo].[ServiceBrokers]
		WHERE Guid = @StrValue
	return @@ROWCOUNT
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[ServiceBrokers_Update]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[ServiceBrokers_Update]'
    DROP PROCEDURE [dbo].[ServiceBrokers_Update];
END
PRINT N'Creating procedure [dbo].[ServiceBrokers_Update]'
GO
CREATE PROCEDURE [dbo].[ServiceBrokers_Update]
	@Id integer,
	@Name varchar(32),
	@Guid varchar(40),
	@OutPasskey varchar(40),
	@InPasskey varchar(40) = NULL,
	@ServiceUrl varchar(256) = NULL,
	@Permitted bit
AS
BEGIN
	DECLARE @Retval integer
	
	UPDATE [dbo].[ServiceBrokers]
	SET Name = @Name, Guid = @Guid, OutPasskey = @OutPasskey, InPasskey = @InPasskey, ServiceUrl = @ServiceUrl,
		Permitted = @Permitted, DateModified = CURRENT_TIMESTAMP
	WHERE Id = @Id
	IF @@ERROR <> 0 OR @@ROWCOUNT = 0
		SET @Retval = 0
	ELSE
		SET @Retval = @Id
		
	SELECT @Retval
	RETURN @Retval
END
GO

/********************************************************************************************************************
 ********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[Users_Add]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[Users_Add]'
    DROP PROCEDURE [dbo].[Users_Add];
END
PRINT N'Creating procedure [dbo].[Users_Add]'
GO
CREATE PROCEDURE [dbo].[Users_Add]
	@Username varchar(32),
	@FirstName varchar(64),
	@LastName varchar(64),
	@ContactEmail varchar(128),
	@UserGroup varchar(32),
	@Password varchar(50)
AS
BEGIN
	INSERT INTO [dbo].[Users] (
		Username, FirstName, LastName, ContactEmail, UserGroup, Password
	)
	VALUES (
		@Username, @FirstName, @LastName, @ContactEmail, @UserGroup, @Password
	)
	SELECT CAST(@@IDENTITY AS int)
	RETURN @@IDENTITY
END
GO
	
/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[Users_Delete]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[Users_Delete]'
    DROP PROCEDURE [dbo].[Users_Delete];
END
PRINT N'Creating procedure [dbo].[Users_Delete]'
GO
CREATE PROCEDURE [dbo].[Users_Delete]
	@UserId int
AS
BEGIN
	DECLARE @Retval integer
	
	DELETE FROM [dbo].[Users]
	WHERE UserId = @UserId
	IF @@ERROR <> 0 OR @@ROWCOUNT = 0
		SET @Retval = 0
	ELSE
		SET @Retval = @UserId
		
	SELECT @Retval
	RETURN @Retval
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[Users_GetList]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[Users_GetList]'
    DROP PROCEDURE [dbo].[Users_GetList];
END
PRINT N'Creating procedure [dbo].[Users_GetList]'
GO
CREATE PROCEDURE [dbo].[Users_GetList]
    @ColumnName varchar(16),
    @StrValue varchar(32) = NULL
AS
BEGIN
	IF LOWER(@ColumnName) = 'username'
		SELECT Username FROM [dbo].[Users]
		ORDER BY Username ASC
	ELSE IF LOWER(@ColumnName) = 'usergroup'
		SELECT Username FROM [dbo].[Users]
		WHERE UserGroup = @StrValue
		ORDER BY Username ASC
	RETURN @@ROWCOUNT
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[Users_RetrieveBy]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[Users_RetrieveBy]'
    DROP PROCEDURE [dbo].[Users_RetrieveBy];
END
PRINT N'Creating procedure [dbo].[Users_RetrieveBy]'
GO
CREATE PROCEDURE [dbo].[Users_RetrieveBy]
    @ColumnName varchar(16) = NULL,
    @IntValue integer,
    @StrValue varchar(32) = NULL
AS
BEGIN
	IF @ColumnName IS NULL
		SELECT * FROM [dbo].[Users]
		ORDER BY UserId ASC
	ELSE IF LOWER(@ColumnName) = 'userid'
		SELECT * FROM [dbo].[Users]
		WHERE UserId = @IntValue
	ELSE IF LOWER(@ColumnName) = 'username'
		SELECT * FROM [dbo].[Users]
		WHERE Username = @StrValue
	return @@ROWCOUNT
END
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[Users_Update]', 'P' ) IS NOT NULL 
BEGIN
	PRINT N'Dropping procedure [dbo].[Users_Update]'
    DROP PROCEDURE [dbo].[Users_Update];
END
PRINT N'Creating procedure [dbo].[Users_Update]'
GO
CREATE PROCEDURE [dbo].[Users_Update]
	@UserId int,
	@FirstName varchar(64),
	@LastName varchar(64),
	@ContactEmail varchar(128),
	@UserGroup varchar(32),
	@Password varchar(50),
	@AccountLocked bit
AS
BEGIN
	DECLARE @Retval integer
	
	UPDATE [dbo].[Users]
	SET FirstName = @FirstName, LastName = @LastName, ContactEmail = @ContactEmail, UserGroup = @UserGroup,
		Password = @Password, AccountLocked = @AccountLocked, DateModified = CURRENT_TIMESTAMP
	WHERE UserId = @UserId
	IF @@ERROR <> 0 OR @@ROWCOUNT = 0
		SET @Retval = 0
	ELSE
		SET @Retval = @UserId
		
	SELECT @Retval
	RETURN @Retval
END
GO
