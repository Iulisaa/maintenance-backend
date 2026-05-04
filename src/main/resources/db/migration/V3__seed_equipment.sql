ALTER TABLE equipment_category
    ADD CONSTRAINT uk_equipment_category_name UNIQUE (name);

INSERT INTO equipment_category (id, name)
VALUES
    (gen_random_uuid(), 'Panouri electrice'),
    (gen_random_uuid(), 'Transformatoare'),
    (gen_random_uuid(), 'Generatoare'),
    (gen_random_uuid(), 'Sisteme UPS'),
    (gen_random_uuid(), 'Sisteme de iluminat'),
    (gen_random_uuid(), 'Prize si circuite'),
    (gen_random_uuid(), 'Motoare electrice'),
    (gen_random_uuid(), 'Instalatii HVAC'),
    (gen_random_uuid(), 'Sisteme de siguranta'),
    (gen_random_uuid(), 'Sisteme de impamantare'),
    (gen_random_uuid(), 'Echipamente de masura'),
    (gen_random_uuid(), 'Rooftop'),
    (gen_random_uuid(), 'Aeroterme'),
    (gen_random_uuid(), 'Boiler'),
    (gen_random_uuid(), 'Chiller'),
    (gen_random_uuid(), 'Hidranti'),
    (gen_random_uuid(), 'Statie pompe'),
    (gen_random_uuid(), 'Retele'),
    (gen_random_uuid(), 'Cazane')
    ON CONFLICT (name) DO NOTHING;