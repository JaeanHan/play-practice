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
public class UserService extends UserDao{
	private static UserService instance;
	private final Scanner sc;
	private UserMst um;
	private UserDtl ud;
	
	private UserService(DBConnectionMgr pool, Scanner sc) {
		super(pool); //추후에 수정예정
		this.sc = sc;
	}
	
	public static UserService getInstance(DBConnectionMgr pool, Scanner sc) {
		if (instance == null) {
			instance = new UserService(pool, sc);
		}
		return instance;
	}
	
	public int greeting() {
		HashMap<String, User> result = helpSignin(sc);
		if (result != null) {
			um = (UserMst) result.get("um");
			ud = (UserDtl) result.get("ud");
			return 1;
		}
		else return 0;
	}
	
	public void update() {
		HashMap<String, User> result = updateInfo(um.getUsername(), sc);
		um = (UserMst) result.get("um");
		ud = (UserDtl) result.get("ud");
	}

	
}
