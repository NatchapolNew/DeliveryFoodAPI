package in.natchapol.deliveryfoodapi.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem {
    private String foodId;
    private int quantity;
    private double price;
    private String name;
}
