/* CREATE FUNCTION */

CREATE OR REPLACE FUNCTION fn_ethosCaseRefGen (numofcases INT, yr INT , office varchar(200)) RETURNS VARCHAR(10) AS $$

-- =============================================
-- Author:		Mohammed Hafejee

-- TEST :		SELECT fn_ethosCaseRefGen (2,2020,'Manchester');
--
-- Create date: 14-APR-2020
-- Description:	Function to generate Ethos case reference numbers for single cases
-- VERSION	:	14-MAR-2020		1.0  - Initial
-- =============================================


  DECLARE currentval integer;
  DECLARE currentyr varchar(10);
  DECLARE currentvalstr varchar(20);


BEGIN

CASE

/***********   1. Manchester   ************/

    WHEN office = 'Manchester' THEN

    -- Acquire Lock on singleReferenceManchester table

SELECT counter, cyear INTO currentval,currentyr FROM singleReferenceManchester FOR UPDATE ;


CASE

    WHEN currentyr <> yr::text AND RIGHT(currentyr, 2) <> RIGHT(yr::text, 2) THEN
UPDATE  singleReferenceManchester SET counter = numofcases, cyear = yr ;
currentval := 0;
    currentyr = yr;


WHEN (currentval + numofcases) > 99999  THEN
UPDATE  singleReferenceManchester SET counter = (numofcases + currentval) - 99999,
                                      cyear = RIGHT(currentyr, 2);

IF (currentval + 1)  > 99999 THEN
                currentval := 0;
                currentyr = CONCAT('00',RIGHT(currentyr, 2));
END IF;
ELSE

UPDATE  singleReferenceManchester SET counter = counter + numofcases ;

END CASE;


    currentval = currentval + 1 ;
    currentvalstr = RIGHT(CONCAT ('00000', currentval) ,5);

    currentyr =  RIGHT(CONCAT('00',currentyr),4);

    currentvalstr = CONCAT(currentvalstr,'/',currentyr);

RETURN  currentvalstr;


/***********   2. Scotland   ************/

WHEN office = 'Scotland' THEN

    -- Acquire Lock on singleReferenceScotland table

SELECT counter, cyear INTO currentval,currentyr FROM singleReferenceScotland FOR UPDATE ;


CASE

    WHEN currentyr <> yr::text AND RIGHT(currentyr, 2) <> RIGHT(yr::text, 2) THEN
UPDATE  singleReferenceScotland SET counter = numofcases, cyear = yr ;
currentval := 0;
    currentyr = yr;


WHEN (currentval + numofcases) > 99999  THEN
UPDATE  singleReferenceScotland SET counter = (numofcases + currentval) - 99999,
                                    cyear = RIGHT(currentyr, 2);

IF (currentval + 1)  > 99999 THEN
                currentval := 0;
                currentyr = CONCAT('00',RIGHT(currentyr, 2));
END IF;
ELSE

UPDATE  singleReferenceScotland SET counter = counter + numofcases ;

END CASE;


    currentval = currentval + 1 ;
    currentvalstr = RIGHT(CONCAT ('00000', currentval) ,5);

    currentyr =  RIGHT(CONCAT('00',currentyr),4);

    currentvalstr = CONCAT(currentvalstr,'/',currentyr);

RETURN  currentvalstr;



/***********   3. Bristol   ************/

WHEN office = 'Bristol' THEN

    -- Acquire Lock on singleReferenceBristol table

SELECT counter, cyear INTO currentval,currentyr FROM singleReferenceBristol FOR UPDATE ;


CASE

    WHEN currentyr <> yr::text AND RIGHT(currentyr, 2) <> RIGHT(yr::text, 2) THEN
UPDATE  singleReferenceBristol SET counter = numofcases, cyear = yr ;
currentval := 0;
    currentyr = yr;


WHEN (currentval + numofcases) > 99999  THEN
UPDATE  singleReferenceBristol SET counter = (numofcases + currentval) - 99999,
                                   cyear = RIGHT(currentyr, 2);

IF (currentval + 1)  > 99999 THEN
                currentval := 0;
                currentyr = CONCAT('00',RIGHT(currentyr, 2));
