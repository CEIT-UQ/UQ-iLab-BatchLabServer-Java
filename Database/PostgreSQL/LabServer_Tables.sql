/********************************************************************************************************************
*/

DROP TABLE IF EXISTS Queue;

CREATE TABLE Queue
(
    Id serial NOT NULL,
    ExperimentId integer NOT NULL,
    SbName varchar(32) NOT NULL,
    UserGroup varchar(64) NOT NULL,
    PriorityHint integer NOT NULL,
    XmlSpecification varchar NOT NULL,
    EstimatedExecTime integer NOT NULL,
    StatusCode varchar(16) NOT NULL,
    UnitId integer DEFAULT -1,
    Cancelled boolean DEFAULT false,

    PRIMARY KEY (Id),
    UNIQUE (ExperimentId, SbName)
);

/********************************************************************************************************************
*/

DROP TABLE IF EXISTS Results;

CREATE TABLE Results
(
    Id serial NOT NULL,
    ExperimentId int NOT NULL,
    SbName varchar(32) NOT NULL,
    UserGroup varchar(64) NOT NULL,
    PriorityHint int NOT NULL,
    StatusCode varchar(16) NOT NULL,
    XmlExperimentResult varchar,
    XmlResultExtension varchar(2048),
    XmlBlobExtension varchar(2048),
    WarningMessages varchar(2048),
    ErrorMessage varchar(2048),
    Notified boolean DEFAULT false,

    PRIMARY KEY (Id),
    UNIQUE (ExperimentId, SbName)
);

/********************************************************************************************************************
*/

DROP TABLE IF EXISTS Statistics;

CREATE TABLE Statistics
(
    Id serial NOT NULL,
    ExperimentId int NOT NULL,
    SbName varchar(32) NOT NULL,
    UserGroup varchar(64) NOT NULL,
    PriorityHint int NOT NULL,
    EstimatedExecTime int NOT NULL,
    TimeSubmitted timestamp NOT NULL,
    QueueLength int NOT NULL,
    EstimatedWaitTime int NOT NULL,
    TimeStarted timestamp,
    UnitId int,
    TimeCompleted timestamp,
    Cancelled boolean DEFAULT false,

    PRIMARY KEY (Id),
    UNIQUE (ExperimentId, SbName)
);

/********************************************************************************************************************
*/

DROP TABLE IF EXISTS LabEquipment;

CREATE TABLE LabEquipment
(
    Id serial NOT NULL,
    ServiceType varchar(8) NOT NULL,
    ServiceUrl varchar(256) NOT NULL,
    Passkey varchar(40),
    Enabled boolean DEFAULT false,
    DateCreated timestamp DEFAULT current_timestamp,
    DateModified timestamp,

    PRIMARY KEY (Id),
    UNIQUE (ServiceUrl)
);

/********************************************************************************************************************
*/

DROP TABLE IF EXISTS LabServer;

CREATE TABLE LabServer
(
    Id serial NOT NULL,
    Name varchar(32) NOT NULL,
    Guid varchar(40) NOT NULL,
    ServiceUrl varchar(256),
    ContactEmail varchar(128),
    CompletedEmail varchar(256),
    FailedEmail varchar(256),
    Authenticate boolean DEFAULT true,
    DateCreated timestamp DEFAULT current_timestamp,
    DateModified timestamp,

    PRIMARY KEY (Id),
    UNIQUE (Name)
);

/********************************************************************************************************************
*/

DROP TABLE IF EXISTS ServiceBrokers;

CREATE TABLE ServiceBrokers
(
    Id serial NOT NULL,
    Name varchar(32) NOT NULL,
    Guid varchar(40) NOT NULL,
    OutPasskey varchar(40) NOT NULL,
    InPasskey varchar(40),
    ServiceUrl varchar(256),
    Permitted boolean DEFAULT false,
    DateCreated timestamp DEFAULT current_timestamp,
    DateModified timestamp,

    PRIMARY KEY(Id),
    UNIQUE(Name)
);

/********************************************************************************************************************
*/

DROP TABLE IF EXISTS Users;

CREATE TABLE Users
(
    UserId serial NOT NULL,
    Username varchar(32) NOT NULL,
    FirstName varchar(64) NOT NULL,
    LastName varchar(64) NOT NULL,
    ContactEmail varchar(128) NOT NULL,
    UserGroup varchar(64) NOT NULL,
    Password varchar(40) NOT NULL,
    AccountLocked boolean DEFAULT FALSE,
    DateCreated timestamp DEFAULT current_timestamp,
    DateModified timestamp,

    PRIMARY KEY(UserId),
    UNIQUE(Username)
);
