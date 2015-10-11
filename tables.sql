drop table appearences;
drop table rehearsals;
drop table appointment_attendances;
drop table appointments;
drop table appearance_requests;
drop table rehearsal_requests;
drop table available_times;
drop table bandmembers;
drop table bands;
drop table instrument_skills;
drop table instruments;
drop table musicians;
drop table streets;
drop table locations;


create table locations (
  id integer primary key,
  name varchar2(50),
  shape sdo_geometry
);

create table streets (
  id integer primary key,
  name varchar2(50),
  shape sdo_geometry
);

create table musicians (
  id integer primary key,
  name varchar2(50),
  birthdate date,
  habitation_id integer references locations(id)
);

create table instruments (
  id integer primary key,
  name varchar2(50)
);

create table instrument_skills (
  musician_id integer references musicians(id),
  instrument_id integer references instruments(id),
  primary key (musician_id, instrument_id)
);

create table bands (
  id integer primary key,
  name varchar2(50),
  leader_id integer references musicians(id),
  costs_per_hour integer
);

create table bandmembers (
  band_id integer references bands(id),
  musician_id integer references musicians(id),
  joined date,
  primary key (band_id, musician_id)
);

create table available_times (
  id integer,
  band_id integer,
  musician_id integer,
  start_time date,
  end_time date,
  foreign key (band_id, musician_id) references bandmembers (band_id, musician_id),
  primary key (id, band_id, musician_id)
);
  
create table rehearsal_requests (
  id integer,
  band_id integer references bands(id),
  start_time date,
  end_time date,
  duration float,
  primary key (id, band_id)
);

create table appearance_requests (
  id integer,
  band_id integer references bands(id),
  start_time date,
  end_time date,
  location_id references locations(id),
  accepted integer CHECK (accepted = 0 or accepted = 1),
  primary key (id, band_id)
);

create table appointments (
  id integer,
  band_id integer references bands(id),
  location_id integer references locations(id),
  grounded integer CHECK (grounded = 0 or grounded = 1),
  start_time date,
  end_time date,
  name varchar2(50),
  description varchar2(50),
  primary key (id, band_id)
);

create table appointment_attendances (
  appointment_id integer,
  band_id integer,
  musician_id integer,
  foreign key (appointment_id, band_id) references appointments (id, band_id),
  foreign key (band_id, musician_id) references bandmembers (band_id, musician_id),
  primary key (appointment_id, musician_id, band_id)
);

create table rehearsals (
  appointment_id integer,
  band_id integer,
  foreign key (appointment_id, band_id) references appointments (id, band_id),
  primary key (appointment_id, band_id)
);

create table appearances (
  appointment_id integer,
  band_id integer,
  foreign key (appointment_id, band_id) references appointments (id, band_id),
  primary key (appointment_id, band_id)
);
