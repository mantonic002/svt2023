package ftn.socialnetwork.service.implementation;

import ftn.socialnetwork.model.dto.LoginRequest;
import ftn.socialnetwork.model.dto.RegisterRequest;
import ftn.socialnetwork.model.entity.Roles;
import ftn.socialnetwork.model.entity.User;
import ftn.socialnetwork.repository.UserRepository;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public void signup(RegisterRequest registerRequest) {
        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Roles.USER);
        user.setDeleted(false);

        userRepository.save(user);

    }

    public UserDetails login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        UserDetails user = (UserDetails) authenticate.getPrincipal();
        return user;
    }
}
