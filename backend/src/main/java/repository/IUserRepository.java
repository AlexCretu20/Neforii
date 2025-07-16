package repository;

import model.User;

import java.util.Optional;

public interface IUserRepository extends ICrudRepository <User>{
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndPassword(String email, String password);
    boolean isUsernameTaken(String username);

}
