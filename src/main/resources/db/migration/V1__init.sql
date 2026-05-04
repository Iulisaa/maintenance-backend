CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE engineers (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                           full_name VARCHAR(150) NOT NULL,
                           email VARCHAR(255) NOT NULL,
                           active BOOLEAN NOT NULL DEFAULT TRUE,
                           max_tasks_per_day INTEGER NOT NULL DEFAULT 5,

                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                           CONSTRAINT uk_engineers_email UNIQUE (email),

                           CONSTRAINT chk_engineers_max_tasks_per_day
                               CHECK (max_tasks_per_day > 0)
);

CREATE TABLE equipments (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                            name VARCHAR(200) NOT NULL,
                            code VARCHAR(100) NOT NULL,
                            active BOOLEAN NOT NULL DEFAULT TRUE,

                            season_type VARCHAR(30) NOT NULL,

                            frequency_per_year INTEGER NOT NULL,
                            estimated_duration_minutes INTEGER NOT NULL,

                            serial_number VARCHAR(150),
                            notes TEXT,

                            default_engineer_id UUID,

                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT uk_equipments_code UNIQUE (code),

                            CONSTRAINT fk_equipments_default_engineer
                                FOREIGN KEY (default_engineer_id)
                                    REFERENCES engineers (id),

                            CONSTRAINT chk_equipments_season_type
                                CHECK (season_type IN ('COLD', 'HEAT', 'UNIVERSAL')),

                            CONSTRAINT chk_equipments_frequency_per_year
                                CHECK (frequency_per_year > 0),

                            CONSTRAINT chk_equipments_estimated_duration_minutes
                                CHECK (estimated_duration_minutes > 0)
);

CREATE TABLE equipment_active_months (
                                         equipment_id UUID NOT NULL,
                                         month_number INTEGER NOT NULL,

                                         CONSTRAINT pk_equipment_active_months
                                             PRIMARY KEY (equipment_id, month_number),

                                         CONSTRAINT fk_equipment_active_months_equipment
                                             FOREIGN KEY (equipment_id)
                                                 REFERENCES equipments (id)
                                                 ON DELETE CASCADE,

                                         CONSTRAINT chk_equipment_active_months_month_number
                                             CHECK (month_number BETWEEN 1 AND 12)
);

CREATE TABLE inspection_tasks (
                                  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                                  equipment_id UUID NOT NULL,
                                  assigned_engineer_id UUID,

                                  planned_date DATE NOT NULL,
                                  planned_year INTEGER NOT NULL,

                                  occurrence_number INTEGER,
                                  generation_key VARCHAR(150),

                                  source VARCHAR(30) NOT NULL DEFAULT 'GENERATED',
                                  status VARCHAR(30) NOT NULL DEFAULT 'PLANNED',

                                  completed_at TIMESTAMP,

                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                  CONSTRAINT fk_inspection_tasks_equipment
                                      FOREIGN KEY (equipment_id)
                                          REFERENCES equipments (id),

                                  CONSTRAINT fk_inspection_tasks_assigned_engineer
                                      FOREIGN KEY (assigned_engineer_id)
                                          REFERENCES engineers (id),

                                  CONSTRAINT uk_inspection_tasks_equipment_generation_key
                                      UNIQUE (equipment_id, generation_key),

                                  CONSTRAINT chk_inspection_tasks_source
                                      CHECK (source IN ('GENERATED', 'MANUAL')),

                                  CONSTRAINT chk_inspection_tasks_status
                                      CHECK (status IN ('PLANNED', 'ASSIGNED', 'COMPLETED', 'CANCELLED', 'SKIPPED')),

                                  CONSTRAINT chk_inspection_tasks_generated_fields
                                      CHECK (
                                          (
                                              source = 'GENERATED'
                                                  AND occurrence_number IS NOT NULL
                                                  AND occurrence_number > 0
                                                  AND generation_key IS NOT NULL
                                                  AND btrim(generation_key) <> ''
                                              )
                                              OR
                                          (
                                              source = 'MANUAL'
                                                  AND occurrence_number IS NULL
                                                  AND generation_key IS NULL
                                              )
                                          ),

                                  CONSTRAINT chk_inspection_tasks_planned_year
                                      CHECK (planned_year BETWEEN 2000 AND 2100),

                                  CONSTRAINT chk_inspection_tasks_completed_at
                                      CHECK (
                                          (status = 'COMPLETED' AND completed_at IS NOT NULL)
                                              OR
                                          (status <> 'COMPLETED')
                                          )
);

