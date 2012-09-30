/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS Queue_Add
(
    integer, varchar, varchar, integer, varchar, integer, varchar
);

CREATE FUNCTION Queue_Add
(
    ExperimentId integer,
    SbName varchar,
    UserGroup varchar,
    PriorityHint integer,
    XmlSpecification varchar,
    EstExecutionTime integer,
    StatusCode varchar
)
RETURNS integer AS
$BODY$
    INSERT INTO Queue (
        ExperimentId,
        SbName,
        UserGroup,
        PriorityHint,
        XmlSpecification,
        EstExecutionTime,
        StatusCode,
        DateCreated
    )
    VALUES (
        $1, $2, $3, $4, $5, $6, $7, current_timestamp
    )
    RETURNING Id;
$BODY$
    LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS Queue_GetCountBy
(
    varchar, integer, varchar
);

CREATE FUNCTION Queue_GetCountBy
(
    ColumnName varchar,
    IntValue integer,
    StrValue varchar
)
RETURNS bigint AS
$BODY$
    SELECT COUNT(*) FROM Queue
    WHERE
        CASE
            WHEN $1 IS NULL THEN
                TRUE
            WHEN $1 = 'statuscode' THEN
                StatusCode = $3
        END
$BODY$
    LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS Queue_RetrieveBy
(
    varchar, integer, varchar
);

CREATE FUNCTION Queue_RetrieveBy
(
    ColumnName varchar,
    IntValue integer,
    StrValue varchar
)
RETURNS TABLE
(
    Id integer,
    ExperimentId integer,
    SbName varchar,
    UserGroup varchar,
    PriorityHint integer,
    XmlSpecification varchar,
    EstExecutionTime integer,
    StatusCode varchar,
    UnitId integer,
    Cancelled boolean,
    DateCreated timestamp
) AS
$BODY$
    SELECT * FROM Queue
    WHERE
        CASE
            WHEN $1 IS NULL THEN
                TRUE
            WHEN $1 = 'id' THEN
                Id = $2
            WHEN $1 = 'experimentid' THEN
                ExperimentId = $2 AND SbName = $3
            WHEN $1 = 'statuscode' THEN
                StatusCode = $3
        END
    ORDER BY PriorityHint DESC
$BODY$
    LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS Queue_UpdateStatus
(
    integer, varchar
);

CREATE FUNCTION Queue_UpdateStatus
(
    Id integer,
    StatusCode varchar
)
RETURNS void AS
$BODY$
    UPDATE Queue SET StatusCode = $2
    WHERE Id = $1;
$BODY$
    LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS Queue_UpdateStatusUnitId
(
    integer, varchar, integer
);

CREATE FUNCTION Queue_UpdateStatusUnitId
(
    Id integer,
    StatusCode varchar,
    UnitId integer
)
RETURNS void AS
$BODY$
    UPDATE Queue SET StatusCode = $2, UnitId = $3
    WHERE Id = $1;
$BODY$
    LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS Statistics_Add
(
    integer, varchar, varchar, integer, integer, integer, integer
);

CREATE FUNCTION Statistics_Add
(
    ExperimentId integer,
    SbName varchar,
    UserGroup varchar,
    PriorityHint integer,
    EstimatedExecTime integer,
    QueueLength integer,
    EstimatedWaitTime integer
)
RETURNS integer AS
$BODY$
    INSERT INTO Statistics (
        ExperimentId,
        SbName,
        UserGroup,
        PriorityHint,
        EstimatedExecTime,
        QueueLength,
        EstimatedWaitTime,
        TimeSubmitted
    )
    VALUES (
        $1, $2, $3, $4, $5, $6, $7, current_timestamp
    )
    RETURNING Id;
$BODY$
    LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS Statistics_Delete
(
    integer
);

CREATE FUNCTION Statistics_Delete
(
    Id integer
)
RETURNS integer AS
$BODY$
    DELETE FROM Statistics
    WHERE Id = $1
    RETURNING Id;
$BODY$
  LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS Statistics_RetrieveBy
(
    varchar, integer, varchar
);

CREATE FUNCTION Statistics_RetrieveBy
(
    ColumnName varchar,
    IntValue integer,
    StrValue varchar
)
RETURNS TABLE
(
    Id integer,
    ExperimentId integer,
    SbName varchar,
    UserGroup varchar,
    PriorityHint integer,
    EstimatedExecTime integer,
    TimeSubmitted timestamp,
    QueueLength integer,
    EstimatedWaitTime integer,
    TimeStarted timestamp,
    UnitId integer,
    TimeCompleted timestamp,
    Cancelled boolean
) AS
$BODY$
    SELECT * FROM Statistics
    WHERE
        CASE
            WHEN $1 IS NULL THEN
                TRUE
            WHEN $1 = 'id' THEN
                Id = $2
            WHEN $1 = 'experimentid' THEN
                ExperimentId = $2 AND SbName = $3
            WHEN $1 = 'usergroup' THEN
                UserGroup = $3
        END
    ORDER BY Id ASC
$BODY$
    LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS Statistics_UpdateCancelled
(
    integer, varchar
);

