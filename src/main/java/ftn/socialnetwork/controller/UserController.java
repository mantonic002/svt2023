package ftn.socialnetwork.controller;

import ftn.socialnetwork.model.dto.UserDTO;
import ftn.socialnetwork.model.entity.User;
import ftn.socialnetwork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserDetailsService userDetailsService;

//    @Autowired
//    AuthenticationManager authenticationManager;

//    @Autowired
//    TokenUtils tokenUtils;



//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody UserDTO userDto) {
//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword());
//        Authentication authentication = authenticationManager.authenticate(authenticationToken);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        try {
//            UserDetails userDetails = userDetailsService.loadUserByUsername(userDto.getUsername());
//            return ResponseEntity.ok(tokenUtils.generateToken(userDetails));
//        } catch (UsernameNotFoundException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
}
