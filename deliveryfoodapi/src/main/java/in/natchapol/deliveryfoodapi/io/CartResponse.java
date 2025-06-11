package in.natchapol.deliveryfoodapi.io;

import lombok.*;

import java.util.HashMap;
import java.util.Map;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {
    private String id;
    private String userId;
    private Map<String,Integer> items = new HashMap<>();

}
