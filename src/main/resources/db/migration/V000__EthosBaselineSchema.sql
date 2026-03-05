-- =====================================================
-- Author: HMCTS Reform
-- Migration: V000__EthosBaselineSchema
-- Created: 2026-03-04
-- Description: Baseline schema for existing ethos-postgres structures.
--              This migration builds the legacy reference tables and functions
--              used by ethos-repl-docmosis-service when starting from an empty DB.
-- =====================================================

DO $$
DECLARE
    office VARCHAR(64);
    offices VARCHAR(64)[] := ARRAY[
        'Manchester', 'Scotland', 'Bristol', 'Leeds',
        'LondonCentral', 'LondonEast', 'LondonSouth',
        'MidlandsEast', 'MidlandsWest', 'Newcastle',
        'Wales', 'Watford'
    ];
BEGIN
    FOREACH office IN ARRAY offices LOOP
        EXECUTE format('CREATE TABLE IF NOT EXISTS singleReference%s ('
                       || 'counter INT, '
                       || 'cyear VARCHAR(10)'
                       || ')', office);

        EXECUTE format('INSERT INTO singleReference%s (counter, cyear) '
                       || 'SELECT 0, EXTRACT(YEAR FROM CURRENT_DATE)::VARCHAR '
                       || 'WHERE NOT EXISTS (SELECT 1 FROM singleReference%s)',
                       office, office);

        EXECUTE format('CREATE TABLE IF NOT EXISTS multipleReference%s ('
                       || 'counter INT'
                       || ')', office);

        EXECUTE format('INSERT INTO multipleReference%s (counter) '
                       || 'SELECT 0 '
                       || 'WHERE NOT EXISTS (SELECT 1 FROM multipleReference%s)',
                       office, office);

        EXECUTE format('CREATE TABLE IF NOT EXISTS subMultipleReference%s ('
                       || 'multref INT, '
                       || 'submultref INT'
                       || ')', office);

        EXECUTE format('CREATE INDEX IF NOT EXISTS IX_subMultipleReference%s_multref '
                       || 'ON subMultipleReference%s (multref)',
                       office, office);
    END LOOP;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'emp_status') THEN
        CREATE TYPE emp_status AS ENUM ('SALARIED', 'FEE_PAID', 'UNKNOWN');
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS judge (
    id SERIAL PRIMARY KEY,
    tribunal_office VARCHAR(100),
    code VARCHAR(100),
    name VARCHAR(100),
    employment_status emp_status
);

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


/* CREATE FUNCTION */

CREATE OR REPLACE FUNCTION fn_ethosMultipleCaseRefGen (numofcases INT, office varchar(200)) RETURNS VARCHAR(200) AS $$

-- =============================================
-- Author:		Mohammed Hafejee

-- TEST :		SELECT fn_ethosMultipleCaseRefGen(2,'Manchester');
--				
-- Create date: 14-APR-2020
-- Description:	Function to generate Ethos case reference numbers for multiple cases
-- VERSION	:	14-MAR-2020		1.0  - Initial
-- =============================================


  DECLARE currentval Integer;
  DECLARE currentvalstr Varchar(200);


BEGIN 

  CASE 

/***********   1. Manchester   ************/  

    WHEN office = 'Manchester' THEN 

    -- Acquire Lock on multipleReferenceManchester table 

    SELECT counter INTO currentval FROM multipleReferenceManchester FOR UPDATE ;
    
      
    CASE 
    
    WHEN currentval = 99999 OR (currentval + numofcases) > 99999 THEN    
   
    currentval = NULL;

    ELSE  
    
    UPDATE  multipleReferenceManchester SET counter = counter + numofcases ;

    END CASE;
        
  
   
    IF currentval IS NOT NULL THEN

    currentval = currentval + 1 ;
    currentvalstr = RIGHT(CONCAT ('00000', currentval) ,5);

    ELSE 
    currentvalstr = CONCAT ('Exception - Not enough multiple reference numbers available in index multipleReference', Office, ' to service requests');

    END IF;


    RETURN  currentvalstr;


