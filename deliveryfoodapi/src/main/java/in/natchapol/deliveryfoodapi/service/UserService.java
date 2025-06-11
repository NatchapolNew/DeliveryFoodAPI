package in.natchapol.deliveryfoodapi.service;

import in.natchapol.deliveryfoodapi.io.UserRequest;
import in.natchapol.deliveryfoodapi.io.UserResponse;

public interface UserService {
    UserResponse registerUser(UserRequest request);
    String findUserId();
}
