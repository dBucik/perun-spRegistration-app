CREATE TABLE requests
(
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	facility_id BIGINT,
	status INTEGER NOT NULL,
	action INTEGER NOT NULL,
	requesting_user_id BIGINT NOT NULL,
	attributes TEXT,
	modified_by BIGINT NOT NULL,
	modified_at TIMESTAMP DEFAULT now()
);

CREATE UNIQUE INDEX requests_id_uindex ON requests (id);

CREATE TABLE approvals
(
	request_id BIGINT NOT NULL,
	user_id BIGINT NOT NULL,
	signed_at TIMESTAMP DEFAULT now()
);