package in.natchapol.deliveryfoodapi.io;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenRequest {
    private  String email;
    private  String password;
}
