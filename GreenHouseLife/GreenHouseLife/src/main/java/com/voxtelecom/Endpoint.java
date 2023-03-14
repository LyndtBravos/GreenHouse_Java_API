package com.voxtelecom;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/show")
public class Endpoint {
	
//	private String hashPassword(String str) {
//		return BCrypt.hashpw(str, BCrypt.gensalt());
//	}
	
//	private boolean checkPassword(String password, String hashedPassword) {
//		if(BCrypt.checkpw(password, hashedPassword)) {
//			return true;
//		}else {
//			return false;
//		}
//	}
	
	@GET
	@Path("/age/{dob}")
	@Produces(MediaType.APPLICATION_JSON)
	public int calculageAge(@PathParam("dob")String dob) {
		LocalDate curDate = LocalDate.now();
		LocalDate dob1 = LocalDate.parse(dob);
		if((dob != null) && (curDate != null)) {
			return Period.between(dob1, curDate).getYears();
		}
		return 0;
	}
	
	public String verifyString(String str) {
		if(str.trim().equals(null) || str.trim().equals("")) {
			return "String is empty";
		}else if(!hasNoSpecialChars(str)) {
			return "String has special characters!!!";
		}else {
			return "";
		}
	}
	
	public String verifyGender(String str) {
		if(str.trim().equalsIgnoreCase("Male") || str.trim().equalsIgnoreCase("Female")) {
			return "";
		}else {
			return "String isn't gender-related, please try again";
		}
	}
	
	public String verifyDependency(String str) {
		if(str.trim().equalsIgnoreCase("true") || str.trim().equalsIgnoreCase("false")) {
			return "";
		}else {
			return "String isn't a boolean value, please try again";
		}
	}
	
	@GET
	@Path("/test/{me}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMe(@PathParam("me")String str) {
		return verifyDependency(str);
	}
	
	public String verifyNumbers(int num) {
		if(num >= 0) {
			return "";
		}else {
			return "Number entered has a negative value";
		}
	}
	
	public String verifyPassword(String str) {
		if(str.length() < 8) {
			return "Password has to be at least 8 characters";
		}else if(str.trim().equals("")) {
			return "Don't forget to enter password";
		}else {
			return "";
		}
	}
	
	public String verifyEmail(String str) {
		String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		if(str.matches(regex)) {
			return "";
		}else if(str.trim().equals("")){
			return "Don't forget to type in Email Address";
		}else {
			return "Email Address isn't valid";
		}
	}
	
	public boolean hasNoSpecialChars(String str) {
		for(int i = 0, j = str.length(); i < j; i++) {
			if(str.charAt(i) > 0 && str.charAt(i) < 32) {
				return false;
			}else if(str.charAt(i) > 32 && str.charAt(i) < 45) {
				return false;
			}else if(str.charAt(i) > 46 && str.charAt(i) < 65) {
				return false;
			}else if(str.charAt(i) > 90 && str.charAt(i) < 97) {
				return false;
			}else if(str.charAt(i) > 122 && str.charAt(i) < 128) {
				return false;
			}
		}
		return true;
	}
	
