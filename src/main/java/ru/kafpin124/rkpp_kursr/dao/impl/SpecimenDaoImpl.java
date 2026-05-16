package ru.kafpin124.rkpp_kursr.dao.impl;


import ru.kafpin124.rkpp_kursr.dao.SpecimenDao;
import ru.kafpin124.rkpp_kursr.model.Specimen;

import java.util.List;

public class SpecimenDaoImpl implements SpecimenDao {

    //TODO: Сделать мапперы + реализовать методы!


//    1. Вставка новой биологической пробы

    private static final String ADD =
            "INSERT INTO public.specimens(order_id, specimen_type, container_type, collection_datetime, barcode) " +
                    "VALUES (?, ?, ?, ?, ?);";
    @Override
    public void add(Specimen specimen) {

    }


//    2. Поиск биологической пробы ао идентификатору

    private static final String FIND_BY_ID = "SELECT id_specimen, order_id, specimen_type, container_type, " +
            "collection_datetime, barcode FROM public.specimens WHERE id_specimen = ?;";
    @Override
    public Specimen getById(Long id) {
        return null;
    }



//    3. Вывод всех биологических проб, находящихся в базе данных

    private static final String GET_ALL = "SELECT id_specimen, order_id, specimen_type, container_type, " +
            "collection_datetime, barcode FROM public.specimens;";
    @Override
    public List<Specimen> getAll() {
        return List.of();
    }




//    4. Изменение значений у существующей биологической пробы

    private static final String UPDATE = "UPDATE public.specimens SET order_id=?, " +
            "specimen_type=?, container_type=?, collection_datetime=?, barcode=? WHERE id_specimen = ?;";
    @Override
    public void update(Specimen specimen) {

    }


//    5. Удаление биологической пробы из базы данных

    private static final String DELETE = "DELETE FROM public.specimens WHERE id_specimen = ?;";
    @Override
    public void delete(Specimen specimen) {

    }


//    6. Поиск биологической пробы по штрих-коду

    private static final String FIND_BY_BARCODE = "SELECT id_specimen, order_id, specimen_type, container_type, " +
            "collection_datetime, barcode FROM public.specimens WHERE barcode = ?;";

    @Override
    public Specimen findByBarcode(String barcode) {
        return null;
    }


//    7. Поиск биологической пробы по идентификатору заказа

    private static final String FIND_BY_ORDER_ID = "SELECT id_specimen, order_id, specimen_type, container_type, " +
            "collection_datetime, barcode FROM public.specimens WHERE order_id = ?;";

    @Override
    public List<Specimen> findByOrderId(Long orderId) {
        return List.of();
    }
}