END IF;
ELSE

UPDATE  singleReferenceBristol SET counter = counter + numofcases ;

END CASE;


    currentval = currentval + 1 ;
    currentvalstr = RIGHT(CONCAT ('00000', currentval) ,5);

    currentyr =  RIGHT(CONCAT('00',currentyr),4);

    currentvalstr = CONCAT(currentvalstr,'/',currentyr);

RETURN  currentvalstr;

/***********   4. Leeds   ************/

WHEN office = 'Leeds' THEN

    -- Acquire Lock on singleReferenceLeeds table

SELECT counter, cyear INTO currentval,currentyr FROM singleReferenceLeeds FOR UPDATE ;


CASE

    WHEN currentyr <> yr::text AND RIGHT(currentyr, 2) <> RIGHT(yr::text, 2) THEN
UPDATE  singleReferenceLeeds SET counter = numofcases, cyear = yr ;
currentval := 0;
    currentyr = yr;


WHEN (currentval + numofcases) > 99999  THEN
UPDATE  singleReferenceLeeds SET counter = (numofcases + currentval) - 99999,
                                 cyear = RIGHT(currentyr, 2);

IF (currentval + 1)  > 99999 THEN
                currentval := 0;
                currentyr = CONCAT('00',RIGHT(currentyr, 2));
END IF;
ELSE

UPDATE  singleReferenceLeeds SET counter = counter + numofcases ;

END CASE;


    currentval = currentval + 1 ;
    currentvalstr = RIGHT(CONCAT ('00000', currentval) ,5);

    currentyr =  RIGHT(CONCAT('00',currentyr),4);

    currentvalstr = CONCAT(currentvalstr,'/',currentyr);

RETURN  currentvalstr;


/***********   5. LondonCentral   ************/

WHEN office = 'LondonCentral' THEN

    -- Acquire Lock on singleReferenceLondonCentral table

SELECT counter, cyear INTO currentval,currentyr FROM singleReferenceLondonCentral FOR UPDATE ;


CASE

    WHEN currentyr <> yr::text AND RIGHT(currentyr, 2) <> RIGHT(yr::text, 2) THEN
UPDATE  singleReferenceLondonCentral SET counter = numofcases, cyear = yr ;
currentval := 0;
    currentyr = yr;


WHEN (currentval + numofcases) > 99999  THEN
UPDATE  singleReferenceLondonCentral SET counter = (numofcases + currentval) - 99999,
                                         cyear = RIGHT(currentyr, 2);

IF (currentval + 1)  > 99999 THEN
                currentval := 0;
                currentyr = CONCAT('00',RIGHT(currentyr, 2));
END IF;
ELSE

UPDATE  singleReferenceLondonCentral SET counter = counter + numofcases ;

END CASE;


    currentval = currentval + 1 ;
    currentvalstr = RIGHT(CONCAT ('00000', currentval) ,5);

    currentyr =  RIGHT(CONCAT('00',currentyr),4);

    currentvalstr = CONCAT(currentvalstr,'/',currentyr);

RETURN  currentvalstr;


/***********   6. LondonEast   ************/

WHEN office = 'LondonEast' THEN

    -- Acquire Lock on singleReferenceLondonEast table

SELECT counter, cyear INTO currentval,currentyr FROM singleReferenceLondonEast FOR UPDATE ;


CASE

    WHEN currentyr <> yr::text AND RIGHT(currentyr, 2) <> RIGHT(yr::text, 2) THEN
UPDATE  singleReferenceLondonEast SET counter = numofcases, cyear = yr ;
currentval := 0;
    currentyr = yr;


WHEN (currentval + numofcases) > 99999  THEN
UPDATE  singleReferenceLondonEast SET counter = (numofcases + currentval) - 99999,
                                      cyear = RIGHT(currentyr, 2);

IF (currentval + 1)  > 99999 THEN
                currentval := 0;
                currentyr = CONCAT('00',RIGHT(currentyr, 2));
END IF;
ELSE

UPDATE  singleReferenceLondonEast SET counter = counter + numofcases ;