/***********   2. Scotland   ************/

   WHEN office = 'Scotland' THEN 

    -- Acquire Lock on multipleReferenceScotland table 

    SELECT counter INTO currentval FROM multipleReferenceScotland FOR UPDATE ;
    
      
    CASE 
    
    WHEN currentval = 99999 OR (currentval + numofcases) > 99999 THEN    
   
    currentval = NULL;

    ELSE  
    
    UPDATE  multipleReferenceScotland SET counter = counter + numofcases ;

    END CASE;
        
  
   
    IF currentval IS NOT NULL THEN

    currentval = currentval + 1 ;
    currentvalstr = RIGHT(CONCAT ('00000', currentval) ,5);

    ELSE 
    currentvalstr = CONCAT ('Exception - Not enough multiple reference numbers available in index multipleReference', Office, ' to service requests');

    END IF;


    RETURN  currentvalstr;



/***********   3. Bristol   ************/

   WHEN office = 'Bristol' THEN 

    -- Acquire Lock on multipleReferenceBristol table 

    SELECT counter INTO currentval FROM multipleReferenceBristol FOR UPDATE ;
    
      
    CASE 
    
    WHEN currentval = 99999 OR (currentval + numofcases) > 99999 THEN    
   
    currentval = NULL;

    ELSE  
    
    UPDATE  multipleReferenceBristol SET counter = counter + numofcases ;

    END CASE;
        
  
   
    IF currentval IS NOT NULL THEN

    currentval = currentval + 1 ;
    currentvalstr = RIGHT(CONCAT ('00000', currentval) ,5);

    ELSE 
    currentvalstr = CONCAT ('Exception - Not enough multiple reference numbers available in index multipleReference', Office, ' to service requests');

    END IF;


    RETURN  currentvalstr;





/***********   4. Leeds   ************/

    WHEN office = 'Leeds' THEN 

    -- Acquire Lock on multipleReferenceLeeds table 

    SELECT counter INTO currentval FROM multipleReferenceLeeds FOR UPDATE ;
    
      
    CASE 
    
    WHEN currentval = 99999 OR (currentval + numofcases) > 99999 THEN    
   
    currentval = NULL;

    ELSE  
    
    UPDATE  multipleReferenceLeeds SET counter = counter + numofcases ;

    END CASE;
        
  
   
    IF currentval IS NOT NULL THEN

    currentval = currentval + 1 ;
    currentvalstr = RIGHT(CONCAT ('00000', currentval) ,5);

    ELSE 
    currentvalstr = CONCAT ('Exception - Not enough multiple reference numbers available in index multipleReference', Office, ' to service requests');

    END IF;


    RETURN  currentvalstr;





/***********   5. LondonCentral   ************/

   WHEN office = 'LondonCentral' THEN 

    -- Acquire Lock on multipleReferenceLondonCentral table 

    SELECT counter INTO currentval FROM multipleReferenceLondonCentral FOR UPDATE ;
    
      
    CASE 
    
    WHEN currentval = 99999 OR (currentval + numofcases) > 99999 THEN    
   
    currentval = NULL;

    ELSE  
    
    UPDATE  multipleReferenceLondonCentral SET counter = counter + numofcases ;

    END CASE;
        
  
   
    IF currentval IS NOT NULL THEN

    currentval = currentval + 1 ;
    currentvalstr = RIGHT(CONCAT ('00000', currentval) ,5);

    ELSE 
    currentvalstr = CONCAT ('Exception - Not enough multiple reference numbers available in index multipleReference', Office, ' to service requests');

    END IF;


    RETURN  currentvalstr;




