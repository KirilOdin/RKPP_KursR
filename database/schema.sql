-- 1. Последовательность для таблицы сотрудников
CREATE SEQUENCE IF NOT EXISTS employees_seq
    MINVALUE 1
    START WITH 1
    INCREMENT BY 1;


-- 1. Таблица сотрудников
CREATE TABLE employees (
    id_employee INTEGER PRIMARY KEY DEFAULT nextval('employees_seq'),
    role VARCHAR(20) NOT NULL CHECK (role IN ('admin', 'lab_assistant', 'lab_doctor')),
    position VARCHAR(50) NOT NULL,
    last_name VARCHAR(30) NOT NULL,
    first_name VARCHAR(30) NOT NULL,
    middle_name VARCHAR(30),
    login VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(100) NOT NULL
);


ALTER SEQUENCE employees_seq OWNED BY employees.id_employee;



-- 2. Последовательность для таблицы пациентов
CREATE SEQUENCE IF NOT EXISTS patients_seq
    MINVALUE 1
    START WITH 1
    INCREMENT BY 1;


-- 2. Таблица пациентов
CREATE TABLE patients (
    id_patient INTEGER PRIMARY KEY DEFAULT nextval('patients_seq'),
    -- legal_representative VARCHAR(200),?НУЖЕН ЛИ ОПЕКУН/представитель и его ФИО?
    policy_number VARCHAR(30) UNIQUE NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    middle_name VARCHAR(50),
    gender CHAR(1) NOT NULL CHECK (gender IN ('м', 'ж')),
    birth_date DATE NOT NULL,
    phone VARCHAR(12)
);


ALTER SEQUENCE patients_seq OWNED BY patients.id_patient;



-- 3. Последовательность для таблицы организаций
CREATE SEQUENCE IF NOT EXISTS organizations_seq
    MINVALUE 1
    START WITH 1
    INCREMENT BY 1;


-- 3. Таблица организации, которая направила пациента на исследование
CREATE TABLE organizations (
    id_org INTEGER PRIMARY KEY DEFAULT nextval('organizations_seq'),
    org_name VARCHAR(200) NOT NULL,
    contract_number VARCHAR(50),
    contact_last_name VARCHAR(50),
    contact_first_name VARCHAR(50),
    contact_middle_name VARCHAR(50),
    contact_person_phone VARCHAR(12)
);


ALTER SEQUENCE organizations_seq OWNED BY organizations.id_org;



-- 4. Последовательность для таблицы статусов заказа
CREATE SEQUENCE IF NOT EXISTS order_statuses_seq
    MINVALUE 1
    START WITH 1
    INCREMENT BY 1;


-- 4. Справочник статусов заказа
CREATE TABLE order_statuses (
    id_status INTEGER PRIMARY KEY DEFAULT nextval('order_statuses_seq'),
    status_name VARCHAR(15) UNIQUE NOT NULL
);


ALTER SEQUENCE order_statuses_seq OWNED BY order_statuses.id_status;


-- Начальные данные
INSERT INTO order_statuses (status_name) VALUES
    ('зарегистрирован'), ('в работе'), ('выполнен'), ('утверждён');



-- 5. Последовательность для таблицы заказов
CREATE SEQUENCE IF NOT EXISTS orders_seq
    MINVALUE 1
    START WITH 1
    INCREMENT BY 1;


-- 5. Таблица заказов
CREATE TABLE orders (
    id_order INTEGER PRIMARY KEY DEFAULT nextval('orders_seq'),
    status_id INT NOT NULL REFERENCES order_statuses(id_status),
    patient_id INT NOT NULL REFERENCES patients(id_patient),
    organization_id INT NULL REFERENCES organizations(id_org),
    registered_by INT NOT NULL REFERENCES employees(id_employee),
    accepted_by INT NULL REFERENCES employees(id_employee),
    verified_by INT NULL REFERENCES employees(id_employee),
    registration_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    acceptance_datetime TIMESTAMP NULL,
    verification_datetime TIMESTAMP NULL
    -- barcode text CHECK(barcode ~ '^[0-9-]{10,17}$')
);


ALTER SEQUENCE orders_seq OWNED BY orders.id_order;



-- 6. Последовательность для таблицы биологических проб
CREATE SEQUENCE IF NOT EXISTS specimens_seq
    MINVALUE 1
    START WITH 1
    INCREMENT BY 1;


-- 6. Таблица биологических проб
CREATE TABLE specimens (
    id_specimen INTEGER PRIMARY KEY DEFAULT nextval('specimens_seq'),
    order_id INT NOT NULL REFERENCES orders(id_order),
    specimen_type VARCHAR(50) NOT NULL,
    container_type VARCHAR(50),
    collection_datetime TIMESTAMP NOT NULL,
    barcode text UNIQUE NOT NULL
        CHECK(barcode ~ '^[0-9-]{10,17}$')
);


ALTER SEQUENCE specimens_seq OWNED BY specimens.id_specimen;



