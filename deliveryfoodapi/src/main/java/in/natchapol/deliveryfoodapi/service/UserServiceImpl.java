package in.natchapol.deliveryfoodapi.service;

import in.natchapol.deliveryfoodapi.entity.UserEntity;
import in.natchapol.deliveryfoodapi.exception.UserAlreadyExistsException;
import in.natchapol.deliveryfoodapi.io.UserRequest;
import in.natchapol.deliveryfoodapi.io.UserResponse;
import in.natchapol.deliveryfoodapi.repository.UserRepository;
import lombok.AllArgsConstructor;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenFacade authenFacade;


    @Override

    public UserResponse registerUser(UserRequest request) {
      Optional <UserEntity> user = userRepository.findByEmail(request.getEmail());
        if(user.isPresent()){
            throw new UserAlreadyExistsException("มีEmailนี้แล้วในระบบ");
        }else {
       UserEntity newUser = convertToEntity(request);
       newUser = userRepository.save(newUser);
       return convertToResponse(newUser);
        }
    }

    @Override
    public String findUserId() {
    String logginEmail = authenFacade.getAuthen().getName();
    UserEntity logginUser = userRepository.findByEmail(logginEmail).orElseThrow(()->new UsernameNotFoundException("ไม่พบผู้ใช้งาน"));
    return logginUser.getId();
    }

    private UserEntity convertToEntity(UserRequest request){
        return UserEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .build();

    }


    private UserResponse convertToResponse(UserEntity registeredUser){
        return UserResponse.builder()
                .id(registeredUser.getId())
                .name(registeredUser.getName())
                .email(registeredUser.getEmail())
                .build();
    }

}
