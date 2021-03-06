package be.kuleuven.cs.jli40d.server.db.repository;

import be.kuleuven.cs.jli40d.core.model.Game;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Pieter
 * @version 1.0
 */
@Repository
public interface GameRepository extends CrudRepository<Game, Integer>
{
    Game findOneByUuid( String uuid );
}