CREATE FUNCTION Statistics_UpdateCancelled
(
    ExperimentId integer,
    SbName varchar
)
RETURNS void AS
$BODY$
    UPDATE Statistics
    SET Cancelled = true, TimeCompleted = current_timestamp
    WHERE ExperimentId = $1 AND SbName = $2
$BODY$
    LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS Statistics_UpdateCompleted
(
    integer, varchar
);

CREATE FUNCTION Statistics_UpdateCompleted
(
    ExperimentId integer,
    SbName varchar
)
RETURNS void AS
$BODY$
    UPDATE Statistics
    SET TimeCompleted = current_timestamp
    WHERE ExperimentId = $1 AND SbName = $2
$BODY$
    LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS Statistics_UpdateStarted
(
    integer, varchar, integer
);

CREATE OR REPLACE FUNCTION Statistics_UpdateStarted
(
    ExperimentId integer,
    SbName varchar,
    UnitId integer
)
RETURNS void AS
$BODY$
    UPDATE Statistics
    SET UnitId = $3, TimeStarted = current_timestamp
    WHERE ExperimentId = $1 AND SbName = $2
$BODY$
    LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS Results_Add
(
    integer, varchar, varchar, int, varchar, varchar, varchar, varchar, varchar, varchar
);

CREATE FUNCTION Results_Add
(
    ExperimentId integer,
    SbName varchar,
    UserGroup varchar,
    PriorityHint int,
    StatusCode varchar,
    XmlExperimentResult varchar,
    XmlResultExtension varchar,
    XmlBlobExtension varchar,
    WarningMessages varchar,
    ErrorMessage varchar
)
RETURNS integer AS
$BODY$
    INSERT INTO Results
    (
        ExperimentId,
        SbName,
        UserGroup,
        PriorityHint,
        StatusCode,
        XmlExperimentResult,
        XmlResultExtension,
        XmlBlobExtension,
        WarningMessages,
        ErrorMessage,
        DateCreated
    )
    VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, current_timestamp)
    RETURNING Id;
$BODY$
    LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS Results_Delete
(
    integer
);

CREATE FUNCTION Results_Delete
(
    Id integer
)
RETURNS integer AS
$BODY$
    DELETE FROM Results
    WHERE Id = $1
    RETURNING Id;
$BODY$
  LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS Results_RetrieveBy
(
    varchar, integer, varchar
);

CREATE FUNCTION Results_RetrieveBy
(
    ColumnName varchar,
    IntValue integer,
    StrValue varchar
)
RETURNS TABLE(
    Id integer,
    ExperimentId integer,
    SbName varchar,
    UserGroup varchar,
    PriorityHint integer,
    StatusCode varchar,
    XmlExperimentResult varchar,
    XmlResultExtension varchar,
    XmlBlobExtension varchar,
    WarningMessages varchar,
    ErrorMessage varchar,
    Notified boolean,
    DateCreated timestamp
) AS
$BODY$
    SELECT * FROM Results
    WHERE
        CASE
            WHEN $1 IS NULL THEN
                TRUE
            WHEN $1 = 'id' THEN
                Id = $2
            WHEN $1 = 'experimentid' THEN
                ExperimentId = $2 AND SbName = $3
            WHEN $1 = 'notified' THEN
                Notified = false
        END
$BODY$
    LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS Results_RetrieveAllNotNotified
(
);

CREATE FUNCTION Results_RetrieveAllNotNotified
(
)
RETURNS TABLE(
    ExperimentId int,
    SbName varchar
) AS
$BODY$
    SELECT (ExperimentId, SbName)
    FROM Results
    WHERE Notified = false
$BODY$
    LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS Results_UpdateNotified
(
    integer, varchar
);

CREATE FUNCTION Results_UpdateNotified
(
    ExperimentId integer,
    SbName varchar
)
RETURNS void AS
$BODY$
    UPDATE Results SET Notified = true
    WHERE ExperimentId = $1 AND SbName = $2
$BODY$
    LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS ServiceBrokers_Add
(
    varchar, varchar, varchar, varchar, varchar, boolean
);

CREATE FUNCTION ServiceBrokers_Add
(
    Name varchar,
    Guid varchar,
    OutPasskey varchar,
    InPasskey varchar,
    ServiceUrl varchar,
    Permitted boolean
)
RETURNS integer AS
$BODY$
    INSERT INTO ServiceBrokers (
        Name,
        Guid,
        OutPasskey,
        InPasskey,
        ServiceUrl,
        Permitted,
        DateCreated
    )
    VALUES (
        $1, $2, $3, $4, $5, $6, current_timestamp
    )
    RETURNING Id;
$BODY$
    LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS ServiceBrokers_Delete
(
    integer
);

CREATE FUNCTION ServiceBrokers_Delete
(
    Id integer
)
RETURNS integer AS
$BODY$
    DELETE FROM ServiceBrokers
    WHERE Id = $1
    RETURNING Id;
$BODY$
  LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS ServiceBrokers_GetList
(
    varchar, varchar
);

