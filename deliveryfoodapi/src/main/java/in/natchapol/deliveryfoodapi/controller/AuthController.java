package in.natchapol.deliveryfoodapi.controller;

import com.sun.jdi.request.DuplicateRequestException;
import in.natchapol.deliveryfoodapi.io.AuthenRequest;
import in.natchapol.deliveryfoodapi.io.AuthenResponse;
import in.natchapol.deliveryfoodapi.service.AppUserDetailService;
import in.natchapol.deliveryfoodapi.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final AppUserDetailService userDetailService;
    private final JwtUtil jwtUtil;


    @PostMapping("/login")
    public AuthenResponse login(@RequestBody AuthenRequest request){
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
    final UserDetails userDetails = userDetailService.loadUserByUsername(request.getEmail());
    final String jwtToken = jwtUtil.generateToken(userDetails);
    return new AuthenResponse(request.getEmail(),jwtToken);
    }
}

