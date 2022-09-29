CREATE OR REPLACE FUNCTION fn_AddClaimServedDate(fromDate date, toDate date, office varchar)
    RETURNS void AS
$$

BEGIN
UPDATE public.case_data cd
SET data_classification = jsonb_set(cast(cd.data_classification AS jsonb), '{claimServedDate}', '"PUBLIC"', true),
    DATA = jsonb_set(cd.data,'{claimServedDate}'::text[],to_jsonb(concat_ws('',(
        SELECT ce.created_date
        FROM case_event ce
        WHERE ce.event_id = 'generateCorrespondence'
          AND ce.case_data_id =cd.id
        ORDER BY ce.created_date
            FETCH FIRST ROW ONLY)::DATE)),
                     true)
WHERE cd.id IN
      (
          SELECT ce.case_data_id
          FROM case_event ce
          WHERE ce.created_date BETWEEN fromDate AND toDate
            AND ce.event_id = 'initiateCase'
      )
  AND NOT DATA ? 'claimServedDate'
  AND EXISTS (SELECT 1 FROM case_event _ce WHERE event_id = 'generateCorrespondence' AND _ce.case_data_id = cd.id)
  AND cd.case_type_id = office;

END;
$$ LANGUAGE plpgsql;