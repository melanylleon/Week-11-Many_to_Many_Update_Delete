DROP TABLE IF EXISTS material;
DROP TABLE IF EXISTS step;
DROP TABLE IF EXISTS project_category;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS project;

CREATE TABLE project (
project_id INT AUTO_INCREMENT NOT NULL,
project_name VARCHAR(128) NOT NULL,
estimated_hours DECIMAL(7, 2),
actual_hours DECIMAL(7, 2),
difficulty INT,
notes TEXT,
PRIMARY KEY (project_id)
);

CREATE TABLE category (
category_id INT AUTO_INCREMENT NOT NULL,
category_name VARCHAR(128) NOT NULL,
PRIMARY KEY (category_id)
);

CREATE TABLE project_category (
project_id INT NOT NULL,
category_id INT NOT NULL,
FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE,
FOREIGN KEY (category_id) REFERENCES category (category_id) ON DELETE CASCADE,
UNIQUE KEY (project_id, category_id)
);

CREATE TABLE step (
step_id INT AUTO_INCREMENT NOT NULL,
project_id INT NOT NULL,
step_text TEXT NOT NULL,
step_order INT NOT NULL,
PRIMARY KEY (step_id),
FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE
);

CREATE TABLE material (
material_id INT AUTO_INCREMENT NOT NULL,
project_id INT NOT NULL,
material_name VARCHAR(128) NOT NULL,
num_required INT,
cost DECIMAL(7, 2),
PRIMARY KEY (material_id),
FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE
);





INSERT INTO project (project_name, estimated_hours, actual_hours, difficulty, notes) VALUES ('Bird House', 5.0, 4.0, 2, 'Make sure to buy cedar wood.');

INSERT INTO material (project_id, material_name, num_required) VALUES (1, '5x5x1 inch cedar wood', 2);
INSERT INTO material (project_id, material_name, num_required) VALUES (1, '5x8x1 inch cedar wood', 3);
INSERT INTO material (project_id, material_name, num_required) VALUES (1, '4x4x1 inch cedar wood', 2);
INSERT INTO material (project_id, material_name, num_required) VALUES (1, 'Wood Glue', 1);
INSERT INTO material (project_id, material_name, num_required) VALUES (1, '1 1/2 inch nails', 28);
INSERT INTO material (project_id, material_name, num_required) VALUES (1, 'Hammer', 1);
INSERT INTO material (project_id, material_name, num_required) VALUES (1, 'Saw', 1);
INSERT INTO material (project_id, material_name, num_required) VALUES (1, 'Drill', 1);

INSERT INTO step (project_id, step_text, step_order) VALUES (1, 'Saw 2 corners off of both 5x8x1 pieces of wood at a 45 degree angle. This will make the shape of the roof.', 1);
INSERT INTO step (project_id, step_text, step_order) VALUES (1, 'Drill a whole into the center of one of the 5x8x1 inch pieces of wood. This is the opening that the birds will use to enter the bird house.', 2);
INSERT INTO step (project_id, step_text, step_order) VALUES (1, 'Add wood glue to the edges of all the pieces of wood and stick them together.', 3);
INSERT INTO step (project_id, step_text, step_order) VALUES (1, 'Hammer the nails into the 4 corners of all of the pieces of wood.', 4);

INSERT INTO category (category_id, category_name) VALUES (1, 'Outdoors');

INSERT INTO project_category (project_id, category_id) VALUES (1, 1);





