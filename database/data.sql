-- 1. Сотрудники (employees)
-- Пароль для всех (кроме doctor) "admin", хеш BCrypt $2a$12$d41HW3xF2ga6UGt.5EfAF.dujgV2gpL.5DUegsG3r.fNEH8HTw0aC
-- У лаборанта-доктора пароль "doctor" (или задать через админа при входе), хеш $2a$12$8vM7sQ4vBz6P0zG3kWbUrujv7F/qN7ILjQ0v3OyL5FJq2kODKuqHe
INSERT INTO public.employees (role, "position", last_name, first_name, middle_name, login, password_hash) VALUES
('admin',      'Системный администратор', 'Иванов',   'Иван',     'Васильевич', 'adminchik', '$2a$12$d41HW3xF2ga6UGt.5EfAF.dujgV2gpL.5DUegsG3r.fNEH8HTw0aC'),
('lab_assistant', 'Фельдшер-лаборант',     'Петрова',  'Мария',    'Сергеевна',  'maria',     '$2a$12$d41HW3xF2ga6UGt.5EfAF.dujgV2gpL.5DUegsG3r.fNEH8HTw0aC'),
('lab_assistant', 'Фельдшер-лаборант',     'Смирнов',  'Алексей',  'Игоревич',   'alex',      '$2a$12$d41HW3xF2ga6UGt.5EfAF.dujgV2gpL.5DUegsG3r.fNEH8HTw0aC'),
('lab_doctor',    'Врач-лаборант',         'Козлова',  'Елена',    'Петровна',   'elena',     '$2a$12$8vM7sQ4vBz6P0zG3kWbUrujv7F/qN7ILjQ0v3OyL5FJq2kODKuqHe');

-- 2. Пациенты (patients)
INSERT INTO public.patients (policy_number, last_name, first_name, middle_name, gender, birth_date, phone) VALUES
('1234567890123456', 'Андреев',   'Андрей',  'Андреевич',  'м', '1985-03-12', '+71234567890'),
('2234567890123457', 'Борисова',  'Борис',   'Борисович',  'м', '1990-07-23', '+79876543210'),
('3234567890123458', 'Викторова', 'Виктория','Викторовна', 'ж', '1978-11-05', '+71112223344'),
('4234567890123459', 'Георгиев',  'Георгий', 'Георгиевич', 'м', '2000-01-15', '+75556667788');

-- 3. Организации (organizations)
INSERT INTO public.organizations (org_name, contract_number, contact_last_name, contact_first_name, contact_middle_name, contact_person_phone) VALUES
('ООО "МедСервис"',   'Д-2025/001', 'Сидоров',   'Петр',   'Петрович', '+79012345678'),
('АО "Здоровье"',     'К-2025/102', 'Кузнецова', 'Анна',   'Ивановна', '+79162345678'),
('ИП Иванов И.И.',    'Б/Н',        'Иванов',    'Иван',   'Иванович', '+79261234567');

-- 4. Справочник анализов (tests)
INSERT INTO public.tests (test_name, biomaterial, execution_time_hours, price, unit) VALUES
('Общий анализ крови',        'кровь', 1,  500.00, '10^9/л'),
('Глюкоза',                   'кровь', 1,  300.00, 'ммоль/л'),
('Общий белок',               'кровь', 2,  400.00, 'г/л'),
('Общий анализ мочи',         'моча',  1,  350.00, ''),
('Бактериологический посев',  'мазок', 48, 1200.00, 'КОЕ/мл');

-- 5. Референсные значения (reference_values)
-- Для каждого теста добавляем нормы (муж/жен, возрастные диапазоны)
-- Общий анализ крови (лейкоциты)
INSERT INTO public.reference_values (test_id, gender_applicable, age_min, age_max, ref_value_min, ref_value_max, ref_text) VALUES
(1, 'м', 18, 120, 4.0, 9.0, NULL),
(1, 'ж', 18, 120, 4.0, 9.0, NULL),
-- Глюкоза (натощак)
(2, 'м', 18, 120, 3.3, 5.5, NULL),
(2, 'ж', 18, 120, 3.3, 5.5, NULL),
-- Общий белок
(3, 'м', 18, 120, 65.0, 85.0, NULL),
(3, 'ж', 18, 120, 65.0, 85.0, NULL),
-- Общий анализ мочи (цвет – текстовое значение)
(4, 'м', 18, 120, NULL, NULL, 'соломенно-жёлтый'),
(4, 'ж', 18, 120, NULL, NULL, 'соломенно-жёлтый'),
-- Бактериологический посев (рост микрофлоры)
(5, 'м', 18, 120, NULL, NULL, 'отсутствие роста'),
(5, 'ж', 18, 120, NULL, NULL, 'отсутствие роста');

