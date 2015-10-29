drop table appearances cascade constraints;
drop table rehearsals cascade constraints;
drop table appointment_attendances cascade constraints;
drop table appointments cascade constraints;
drop table appearance_requests cascade constraints;
drop table rehearsal_requests cascade constraints;
drop table available_times cascade constraints;
drop table bandmembers cascade constraints;
drop table bands cascade constraints;
drop table instrument_skills cascade constraints;
drop table instruments cascade constraints;
drop table musicians cascade constraints;
drop table streets cascade constraints;
drop table locations cascade constraints;


create table locations (
  id integer,
  name varchar2(50),
  shape sdo_geometry,
  constraint pk_Location primary key (id)
);

create table streets (
  id integer,
  name varchar2(50),
  shape sdo_geometry,
  constraint pk_Street primary key (id)
);

create table musicians (
  id integer,
  username varchar2(50),
  password varchar2(50),
  first_name varchar2(50),
  last_name varchar2(50),
  birthdate date,
  habitation_id integer,
  constraint pk_Musician primary key (id),
  constraint fk_Musicians_Locations foreign key (habitation_id) references locations(id),
  constraint unique_Musician_Username unique (username)
);

create table instruments (
  id integer,
  name varchar2(50),
  constraint pk_Instrument primary key (id)
);

create table instrument_skills (
  musician_id integer,
  instrument_id integer,
  constraint pk_Instrument_skill primary key (musician_id, instrument_id),
  constraint fk_Instrument_skills_Musicians foreign key (musician_id) references musicians(id),
  constraint fk_Instrument_skills_Instrumen foreign key (instrument_id) references instruments(id)
);

create table bands (
  id integer,
  name varchar2(50),
  leader_id integer,
  costs_per_hour integer,
  constraint pk_Band primary key (id),
  constraint fk_Bands_Musicians foreign key (leader_id) references musicians(id),
  constraint unique_Band_Name unique (name)
);

create table bandmembers (
  band_id integer,
  musician_id integer,
  joined date,
  constraint pk_Bandmember primary key (band_id, musician_id),
  constraint fk_Bandmembers_Bands foreign key (band_id) references bands(id),
  constraint fk_Bandmembers_Musicians foreign key (musician_id) references musicians(id)
);

create table available_times (
  id integer,
  band_id integer,
  musician_id integer,
  start_time date,
  end_time date,
  constraint pk_Available_time primary key (id, band_id, musician_id),
  constraint fk_Available_times_Bandmembers foreign key (band_id, musician_id) references bandmembers (band_id, musician_id)
);
  
create table rehearsal_requests (
  id integer,
  band_id integer,
  start_time date,
  end_time date,
  duration float,
  constraint pk_Rehearsal_request primary key (id, band_id),
  constraint fk_Rehearsal_requests_Bandmemb foreign key (band_id) references bands (id)
);

create table appearance_requests (
  id integer,
  band_id integer,
  start_time date,
  end_time date,
  location_id integer,
  accepted integer,
  constraint pk_Appearance_requests primary key (id, band_id),
  constraint fk_Appearance_requests_Bands foreign key (band_id) references bands (id),
  constraint fk_Appearance_requests_Locatio foreign key (location_id) references locations (id),
  constraint ck_Appearance_requests_Accepte check (accepted = 0 or accepted = 1)
);

create table appointments (
  id integer,
  band_id integer,
  location_id integer,
  grounded integer,
  start_time date,
  end_time date,
  name varchar2(50),
  description varchar2(50),
  constraint pk_Appointment primary key (id, band_id),
  constraint fk_Appointments_Bands foreign key (band_id) references bands (id),
  constraint fk_Appointments_Locations foreign key (location_id) references locations (id),
  constraint ck_Appointments_requests_Accep check (grounded = 0 or grounded = 1)
);

create table appointment_attendances (
  appointment_id integer,
  band_id integer,
  musician_id integer,
  constraint pk_Appointment_attendance primary key (appointment_id, musician_id, band_id),
  constraint fk_Appointment_attendances_App foreign key (appointment_id, band_id) references appointments (id, band_id),
  constraint fk_Appointment_attendances_Ban foreign key (band_id, musician_id) references bandmembers (band_id, musician_id)
);

create table rehearsals (
  appointment_id integer,
  band_id integer,
  constraint pk_Rehearsal primary key (appointment_id, band_id),
  constraint fk_Rehearsals_Appointments foreign key (appointment_id, band_id) references appointments (id, band_id)
);

create table appearances (
  appointment_id integer,
  band_id integer,
  constraint pk_Appearance primary key (appointment_id, band_id),
  constraint fk_Appearances_Appointments foreign key (appointment_id, band_id) references appointments (id, band_id)
);