/***********   6. LondonEast   ************/

   WHEN office = 'LondonEast' THEN 

    -- Acquire Lock on multipleReferenceLondonEast table 

    SELECT counter INTO currentval FROM multipleReferenceLondonEast FOR UPDATE ;
    
      
    CASE 
    
    WHEN currentval = 99999 OR (currentval + numofcases) > 99999 THEN    
   
    currentval = NULL;

    ELSE  
    
    UPDATE  multipleReferenceLondonEast SET counter = counter + numofcases ;

    END CASE;
        
  
   
    IF currentval IS NOT NULL THEN

    currentval = currentval + 1 ;
    currentvalstr = RIGHT(CONCAT ('00000', currentval) ,5);

    ELSE 
    currentvalstr = CONCAT ('Exception - Not enough multiple reference numbers available in index multipleReference', Office, ' to service requests');

    END IF;


    RETURN  currentvalstr;





  /***********   7. LondonSouth   ************/

   WHEN office = 'LondonSouth' THEN 

    -- Acquire Lock on multipleReferenceLondonSouth table 

    SELECT counter INTO currentval FROM multipleReferenceLondonSouth FOR UPDATE ;
    
      
    CASE 
    
    WHEN currentval = 99999 OR (currentval + numofcases) > 99999 THEN    
   
    currentval = NULL;

    ELSE  
    
    UPDATE  multipleReferenceLondonSouth SET counter = counter + numofcases ;

    END CASE;
        
  
   
    IF currentval IS NOT NULL THEN

    currentval = currentval + 1 ;
    currentvalstr = RIGHT(CONCAT ('00000', currentval) ,5);

    ELSE 
    currentvalstr = CONCAT ('Exception - Not enough multiple reference numbers available in index multipleReference', Office, ' to service requests');

    END IF;


    RETURN  currentvalstr;




  /***********   8. MidlandsEast   ************/

    WHEN office = 'MidlandsEast' THEN 

    -- Acquire Lock on multipleReferenceMidlandsEast table 

    SELECT counter INTO currentval FROM multipleReferenceMidlandsEast FOR UPDATE ;
    
      
    CASE 
    
    WHEN currentval = 99999 OR (currentval + numofcases) > 99999 THEN    
   
    currentval = NULL;

    ELSE  
    
    UPDATE  multipleReferenceMidlandsEast SET counter = counter + numofcases ;

    END CASE;
        
  
   
    IF currentval IS NOT NULL THEN

    currentval = currentval + 1 ;
    currentvalstr = RIGHT(CONCAT ('00000', currentval) ,5);

    ELSE 
    currentvalstr = CONCAT ('Exception - Not enough multiple reference numbers available in index multipleReference', Office, ' to service requests');

    END IF;


    RETURN  currentvalstr;





  /***********   9. MidlandsWest   ************/

    WHEN office = 'MidlandsWest' THEN 

    -- Acquire Lock on multipleReferenceMidlandsWest table 

    SELECT counter INTO currentval FROM multipleReferenceMidlandsWest FOR UPDATE ;
    
      
    CASE 
    
    WHEN currentval = 99999 OR (currentval + numofcases) > 99999 THEN    
   
    currentval = NULL;

    ELSE  
    
    UPDATE  multipleReferenceMidlandsWest SET counter = counter + numofcases ;

    END CASE;
        
  
   
    IF currentval IS NOT NULL THEN

    currentval = currentval + 1 ;
    currentvalstr = RIGHT(CONCAT ('00000', currentval) ,5);

    ELSE 
    currentvalstr = CONCAT ('Exception - Not enough multiple reference numbers available in index multipleReference', Office, ' to service requests');

    END IF;


    RETURN  currentvalstr;




  /***********   10. Newcastle   ************/

   WHEN office = 'Newcastle' THEN 

    -- Acquire Lock on multipleReferenceNewcastle table 

    SELECT counter INTO currentval FROM multipleReferenceNewcastle FOR UPDATE ;
    
      
    CASE 
    
    WHEN currentval = 99999 OR (currentval + numofcases) > 99999 THEN    
   
    currentval = NULL;

    ELSE  
    
    UPDATE  multipleReferenceNewcastle SET counter = counter + numofcases ;

    END CASE;
        
  
   
    IF currentval IS NOT NULL THEN

    currentval = currentval + 1 ;
    currentvalstr = RIGHT(CONCAT ('00000', currentval) ,5);

    ELSE 
    currentvalstr = CONCAT ('Exception - Not enough multiple reference numbers available in index multipleReference', Office, ' to service requests');

    END IF;


    RETURN  currentvalstr;