CREATE FUNCTION ServiceBrokers_GetList
(
    ColumnName varchar,
    StrValue varchar
)
RETURNS TABLE
(
    Name varchar
) AS
$BODY$
    SELECT Name FROM ServiceBrokers
    WHERE
        CASE
            WHEN $1 = 'name' THEN
                TRUE
        END
    ORDER BY
        CASE
            WHEN $1 = 'name' THEN
                Name
        END
    ASC
$BODY$
    LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS ServiceBrokers_RetrieveBy
(
    varchar, integer, varchar
);

CREATE FUNCTION ServiceBrokers_RetrieveBy
(
    ColumnName varchar,
    IntValue integer,
    StrValue varchar
)
RETURNS TABLE
(
    Id integer,
    Name varchar,
    Guid varchar,
    OutPasskey varchar,
    InPasskey varchar,
    ServiceUrl varchar,
    Permitted boolean,
    DateCreated timestamp,
    DateModified timestamp
) AS
$BODY$
    SELECT * FROM ServiceBrokers
    WHERE
        CASE
            WHEN $1 IS NULL THEN
                TRUE
            WHEN $1 = 'id' THEN
                Id = $2
            WHEN $1 = 'name' THEN
                Name = $3
            WHEN $1 = 'guid' THEN
                Guid = $3
        END
    ORDER BY Name ASC
$BODY$
    LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS ServiceBrokers_Update
(
    integer, varchar, varchar, varchar, varchar, varchar, boolean
);

CREATE FUNCTION ServiceBrokers_Update
(
    Id integer,
    Name varchar,
    Guid varchar,
    OutPasskey varchar,
    InPasskey varchar,
    ServiceUrl varchar,
    Permitted boolean
)
RETURNS integer AS
$BODY$
    UPDATE ServiceBrokers SET (
    Name,
    Guid,
    OutPasskey,
    InPasskey,
    ServiceUrl,
    Permitted,
    DateModified
    )
    = ($2, $3, $4, $5, $6, $7, current_timestamp)
    WHERE Id = $1
    RETURNING Id;
$BODY$
    LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS Users_Add
(
    varchar, varchar, varchar, varchar, varchar, varchar
);

CREATE FUNCTION Users_Add
(
    Username varchar(32),
    FirstName varchar(64),
    LastName varchar(64),
    ContactEmail varchar(128),
    UserGroup varchar(32),
    Password varchar(40)
)
RETURNS integer AS
$BODY$
    INSERT INTO Users (
        Username,
        FirstName,
        LastName,
        ContactEmail,
        UserGroup,
        Password
    )
    VALUES (
        $1, $2, $3, $4, $5, $6
    )
    RETURNING UserId;
$BODY$
    LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS Users_Delete
(
    integer
);

CREATE FUNCTION Users_Delete
(
    UserId integer
)
RETURNS integer AS
$BODY$
    DELETE FROM Users
    WHERE UserId = $1
    RETURNING UserId;
$BODY$
  LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS Users_GetList
(
    varchar, varchar
);

CREATE FUNCTION Users_GetList
(
    ColumnName varchar,
    StrValue varchar
)
RETURNS TABLE
(
    Username varchar
) AS
$BODY$
    SELECT Username FROM Users
    WHERE
        CASE
            WHEN $1 = 'username' THEN
                TRUE
            WHEN $1 = 'usergroup' THEN
                UserGroup = $2
        END
    ORDER BY Username ASC
$BODY$
    LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS Users_GetRecordCount
(
);

CREATE FUNCTION Users_GetRecordCount
(
)
RETURNS bigint AS
$BODY$
    SELECT COUNT(*) FROM Users
$BODY$
    LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS Users_RetrieveBy
(
    varchar, integer, varchar
);

CREATE FUNCTION Users_RetrieveBy
(
    ColumnName varchar,
    IntValue integer,
    StrValue varchar
)
RETURNS TABLE
(
    UserId integer,
    Username varchar,
    FirstName varchar,
    LastName varchar,
    ContactEmail varchar,
    UserGroup varchar,
    Password varchar,
    AccountLocked boolean,
    DateCreated timestamp,
    DateModified timestamp
) AS
$BODY$
    SELECT * FROM Users
    WHERE
        CASE
            WHEN $1 = 'userid' THEN
                UserId = $2
            WHEN $1 = 'username' THEN
                Username = $3
            WHEN $1 = 'usergroup' THEN
                UserGroup = $3
        END
    ORDER BY UserId ASC
$BODY$
    LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS Users_Update
(
    integer, varchar, varchar, varchar, varchar, varchar, boolean
);

CREATE FUNCTION Users_Update
(
    UserId integer,
    FirstName varchar(64),
    LastName varchar(64),
    ContactEmail varchar(128),
    UserGroup varchar(32),
    Password varchar(40),
    Locked boolean
)
RETURNS integer AS
$BODY$
    UPDATE Users SET (
        FirstName,
        LastName,
        ContactEmail,
        UserGroup,
        Password,
        AccountLocked,
        DateModified
    )
    = ($2, $3, $4, $5, $6, $7, current_timestamp)
    WHERE UserId = $1
    RETURNING UserId;
$BODY$
    LANGUAGE sql VOLATILE;
