package repository;

import model.User;
import utils.DatabaseConnection;

import javax.naming.OperationNotSupportedException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository implements IUserRepository {

    @Override
    public void save(User user) {
        String sql = "INSERT INTO users (username, email, password, phone_number, description) VALUES (?, ?, ?, ?, ?)";
        try(Connection connection = DatabaseConnection.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getPhoneNumber());
            preparedStatement.setString(5, user.getDescription());
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM USERS WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                User user = new User(
                        //resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("phone_number"),
                        resultSet.getString("description")
                        //resultSet.getTimestamp("created_at").toLocalDateTime()
                );
                //TO DO: wrapper
                user.setId(resultSet.getInt("id"));
                user.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                return Optional.of(user);
            } else {
                return Optional.empty();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        try(Connection connection = DatabaseConnection.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<User> users = new ArrayList<>();

            while(resultSet.next()) {
                User user = new User(
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("phone_number"),
                        resultSet.getString("description")
                );
                user.setId(resultSet.getInt("id"));
                user.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                users.add(user);
            }

            return users;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return List.of();
    }

    @Override
    public void update(User entity) throws OperationNotSupportedException {
        throw new OperationNotSupportedException("TO DO");
    }


    @Override
    public void deleteById(int id) {
        Optional<User> userOptional = findById(id);
        if(userOptional.isEmpty()){
            System.out.println("The entered user does not exist in order to be deleted!\n");
        }
        else{
            String sql = "DELETE FROM users WHERE id = ?";
            try(Connection connection = DatabaseConnection.getConnection()){
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, id);

                //affected rows by sql statement;>0 means it has been deleted
                int rowsAffected = preparedStatement.executeUpdate();

                if(rowsAffected > 0){
                    System.out.printf("The user has been deleted succesfully\n");
                }
                else{
                    System.out.println("Could not delete user\n");
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    @Override
    public Optional<User> findByEmail(String email){
        String sql="SELECT * FROM users WHERE email = ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)){

            preparedStatement.setString(1,email);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                User user = new User(
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("phone_number"),
                        resultSet.getString("description")
                );
                user.setId(resultSet.getInt("id"));
                user.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                return Optional.of(user);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password){
        String sql="SELECT * FROM users WHERE email = ? AND password = ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)){

            preparedStatement.setString(1,email);
            preparedStatement.setString(2,password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                User user = new User(
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("phone_number"),
                        resultSet.getString("description")
                );
                user.setId(resultSet.getInt("id"));
                user.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                return Optional.of(user);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean isUsernameTaken(String username) {
        String sql="SELECT 1 FROM users WHERE username = ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)){

            preparedStatement.setString(1,username);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }



}