END CASE;


    currentval = currentval + 1 ;
    currentvalstr = RIGHT(CONCAT ('00000', currentval) ,5);

    currentyr =  RIGHT(CONCAT('00',currentyr),4);

    currentvalstr = CONCAT(currentvalstr,'/',currentyr);

RETURN  currentvalstr;


/***********   7. LondonSouth   ************/

WHEN office = 'LondonSouth' THEN

    -- Acquire Lock on singleReferenceLondonSouth table

SELECT counter, cyear INTO currentval,currentyr FROM singleReferenceLondonSouth FOR UPDATE ;


CASE

    WHEN currentyr <> yr::text AND RIGHT(currentyr, 2) <> RIGHT(yr::text, 2) THEN
UPDATE  singleReferenceLondonSouth SET counter = numofcases, cyear = yr ;
currentval := 0;
    currentyr = yr;


WHEN (currentval + numofcases) > 99999  THEN
UPDATE  singleReferenceLondonSouth SET counter = (numofcases + currentval) - 99999,
                                       cyear = RIGHT(currentyr, 2);

IF (currentval + 1)  > 99999 THEN
                currentval := 0;
                currentyr = CONCAT('00',RIGHT(currentyr, 2));
END IF;
ELSE

UPDATE  singleReferenceLondonSouth SET counter = counter + numofcases ;

END CASE;


    currentval = currentval + 1 ;
    currentvalstr = RIGHT(CONCAT ('00000', currentval) ,5);

    currentyr =  RIGHT(CONCAT('00',currentyr),4);

    currentvalstr = CONCAT(currentvalstr,'/',currentyr);

RETURN  currentvalstr;


/***********   8. MidlandsEast   ************/

WHEN office = 'MidlandsEast' THEN

    -- Acquire Lock on singleReferenceMidlandsEast table

SELECT counter, cyear INTO currentval,currentyr FROM singleReferenceMidlandsEast FOR UPDATE ;


CASE

    WHEN currentyr <> yr::text AND RIGHT(currentyr, 2) <> RIGHT(yr::text, 2) THEN
UPDATE  singleReferenceMidlandsEast SET counter = numofcases, cyear = yr ;
currentval := 0;
    currentyr = yr;


WHEN (currentval + numofcases) > 99999  THEN
UPDATE  singleReferenceMidlandsEast SET counter = (numofcases + currentval) - 99999,
                                        cyear = RIGHT(currentyr, 2);

IF (currentval + 1)  > 99999 THEN
                currentval := 0;
                currentyr = CONCAT('00',RIGHT(currentyr, 2));
END IF;
ELSE

UPDATE  singleReferenceMidlandsEast SET counter = counter + numofcases ;

END CASE;


    currentval = currentval + 1 ;
    currentvalstr = RIGHT(CONCAT ('00000', currentval) ,5);

    currentyr =  RIGHT(CONCAT('00',currentyr),4);

    currentvalstr = CONCAT(currentvalstr,'/',currentyr);

RETURN  currentvalstr;


/***********   9. MidlandsWest   ************/

WHEN office = 'MidlandsWest' THEN

    -- Acquire Lock on singleReferenceMidlandsWest table

SELECT counter, cyear INTO currentval,currentyr FROM singleReferenceMidlandsWest FOR UPDATE ;


CASE

    WHEN currentyr <> yr::text AND RIGHT(currentyr, 2) <> RIGHT(yr::text, 2) THEN
UPDATE  singleReferenceMidlandsWest SET counter = numofcases, cyear = yr ;
currentval := 0;
    currentyr = yr;


WHEN (currentval + numofcases) > 99999  THEN
UPDATE  singleReferenceMidlandsWest SET counter = (numofcases + currentval) - 99999,
                                        cyear = RIGHT(currentyr, 2);

IF (currentval + 1)  > 99999 THEN
                currentval := 0;
                currentyr = CONCAT('00',RIGHT(currentyr, 2));
END IF;
ELSE

UPDATE  singleReferenceMidlandsWest SET counter = counter + numofcases ;

