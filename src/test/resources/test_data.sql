delete from event_logs;
delete from drones;
delete from drone_models;
delete from medications;

insert into drone_models (model_name, weight) values 
('Lightweight', 200),
('Middleweight', 300),
('Cruiserweight', 400);

insert into medications (medication_name, weight, medication_code) values 
('name1', 50, 'EXISTS1'), 
('name2', 1000, 'EXISTS2');

insert into drones (drone_number, model_name, battery_capacity, state) values 
('111', 'Lightweight', 100, 'IDLE'),
('222', 'Lightweight', 100, 'LOADED'),
('333', 'Lightweight', 10, 'IDLE');