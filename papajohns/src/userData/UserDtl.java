package userData;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDtl extends User {
	private int usercode;
	private String address;
	private String phone;
	private String preference;
	private LocalDateTime create_date;
	private LocalDateTime update_date;
}