	public String validateDate(String str) {
		if(str.trim().equals("")) {
			return "Don't forget to type in Date of Birth";
		}else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			sdf.setLenient(false);
			
			try {
				Date javaDate = sdf.parse(str);
				System.out.println("This: " + str + " is a valid date");
			}catch(ParseException e) {
				e.printStackTrace();
				return "Mentioned Date isn't a valid one";
			}
			return "";
		}
	}
	
	public Connection getConn() {
		String url = "jdbc:mysql://localhost:3306/greenhouse?useUnicode=yes&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true";
		Connection con = null;
		System.out.println("Can you reach this?");
		try {
			con = DriverManager.getConnection(url, "root", "psycho");
			System.out.println("Did you get to this?");
		}catch(SQLException ex) {
			ex.printStackTrace();
		}
		return con;
	}
	
	DatabaseConnection dc = new DatabaseConnection();
	PreparedStatement ps = null;
	ResultSet rs = null;
	
	@GET
	@Path("/hello")
	@Produces(MediaType.APPLICATION_JSON)
	public String helloWorld() {
		return "Hello World";
	}
	
	@GET
	@Path("/myPlan/{userID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Plan getPlan(@PathParam("userID") int userID){
		Connection con = dc.getConn();
		Plan plan = null; 
		String query = "SELECT * FROM greenhouse.plancalc WHERE userID = ?;";
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, userID);
			rs = ps.executeQuery();
			while(rs.next()) {
				int ID = rs.getInt(1);
				boolean dependency = rs.getBoolean(3);
				String gender = rs.getString(4);
				int income = rs.getInt(5);
				plan = new Plan(ID, rs.getString(2), dependency, gender, income, userID);
			}
		}catch(SQLException ex) {
			ex.printStackTrace();
		}
		return plan;
	}
	
	@GET
	@Path("/myPlans/{planID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Plan getPlan2(@PathParam("planID") int planID){
		Connection con = dc.getConn();
		Plan plan = null; 
		String query = "SELECT * from greenhouse.plancalc WHERE IDplanCalc = ?;";
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, planID);
			rs = ps.executeQuery();
			while(rs.next()) {
				int ID = rs.getInt(1);
				String DOB = rs.getString(2);
				boolean dependency = rs.getBoolean(3);
				String gender = rs.getString(4);
				int income = rs.getInt(5);
				plan = new Plan(ID, DOB, dependency, gender, income, planID);
			}
		}catch(SQLException ex) {
			ex.printStackTrace();
		}
		return plan;
	}
	
	@GET
	@Path("/seekEmail/{email}")
	public User getEmail(@PathParam("email") String emailAddress) {
		Connection con = dc.getConn();
		String query = "SELECT * FROM greenhouse.users WHERE BINARY emailAddress = ?;";
		User user = null;
		try {
			ps = con.prepareStatement(query);
			ps.setString(1, emailAddress);
			rs = ps.executeQuery();
			String name = null;
			String surname = null;
			String email = null;
			String password = null;
			int rowCount = 0;
			while(rs.next()) {
				name = rs.getString(2);
				surname = rs.getString(3);
				email = rs.getString(4);
				password = rs.getString(5);
				rowCount++;
			}
			if(rowCount == 1) {
				user = new User(1, name, surname, email, password);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return user;
	}
	
	@POST
	@Path("/myInfo/")
	@Produces(MediaType.APPLICATION_JSON)
	public User getUser(User user) {
		Connection con = getConn();
		String query = "SELECT *  FROM greenhouse.users WHERE BINARY emailAddress = ? AND BINARY password = sha1(?);";
		try {
			ps = con.prepareStatement(query);
			ps.setString(1, user.getEmail());
			ps.setString(2, user.getPassword());
			rs = ps.executeQuery();
			while(rs.next()) {
				int ID = rs.getInt(1); 
				String name = rs.getString(2);
				String surname = rs.getString(3);
				user = new User(ID, name, surname, user.getEmail(), user.getPassword());
			}
		}catch(SQLException ex) {
			ex.printStackTrace();
		}
		return user;
	}
	
	@GET
	@Path("/getClient/{userID}/{planID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Client getClient(@PathParam("userID") int uID, @PathParam("planID") int pID) {
		Connection con = dc.getConn();
		Client client = null;
		String query = "SELECT * FROM greenhouse.clients WHERE planID = ? AND userID = ?;";
		int i = 0;
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, uID);
			ps.setInt(2, pID);
			rs = ps.executeQuery();
			while(rs.next()) {
				int premium = rs.getInt(2);
				int cover = rs.getInt(3);
				client = new Client(pID, uID, premium, cover);
				i++;
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		if(i == 1) {
			return client;
		}else {
			return client;
		}
	}
	
	@POST
	@Path("/addUser/{name}/{surname}")
	@Produces(MediaType.APPLICATION_JSON)
	public String addUser(User user, @PathParam("name") String name, @PathParam("surname") String surname) {
		Connection con = dc.getConn();
		String query = "INSERT INTO `greenhouse`.`users` (`name`, `surname`, `emailAddress`, `password`) VALUES (?, ?, ?, sha1(?));";
		int i = 0;
		if(verifyString(name).equals("") && verifyString(surname).equals("") && verifyEmail(user.getEmail()).equals("") && verifyPassword(user.getPassword()).equals("")){
			try {
				ps = con.prepareStatement(query);
				ps.setString(1, name);
				ps.setString(2, surname);
				ps.setString(3, user.getEmail());
				ps.setString(4, user.getPassword());
				i = ps.executeUpdate();
			}catch(SQLException ex) {
				return "An unknown error occurred";
			}
			if(i == 1) {
				return "Added New User Successfully";
			}else {
				return "Failed To Add User, Try Again";
			}
		}else{
			if(!verifyString(name).equals("")) {
				return "Please type in proper name with only space, dot and full stop only for special characters";
			}else if(!verifyString(surname).equals("")) {
				return "Please type in proper surname with only space, dot and full stop only for special characters";
			}else if(!verifyEmail(user.getEmail()).equals("")) {
				return "Please type in proper Email Address to proceed";
			}else {
				return "Password value isn't allowed. It should be at least 8 characters";
			}
		}
	}
	
	@POST
	@Path("/addPlan/{userID}/{dep}")
	@Produces(MediaType.APPLICATION_JSON)
	public String addPlan(Plan plan, @PathParam("userID") int ID, @PathParam("dep") String dep) {
		Connection con = dc.getConn();
		String query = "INSERT INTO `greenhouse`.`plancalc` (`DOB`,`Dependency?`,`Gender`,`Income`,`userID`) VALUES (?,?,?,?,?);";
		int i = 0;
		
		if(!verifyNumbers(plan.getIncome()).equals("")) {
			return "Please submit a positive number for Income";
		}
		
		if(validateDate(plan.getDOB()).equals("") && verifyNumbers(plan.getIncome()).equals("") && verifyDependency(dep).equals("") && verifyGender(plan.getGender()).equals("")) {
			if(dep.trim().equals("true")) {
				plan.setDependency(true);
			}else {
				plan.setDependency(false);
			}
			try {
				ps = con.prepareStatement(query);
				ps.setString(1, plan.getDOB());
				ps.setBoolean(2, plan.isDependency());
				ps.setString(3, plan.getGender());
				ps.setInt(4, plan.getIncome());
				ps.setInt(5, ID);
				i = ps.executeUpdate();
			}catch(SQLException ex) {
				return "An unknown error occurred";
			}catch(NumberFormatException ex) {
				return "Please enter valid numbers to proceed";
			}
			if(i == 1) {
				return "Added The New Info Under User Successfully";
			}else {
				return "Failed To Execute, Please Try Again";
			}
		}else {
			if(!validateDate(plan.getDOB()).equals("")) {
				return "Date mentioned isn't valid";
			}else if(!verifyGender(plan.getGender()).equals("")) {
				return "Please submit either Male or Female on Gender to proceed";
			}else if(!verifyDependency(dep).equals("")) {
				return "Please submit either true or false for Dependency";
			}else {
				return "Please submit a positive number for Income";
			}
		}
	}
	
	@POST
	@Path("/buyPlan/")
	@Produces(MediaType.APPLICATION_JSON)
	public String buyPlan(Client client) {
		Connection con = dc.getConn();
		String query = "INSERT INTO `greenhouse`.`clients` (`Premium`, `Cover`, `planID`, `userID`) VALUES (?, ?, ?, ?);";
		int i = 0;
		
		if(!verifyNumbers(client.getPremium()).equals("")) {
			return "Please enter a valid number for Premium";
		}else if(!verifyNumbers(client.getCover()).equals("")){
			return "Please enter a valid number for Plan Cover";
		}
		
		if(verifyNumbers(client.getPremium()).equals("") && verifyNumbers(client.getCover()).equals("")) {
			try {
				ps = con.prepareStatement(query);
				ps.setInt(1, client.getPremium());
				ps.setInt(2, client.getCover());
				ps.setInt(3, client.getPlanID());
				ps.setInt(4, client.getUserID());
				i = ps.executeUpdate();
			}catch(SQLException ex) {
				return "Please enter your data in the right format for this to succeed";
			}catch(NumberFormatException ex) {
				return "Please enter valid numbers to proceed";
			}
			if(i == 1) {
				return "Added The New Plan Under User Successfully";
			}else {
				return "Failed To Execute, Please Try Again";
			}
		}else {
			if(!verifyNumbers(client.getPremium()).equals("")) {
				return "Please enter a valid number for Premium";
			}else {
				return "Please enter a valid number for Plan Cover";
			}
		}
	}
	
	@PUT
	@Path("/updateUser/{userID}")
	@Produces(MediaType.APPLICATION_JSON)
	public String updateUser(User user, @PathParam("userID") int ID) {
		Connection con = dc.getConn();
		String query = "UPDATE `greenhouse`.`users` SET `name` = ?, `surname` = ?, `emailAddress` = ? WHERE `usersID` = ?;";
		int i = 0;
		if(verifyString(user.getName()).equals("") && verifyString(user.getSurname()).equals("") && verifyEmail(user.getEmail()).equals("")) {
			try {
				ps = con.prepareStatement(query);
				ps.setString(1, user.getName());
				ps.setString(2, user.getSurname());
				ps.setString(3, user.getEmail());
				ps.setInt(4, ID);
				i = ps.executeUpdate();
			}catch(SQLException ex) {
				return "Please enter your data in the right format for this to succeed";
			}
			if(i == 1) {
				return "Updated User Successfully";
			}else {
				return "Failed To Execute String Query Successfully, Please Try Again";
			}
		}else {
			if(!verifyString(user.getName()).equals("")){
				return "Please type in a valid name to proceed";
			}else if(!verifyString(user.getSurname()).equals("")) {
				return "Please type in a valid surname to proceed";
			}else {
				return "Email Address isn't valid";
			}
		}
	}
	
	@PUT
	@Path("/updateClient/{userID}/{planID}")
	@Produces(MediaType.APPLICATION_JSON)
	public String updateClient(Client client, @PathParam("userID") int uID, @PathParam("planID") int pID) {
		Connection con = dc.getConn();
		String query = "UPDATE `greenhouse`.`clients` SET `Premium` = ?, `Cover` = ? WHERE `planID` = ? AND `userID` = ?;";
		int i = 0;
		
		try {
			if(verifyNumbers(client.getPremium()).equals("") && verifyNumbers(client.getCover()).equals("")) {
				ps = con.prepareStatement(query);
				ps.setInt(1, client.getPremium());
				ps.setInt(2, client.getCover());
				ps.setInt(3, pID);
				ps.setInt(4, uID);
				i = ps.executeUpdate();
			}
		}catch(SQLException e) {
			return "An unknown error occured. Please try again";
		}catch(NumberFormatException e) {
			return "Please type in valid numbers to proceed";
		}
		if(i == 1) {
			return "Updated User's Plan Sucessfully"; 
		}else {
			return "Something went wrong. Query didn't execute";
		}
	}
	
	@PUT
	@Path("/forgotPassword/{email}")
	@Produces(MediaType.APPLICATION_JSON)
	public String forgotPassword(User user, @PathParam("email") String email) {
		Connection con = dc.getConn();
		String query = "UPDATE `greenhouse`.`users` SET `password` = sha1(?) WHERE `emailAddress` = ?;";
		int i = 0;
		if(verifyEmail(email).equals("") && verifyPassword(user.getPassword()).equals("")) {
			try {
				ps = con.prepareStatement(query);
				ps.setString(1, user.getPassword());
				ps.setString(2, email);
				i = ps.executeUpdate();
			}catch(SQLException ex) {
				return "Please enter your data in the right format for this to succeed";
			}
			if(i == 1) {
				return "Updated Password for Email: " + email + " Successfully";
			}else {
				return "Failed To Execute, Please Try Again";
			}
		}else {
			if(!verifyEmail(email).equals("")) {
				return "Email Address isn't valid";
			}else {
				return "Password isn't valid. It should be more than 8 characters";
			}
		}
	}
	
	@PUT
	@Path("/updatePlan/{planID}/{dep}")
	@Produces(MediaType.APPLICATION_JSON)
	public String updatePlan(Plan plan, @PathParam("planID") int ID, @PathParam("dep")String dep) {
		Connection con = dc.getConn();
		String query = "UPDATE `greenhouse`.`plancalc` SET `DOB` = ?, `Dependency?` = ?, `Gender` = ?, `Income` = ? WHERE `IDplanCalc` = ?;";
		int i = 0;
		
		if(!verifyNumbers(plan.getIncome()).equals("")) {
			return "Please enter a positive number for Income";
		}
		
		if(validateDate(plan.getDOB()).equals("") && verifyDependency(dep).equals("") && verifyGender(plan.getGender()).equals("") && verifyNumbers(plan.getIncome()).equals("")) {
			if(dep.trim().equals("true")) {
				plan.setDependency(true);
			}else {
				plan.setDependency(false);
			}
			try {
				ps = con.prepareStatement(query);
				ps.setString(1, plan.getDOB());
				ps.setBoolean(2, plan.isDependency());
				ps.setString(3, plan.getGender());
				ps.setInt(4, plan.getIncome());
				ps.setInt(5, ID);
				i = ps.executeUpdate();
			}catch(SQLException ex) {
				return "Please enter your data in the right format for this to succeed";
			}catch(NumberFormatException ex) {
				return "Please submit a valid number for Income";
			}
			if(i == 1) {
				return "Updated Info Successfully";
			}else {
				return "Failed To Execute String Query Successfully, Please Try Again";
			}
		}else {
			if(!validateDate(plan.getDOB()).equals("")) {
				return "Date entered isn't valid";
			}else if(!verifyDependency(dep).equals("")) {
				return "Please submit only true or false for Dependency";
			}else if(!verifyNumbers(plan.getIncome()).equals("")) {
				return "Please submit a positive number for Income";
			}else {
				return "Please submit only Male or Female for Gender";
			}
		}
	}
	
	@DELETE
	@Path("/deleteUser/{userID}")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteUser(@PathParam("userID") int ID) {
		Connection con = dc.getConn();
		String query = "DELETE FROM `greenhouse`.`users` WHERE usersID = ?;";
		int i = 0;
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, ID);
			i = ps.executeUpdate();
		}catch(SQLException ex) {
			return "An error occurred, please try once more";
		}
		if(i == 1) {
			return "Deleted User Successfully";
		}else {
			return "Failed To Execute, Please Try Again";
		}
	}
	
	@DELETE
	@Path("/deletePlan/{planID}")
	@Produces(MediaType.APPLICATION_JSON)
	public String deletePlan(@PathParam("planID") int ID) {
		Connection con = dc.getConn();
		String query = "DELETE FROM `greenhouse`.`plancalc` WHERE IDplanCalc = ?;";
		int i = 0;
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, ID);
			i = ps.executeUpdate();
		}catch(SQLException ex) {
			return "An error occurred, please try once more";
		}
		if(i == 1) {
			return "Deleted Info with ID Successfully";
		}else {
			return "Failed To Execute, Please Try Again";
		}
	}
	
	@DELETE
	@Path("/deleteClient/{userID}/{planID}")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteClient(@PathParam("userID") int uID, @PathParam("planID") int pID) {
		Connection con = dc.getConn();
		String query = "DELETE FROM `greenhouse`.`clients` WHERE userID = ? AND planID = ?;";
		int i = 0;
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, uID);
			ps.setInt(2, pID);
			i = ps.executeUpdate();
		}catch(SQLException e) {
			return "An unknown error occurred";
		}
		if(i == 1) {
			return "Deleted Plan Successfully";
		}else {
			return "An error occured. Query didn't execute!";
		}
	}
	
	@POST
	@Path("/sendOTP/{email}")
	@Produces(MediaType.APPLICATION_JSON)
	public String sendMail(@PathParam("email")String email) {
		String from = "lindtbravos@gmail.com";
		
		int OTP = (int) Math.floor(Math.random() * (9999 - 1111) + 1111);
		
		Properties properties = System.getProperties();
		
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.socketFactory.port", "465");
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		
		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, "Fvcklove#1");
			}
		});
		
		session.setDebug(true);
		
		Connection con = dc.getConn();
		String query = "INSERT INTO `greenhouse`.`otp_table` (`OTP`, `email`) VALUES (?, ?);";
		int i = 0;
		
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, OTP);
			ps.setString(2, email);
			i = ps.executeUpdate();
			if(i == 1) {
				System.out.println("Added OTP successfully");
			}else {
				System.out.println("Failed to add OTP successfully");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		try {
			MimeMessage message = new MimeMessage(session);
			
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
			message.setSubject("OTP 4 Password Reset");
			message.setText("Hey Brian, this is Brian from another email address. Freaky, huh? Is this the multiverse?\nAnyway, here's your OTP: " + OTP);
			System.out.println("Sending...");
			
			Transport.send(message);
			return "Sent email successfully";
		}catch(MessagingException e) {
			e.printStackTrace();
		}
		return "Email didn't send. An error occured";
	}
	
	@GET
	@Path("/getOTP/{email}")
	@Produces(MediaType.APPLICATION_JSON)
	public int getOTP(@PathParam("email")String email) {
		Connection con = dc.getConn();
		String query = "SELECT * FROM greenhouse.otp_table WHERE email = ?;";
		int otp = 0;
		try {
			ps = con.prepareStatement(query);
			ps.setString(1, email);
			rs = ps.executeQuery();
			while(rs.next()) {
				otp = rs.getInt(2);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return otp;
	}
	
	@DELETE
	@Path("/deleteOTP/{email}")
	@Produces(MediaType.APPLICATION_JSON)
	public void delOTP(@PathParam("email")String email) {
		Connection con = dc.getConn();
		String query = "DELETE FROM `greenhouse`.`otp_table` WHERE email = ?;";
		int i = 0;
		try {
			ps = con.prepareStatement(query);
			ps.setString(1, email);
			i = ps.executeUpdate();
			if(i == 0) {
				System.out.println("Deleted OTP for Email: " + email + " successfully");
			}else {
				System.out.println("Failed to delete OTP");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
}