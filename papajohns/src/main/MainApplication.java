package main;

import java.util.Scanner;

import db.DBConnectionMgr;
import userDao.UserDao;

public class MainApplication {

	public static void main(String[] args) {
		UserDao controller = new UserDao(DBConnectionMgr.getInstance());
		Scanner sc = new Scanner(System.in);
		
		controller.signUp("testJaean", "1234", "JaeAn");
		
		controller.updateAddress("testJaean", "용인시 수지구 죽전동");

		controller.updatePhone("testJaean", "01035189984");
		
		controller.updatePreference("testJaean", "고구마 베이컨 피자");
		
//		System.out.println(controller.getUserByUsername("jaean1999"));
		
//		controller.printInfoPrettier("jaeanana");
//		System.out.println(controller.getUserAll());
//		System.out.println();
		System.out.println("삭제 전: ");
		controller.printUserAll();
		controller.deleteInfoByUsername("testJaean");
		System.out.println("삭제 후: ");
		controller.printUserAll();
	}

}
