package userDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;
import db.DBConnectionMgr;
import lombok.RequiredArgsConstructor;
import userData.User;
import userData.UserDtl;
import userData.UserMst;

@RequiredArgsConstructor
public class UserDao {
	private final DBConnectionMgr pool;
	
	public int signUp(String username, String password, String name) {
		String sql = null;
		Connection con = null;
		PreparedStatement pstmt=null;
		int result = 0;
		
		try {
			con=pool.getConnection();
			sql = "INSERT INTO\r\n"
					+ "	user_mst\r\n"
					+ "VALUES(\r\n"
					+ "	0,\r\n"
					+ "	?,\r\n"
					+ "	?,\r\n"
					+ "	?,\r\n"
					+ "	NOW(),\r\n"
					+ "	NOW()\r\n"
					+ ")";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			pstmt.setString(3, name);
			result = pstmt.executeUpdate();
		} catch(SQLException e) {
			System.out.println("signUp SQL Error");
		}
			catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return result;
	}
	
	public void updateInfo(String username, Scanner sc) {
		while(true) {
			System.out.print("변경하고 싶은 정보의 번호를 입력해주세요: ");
			System.out.println("[1. Address]");
			System.out.println("[2. Preference]");
			System.out.println("[3. Contact]");
			System.out.println("[0. quit]");
			
			try { // consider typo
				int num = sc.nextInt(); 
				sc.nextLine();
				
				if (num==1) {
					System.out.print("변경하실 주소를 입력해 주세요: ");
					updateAddress(username, sc.nextLine());
				} else if (num==2) {
					System.out.println("가장 좋아하는 메뉴를 입력해주세요!: ");
					updatePreference(username, sc.nextLine());
				} else if (num==3) {
					System.out.println("바뀐 연락처를 입력해주세요: ");
					updatePhone(username, sc.nextLine());
				} else if (num==0) {
					System.out.println("정보 변경을 종료합니다.");
					break;
				}
			} catch (InputMismatchException e) {
				System.out.println("오타에 주의하세요.");
			} finally {
				System.out.println("[바뀐 정보입니다!]");
				printInfoPrettier(username);
			}
		}
	}
	
	private int updateAddress(String username, String address) {
		String sql = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		int result = 0;
		
		try {
			con = pool.getConnection();
			sql = "UPDATE\r\n"
					+ "	user_dtl\r\n"
					+ "SET\r\n"
					+ "	address=?,\r\n"
					+ "	update_date = NOW()\r\n"
					+ "WHERE\r\n"
					+ "	usercode = (SELECT\r\n"
					+ "						usercode\r\n"
					+ "					from\r\n"
					+ "						user_mst\r\n"
					+ "					where\r\n"
					+ "						username = ?\r\n"
					+ "					)";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, address);
			pstmt.setString(2, username);
			result = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println("updateAdress SQL Error");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return result;
	}

	private int updatePhone(String username, String phone) {
		String sql = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		int result = 0;
		
		try {
			con = pool.getConnection();
			sql = "UPDATE\r\n"
					+ "	user_dtl\r\n"
					+ "SET\r\n"
					+ "	phone = ?,\r\n"
					+ "	update_date = NOW()\r\n"
					+ "WHERE\r\n"
					+ "	usercode = (SELECT\r\n"
					+ "						usercode\r\n"
					+ "					from\r\n"
					+ "						user_mst\r\n"
					+ "					where\r\n"
					+ "						username = ?\r\n"
					+ "					)";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, phone);
			pstmt.setString(2, username);
			result = pstmt.executeUpdate();
		} catch (SQLException e ) {
			System.out.println("updatePhone SQL Error");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		
		return result;
	}

	private int updatePreference(String username, String menu) {
		String sql = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		int result = 0;
		
		try {
			con = pool.getConnection();
			sql = "UPDATE\r\n"
					+ "	user_dtl\r\n"
					+ "SET\r\n"
					+ "	preference = ?,\r\n"
					+ "	update_date = NOW()\r\n"
					+ "WHERE\r\n"
					+ "	usercode = (SELECT\r\n"
					+ "						usercode\r\n"
					+ "					from\r\n"
					+ "						user_mst\r\n"
					+ "					where\r\n"
					+ "						username = ?\r\n"
					+ "					)";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, menu);
			pstmt.setString(2, username);
			result = pstmt.executeUpdate();
		} 
		catch (SQLException e) {
			System.out.println("updatePreference SQL Error");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			pool.freeConnection(con, pstmt);
		}
		
