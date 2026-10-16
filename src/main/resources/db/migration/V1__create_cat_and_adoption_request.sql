CREATE TABLE cat (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(60)  NOT NULL,
    sex         VARCHAR(10)  NOT NULL,
    birth_date  DATE,
    description VARCHAR(2000),
    status      VARCHAR(20)  NOT NULL,
    version     BIGINT       NOT NULL,
    arrived_at  TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE adoption_request (
    id              BIGSERIAL    PRIMARY KEY,
    cat_id          BIGINT       NOT NULL REFERENCES cat (id),
    applicant_name  VARCHAR(120) NOT NULL,
    applicant_email VARCHAR(254) NOT NULL,
    message         VARCHAR(2000),
    status          VARCHAR(20)  NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    decided_at      TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_cat_status ON cat (status);
CREATE INDEX idx_adoption_request_cat ON adoption_request (cat_id);
