package chess.db.model;

import org.springframework.data.repository.CrudRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called engineRepository
// CRUD refers Create, Read, Update, Delete

public interface EngineRepository extends CrudRepository<Engine, Integer> {

}