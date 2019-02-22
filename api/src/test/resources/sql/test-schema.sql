create table requests
(
	id bigint auto_increment not null
		constraint requests_pkey
			primary key,
	facility_id bigint,
	status integer not null,
	action integer not null,
	requesting_user_id bigint not null,
	attributes varchar,
	modified_by bigint not null,
	modified_at timestamp default now()
);

create table approvals
(
	request_id bigint not null
	  primary key references requests on update cascade on delete cascade,
	signer_id bigint not null,
	signer_name varchar not null,
	signer_input varchar not null,
	signed_at timestamp default now() not null
);