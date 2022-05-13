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
public class UserMst extends User {
	private int usercode;
	private String username;
	private String password;
	private String name;
	private LocalDateTime create_date;
	private LocalDateTime update_date;
	
}