END CASE;


    currentval = currentval + 1 ;
    currentvalstr = RIGHT(CONCAT ('00000', currentval) ,5);

    currentyr =  RIGHT(CONCAT('00',currentyr),4);

    currentvalstr = CONCAT(currentvalstr,'/',currentyr);

RETURN  currentvalstr;


/***********   10. Newcastle   ************/

WHEN office = 'Newcastle' THEN

    -- Acquire Lock on singleReferenceNewcastle table

SELECT counter, cyear INTO currentval,currentyr FROM singleReferenceNewcastle FOR UPDATE ;


CASE

    WHEN currentyr <> yr::text AND RIGHT(currentyr, 2) <> RIGHT(yr::text, 2) THEN
UPDATE  singleReferenceNewcastle SET counter = numofcases, cyear = yr ;
currentval := 0;
    currentyr = yr;


WHEN (currentval + numofcases) > 99999  THEN
UPDATE  singleReferenceNewcastle SET counter = (numofcases + currentval) - 99999,
                                     cyear = RIGHT(currentyr, 2);

IF (currentval + 1)  > 99999 THEN
                currentval := 0;
                currentyr = CONCAT('00',RIGHT(currentyr, 2));
END IF;
ELSE

UPDATE  singleReferenceNewcastle SET counter = counter + numofcases ;

END CASE;


    currentval = currentval + 1 ;
    currentvalstr = RIGHT(CONCAT ('00000', currentval) ,5);

    currentyr =  RIGHT(CONCAT('00',currentyr),4);

    currentvalstr = CONCAT(currentvalstr,'/',currentyr);

RETURN  currentvalstr;


/***********   11. Wales   ************/

WHEN office = 'Wales' THEN

    -- Acquire Lock on singleReferenceWales table

SELECT counter, cyear INTO currentval,currentyr FROM singleReferenceWales FOR UPDATE ;


CASE

    WHEN currentyr <> yr::text AND RIGHT(currentyr, 2) <> RIGHT(yr::text, 2) THEN
UPDATE  singleReferenceWales SET counter = numofcases, cyear = yr ;
currentval := 0;
    currentyr = yr;


WHEN (currentval + numofcases) > 99999  THEN
UPDATE  singleReferenceWales SET counter = (numofcases + currentval) - 99999,
                                 cyear = RIGHT(currentyr, 2);

IF (currentval + 1)  > 99999 THEN
                currentval := 0;
                currentyr = CONCAT('00',RIGHT(currentyr, 2));
END IF;
ELSE

UPDATE  singleReferenceWales SET counter = counter + numofcases ;

END CASE;


    currentval = currentval + 1 ;
    currentvalstr = RIGHT(CONCAT ('00000', currentval) ,5);

    currentyr =  RIGHT(CONCAT('00',currentyr),4);

    currentvalstr = CONCAT(currentvalstr,'/',currentyr);

RETURN  currentvalstr;


/***********  12. Watford   ************/

WHEN office = 'Watford' THEN

    -- Acquire Lock on singleReferenceWatford table

SELECT counter, cyear INTO currentval,currentyr FROM singleReferenceWatford FOR UPDATE ;


CASE

    WHEN currentyr <> yr::text AND RIGHT(currentyr, 2) <> RIGHT(yr::text, 2) THEN
UPDATE  singleReferenceWatford SET counter = numofcases, cyear = yr ;
currentval := 0;
    currentyr = yr;


WHEN (currentval + numofcases) > 99999  THEN
UPDATE  singleReferenceWatford SET counter = (numofcases + currentval) - 99999,
                                   cyear = RIGHT(currentyr, 2);

IF (currentval + 1)  > 99999 THEN
                currentval := 0;
                currentyr = CONCAT('00',RIGHT(currentyr, 2));
END IF;
ELSE

UPDATE  singleReferenceWatford SET counter = counter + numofcases ;

END CASE;


    currentval = currentval + 1 ;
    currentvalstr = RIGHT(CONCAT ('00000', currentval) ,5);

    currentyr =  RIGHT(CONCAT('00',currentyr),4);

    currentvalstr = CONCAT(currentvalstr,'/',currentyr);

RETURN  currentvalstr;




END CASE ;

END;
   $$ LANGUAGE plpgsql;

