package lk.ijse.dep9.dao;

import lk.ijse.dep9.dao.exception.ConstraintViolationException;
import lk.ijse.dep9.entity.SuperEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface CrudDAO<T extends SuperEntity, ID extends Serializable> extends SuperDAO {

    long count();

    boolean existsById(ID PK);

    void deleteById(ID PK) throws ConstraintViolationException;

    List<T> findAll();

    Optional<T> findById(ID PK);

    T save(T entity);

    T update(T entity);

}
