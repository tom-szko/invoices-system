package pl.coderstrust.repository;

import java.io.Serializable;
import java.util.Optional;

public interface BaseRepository<T, ID extends Serializable> {

  <S extends T> S save(S var1) throws RepositoryOperationException;

  Optional<T> findById(ID var1) throws RepositoryOperationException;

  boolean existsById(ID var1) throws RepositoryOperationException;

  Iterable<T> findAll() throws RepositoryOperationException;

  long count() throws RepositoryOperationException;

  void deleteById(ID var1) throws RepositoryOperationException;

  void deleteAll() throws RepositoryOperationException;
}
