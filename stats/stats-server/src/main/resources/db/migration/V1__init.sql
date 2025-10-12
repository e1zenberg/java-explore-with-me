CREATE TABLE IF NOT EXISTS hits (
                                    id          BIGSERIAL PRIMARY KEY,
                                    app         VARCHAR(100) NOT NULL,
    uri         VARCHAR(512) NOT NULL,
    ip          VARCHAR(45)  NOT NULL,
    timestamp   TIMESTAMP    NOT NULL
    );

CREATE INDEX IF NOT EXISTS idx_hits_uri       ON hits(uri);
CREATE INDEX IF NOT EXISTS idx_hits_timestamp ON hits(timestamp);
CREATE INDEX IF NOT EXISTS idx_hits_ip        ON hits(ip);
