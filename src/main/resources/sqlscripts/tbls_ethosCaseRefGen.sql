/* CREATE TABLES */

-- =============================================
-- Author:		Mohammed Hafejee
--				
-- Create date: 14-APR-2020
-- Description:	Script to create base tables used by function fn_ethosCaseRefGen
-- VERSION	:	14-MAR-2020		1.0  - Initial
-- =============================================

/***********   1. Manchester   ************/  

DROP TABLE IF EXISTS singleReferenceManchester;
CREATE TABLE singleReferenceManchester 
  (
  counter int,
  cyear varchar(10) 
  );
DELETE FROM singleReferenceManchester ; -- remove any existing values in case the script is ran more than once
INSERT INTO singleReferenceManchester VALUES (0,EXTRACT(YEAR FROM CURRENT_DATE));

/***********   2. Scotland   ************/  

DROP TABLE IF EXISTS singleReferenceScotland;
CREATE TABLE singleReferenceScotland 
  (
  counter int,
  cyear varchar(10) 
  );
DELETE FROM singleReferenceScotland ; -- remove any existing values in case the script is ran more than once
INSERT INTO singleReferenceScotland VALUES (0,EXTRACT(YEAR FROM CURRENT_DATE));

/***********   3. Bristol   ************/  

DROP TABLE IF EXISTS singleReferenceBristol;
CREATE TABLE singleReferenceBristol 
  (
  counter int,
  cyear varchar(10) 
  );
DELETE FROM singleReferenceBristol ; -- remove any existing values in case the script is ran more than once
INSERT INTO singleReferenceBristol VALUES (0,EXTRACT(YEAR FROM CURRENT_DATE));


/***********   4. Leeds   ************/  

DROP TABLE IF EXISTS singleReferenceLeeds;
CREATE TABLE singleReferenceLeeds 
  (
  counter int,
  cyear varchar(10) 
  );
DELETE FROM singleReferenceLeeds ; -- remove any existing values in case the script is ran more than once
INSERT INTO singleReferenceLeeds VALUES (0,EXTRACT(YEAR FROM CURRENT_DATE));


/***********   5. LondonCentral   ************/  

DROP TABLE IF EXISTS singleReferenceLondonCentral;
CREATE TABLE singleReferenceLondonCentral 
  (
  counter int,
  cyear varchar(10) 
  );
DELETE FROM singleReferenceLondonCentral ; -- remove any existing values in case the script is ran more than once
INSERT INTO singleReferenceLondonCentral VALUES (0,EXTRACT(YEAR FROM CURRENT_DATE));


/***********   6. LondonEast   ************/  

DROP TABLE IF EXISTS singleReferenceLondonEast;
CREATE TABLE singleReferenceLondonEast 
  (
  counter int,
  cyear varchar(10) 
  );
DELETE FROM singleReferenceLondonEast ; -- remove any existing values in case the script is ran more than once
INSERT INTO singleReferenceLondonEast VALUES (0,EXTRACT(YEAR FROM CURRENT_DATE));


/***********   7. LondonSouth   ************/  

DROP TABLE IF EXISTS singleReferenceLondonSouth;
CREATE TABLE singleReferenceLondonSouth 
  (
  counter int,
  cyear varchar(10) 
  );
DELETE FROM singleReferenceLondonSouth ; -- remove any existing values in case the script is ran more than once
INSERT INTO singleReferenceLondonSouth VALUES (0,EXTRACT(YEAR FROM CURRENT_DATE));

/***********   8. MidlandsEast   ************/  

DROP TABLE IF EXISTS singleReferenceMidlandsEast;
CREATE TABLE singleReferenceMidlandsEast 
  (
  counter int,
  cyear varchar(10) 
  );
DELETE FROM singleReferenceMidlandsEast ; -- remove any existing values in case the script is ran more than once
INSERT INTO singleReferenceMidlandsEast VALUES (0,EXTRACT(YEAR FROM CURRENT_DATE));

/***********   9. MidlandsWest   ************/  

DROP TABLE IF EXISTS singleReferenceMidlandsWest;
CREATE TABLE singleReferenceMidlandsWest 
  (
  counter int,
  cyear varchar(10) 
  );
DELETE FROM singleReferenceMidlandsWest ; -- remove any existing values in case the script is ran more than once
INSERT INTO singleReferenceMidlandsWest VALUES (0,EXTRACT(YEAR FROM CURRENT_DATE));

/***********   10. Newcastle   ************/  

DROP TABLE IF EXISTS singleReferenceNewcastle;
CREATE TABLE singleReferenceNewcastle 
  (
  counter int,
  cyear varchar(10) 
  );
DELETE FROM singleReferenceNewcastle ; -- remove any existing values in case the script is ran more than once
INSERT INTO singleReferenceNewcastle VALUES (0,EXTRACT(YEAR FROM CURRENT_DATE));

/***********   11. Wales   ************/  

DROP TABLE IF EXISTS singleReferenceWales;
CREATE TABLE singleReferenceWales 
  (
  counter int,
  cyear varchar(10) 
  );
DELETE FROM singleReferenceWales ; -- remove any existing values in case the script is ran more than once
INSERT INTO singleReferenceWales VALUES (0,EXTRACT(YEAR FROM CURRENT_DATE));


/***********   12. Watford   ************/  

DROP TABLE IF EXISTS singleReferenceWatford;
CREATE TABLE singleReferenceWatford 
  (
  counter int,
  cyear varchar(10) 
  );
DELETE FROM singleReferenceWatford ; -- remove any existing values in case the script is ran more than once
INSERT INTO singleReferenceWatford VALUES (0,EXTRACT(YEAR FROM CURRENT_DATE));