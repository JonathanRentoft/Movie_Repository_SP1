package app.dao;

import java.util.List;

public interface CrudDAO<T, ID> {
    void save(T t);
    T findById(ID id);
    List<T> getAll();
    void update(T t);
    void delete(ID id);
}