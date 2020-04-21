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


