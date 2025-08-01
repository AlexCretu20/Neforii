//package ro.neforii.repository;
//
//import ro.neforii.model.User;
//
//import java.util.Optional;
//
//public interface IUserRepository extends ICrudRepository<User> {
//    Optional<User> findByEmail(String email);
//
//    Optional<User> findByEmailAndPassword(String email, String password);
//
//    Optional<User> findByUsername(String username);
//
//    boolean isUsernameTaken(String username);
//}