-- 7. Последовательность для таблицы справочника анализов
CREATE SEQUENCE IF NOT EXISTS tests_seq
    MINVALUE 1
    START WITH 1
    INCREMENT BY 1;


-- 7. Справочник анализов
CREATE TABLE tests (
    id_test INTEGER PRIMARY KEY DEFAULT nextval('tests_seq'),
    test_name VARCHAR(50) NOT NULL UNIQUE,
    biomaterial VARCHAR(50) NOT NULL,
    execution_time_hours INT NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    unit VARCHAR(20) NOT NULL
);


ALTER SEQUENCE tests_seq OWNED BY tests.id_test;



-- 8. Последовательность для таблицы референсных значений
CREATE SEQUENCE IF NOT EXISTS reference_values_seq
    MINVALUE 1
    START WITH 1
    INCREMENT BY 1;


-- 8. Референсные значения
CREATE TABLE reference_values (
    id_reference INTEGER PRIMARY KEY DEFAULT nextval('reference_values_seq'),
    test_id INT NOT NULL REFERENCES tests(id_test),
    gender_applicable CHAR(1) NOT NULL CHECK (gender_applicable IN ('м', 'ж')),
    age_min INT NULL,
    age_max INT NULL,
    ref_value_min NUMERIC(10,3) NULL,
    ref_value_max NUMERIC(10,3) NULL,
    ref_text TEXT NULL,
    CONSTRAINT check_ref_type CHECK (
        (ref_value_min IS NOT NULL AND ref_value_max IS NOT NULL AND ref_text IS NULL) OR
        (ref_value_min IS NULL AND ref_value_max IS NULL AND ref_text IS NOT NULL)
    )
);


ALTER SEQUENCE reference_values_seq OWNED BY reference_values.id_reference;



--  9. Последовательность для позиции заказов
CREATE SEQUENCE IF NOT EXISTS order_items_seq
    MINVALUE 1
    START WITH 1
    INCREMENT BY 1;


-- 9. Таблица позиции заказаов
CREATE TABLE order_items (
    id_item INTEGER PRIMARY KEY DEFAULT nextval('order_items_seq'),
    order_id INT NOT NULL REFERENCES orders(id_order),
    test_id INT NOT NULL REFERENCES tests(id_test),
    specimen_id INT NOT NULL REFERENCES specimens(id_specimen),
    status VARCHAR(20) NOT NULL DEFAULT 'назначен'
        CHECK (status IN ('назначен', 'в работе', 'выполнен')),
    result_value NUMERIC(10,3) NULL,
    result_text TEXT NULL,
    is_abnormal BOOLEAN NULL,
    entered_by INT NULL REFERENCES employees(id_employee),
    entry_datetime TIMESTAMP NULL,
    CONSTRAINT one_result_type CHECK (
        (result_value IS NULL AND result_text IS NULL) OR
        (result_value IS NOT NULL OR result_text IS NOT NULL)
    )
);


ALTER SEQUENCE order_items_seq OWNED BY order_items.id_item;



-- Индексы
CREATE INDEX idx_orders_patient ON orders(patient_id);
CREATE INDEX idx_orders_status ON orders(status_id);
CREATE INDEX idx_orders_verified_by ON orders(verified_by);
CREATE INDEX idx_specimens_order ON specimens(order_id);
CREATE INDEX idx_specimens_barcode ON specimens(barcode);
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_test ON order_items(test_id);
CREATE INDEX idx_order_items_specimen ON order_items(specimen_id);
CREATE INDEX idx_order_items_status ON order_items(status);
CREATE INDEX idx_reference_test ON reference_values(test_id);
CREATE INDEX idx_organizations_name ON organizations(org_name);




-- 1. Количество анализов по видам
CREATE OR REPLACE FUNCTION get_test_count_by_type(
    from_date TIMESTAMPTZ,
    to_date TIMESTAMPTZ
)
RETURNS TABLE (test_name TEXT, cnt BIGINT)
LANGUAGE sql
AS $$
    SELECT t.test_name, COUNT(oi.id_item) AS cnt
    FROM public.order_items oi
    JOIN public.tests t ON oi.test_id = t.id_test
    JOIN public.orders o ON oi.order_id = o.id_order
    WHERE o.registration_datetime >= from_date
      AND o.registration_datetime <= to_date
      AND oi.status = 'выполнен'
    GROUP BY t.test_name
    ORDER BY cnt DESC;
$$;

-- 2. Нагрузка сотрудников
CREATE OR REPLACE FUNCTION get_workload_by_employee(
    from_date TIMESTAMPTZ,
    to_date TIMESTAMPTZ
)
RETURNS TABLE (fullname TEXT, cnt BIGINT)
LANGUAGE sql
AS $$
    SELECT e.last_name || ' ' || e.first_name || ' ' || e.middle_name AS fullname,
           COUNT(oi.id_item) AS cnt
    FROM public.order_items oi
    JOIN public.employees e ON oi.entered_by = e.id_employee
    JOIN public.orders o ON oi.order_id = o.id_order
    WHERE o.registration_datetime >= from_date
      AND o.registration_datetime <= to_date
      AND oi.status = 'выполнен'
    GROUP BY fullname
    ORDER BY cnt DESC;
