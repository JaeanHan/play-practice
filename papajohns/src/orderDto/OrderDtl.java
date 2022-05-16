package orderDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDtl extends Order{
	private int ordercode;
	private int productCode;
	private int quantity;
	private String howToGet;
}
