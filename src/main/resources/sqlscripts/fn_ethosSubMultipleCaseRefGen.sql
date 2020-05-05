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