/***********   11. Wales   ************/

   WHEN office = 'Wales' THEN 

    -- Acquire Lock on multipleReferenceWales table 

    SELECT counter INTO currentval FROM multipleReferenceWales FOR UPDATE ;
    
      
    CASE 
    
    WHEN currentval = 99999 OR (currentval + numofcases) > 99999 THEN    
   
    currentval = NULL;

    ELSE  
    
    UPDATE  multipleReferenceWales SET counter = counter + numofcases ;

    END CASE;
        
  
   
    IF currentval IS NOT NULL THEN

    currentval = currentval + 1 ;
    currentvalstr = RIGHT(CONCAT ('00000', currentval) ,5);

    ELSE 
    currentvalstr = CONCAT ('Exception - Not enough multiple reference numbers available in index multipleReference', Office, ' to service requests');

    END IF;


    RETURN  currentvalstr;




  /***********  12. Watford   ************/

   WHEN office = 'Watford' THEN 

    -- Acquire Lock on multipleReferenceWatford table 

    SELECT counter INTO currentval FROM multipleReferenceWatford FOR UPDATE ;
    
      
    CASE 
    
    WHEN currentval = 99999 OR (currentval + numofcases) > 99999 THEN    
   
    currentval = NULL;

    ELSE  
    
    UPDATE  multipleReferenceWatford SET counter = counter + numofcases ;

    END CASE;
        
  
   
    IF currentval IS NOT NULL THEN

    currentval = currentval + 1 ;
    currentvalstr = RIGHT(CONCAT ('00000', currentval) ,5);

    ELSE 
    currentvalstr = CONCAT ('Exception - Not enough multiple reference numbers available in index multipleReference', Office, ' to service requests');

    END IF;


    RETURN  currentvalstr;





END CASE ;

END;
   $$ LANGUAGE plpgsql;



/* CREATE FUNCTION */

CREATE OR REPLACE FUNCTION fn_ethosSubMultipleCaseRefGen ( p_multref INT, p_numofcases INT, office varchar(200)) RETURNS VARCHAR(100) AS $$

-- =============================================
-- Author:		Mohammed Hafejee

-- TEST :		SELECT fn_ethosSubMultipleCaseRefGen(243, 1, 'Manchester');
--				
-- Create date: 14-APR-2020
-- Description:	Function to generate Ethos case reference numbers for submultiple cases
-- VERSION	:	14-APR-2020		1.0  - Initial
--        	:	29-APR-2020		1.1  - replaced RIGHT (CONCAT ( '00000', p_multref) ,5) with p_multref to prevent truncation
-- =============================================

  DECLARE c_submultref integer;
  DECLARE c_submultrefstr varchar(200);
  DECLARE c_multrefstr varchar(20);

BEGIN 

  CASE 

/***********   1. Manchester   ************/  

    WHEN office = 'Manchester' THEN 

    -- Acquire Lock on subMultipleReferenceManchester table 

    SELECT submultref INTO c_submultref FROM subMultipleReferenceManchester WHERE multref  = p_multref FOR UPDATE ;
    
    CASE WHEN c_submultref IS NULL THEN
    
    INSERT INTO subMultipleReferenceManchester VALUES  (p_multref, p_numofcases);

    c_multrefstr = p_multref;
    c_submultref = 1;

    c_submultrefstr = CONCAT(c_multrefstr,'/', c_submultref::text);  

    ELSE 
    
    UPDATE subMultipleReferenceManchester SET submultref  = c_submultref + p_numofcases WHERE multref  = p_multref;
    
    c_submultref = c_submultref + 1;
    
    c_multrefstr = p_multref;
    
    c_submultrefstr = CONCAT(c_multrefstr,'/', c_submultref::text);
    
    END CASE;

    RETURN c_submultrefstr;
      


