package in.natchapol.deliveryfoodapi.service;

import in.natchapol.deliveryfoodapi.entity.UserEntity;
import in.natchapol.deliveryfoodapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
@AllArgsConstructor
public class AppUserDetailService implements UserDetailsService{
    private final UserRepository userRepository;



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       UserEntity user = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("ไม่พบบัญชีผู้ใช้"));
       return new User(user.getEmail(), user.getPassword(), Collections.emptyList()) {
       };
    }


}
