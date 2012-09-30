/*********************************************************************************************************************/

DROP TABLE IF EXISTS Queue;

CREATE TABLE Queue
(
    Id serial NOT NULL,
    ExperimentId integer NOT NULL,
    SbName varchar(32) NOT NULL,
    UserGroup varchar(64) NOT NULL,
    PriorityHint integer NOT NULL,
    XmlSpecification varchar NOT NULL,
    EstExecutionTime integer NOT NULL,
    StatusCode varchar(16) NOT NULL,
    UnitId integer DEFAULT -1,
    Cancelled boolean DEFAULT false,
    DateCreated timestamp,

    PRIMARY KEY(ExperimentId, SbName)
);

/*********************************************************************************************************************/

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

    PRIMARY KEY(ExperimentId, SbName)
);

/*********************************************************************************************************************/

DROP TABLE IF EXISTS Results;

CREATE TABLE Results
(
    Id serial NOT NULL,
    ExperimentId int NOT NULL,
    SbName varchar(32) NOT NULL,
    UserGroup varchar(64) NOT NULL,
    PriorityHint int NOT NULL,
    StatusCode varchar(16) NOT NULL,
    XmlExperimentResult varchar NULL,
    XmlResultExtension varchar(2048) NULL,
    XmlBlobExtension varchar(2048) NULL,
    WarningMessages varchar(2048) NULL,
    ErrorMessage varchar(2048) NULL,
    Notified boolean DEFAULT false,
    DateCreated timestamp,

    PRIMARY KEY(ExperimentId, SbName)
);

/*********************************************************************************************************************/

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
    DateCreated timestamp,
    DateModified timestamp,

    PRIMARY KEY(Name)
);

/*********************************************************************************************************************/

DROP TABLE IF EXISTS Users;

CREATE TABLE Users
(
    UserId serial NOT NULL,
    Username varchar(32) NOT NULL,
    FirstName varchar(64) NOT NULL,
    LastName varchar(64) NOT NULL,
    ContactEmail varchar(128) NOT NULL,
    UserGroup varchar(32) NOT NULL,
    Password varchar(40) NOT NULL,
    AccountLocked boolean DEFAULT FALSE,
    DateCreated timestamp DEFAULT current_timestamp,
    DateModified timestamp,

    PRIMARY KEY(Username)
);
