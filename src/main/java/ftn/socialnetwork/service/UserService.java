package ftn.socialnetwork.service;


import ftn.socialnetwork.model.entity.User;
import ftn.socialnetwork.model.dto.UserDTO;

public interface UserService {

    User findByUsername(String username);

    User findById(Long id);

    public boolean verifyPassword(User user, String password);

    public User updateUser(UserDTO updatedUser);


    public void updatePassword(User user, String newPassword);

    User createUser(UserDTO userDTO);
}