CREATE TABLE inspection_reports (
                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                                    report_number VARCHAR(50) NOT NULL,
                                    report_type VARCHAR(30) NOT NULL,
                                    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',

                                    report_template_code VARCHAR(100) NOT NULL DEFAULT 'DEFAULT',

                                    file_name VARCHAR(255),
                                    content_type VARCHAR(100),
                                    storage_path TEXT,
                                    file_size_bytes BIGINT,

                                    generated_at TIMESTAMP,
                                    finalized_at TIMESTAMP,

                                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                    CONSTRAINT uk_inspection_reports_report_number
                                        UNIQUE (report_number),

                                    CONSTRAINT uk_inspection_reports_storage_path
                                        UNIQUE (storage_path),

                                    CONSTRAINT chk_inspection_reports_report_type
                                        CHECK (report_type IN ('SINGLE_EQUIPMENT', 'MULTI_EQUIPMENT')),

                                    CONSTRAINT chk_inspection_reports_status
                                        CHECK (status IN ('DRAFT', 'FINALIZED')),

                                    CONSTRAINT chk_inspection_reports_file_size_bytes
                                        CHECK (file_size_bytes IS NULL OR file_size_bytes >= 0),

                                    CONSTRAINT chk_inspection_reports_finalized_at
                                        CHECK (
                                            (status = 'FINALIZED' AND finalized_at IS NOT NULL)
                                                OR
                                            (status = 'DRAFT')
                                            )
);

CREATE TABLE inspection_report_items (
                                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                                         inspection_report_id UUID NOT NULL,
                                         inspection_task_id UUID NOT NULL,
                                         engineer_id UUID NOT NULL,

                                         observations TEXT NOT NULL,
                                         result VARCHAR(50) NOT NULL,

                                         performed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                         CONSTRAINT fk_inspection_report_items_report
                                             FOREIGN KEY (inspection_report_id)
                                                 REFERENCES inspection_reports (id)
                                                 ON DELETE CASCADE,

                                         CONSTRAINT fk_inspection_report_items_task
                                             FOREIGN KEY (inspection_task_id)
                                                 REFERENCES inspection_tasks (id),

                                         CONSTRAINT fk_inspection_report_items_engineer
                                             FOREIGN KEY (engineer_id)
                                                 REFERENCES engineers (id),

                                         CONSTRAINT uk_inspection_report_items_task_id
                                             UNIQUE (inspection_task_id),

                                         CONSTRAINT chk_inspection_report_items_result
                                             CHECK (result IN ('PASSED', 'FAILED', 'FOLLOW_UP')),

                                         CONSTRAINT chk_inspection_report_items_observations_not_blank
                                             CHECK (btrim(observations) <> '')
);

CREATE INDEX idx_engineers_active
    ON engineers (active);

CREATE INDEX idx_equipments_active
    ON equipments (active);

CREATE INDEX idx_equipments_season_type
    ON equipments (season_type);

CREATE INDEX idx_equipments_default_engineer_id
    ON equipments (default_engineer_id);

CREATE INDEX idx_equipment_active_months_month_number
    ON equipment_active_months (month_number);

CREATE INDEX idx_inspection_tasks_equipment_id
    ON inspection_tasks (equipment_id);

CREATE INDEX idx_inspection_tasks_assigned_engineer_id
    ON inspection_tasks (assigned_engineer_id);

CREATE INDEX idx_inspection_tasks_planned_date
    ON inspection_tasks (planned_date);

CREATE INDEX idx_inspection_tasks_status_planned_date
    ON inspection_tasks (status, planned_date);

CREATE INDEX idx_inspection_tasks_engineer_planned_date
    ON inspection_tasks (assigned_engineer_id, planned_date);

CREATE INDEX idx_inspection_tasks_equipment_planned_date
    ON inspection_tasks (equipment_id, planned_date);

CREATE INDEX idx_inspection_tasks_generation_key
    ON inspection_tasks (generation_key);

CREATE INDEX idx_inspection_reports_status
    ON inspection_reports (status);

CREATE INDEX idx_inspection_reports_generated_at
    ON inspection_reports (generated_at);

CREATE INDEX idx_inspection_report_items_report_id
    ON inspection_report_items (inspection_report_id);

CREATE INDEX idx_inspection_report_items_task_id
    ON inspection_report_items (inspection_task_id);

CREATE INDEX idx_inspection_report_items_engineer_id
    ON inspection_report_items (engineer_id);

CREATE INDEX idx_inspection_report_items_result
    ON inspection_report_items (result);