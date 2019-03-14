CREATE TABLE requests
(
	id BIGSERIAL CONSTRAINT requests_pkey PRIMARY KEY,
	facility_id BIGINT,
	status INTEGER NOT NULL,
	action INTEGER NOT NULL,
	requesting_user_id BIGINT NOT NULL,
	attributes VARCHAR,
	modified_by BIGINT NOT NULL,
	modified_at TIMESTAMP DEFAULT NOW()
);

CREATE UNIQUE index requests_id_uindex ON requests (id);

CREATE TABLE approvals
(
	facility_id INTEGER NOT NULL,
	link VARCHAR(256) NOT NULL,
	hash VARCHAR(256) NOT NULL,
	user_id BIGINT,
	user_email VARCHAR(256) NOT NULL,
	user_name VARCHAR(256),
	valid_until TIMESTAMP NOT NULL,
	signed_at TIMESTAMP
);