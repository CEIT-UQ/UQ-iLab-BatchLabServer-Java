/********************************************************************************************************************
*/

DROP TABLE IF EXISTS Experiments;

CREATE TABLE Experiments
(
    ExperimentId serial,
    LabServerGuid varchar(40) NOT NULL,

    PRIMARY KEY(ExperimentId)
);
