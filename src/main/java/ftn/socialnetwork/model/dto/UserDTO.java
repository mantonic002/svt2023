package ftn.socialnetwork.model.dto;

import ftn.socialnetwork.model.entity.User;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;

    private String username;

    private String password;

    private String firstName;

    private String lastName;

    private String email;

    private String displayName;

    private String description;


    public UserDTO(User createdUser) {
        this.id = createdUser.getId();
        this.username = createdUser.getUsername();
        this.email = createdUser.getEmail();
        this.firstName = createdUser.getFirstName();
        this.lastName = createdUser.getLastName();

        if (createdUser.getDisplayName() == null) {
            this.displayName = createdUser.getUsername();
        } else {
            this.displayName = createdUser.getDisplayName();
        }
    }
}