/***********   2. Scotland   ************/

   WHEN office = 'Scotland' THEN 

    -- Acquire Lock on subMultipleReferenceScotland table 

    SELECT submultref INTO c_submultref FROM subMultipleReferenceScotland WHERE multref  = p_multref FOR UPDATE ;
    
    CASE WHEN c_submultref IS NULL THEN
    
    INSERT INTO subMultipleReferenceScotland VALUES  (p_multref, p_numofcases);

    c_multrefstr = p_multref;
    c_submultref = 1;

    c_submultrefstr = CONCAT(c_multrefstr,'/', c_submultref::text);  

    ELSE 
    
    UPDATE subMultipleReferenceScotland SET submultref  = c_submultref + p_numofcases WHERE multref  = p_multref;
    
    c_submultref = c_submultref + 1;
    
    c_multrefstr = p_multref;
    
    c_submultrefstr = CONCAT(c_multrefstr,'/', c_submultref::text);
    
    END CASE;

    RETURN c_submultrefstr;



/***********   3. Bristol   ************/

  WHEN office = 'Bristol' THEN 

    -- Acquire Lock on subMultipleReferenceBristol table 

    SELECT submultref INTO c_submultref FROM subMultipleReferenceBristol WHERE multref  = p_multref FOR UPDATE ;
    
    CASE WHEN c_submultref IS NULL THEN
    
    INSERT INTO subMultipleReferenceBristol VALUES  (p_multref, p_numofcases);

    c_multrefstr = p_multref;
    c_submultref = 1;

    c_submultrefstr = CONCAT(c_multrefstr,'/', c_submultref::text);  

    ELSE 
    
    UPDATE subMultipleReferenceBristol SET submultref  = c_submultref + p_numofcases WHERE multref  = p_multref;
    
    c_submultref = c_submultref + 1;
    
    c_multrefstr = p_multref;
    
    c_submultrefstr = CONCAT(c_multrefstr,'/', c_submultref::text);
    
    END CASE;

    RETURN c_submultrefstr;





/***********   4. Leeds   ************/

   WHEN office = 'Leeds' THEN 

    -- Acquire Lock on subMultipleReferenceLeeds table 

    SELECT submultref INTO c_submultref FROM subMultipleReferenceLeeds WHERE multref  = p_multref FOR UPDATE ;
    
    CASE WHEN c_submultref IS NULL THEN
    
    INSERT INTO subMultipleReferenceLeeds VALUES  (p_multref, p_numofcases);

    c_multrefstr = p_multref;
    c_submultref = 1;

    c_submultrefstr = CONCAT(c_multrefstr,'/', c_submultref::text);  

    ELSE 
    
    UPDATE subMultipleReferenceLeeds SET submultref  = c_submultref + p_numofcases WHERE multref  = p_multref;
    
    c_submultref = c_submultref + 1;
    
    c_multrefstr = p_multref;
    
    c_submultrefstr = CONCAT(c_multrefstr,'/', c_submultref::text);
    
    END CASE;

    RETURN c_submultrefstr;





/***********   5. LondonCentral   ************/

   WHEN office = 'LondonCentral' THEN 

    -- Acquire Lock on subMultipleReferenceLondonCentral table 

    SELECT submultref INTO c_submultref FROM subMultipleReferenceLondonCentral WHERE multref  = p_multref FOR UPDATE ;
    
    CASE WHEN c_submultref IS NULL THEN
    
    INSERT INTO subMultipleReferenceLondonCentral VALUES  (p_multref, p_numofcases);

    c_multrefstr = p_multref;
    c_submultref = 1;

    c_submultrefstr = CONCAT(c_multrefstr,'/', c_submultref::text);  

    ELSE 
    
    UPDATE subMultipleReferenceLondonCentral SET submultref  = c_submultref + p_numofcases WHERE multref  = p_multref;
    
    c_submultref = c_submultref + 1;
    
    c_multrefstr = p_multref;
    
    c_submultrefstr = CONCAT(c_multrefstr,'/', c_submultref::text);
    
    END CASE;

    RETURN c_submultrefstr;




