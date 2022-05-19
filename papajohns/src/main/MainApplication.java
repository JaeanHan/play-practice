package main;

import java.util.Scanner;

import db.DBConnectionMgr;
import userService.UserService;

public class MainApplication {

	public static void main(String[] args) {
		DBConnectionMgr pool = DBConnectionMgr.getInstance();
		Scanner sc = new Scanner(System.in);
		UserService userService = new UserService(sc, pool);
		
		int signinResult = userService.greeting();
		if (signinResult == 1) {
			System.out.println(userService.getUm().getUsername() + "님 환영합니다!");
			while(true) {
				System.out.println("실행 할 기능을 입력해주세요");
				System.out.println("[1. 유저 정보 변경]");
				System.out.println("[2. 피자 주문]");
				System.out.println("[0. 종료]");
				
				int num = sc.nextInt();
				sc.nextLine();
				
				if(num==1) {
					userService.update();
				} else if(num==2) {
					System.out.println("준비중인 서비스입니다.");
				} else if(num==0) {
					System.out.println("프로그램을 종료합니다.");
					break;
				} else {
					continue;
				}
				
			}
			
		}
		
		System.out.println("안녕히가세요!");
		
	}

}

