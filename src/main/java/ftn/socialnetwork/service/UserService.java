package ftn.socialnetwork.service;


import ftn.socialnetwork.model.entity.User;
import ftn.socialnetwork.model.dto.UserDTO;

public interface UserService {

    User findByUsername(String username);

    User createUser(UserDTO userDTO);
}
