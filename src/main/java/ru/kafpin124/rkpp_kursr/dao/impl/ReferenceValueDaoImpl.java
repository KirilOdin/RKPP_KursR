package ru.kafpin124.rkpp_kursr.dao.impl;



import ru.kafpin124.rkpp_kursr.dao.ReferenceValueDao;
import ru.kafpin124.rkpp_kursr.model.ReferenceValue;

import java.util.List;

public class ReferenceValueDaoImpl implements ReferenceValueDao {


    //TODO: Сделать мапперы + реализовать методы!


//    1. Вставка нового референсного значения

    private static final String ADD = "INSERT INTO public.reference_values(test_id, gender_applicable, age_min, age_max, ref_value_min, ref_value_max, ref_text) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?);";

    @Override
    public void add(ReferenceValue referenceValue) {

    }


//    2. Поиск референсного значения по идентификатору

    private static final String GET_BY_ID = "SELECT id_reference, test_id, gender_applicable, " +
            "age_min, age_max, ref_value_min, ref_value_max, ref_text " +
            "FROM public.reference_values WHERE id_reference = ?;";


    @Override
    public ReferenceValue getById(Long id) {
        return null;
    }


//    3. Вывод всех референсных значений, находящихся в базе данных

    private static final String GET_ALL = "SELECT id_reference, test_id, gender_applicable, " +
            "age_min, age_max, ref_value_min, ref_value_max, ref_text " +
            "FROM public.reference_values;";

    @Override
    public List<ReferenceValue> getAll() {
        return List.of();
    }


//    4. Изменение значений у существующего референсного значения

    private static final String UPDATE = "UPDATE public.reference_values SET test_id=?, gender_applicable=?, " +
            "age_min=?, age_max=?, ref_value_min=?, ref_value_max=?, ref_text=? WHERE id_reference = ?;";

    @Override
    public void update(ReferenceValue referenceValue) {

    }

//    5.

    private static final String DELETE = "DELETE FROM public.reference_values WHERE id_reference = ?;";

    @Override
    public void delete(ReferenceValue referenceValue) {

    }

//    6.

    private static final String FIND_BY_TEST_ID = "SELECT id_reference, test_id, gender_applicable, " +
            "age_min, age_max, ref_value_min, ref_value_max, ref_text " +
            "FROM public.reference_values WHERE test_id = ?;";

    @Override
    public List<ReferenceValue> findByTestId(Long testId) {
        return List.of();
    }

//    7.


    private static final String FIND_BY_TEST_AND_GENDER_AND_AGE = "SELECT id_reference, test_id, gender_applicable, " +
            "age_min, age_max, ref_value_min, ref_value_max, ref_text " +
            "FROM public.reference_values WHERE test_id = ? " +
            "AND gender_applicable = ? " +
            "AND (age_min IS NULL OR age_min <= ?) " +
            "AND (age_max IS NULL OR age_max >= ?);";


    @Override
    public ReferenceValue findByTestAndGenderAndAge(Long testId, char gender, int age) {
        return null;
    }
}
