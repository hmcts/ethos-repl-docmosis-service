CREATE OR REPLACE FUNCTION fn_persistentQ_InsertFirstMultipleCountVal(p_multipleRef VARCHAR(25))
RETURNS INTEGER AS $$
BEGIN
    INSERT INTO multiplecounter(multipleref, counter)
    VALUES (p_multipleRef, 0)
    ON CONFLICT (multipleref) DO NOTHING;

    RETURN 1;
END;
$$ LANGUAGE plpgsql;
