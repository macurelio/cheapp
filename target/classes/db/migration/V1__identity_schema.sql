-- Identity & Access schema (PostgreSQL)

create table if not exists permissions (
    id bigserial primary key,
    name varchar(100) not null unique
);

create table if not exists roles (
    id bigserial primary key,
    name varchar(100) not null unique
);

create table if not exists role_permissions (
    role_id bigint not null references roles(id) on delete cascade,
    permission_id bigint not null references permissions(id) on delete cascade,
    primary key (role_id, permission_id)
);

create table if not exists users (
    id bigserial primary key,
    email varchar(255) not null unique,
    password_hash varchar(255) not null,
    enabled boolean not null default true,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table if not exists user_roles (
    user_id bigint not null references users(id) on delete cascade,
    role_id bigint not null references roles(id) on delete cascade,
    primary key (user_id, role_id)
);

-- Seed mínimo: roles base
insert into roles(name) values ('USER') on conflict do nothing;
insert into roles(name) values ('ADMIN') on conflict do nothing;
