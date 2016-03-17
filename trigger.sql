CREATE OR REPLACE TRIGGER BANDS_BEFOREINSERT 
BEFORE INSERT ON BANDS 
FOR EACH ROW 
DECLARE
  maxId integer;  
BEGIN
   select nvl (max(id), -1) into maxId from bands;      
  :new.id := maxId + 1;   
END;






CREATE OR REPLACE TRIGGER MUSICIANS_BEFOREINSERT 
BEFORE INSERT ON MUSICIANS 
FOR EACH ROW 
DECLARE
  maxId integer;  
BEGIN
  select nvl (max(id), -1) into maxId from musicians;
  :new.id := maxId + 1;   
END;




CREATE OR REPLACE TRIGGER STREETS_BEFOREINSERT 
BEFORE INSERT ON STREETS 
FOR EACH ROW 
DECLARE
  maxId integer;  
BEGIN
  select nvl (max(id), -1) into maxId from streets;      
  :new.id := maxId + 1;   
END;

CREATE OR REPLACE TRIGGER STREETS_BEFOREUPDATE 
BEFORE UPDATE ON STREETS 
FOR EACH ROW 
BEGIN
  :new.id := :old.id;
END;




create or replace TRIGGER LOCATIONS_BEFOREINSERT 
BEFORE INSERT ON LOCATIONS 
FOR EACH ROW 
DECLARE
  maxId integer;  
BEGIN
  select nvl (max(id), -1) into maxId from locations;      
  :new.id := maxId + 1;   
END;

CREATE OR REPLACE TRIGGER LOCATIONS_BEFOREUPDATE 
BEFORE UPDATE ON LOCATIONS 
FOR EACH ROW 
BEGIN
  :new.id := :old.id;
END;


create or replace TRIGGER AVTIMES_BEFOREINSERT 
BEFORE INSERT ON AVAILABLE_TIMES
FOR EACH ROW 
DECLARE
  maxId integer;  
BEGIN
  select nvl (max(id), -1) into maxId from available_times;      
  :new.id := maxId + 1;   
END;