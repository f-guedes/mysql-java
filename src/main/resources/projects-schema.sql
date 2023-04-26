DROP TABLE IF EXISTS material;
DROP TABLE IF EXISTS step;
DROP TABLE IF EXISTS project_category;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS project;

CREATE TABLE project(
  project_id INT AUTO_INCREMENT NOT NULL,
  project_name VARCHAR(128) NOT NULL,
  estimated_hours DECIMAL(7,2),
  actual_hours DECIMAL(7,2),
  difficulty INT,
  notes TEXT,
  PRIMARY KEY (project_id)
);

CREATE TABLE category(
  category_id INT AUTO_INCREMENT NOT NULL,
  category_name VARCHAR(128) NOT NULL,
  PRIMARY KEY (category_id)
);

CREATE TABLE project_category(
  project_id INT NOT NULL,
  category_id INT NOT NULL,
  FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE,
  FOREIGN KEY (category_id) REFERENCES category (category_id) ON DELETE CASCADE,
  UNIQUE KEY (project_id, category_id)
);

create TABLE step(
  step_id INT AUTO_INCREMENT NOT NULL,
  project_id INT NOT NULL,
  step_text TEXT NOT NULL,
  step_order INT NOT NULL,
  PRIMARY KEY (step_id),
  FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE
);

CREATE TABLE material(
  material_id INT AUTO_INCREMENT NOT NULL,
  project_id INT NOT NULL,
  material_name VARCHAR(128) NOT NULL,
  num_required INT,
  cost DECIMAL(7,2),
  PRIMARY KEY (material_id),
  FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE
);

-- Some data

INSERT INTO project (project_name, estimated_hours, actual_hours, difficulty, notes) VALUES ('Test project', 5, 5, 1, 'This is only a test project.');
INSERT INTO project (project_name, estimated_hours, actual_hours, difficulty, notes) VALUES ('Very difficult Java-based MySQL App', 12, 18, 5, 'Learning is like climbing a long staircase. It needs to be done step by step.');
INSERT INTO category (category_name) VALUES ('Doors and Windows');

INSERT INTO project (project_name, estimated_hours, actual_hours, difficulty, notes) VALUES ('Mow the lawn', 2, 2.5, 1, 'It''s in most americans'' to-do list on a Saturday morning during the spring.');
INSERT INTO material (project_id, material_name, num_required) VALUES (3, 'Lawnmower', 1);
INSERT INTO step (project_id, step_text, step_order) VALUES (3, 'Check oil and add gas as needed to lawn mower', 1);
INSERT INTO step (project_id, step_text, step_order) VALUES (3, 'Mow the lawn', 2);
INSERT INTO step (project_id, step_text, step_order) VALUES (3, 'Clean and store lawnmower', 3);
INSERT INTO category (category_name) VALUES ('Landscape');
INSERT INTO project_category (project_id, category_id) VALUES (3, 2);

INSERT INTO project (project_name, estimated_hours, actual_hours, difficulty, notes) VALUES ('Paint the living room walls', 2, 3, 2, 'Give your living room a new look');
INSERT INTO material (project_id, material_name, num_required) VALUES (4, 'primer, as needed', 1);
INSERT INTO material (project_id, material_name, num_required) VALUES (4, 'gallon of paint of desired color', 1);
INSERT INTO step (project_id, step_text, step_order) VALUES (4, 'Clean walls and spot prime damaged areas as needed. If radically changing colors, prime the entire wall.', 1);
INSERT INTO step (project_id, step_text, step_order) VALUES (4, 'Make the cuts around doors, windows and baseboards.', 1);
INSERT INTO step (project_id, step_text, step_order) VALUES (4, 'Paint room. Recoat after a few hours if needed.', 3);
INSERT INTO category (category_name) VALUES ('Painting');
INSERT INTO project_category (project_id, category_id) VALUES (4, 3);