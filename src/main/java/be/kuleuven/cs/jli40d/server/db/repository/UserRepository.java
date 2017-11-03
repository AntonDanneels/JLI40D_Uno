package be.kuleuven.cs.jli40d.server.db.repository;

import be.kuleuven.cs.jli40d.core.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Pieter
 * @version 1.0
 */
@Repository
public interface UserRepository extends CrudRepository<User, Integer>
{
    User findUserByUsernameIgnoreCase(String username);

    List<User> findAllByOrderByScoreDesc();
}
