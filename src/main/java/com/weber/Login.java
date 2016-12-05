package com.weber;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class HelloWorld
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.getSession().setAttribute("user", null);
        request.getRequestDispatcher("index.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Statement stmt=null;
		try{  
			ArrayList<Shift> myshifts = new ArrayList<Shift>();
			ArrayList<TimeOff> approvedtimesoff = new ArrayList<TimeOff>();
			ArrayList<TimeOff> notapprovedtimesoff = new ArrayList<TimeOff>();
			Context initContext = new InitialContext();
			 Context envContext  = (Context)initContext.lookup("java:/comp/env");
			 DataSource ds = (DataSource)envContext.lookup("jdbc/MySQLDS");
			 Connection con = ds.getConnection();
			stmt = con.createStatement();
						String username = (String) request.getParameter("username");
						 String sql = "SELECT * FROM GUARDS WHERE email='"+username+"';";
						 String password = (String) request.getParameter("password");
						 int passw = hash(password);
						 ResultSet rs = stmt.executeQuery(sql);
						if(rs.next()){
							int pass = rs.getInt("password");
							System.out.println(pass);
							System.out.println(passw);
							if(pass==passw){
								System.out.println("Create user");
								String firstName = rs.getString("firstName");
								String lastName = rs.getString("lastName");
								int age =rs.getInt("age");
								String pool=rs.getString("mainPool");
								boolean otherpools=rs.getBoolean("otherPools");
								int rank=rs.getInt("rank");
								String position =rs.getString("position");
								int managerMinHours = rs.getInt("managerMinHours");
				
								 sql = "SELECT * FROM SHIFTS WHERE email='"+username+"';";
								 rs = stmt.executeQuery(sql);
								while(rs.next()){
									int id = rs.getInt("id");
									String guard = rs.getString("guard");
									Timestamp startTime= rs.getTimestamp("startTime");
									Timestamp endTime = rs.getTimestamp("endTime");
									String poolshift = rs.getString("pool");
									int length = rs.getInt("length");
									boolean managerRequired = rs.getBoolean("managerRequired");
									String shiftPosition = rs.getString("position");
									myshifts.add(new Shift(startTime,endTime,poolshift,length,guard,id,username,managerRequired,shiftPosition));
								}
								stmt = con.createStatement();
								 sql = "SELECT * FROM TIMEOFF WHERE email='"+username+"';";
								 rs = stmt.executeQuery(sql);
								while (rs.next()){
									String guard = rs.getString("guard");
									Timestamp startTime2 = rs.getTimestamp("startTime");
									Timestamp endTime2 = rs.getTimestamp("endTime");
									int id = rs.getInt("id");
									String reason = rs.getString("reason");
									boolean approved = rs.getBoolean("approved");
									if(approved==true){
										approvedtimesoff.add(new TimeOff(startTime2,endTime2,guard,true,username,pool,id,reason));
									}
									else{
										notapprovedtimesoff.add(new TimeOff(startTime2,endTime2,guard,false,username,pool,id,reason));
									}
									
								}
								System.out.println("Initializing User");
								request.getSession().setAttribute("user", new User( firstName,lastName,  position,  pool,  myshifts,
										 approvedtimesoff, notapprovedtimesoff,  age,  rank,  otherpools,username,managerMinHours));
								System.out.println("Created User");

							}
							}else{
								response.sendRedirect("/WrongPass");
								return;

							}
						
						
							
	
			
			con.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
        request.getRequestDispatcher("index.jsp").forward(request, response);

	}
	
	public static int hash(String password){
		int x = 0;
		String [] pass = password.split("");
		for (int i=0; i<password.length();i++){
			 x +=  password.charAt(i);
		}
		return x;
		
	}

}
