CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE equipment_category (
                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    name VARCHAR(120) NOT NULL,
                                    description VARCHAR(500),
                                    active BOOLEAN NOT NULL DEFAULT TRUE,
                                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP
);

ALTER TABLE equipments
    ADD COLUMN category_id UUID;

INSERT INTO equipment_category (name, description, active)
VALUES ('Uncategorized', 'Default category for existing equipment records.', TRUE);

UPDATE equipments
SET category_id = (
    SELECT id FROM equipment_category WHERE name = 'Uncategorized' LIMIT 1
    )
WHERE category_id IS NULL;

ALTER TABLE equipments
    ALTER COLUMN category_id SET NOT NULL;

ALTER TABLE equipments
    ADD CONSTRAINT fk_equipment_category
        FOREIGN KEY (category_id)
            REFERENCES equipment_category(id);

CREATE INDEX idx_equipments_category_id ON equipments(category_id);
CREATE INDEX idx_equipments_category_active ON equipment_category(active);