/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[Queue]', 'U' ) IS NOT NULL
BEGIN
    PRINT N'Dropping table [dbo].[Queue]'
    DROP TABLE [dbo].[Queue];
END
PRINT N'Creating table [dbo].[Queue]'
GO
CREATE TABLE [dbo].[Queue] (
    Id int IDENTITY (1, 1) NOT NULL,
    ExperimentId int NOT NULL,
    SbName varchar(32) NOT NULL,
    UserGroup varchar(64) NOT NULL,
    PriorityHint int NOT NULL,
    XmlSpecification varchar(max) NOT NULL,
    EstimatedExecTime int NOT NULL,
    StatusCode varchar(16) NOT NULL,
    UnitId int DEFAULT -1,
    Cancelled bit DEFAULT 0,

    PRIMARY KEY (Id),
    UNIQUE (ExperimentId, SbName)
)
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[Results]', 'U' ) IS NOT NULL
BEGIN
    PRINT N'Dropping table [dbo].[Results]'
    DROP TABLE [dbo].[Results];
END
PRINT N'Creating table [dbo].[Results]'
GO
CREATE TABLE [dbo].[Results] (
    Id int IDENTITY (1, 1) NOT NULL,
    ExperimentId int NOT NULL,
    SbName varchar(32) NOT NULL,
    UserGroup varchar(64) NOT NULL,
    PriorityHint int NOT NULL,
    StatusCode varchar(16) NOT NULL,
    XmlExperimentResult varchar(max),
    XmlResultExtension varchar(2048),
    XmlBlobExtension varchar(2048),
    WarningMessages varchar(2048),
    ErrorMessage varchar(2048),
    Notified bit DEFAULT 0,

    PRIMARY KEY (Id),
    UNIQUE (ExperimentId, SbName)
)
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[Statistics]', 'U' ) IS NOT NULL
BEGIN
    PRINT N'Dropping table [dbo].[Statistics]'
    DROP TABLE [dbo].[Statistics];
END
PRINT N'Creating table [dbo].[Statistics]'
GO
CREATE TABLE [dbo].[Statistics] (
    Id int IDENTITY (1, 1) NOT NULL,
    ExperimentId int NOT NULL,
    SbName varchar(32) NOT NULL,
    UserGroup varchar(64) NOT NULL,
    PriorityHint int NOT NULL,
    EstimatedExecTime int NOT NULL,
    TimeSubmitted datetime NOT NULL,
    QueueLength int NOT NULL,
    EstimatedWaitTime int NOT NULL,
    TimeStarted datetime,
    UnitId int,
    TimeCompleted datetime,
    Cancelled bit DEFAULT 0,

    PRIMARY KEY (Id),
    UNIQUE (ExperimentId, SbName)
)
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[LabEquipment]', 'U' ) IS NOT NULL
BEGIN
	PRINT N'Dropping table [dbo].[LabEquipment]'
    DROP TABLE [dbo].[LabEquipment];
END
PRINT N'Creating table [dbo].[LabEquipment]'
GO
CREATE TABLE [dbo].[LabEquipment]
(
    Id int IDENTITY (1, 1) NOT NULL,
    ServiceType varchar(8) NOT NULL,
    ServiceUrl varchar(256) NOT NULL,
    Passkey varchar(40),
    Enabled bit DEFAULT 0,
    DateCreated datetime DEFAULT CURRENT_TIMESTAMP,
    DateModified datetime,

    PRIMARY KEY (Id),
    UNIQUE (ServiceUrl)
)
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[LabServer]', 'U' ) IS NOT NULL
BEGIN
	PRINT N'Dropping table [dbo].[LabServer]'
    DROP TABLE [dbo].[LabServer];
END
PRINT N'Creating table [dbo].[LabServer]'
GO
CREATE TABLE [dbo].[LabServer]
(
    Id int IDENTITY (1, 1) NOT NULL,
    Name varchar(32) NOT NULL,
    Guid varchar(40) NOT NULL,
    ServiceUrl varchar(256),
    ContactEmail varchar(128),
    CompletedEmail varchar(256),
    FailedEmail varchar(256),
    Authenticate bit DEFAULT 1,
    DateCreated datetime DEFAULT CURRENT_TIMESTAMP,
    DateModified datetime,

    PRIMARY KEY (Id),
    UNIQUE (Name)
)
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[ServiceBrokers]', 'U' ) IS NOT NULL
BEGIN
	PRINT N'Dropping table [dbo].[ServiceBrokers]'
    DROP TABLE [dbo].[ServiceBrokers];
END
PRINT N'Creating table [dbo].[ServiceBrokers]'
GO
CREATE TABLE [dbo].[ServiceBrokers] (
    Id int IDENTITY (1, 1) NOT NULL,
    Name varchar(32) NOT NULL,
    Guid varchar(40) NOT NULL,
    OutPasskey varchar(40) NOT NULL,
    InPasskey varchar(40),
    ServiceUrl varchar(256),
    Permitted bit DEFAULT 0,
    DateCreated datetime DEFAULT CURRENT_TIMESTAMP,
    DateModified datetime,

    PRIMARY KEY (Id),
    UNIQUE (Name)
)
GO

/********************************************************************************************************************
*/

IF OBJECT_ID (N'[dbo].[Users]', 'U' ) IS NOT NULL
BEGIN
	PRINT N'Dropping table [dbo].[Users]'
    DROP TABLE [dbo].[Users];
END
PRINT N'Creating table [dbo].[Users]'
GO
CREATE TABLE [dbo].[Users] (
    UserId int IDENTITY (1, 1) NOT NULL,
    Username varchar(32) NOT NULL,
    FirstName varchar(64) NOT NULL,
    LastName varchar(64) NOT NULL,
    ContactEmail varchar(128) NOT NULL,
    UserGroup varchar(64) NOT NULL,
    Password varchar(40) NOT NULL,
    AccountLocked bit DEFAULT 0,
    DateCreated datetime DEFAULT CURRENT_TIMESTAMP,
    DateModified datetime,

    PRIMARY KEY (UserId),
    UNIQUE (Username)
)
GO

