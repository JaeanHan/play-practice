package userDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

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
	
	public int updateAddress(String username, String address) {
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

	public int updatePhone(String username, String phone) {
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

	public int updatePreference(String username, String menu) {
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

	public int deleteInfoByUsername(String username) {
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
				
		for(int i=0; i<userList.size()/2; i++) { //um, ud 묶어서 출력, 여전히 무작위
			System.out.println(userList.get("um"+i));
			System.out.println(userList.get("ud"+i));
			System.out.println();
		}
		
//		for(String temp : userList.keySet()) { 무작위 선택
//			System.out.println(userList.get(temp));
//		}
		
	}

	public int signIn() {
		String sql = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		int result = 0;
		
		try {
			con = pool.getConnection();
			sql = "";
			//로그인
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
