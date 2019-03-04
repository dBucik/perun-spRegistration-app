drop table if exists requests cascade;
drop table if exists approvals cascade;

create table requests
(
	id bigint auto_increment
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
	  references requests on update cascade on delete cascade,
	signer_id bigint not null,
	signer_name varchar not null,
	signer_input varchar not null,
	signed_at timestamp default now() not null
);