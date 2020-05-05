/* CREATE TABLES */

-- =============================================
-- Author:		Mohammed Hafejee
--				
-- Create date: 14-APR-2020
-- Description:	Script to create base tables used by function fn_ethosSubMultipleCaseRefGen
-- VERSION	:	14-APR-2020		1.0  - Initial
-- =============================================

/***********   1. Manchester   ************/  

DROP TABLE IF EXISTS subMultipleReferenceManchester;
CREATE TABLE subMultipleReferenceManchester 
  (
  multref int,
  submultref int
  );

CREATE INDEX IX_subMultipleReferenceManchester_multref ON subMultipleReferenceManchester(multref);

/***********   2. Scotland   ************/  

DROP TABLE IF EXISTS subMultipleReferenceScotland;
CREATE TABLE subMultipleReferenceScotland 
  (
    multref int,
   submultref int
  );

CREATE INDEX IX_subMultipleReferenceScotland_multref ON subMultipleReferenceScotland(multref);

/***********   3. Bristol   ************/  

DROP TABLE IF EXISTS subMultipleReferenceBristol;
CREATE TABLE subMultipleReferenceBristol 
  (
    multref int,
   submultref int
  );

CREATE INDEX IX_subMultipleReferenceBristol_multref ON subMultipleReferenceBristol(multref);

/***********   4. Leeds   ************/  

DROP TABLE IF EXISTS subMultipleReferenceLeeds;
CREATE TABLE subMultipleReferenceLeeds 
  (
    multref int,
   submultref int
  );

CREATE INDEX IX_subMultipleReferenceLeeds_multref ON subMultipleReferenceLeeds(multref);

/***********   5. LondonCentral   ************/  

DROP TABLE IF EXISTS subMultipleReferenceLondonCentral;
CREATE TABLE subMultipleReferenceLondonCentral 
  (
    multref int,
   submultref int
  );

CREATE INDEX IX_subMultipleReferenceLondonCentral_multref ON subMultipleReferenceLondonCentral(multref);

/***********   6. LondonEast   ************/  

DROP TABLE IF EXISTS subMultipleReferenceLondonEast;
CREATE TABLE subMultipleReferenceLondonEast 
  (
    multref int,
   submultref int
  );

CREATE INDEX IX_subMultipleReferenceLondonEast_multref ON subMultipleReferenceLondonEast(multref);

/***********   7. LondonSouth   ************/  

DROP TABLE IF EXISTS subMultipleReferenceLondonSouth;
CREATE TABLE subMultipleReferenceLondonSouth 
  (
    multref int,
   submultref int
  );
  
CREATE INDEX IX_subMultipleReferenceLondonSouth_multref ON subMultipleReferenceLondonSouth(multref);

/***********   8. MidlandsEast   ************/  

DROP TABLE IF EXISTS subMultipleReferenceMidlandsEast;
CREATE TABLE subMultipleReferenceMidlandsEast 
  (
    multref int,
   submultref int
  );

CREATE INDEX IX_subMultipleReferenceMidlandsEast_multref ON subMultipleReferenceMidlandsEast(multref);

/***********   9. MidlandsWest   ************/  

DROP TABLE IF EXISTS subMultipleReferenceMidlandsWest;
CREATE TABLE subMultipleReferenceMidlandsWest 
  (
    multref int,
   submultref int
  );

CREATE INDEX IX_subMultipleReferenceMidlandsWest_multref ON subMultipleReferenceMidlandsWest(multref);

/***********   10. Newcastle   ************/  

DROP TABLE IF EXISTS subMultipleReferenceNewcastle;
CREATE TABLE subMultipleReferenceNewcastle 
  (
    multref int,
   submultref int
  );

CREATE INDEX IX_subMultipleReferenceNewcastle_multref ON subMultipleReferenceNewcastle(multref);

/***********   11. Wales   ************/  

DROP TABLE IF EXISTS subMultipleReferenceWales;
CREATE TABLE subMultipleReferenceWales 
  (
    multref int,
   submultref int
  );

CREATE INDEX IX_subMultipleReferenceWales_multref ON subMultipleReferenceWales(multref);

/***********   12. Watford   ************/  

DROP TABLE IF EXISTS subMultipleReferenceWatford;
CREATE TABLE subMultipleReferenceWatford 
  (
    multref int,
   submultref int
  );

CREATE INDEX IX_subMultipleReferenceWatford_multref ON subMultipleReferenceWatford(multref);