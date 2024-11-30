package controller.impl;

import controller.UserController;
import dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import service.UserService;
import util.HttpStatusCode;

public class UserControllerImpl implements UserController {
    private UserService userService;

    public ResponseEntity<HttpStatusCode> createNewUser(@RequestBody UserDTO registerRequest) {
        int response = userService.createNewUser(registerRequest);
        return ResponseEntity.status(response).body(HttpStatusCode.valueOf(response));
    }

    public ResponseEntity<HttpStatusCode> login(@RequestBody UserDTO loginRequest) {
        int response = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.status(response).body(HttpStatusCode.valueOf(response));
    }
}







//        if ("user".equals(loginRequest.getUsername()) && "password".equals(loginRequest.getPassword())) {
//            return ResponseEntity.ok("Login successful");
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
//        }


