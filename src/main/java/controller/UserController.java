package controller;

import dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import util.HttpStatusCode;

@RestController
@RequestMapping("/api/users/")
public interface UserController {
    @PostMapping("/register")
    ResponseEntity<HttpStatusCode> createNewUser(@RequestBody UserDTO registerRequest);

    @PostMapping("/login")
    ResponseEntity<HttpStatusCode> login(@RequestBody UserDTO loginRequest);
}
