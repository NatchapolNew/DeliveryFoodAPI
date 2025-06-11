package in.natchapol.deliveryfoodapi.controller;

import in.natchapol.deliveryfoodapi.io.UserRequest;
import in.natchapol.deliveryfoodapi.io.UserResponse;
import in.natchapol.deliveryfoodapi.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@RequestBody UserRequest request){
        return userService.registerUser(request);
    }
}
