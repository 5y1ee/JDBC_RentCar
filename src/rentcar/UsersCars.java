package rentcar;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UsersCars {
	int ucno; // pk
	int paymentsId; // fk
	int carId;
	int userId;
	String startDate;
	String endDate;
	int price;
	String status;
	
	
	
}
