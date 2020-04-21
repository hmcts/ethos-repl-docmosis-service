/* CREATE TABLES */

  -- =============================================
-- Author:		Mohammed Hafejee
--				
-- Create date: 14-APR-2020
-- Description:	Script to create base tables used by function fn_ethosMultipleCaseRefGen
-- VERSION	:	14-MAR-2020		1.0  - Initial
-- =============================================

/***********   1. Manchester   ************/  

DROP TABLE IF EXISTS multipleReferenceManchester;
CREATE TABLE multipleReferenceManchester 
  (
  counter int
   
  );
DELETE FROM multipleReferenceManchester ; -- remove any existing values in case the script is ran more than once
INSERT INTO multipleReferenceManchester VALUES (0);

/***********   2. Scotland   ************/  

DROP TABLE IF EXISTS multipleReferenceScotland;
CREATE TABLE multipleReferenceScotland 
  (
  counter int
   
  );
DELETE FROM multipleReferenceScotland ; -- remove any existing values in case the script is ran more than once
INSERT INTO multipleReferenceScotland VALUES (0);

/***********   3. Bristol   ************/  

DROP TABLE IF EXISTS multipleReferenceBristol;
CREATE TABLE multipleReferenceBristol 
  (
  counter int
   
  );
DELETE FROM multipleReferenceBristol ; -- remove any existing values in case the script is ran more than once
INSERT INTO multipleReferenceBristol VALUES (0);


/***********   4. Leeds   ************/  

DROP TABLE IF EXISTS multipleReferenceLeeds;
CREATE TABLE multipleReferenceLeeds 
  (
  counter int
   
  );
DELETE FROM multipleReferenceLeeds ; -- remove any existing values in case the script is ran more than once
INSERT INTO multipleReferenceLeeds VALUES (0);


/***********   5. LondonCentral   ************/  

DROP TABLE IF EXISTS multipleReferenceLondonCentral;
CREATE TABLE multipleReferenceLondonCentral 
  (
  counter int
   
  );
DELETE FROM multipleReferenceLondonCentral ; -- remove any existing values in case the script is ran more than once
INSERT INTO multipleReferenceLondonCentral VALUES (0);


/***********   6. LondonEast   ************/  

DROP TABLE IF EXISTS multipleReferenceLondonEast;
CREATE TABLE multipleReferenceLondonEast 
  (
  counter int
   
  );
DELETE FROM multipleReferenceLondonEast ; -- remove any existing values in case the script is ran more than once
INSERT INTO multipleReferenceLondonEast VALUES (0);


/***********   7. LondonSouth   ************/  

DROP TABLE IF EXISTS multipleReferenceLondonSouth;
CREATE TABLE multipleReferenceLondonSouth 
  (
  counter int
   
  );
DELETE FROM multipleReferenceLondonSouth ; -- remove any existing values in case the script is ran more than once
INSERT INTO multipleReferenceLondonSouth VALUES (0);

/***********   8. MidlandsEast   ************/  

DROP TABLE IF EXISTS multipleReferenceMidlandsEast;
CREATE TABLE multipleReferenceMidlandsEast 
  (
  counter int
   
  );
DELETE FROM multipleReferenceMidlandsEast ; -- remove any existing values in case the script is ran more than once
INSERT INTO multipleReferenceMidlandsEast VALUES (0);

/***********   9. MidlandsWest   ************/  

DROP TABLE IF EXISTS multipleReferenceMidlandsWest;
CREATE TABLE multipleReferenceMidlandsWest 
  (
  counter int
   
  );
DELETE FROM multipleReferenceMidlandsWest ; -- remove any existing values in case the script is ran more than once
INSERT INTO multipleReferenceMidlandsWest VALUES (0);

/***********   10. Newcastle   ************/  

DROP TABLE IF EXISTS multipleReferenceNewcastle;
CREATE TABLE multipleReferenceNewcastle 
  (
  counter int
   
  );
DELETE FROM multipleReferenceNewcastle ; -- remove any existing values in case the script is ran more than once
INSERT INTO multipleReferenceNewcastle VALUES (0);

/***********   11. Wales   ************/  

DROP TABLE IF EXISTS multipleReferenceWales;
CREATE TABLE multipleReferenceWales 
  (
  counter int
   
  );
DELETE FROM multipleReferenceWales ; -- remove any existing values in case the script is ran more than once
INSERT INTO multipleReferenceWales VALUES (0);


/***********   12. Watford   ************/  

DROP TABLE IF EXISTS multipleReferenceWatford;
CREATE TABLE multipleReferenceWatford 
  (
  counter int
   
  );
DELETE FROM multipleReferenceWatford ; -- remove any existing values in case the script is ran more than once
INSERT INTO multipleReferenceWatford VALUES (0);