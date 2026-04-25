CREATE TABLE engineers (
                           id UUID PRIMARY KEY,
                           full_name VARCHAR(255) NOT NULL,
                           email VARCHAR(255) NOT NULL UNIQUE,
                           active BOOLEAN NOT NULL,
                           max_tasks_per_day INTEGER NOT NULL
);

CREATE TABLE holidays (
                          id UUID PRIMARY KEY,
                          holiday_date DATE NOT NULL UNIQUE,
                          name VARCHAR(255) NOT NULL
);

CREATE TABLE equipments (
                            id UUID PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            code VARCHAR(255) NOT NULL UNIQUE,
                            active BOOLEAN NOT NULL,
                            season_type VARCHAR(32) NOT NULL,
                            serial_number VARCHAR(255),
                            notes VARCHAR(255),
                            assigned_engineer_id UUID NOT NULL REFERENCES engineers(id)
);

CREATE TABLE maintenance_rules (
                                   id UUID PRIMARY KEY,
                                   equipment_id UUID NOT NULL UNIQUE REFERENCES equipments(id),
                                   recurrence_per_year INTEGER NOT NULL,
                                   estimated_duration_minutes INTEGER NOT NULL,
                                   report_template_code VARCHAR(255) NOT NULL
);

CREATE TABLE maintenance_tasks (
                                   id UUID PRIMARY KEY,
                                   equipment_id UUID NOT NULL REFERENCES equipments(id),
                                   assigned_engineer_id UUID NOT NULL REFERENCES engineers(id),
                                   scheduled_date DATE NOT NULL,
                                   status VARCHAR(32) NOT NULL,
                                   generated_by_planner BOOLEAN NOT NULL,
                                   completed_at TIMESTAMP NULL
);

CREATE TABLE maintenance_reports (
                                     id UUID PRIMARY KEY,
                                     task_id UUID NOT NULL UNIQUE REFERENCES maintenance_tasks(id),
                                     observations TEXT NOT NULL,
                                     result VARCHAR(50) NOT NULL,
                                     performed_at TIMESTAMP NOT NULL,
                                     created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_tasks_engineer_date ON maintenance_tasks(assigned_engineer_id, scheduled_date);
CREATE INDEX idx_tasks_date ON maintenance_tasks(scheduled_date);
CREATE INDEX idx_equipments_assigned_engineer ON equipments(assigned_engineer_id);