/***********   6. LondonEast   ************/

   WHEN office = 'LondonEast' THEN 

    -- Acquire Lock on subMultipleReferenceLondonEast table 

    SELECT submultref INTO c_submultref FROM subMultipleReferenceLondonEast WHERE multref  = p_multref FOR UPDATE ;
    
    CASE WHEN c_submultref IS NULL THEN
    
    INSERT INTO subMultipleReferenceLondonEast VALUES  (p_multref, p_numofcases);

    c_multrefstr = p_multref;
    c_submultref = 1;

    c_submultrefstr = CONCAT(c_multrefstr,'/', c_submultref::text);  

    ELSE 
    
    UPDATE subMultipleReferenceLondonEast SET submultref  = c_submultref + p_numofcases WHERE multref  = p_multref;
    
    c_submultref = c_submultref + 1;
    
    c_multrefstr = p_multref;
    
    c_submultrefstr = CONCAT(c_multrefstr,'/', c_submultref::text);
    
    END CASE;

    RETURN c_submultrefstr;




  /***********   7. LondonSouth   ************/

   WHEN office = 'LondonSouth' THEN 

    -- Acquire Lock on subMultipleReferenceLondonSouth table 

    SELECT submultref INTO c_submultref FROM subMultipleReferenceLondonSouth WHERE multref  = p_multref FOR UPDATE ;
    
    CASE WHEN c_submultref IS NULL THEN
    
    INSERT INTO subMultipleReferenceLondonSouth VALUES  (p_multref, p_numofcases);

    c_multrefstr = p_multref;
    c_submultref = 1;

    c_submultrefstr = CONCAT(c_multrefstr,'/', c_submultref::text);  

    ELSE 
    
    UPDATE subMultipleReferenceLondonSouth SET submultref  = c_submultref + p_numofcases WHERE multref  = p_multref;
    
    c_submultref = c_submultref + 1;
    
    c_multrefstr = p_multref;
    
    c_submultrefstr = CONCAT(c_multrefstr,'/', c_submultref::text);
    
    END CASE;

    RETURN c_submultrefstr;




  /***********   8. MidlandsEast   ************/

    WHEN office = 'MidlandsEast' THEN 

    -- Acquire Lock on subMultipleReferenceMidlandsEast table 

    SELECT submultref INTO c_submultref FROM subMultipleReferenceMidlandsEast WHERE multref  = p_multref FOR UPDATE ;
    
    CASE WHEN c_submultref IS NULL THEN
    
    INSERT INTO subMultipleReferenceMidlandsEast VALUES  (p_multref, p_numofcases);

    c_multrefstr = p_multref;
    c_submultref = 1;

    c_submultrefstr = CONCAT(c_multrefstr,'/', c_submultref::text);  

    ELSE 
    
    UPDATE subMultipleReferenceMidlandsEast SET submultref  = c_submultref + p_numofcases WHERE multref  = p_multref;
    
    c_submultref = c_submultref + 1;
    
    c_multrefstr = p_multref;
    
    c_submultrefstr = CONCAT(c_multrefstr,'/', c_submultref::text);
    
    END CASE;

    RETURN c_submultrefstr;





  /***********   9. MidlandsWest   ************/

   WHEN office = 'MidlandsWest' THEN 

    -- Acquire Lock on subMultipleReferenceMidlandsWest table 

    SELECT submultref INTO c_submultref FROM subMultipleReferenceMidlandsWest WHERE multref  = p_multref FOR UPDATE ;
    
    CASE WHEN c_submultref IS NULL THEN
    
    INSERT INTO subMultipleReferenceMidlandsWest VALUES  (p_multref, p_numofcases);

    c_multrefstr = p_multref;
    c_submultref = 1;

    c_submultrefstr = CONCAT(c_multrefstr,'/', c_submultref::text);  

    ELSE 
    
    UPDATE subMultipleReferenceMidlandsWest SET submultref  = c_submultref + p_numofcases WHERE multref  = p_multref;
    
    c_submultref = c_submultref + 1;
    
    c_multrefstr = p_multref;
    
    c_submultrefstr = CONCAT(c_multrefstr,'/', c_submultref::text);
    
    END CASE;

    RETURN c_submultrefstr;




  /***********   10. Newcastle   ************/

   WHEN office = 'Newcastle' THEN 

    -- Acquire Lock on subMultipleReferenceNewcastle table 

    SELECT submultref INTO c_submultref FROM subMultipleReferenceNewcastle WHERE multref  = p_multref FOR UPDATE ;
    
    CASE WHEN c_submultref IS NULL THEN
    
    INSERT INTO subMultipleReferenceNewcastle VALUES  (p_multref, p_numofcases);

    c_multrefstr = p_multref;
    c_submultref = 1;

    c_submultrefstr = CONCAT(c_multrefstr,'/', c_submultref::text);  

    ELSE 
    
    UPDATE subMultipleReferenceNewcastle SET submultref  = c_submultref + p_numofcases WHERE multref  = p_multref;
    
    c_submultref = c_submultref + 1;
    
    c_multrefstr = p_multref;
    
    c_submultrefstr = CONCAT(c_multrefstr,'/', c_submultref::text);
    
    END CASE;

    RETURN c_submultrefstr;





