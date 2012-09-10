/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS Experiments_Add
(
    varchar
);

CREATE FUNCTION Experiments_Add
(
    LabServerGuid varchar(40)
)
RETURNS integer AS
$BODY$
    INSERT INTO Experiments (
        LabServerGuid
    )
    VALUES ($1)
    RETURNING ExperimentId;
$BODY$
    LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS Experiments_GetNextExperimentId
(
);

CREATE FUNCTION Experiments_GetNextExperimentId
(
)
RETURNS integer AS
$BODY$
    SELECT (max(ExperimentId) + 1) FROM Experiments
$BODY$
    LANGUAGE sql VOLATILE;

/*********************************************************************************************************************/

DROP FUNCTION IF EXISTS Experiments_RetrieveBy
(
    varchar, integer, varchar
);

CREATE FUNCTION Experiments_RetrieveBy
(
    ColumnName varchar,
    IntValue integer,
    StrValue varchar
)
RETURNS TABLE
(
    ExperimentId integer,
    LabServerGuid varchar
) AS
$BODY$
    SELECT * FROM Experiments
    WHERE
        CASE
            WHEN $1 IS NULL THEN
                TRUE
            WHEN $1 = 'experimentid' THEN
                ExperimentId = $2
            WHEN $1 = 'labserverguid' THEN
                LabServerGuid = $3
        END
    ORDER BY ExperimentId
$BODY$
    LANGUAGE sql VOLATILE;