$$;

-- 3. Выручка по организациям
CREATE OR REPLACE FUNCTION get_revenue_by_organization(
    from_date TIMESTAMPTZ,
    to_date TIMESTAMPTZ
)
RETURNS TABLE (org_name TEXT, total_revenue NUMERIC)
LANGUAGE sql
AS $$
    SELECT org.org_name, SUM(t.price) AS total_revenue
    FROM public.order_items oi
    JOIN public.tests t ON oi.test_id = t.id_test
    JOIN public.orders o ON oi.order_id = o.id_order
    JOIN public.organizations org ON o.organization_id = org.id_org
    WHERE o.registration_datetime >= from_date
      AND o.registration_datetime <= to_date
      AND oi.status = 'выполнен'
    GROUP BY org.org_name
    ORDER BY total_revenue DESC;
$$;






CREATE SEQUENCE IF NOT EXISTS audit_log_seq;

CREATE TABLE audit_log (
    id BIGINT PRIMARY KEY DEFAULT nextval('audit_log_seq'),
    table_name TEXT NOT NULL,
    operation TEXT NOT NULL CHECK (operation IN ('INSERT', 'UPDATE', 'DELETE')),
    record_id BIGINT NOT NULL,          -- идентификатор изменённой строки
    old_data JSONB,                     -- старые данные (для UPDATE/DELETE)
    new_data JSONB,                     -- новые данные (для INSERT/UPDATE)
    changed_by TEXT,                    -- пользователь БД, выполнивший операцию
    changed_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

ALTER SEQUENCE audit_log_seq OWNED BY audit_log.id;





CREATE OR REPLACE FUNCTION audit_trigger_func()
RETURNS TRIGGER AS $$
DECLARE
    pk_column TEXT := TG_ARGV[0];  -- имя первичного ключа, передаётся при создании триггера
    pk_value BIGINT;
BEGIN
    IF TG_OP = 'INSERT' THEN
        EXECUTE format('SELECT ($1).%I', pk_column) INTO pk_value USING NEW;
        INSERT INTO audit_log (table_name, operation, record_id, new_data, changed_by)
        VALUES (TG_TABLE_NAME, 'INSERT', pk_value, to_jsonb(NEW), current_user);
        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' THEN
        EXECUTE format('SELECT ($1).%I', pk_column) INTO pk_value USING NEW;
        INSERT INTO audit_log (table_name, operation, record_id, old_data, new_data, changed_by)
        VALUES (TG_TABLE_NAME, 'UPDATE', pk_value, to_jsonb(OLD), to_jsonb(NEW), current_user);
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        EXECUTE format('SELECT ($1).%I', pk_column) INTO pk_value USING OLD;
        INSERT INTO audit_log (table_name, operation, record_id, old_data, changed_by)
        VALUES (TG_TABLE_NAME, 'DELETE', pk_value, to_jsonb(OLD), current_user);
        RETURN OLD;
    END IF;
END;
$$ LANGUAGE plpgsql;


-- employees (PK = id_employee)
CREATE TRIGGER employees_audit
AFTER INSERT OR UPDATE OR DELETE ON public.employees
FOR EACH ROW EXECUTE FUNCTION audit_trigger_func('id_employee');



-- patients (PK = id_patient)
CREATE TRIGGER patients_audit AFTER INSERT OR UPDATE OR DELETE ON public.patients
FOR EACH ROW EXECUTE FUNCTION audit_trigger_func('id_patient');

-- orders (PK = id_order)
CREATE TRIGGER orders_audit AFTER INSERT OR UPDATE OR DELETE ON public.orders
FOR EACH ROW EXECUTE FUNCTION audit_trigger_func('id_order');

-- specimens (PK = id_specimen)
CREATE TRIGGER specimens_audit AFTER INSERT OR UPDATE OR DELETE ON public.specimens
FOR EACH ROW EXECUTE FUNCTION audit_trigger_func('id_specimen');

-- tests (PK = id_test)
CREATE TRIGGER tests_audit AFTER INSERT OR UPDATE OR DELETE ON public.tests
FOR EACH ROW EXECUTE FUNCTION audit_trigger_func('id_test');

-- order_items (PK = id_item)
CREATE TRIGGER order_items_audit AFTER INSERT OR UPDATE OR DELETE ON public.order_items
FOR EACH ROW EXECUTE FUNCTION audit_trigger_func('id_item');

-- reference_values (PK = id_reference)
CREATE TRIGGER reference_values_audit AFTER INSERT OR UPDATE OR DELETE ON public.reference_values
FOR EACH ROW EXECUTE FUNCTION audit_trigger_func('id_reference');

-- organizations (PK = id_org)
CREATE TRIGGER organizations_audit AFTER INSERT OR UPDATE OR DELETE ON public.organizations
FOR EACH ROW EXECUTE FUNCTION audit_trigger_func('id_org');