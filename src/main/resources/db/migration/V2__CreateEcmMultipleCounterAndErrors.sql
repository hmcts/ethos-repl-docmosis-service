CREATE TABLE IF NOT EXISTS multiplecounter (
    multipleref VARCHAR(25) PRIMARY KEY,
    counter INTEGER DEFAULT 1
);

CREATE INDEX IF NOT EXISTS ix_multiplecounter_multipleref ON multiplecounter(multipleref);

CREATE TABLE IF NOT EXISTS multipleerrors (
    id SERIAL PRIMARY KEY,
    multipleref VARCHAR(25),
    ethoscaseref VARCHAR(25),
    description VARCHAR(250)
);

CREATE INDEX IF NOT EXISTS ix_multipleerrors_multipleref_ethoscaseref
    ON multipleerrors(multipleref, ethoscaseref);

CREATE OR REPLACE FUNCTION fn_persistentQ_getNextMultipleCountVal(p_multipleRef VARCHAR(25))
RETURNS INTEGER AS $$
DECLARE currentval INTEGER;
BEGIN
    SELECT counter INTO currentval
    FROM multiplecounter
    WHERE multipleref = p_multipleRef
    FOR UPDATE;

    CASE
        WHEN currentval IS NULL THEN
            INSERT INTO multiplecounter(multipleref) VALUES (p_multipleRef);
            currentval := 1;
        ELSE
            currentval := currentval + 1;
            UPDATE multiplecounter SET counter = currentval WHERE multipleref = p_multipleRef;
    END CASE;

    RETURN currentval;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION fn_persistentQ_InsertFirstMultipleCountVal(p_multipleRef VARCHAR(25))
RETURNS INTEGER AS $$
BEGIN
    LOCK TABLE multiplecounter;

    INSERT INTO multiplecounter
    SELECT p_multipleRef, 0
    WHERE NOT EXISTS (
        SELECT 1 FROM multiplecounter WHERE multipleref = p_multipleRef
    );

    RETURN 1;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION fn_persistentQ_logMultipleError(
    p_multipleRef VARCHAR(25),
    p_ethosCaseRef VARCHAR(25),
    p_description VARCHAR(250)
)
RETURNS VARCHAR(5) AS $$
BEGIN
    INSERT INTO multipleerrors(multipleref, ethoscaseref, description)
    VALUES (p_multipleRef, p_ethosCaseRef, p_description);

    RETURN 'ok';
END;
$$ LANGUAGE plpgsql;
