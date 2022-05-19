package userService;

import java.util.HashMap;
import java.util.Scanner;

import db.DBConnectionMgr;
import lombok.Data;
import userDao.UserDao;
import userDto.User;
import userDto.UserDtl;
import userDto.UserMst;

@Data
public class UserService {
	private static UserService instance;
	private final Scanner sc;
	private UserMst um; //user session
	private UserDtl ud;
	private final UserDao userDao;
	
	public UserService(Scanner sc, DBConnectionMgr pool) {
		this.sc = sc;
		this.userDao = new UserDao(pool);
	}
	
	public int greeting() {
		HashMap<String, User> result = userDao.helpSignin(sc);
		if (result != null) {
			um = (UserMst) result.get("um");
			ud = (UserDtl) result.get("ud");
			return 1;
		}
		else return 0;
	}
	
	public void update() {
		HashMap<String, User> result = userDao.updateInfo(um.getUsername(), sc);
		um = (UserMst) result.get("um");
		ud = (UserDtl) result.get("ud");
	}

	
}
