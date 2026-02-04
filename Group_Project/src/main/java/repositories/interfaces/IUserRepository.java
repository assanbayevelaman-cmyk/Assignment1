package org.example.repositories.interfaces;

import org.example.entities.User;
import java.util.List;

public interface IUserRepository {
    boolean createUser(User user);
    User getUser(int id);
    User getUserByUsername(String username);
    User getUserByEmail(String email);
    List<User> getAllUsers();
    List<User> getUsersByRole(String role);
    boolean updateUser(User user);
    boolean updatePassword(int userId, String newPassword);
    boolean updateRole(int userId, String newRole);
    boolean deleteUser(int id);
    User authenticate(String username, String password);
    boolean usernameExists(String username);
    boolean emailExists(String email);
    int getUserCount();
    int getUserCountByRole(String role);
}