		return result;
		
	}

	
	private HashMap<String, User> getUserByUsername(String username) {
		String sql = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		HashMap<String, User> hashMapResult = new HashMap<String, User>();
		
		try {
			con = pool.getConnection();
			sql = "SELECT\r\n"
					+ "	*\r\n"
					+ "FROM user_mst um LEFT OUTER JOIN user_dtl ud ON(um.usercode = ud.usercode)\r\n"
					+ "WHERE\r\n"
					+ "	um.username = ?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, username);
			rs = pstmt.executeQuery();
			rs.next();
			try {
				UserMst um = UserMst.builder()
									.usercode(rs.getInt(1))
									.username(rs.getString(2))
									.password(rs.getString(3))
									.name(rs.getString(4))
									.create_date(rs.getTimestamp(5).toLocalDateTime())
									.update_date(rs.getTimestamp(6).toLocalDateTime())
									.build();
				
				UserDtl ud = UserDtl.builder()
									.usercode(rs.getInt(7))
									.address(rs.getString(8))
									.phone(rs.getString(9))
									.preference(rs.getString(10))
									.create_date(rs.getTimestamp(11).toLocalDateTime())
									.update_date(rs.getTimestamp(12).toLocalDateTime())
									.build();
				
				hashMapResult.put("um", um);
				hashMapResult.put("ud", ud);
				
			} catch (SQLException e) {
				System.out.println("getUserByUsername SQL Error");
				System.out.println("존재하지 않는 유저네임입니다.");
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		
		return hashMapResult;
	}

	public void printInfoPrettier(String username) {
		HashMap<String, User> printable = getUserByUsername(username);
		System.out.println(printable.get("um"));
		System.out.println(printable.get("ud"));
	}

	public int deleteByUsername(String username) {
		String sql = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		int result = 0;
		
		try {
			con = pool.getConnection();
			sql = "DELETE\r\n"
					+ "FROM\r\n"
					+ "	user_mst\r\n"
					+ "WHERE\r\n"
					+ "	username = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, username);
			result = pstmt.executeUpdate();
		} 
		catch (SQLException e) {
			System.out.println("deleteInfoByUsername SQL Erroe");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return result;
	}

	private HashMap<String, User> getUserAll() {
		String sql = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
//		ArrayList<User> userList = new ArrayList<User>();
		HashMap<String, User> userMap = new HashMap<String, User>();
		int i = 0;
		
		try {
			con = pool.getConnection();
			sql = "SELECT\r\n"
					+ "	*\r\n"
					+ "FROM user_mst um INNER JOIN user_dtl ud ON(um.usercode = ud.usercode)";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				UserMst um = UserMst.builder()
									.usercode(rs.getInt(1))
									.username(rs.getString(2))
									.password(rs.getString(3))
									.name(rs.getString(4))
									.create_date(rs.getTimestamp(5).toLocalDateTime())
									.update_date(rs.getTimestamp(6).toLocalDateTime())
									.build();
				
				UserDtl ud = UserDtl.builder()
									.usercode(rs.getInt(7))
									.address(rs.getString(8))
									.phone(rs.getString(9))
									.preference(rs.getString(10))
									.create_date(rs.getTimestamp(11).toLocalDateTime())
									.update_date(rs.getTimestamp(12).toLocalDateTime())
									.build();
				
				userMap.put("um"+i, um);
				userMap.put("ud"+i, ud);
				i += 1;
			}
			
		} catch (SQLException e) {
			System.out.println("getUserAll SQL Error");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		
	return userMap;
	}

	public void printUserAll() {
		HashMap<String, User> userList = getUserAll();
				
		for(int i=0; i<userList.size()/2; i++) { // still, but at least  um and ud groupped
			System.out.println(userList.get("um"+i));
			System.out.println(userList.get("ud"+i));
			System.out.println();
		}
		
//		for(String temp : userList.keySet()) { I don't like randomness
//			System.out.println(userList.get(temp));
//		}
		
	}

	public HashMap<String, User> SignIn(String username, String password) {
		String sql = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		HashMap<String, User> userMap = new HashMap<>();
		
		try {
			con = pool.getConnection();
			sql = "SELECT\r\n"
					+ "	*\r\n"
					+ "FROM user_mst um INNER JOIN user_dtl ud ON(um.usercode = ud.usercode)\r\n"
					+ "WHERE um.username = ? AND um.password = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			rs = pstmt.executeQuery();
			rs.next();
			
			/*
			 * if(CheckIfRight) {
			 *     userMap = getUserByUsername(username);
			 * } else {
			 *     System.out.println("incorrect!");
			 *     return null;
			 * }
			 */
			
			UserMst temp = UserMst.builder()
									.usercode(rs.getInt(1))
									.username(rs.getString(2))
									.password(rs.getString(3))
									.name(rs.getString(4))
									.create_date(rs.getTimestamp(5).toLocalDateTime())
									.update_date(rs.getTimestamp(6).toLocalDateTime())
									.build();
			UserDtl temp2 = UserDtl.builder()
									.usercode(rs.getInt(7))
									.address(rs.getString(8))
									.phone(rs.getString(9))
									.preference(rs.getString(10))
									.create_date(rs.getTimestamp(11).toLocalDateTime())
									.update_date(rs.getTimestamp(12).toLocalDateTime())
									.build();
			userMap.put("um", temp);
			userMap.put("ud", temp2);
			
			sql=((UserMst)userMap.get("um")).getName();
		} catch (SQLException e) {
			System.out.println("아이디와 비밀번호를 확인해주세요");
			sql = "Nobody";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println(sql + "님 환영합니다!");
			pool.freeConnection(con, pstmt, rs);
		}
		
		return userMap;
	}
}
