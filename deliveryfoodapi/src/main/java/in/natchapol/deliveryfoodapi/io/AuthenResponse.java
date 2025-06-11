package in.natchapol.deliveryfoodapi.io;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenResponse {
    private String email;
    private String token;

}