/***********   11. Wales   ************/

   WHEN office = 'Wales' THEN 

    -- Acquire Lock on subMultipleReferenceWales table 

    SELECT submultref INTO c_submultref FROM subMultipleReferenceWales WHERE multref  = p_multref FOR UPDATE ;
    
    CASE WHEN c_submultref IS NULL THEN
    
    INSERT INTO subMultipleReferenceWales VALUES  (p_multref, p_numofcases);

    c_multrefstr = p_multref;
    c_submultref = 1;

    c_submultrefstr = CONCAT(c_multrefstr,'/', c_submultref::text);  

    ELSE 
    
    UPDATE subMultipleReferenceWales SET submultref  = c_submultref + p_numofcases WHERE multref  = p_multref;
    
    c_submultref = c_submultref + 1;
    
    c_multrefstr = p_multref;
    
    c_submultrefstr = CONCAT(c_multrefstr,'/', c_submultref::text);
    
    END CASE;

    RETURN c_submultrefstr;




  /***********  12. Watford   ************/

   WHEN office = 'Watford' THEN 

    -- Acquire Lock on subMultipleReferenceWatford table 

    SELECT submultref INTO c_submultref FROM subMultipleReferenceWatford WHERE multref  = p_multref FOR UPDATE ;
    
    CASE WHEN c_submultref IS NULL THEN
    
    INSERT INTO subMultipleReferenceWatford VALUES  (p_multref, p_numofcases);

    c_multrefstr = p_multref;
    c_submultref = 1;

    c_submultrefstr = CONCAT(c_multrefstr,'/', c_submultref::text);  

    ELSE 
    
    UPDATE subMultipleReferenceWatford SET submultref  = c_submultref + p_numofcases WHERE multref  = p_multref;
    
    c_submultref = c_submultref + 1;
    
    c_multrefstr = p_multref;
    
    c_submultrefstr = CONCAT(c_multrefstr,'/', c_submultref::text);
    
    END CASE;

    RETURN c_submultrefstr;




END CASE ;

END;
   $$ LANGUAGE plpgsql;