-- 6. Статусы заказов
-- INSERT INTO public.order_statuses (status_name) VALUES ('зарегистрирован'), ('в работе'), ('выполнен'), ('утверждён');

-- 7. Заказы, пробы, позиции заказов

-- Заказ 1: зарегистрирован, пациент Андреев, лаборант Петрова
INSERT INTO public.orders (status_id, patient_id, organization_id, registered_by, registration_datetime)
VALUES (1, 1, NULL, 2, '2026-05-20 09:00:00');

INSERT INTO public.specimens (order_id, specimen_type, container_type, collection_datetime, barcode)
VALUES (1, 'кровь', 'пробирка вакуумная', '2026-05-20 08:45:00', '1234567890123');

INSERT INTO public.order_items (order_id, test_id, specimen_id, status)
VALUES (1, 1, 1, 'назначен'),
       (1, 2, 1, 'назначен');

-- Заказ 2: в работе, пациент Борисова, лаборант Смирнов, организация ООО "МедСервис"
INSERT INTO public.orders (status_id, patient_id, organization_id, registered_by, registration_datetime, accepted_by, acceptance_datetime)
VALUES (2, 2, 1, 3, '2026-05-21 10:15:00', 3, '2026-05-21 10:30:00');

INSERT INTO public.specimens (order_id, specimen_type, container_type, collection_datetime, barcode)
VALUES (2, 'моча', 'контейнер стерильный', '2026-05-21 10:00:00', '2234567890124');

INSERT INTO public.order_items (order_id, test_id, specimen_id, status)
VALUES (2, 4, 2, 'назначен');

-- Заказ 3: выполнен, пациент Викторова, лаборант Петрова, врач Козлова
INSERT INTO public.orders (status_id, patient_id, registered_by, registration_datetime, verified_by, verification_datetime)
VALUES (3, 3, 2, '2026-05-22 12:00:00', 4, '2026-05-22 14:00:00');

INSERT INTO public.specimens (order_id, specimen_type, container_type, collection_datetime, barcode)
VALUES (3, 'кровь', 'пробирка вакуумная', '2026-05-22 11:45:00', '3234567890125');

-- Позиции с уже введёнными результатами
INSERT INTO public.order_items (order_id, test_id, specimen_id, status, result_value, is_abnormal, entered_by, entry_datetime)
VALUES (3, 1, 3, 'выполнен', 5.2, false, 2, '2026-05-22 13:30:00'),
       (3, 2, 3, 'выполнен', 6.1, true, 2, '2026-05-22 13:35:00');  -- глюкоза повышена

-- Заказ 4: утверждён, пациент Георгиев, организация АО "Здоровье", врач Козлова
INSERT INTO public.orders (status_id, patient_id, organization_id, registered_by, registration_datetime, verified_by, verification_datetime)
VALUES (4, 4, 2, 2, '2026-05-23 08:00:00', 4, '2026-05-23 10:30:00');

INSERT INTO public.specimens (order_id, specimen_type, container_type, collection_datetime, barcode)
VALUES (4, 'мазок', 'пробирка с транспортной средой', '2026-05-23 07:50:00', '4234567890126');

INSERT INTO public.order_items (order_id, test_id, specimen_id, status, result_value, result_text, is_abnormal, entered_by, entry_datetime)
VALUES (4, 5, 4, 'выполнен', NULL, 'рост Staphylococcus epidermidis 10^3 КОЕ/мл', true, 2, '2026-05-23 09:30:00');

-- Заказ 5: зарегистрирован, пациент Андреев, без организации
INSERT INTO public.orders (status_id, patient_id, registered_by, registration_datetime)
VALUES (1, 1, 3, '2026-05-24 11:00:00');

INSERT INTO public.specimens (order_id, specimen_type, container_type, collection_datetime, barcode)
VALUES (5, 'кровь', 'пробирка вакуумная', '2026-05-24 10:50:00', '5234567890127');

INSERT INTO public.order_items (order_id, test_id, specimen_id, status)
VALUES (5, 3, 5, 'назначен');