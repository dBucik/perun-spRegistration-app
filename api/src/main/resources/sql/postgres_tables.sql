create table requests
(
	id bigserial not null
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

alter table requests owner to spreg_access;

create unique index requests_id_uindex
	on requests (id);

create table approvals
(
	request_id bigint not null
		constraint approvals_pk
			primary key
		constraint approvals_requests_id_fk
			references requests
				on update cascade on delete cascade,
	signer_id bigint not null,
	signer_name varchar not null,
	signer_input varchar not null,
	signed_at timestamp default now() not null
);

alter table approvals owner to spreg_access;