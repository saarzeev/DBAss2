

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import hib.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.*;

public class Assignment {

	public static void main(String[] args) {
		
		System.out.println(insertUser("oren", "orenPass", "oren", "shor", "24", "02", "1992"));

	}
	
	public static boolean isExistUsername(String username) {
		Session session = HibernateUtil.currentSession();
		
		String hql = ("FROM Users WHERE username = \'" +  username + "\'" );
		Query query = session.createQuery(hql);
		List<Users> results = query.list();
		
		//session.close();
		//HibernateUtil.closeSession();
		
		return (results.size() > 0);
	}
	
	public static String insertUser(String username, String password, String first_name, String
			last_name, String day_of_birth, String month_of_birth, String year_of_birth) {
		if(isExistUsername(username)) {
			return null;
		}
		
		String userID = "";
		Users newUser = initializeNewUser(username, password, first_name, last_name, day_of_birth, month_of_birth, year_of_birth);
		
		userID = insertUserIntoDB(userID, newUser);
		
		return userID;
	}

	private static String insertUserIntoDB(String userID, Users newUser) {
		Session session = HibernateUtil.currentSession();
		try {
			Transaction tx = session.beginTransaction();
			userID =  session.save(newUser).toString();
			tx.commit();
		} catch(HibernateException e) {
			e.printStackTrace();
			//System.out.println(e.getCause());
		}
		//HibernateUtil.closeSession();
		return userID;
	}
	
	private static Users initializeNewUser(String username, String password, String first_name, String
			last_name, String day_of_birth, String month_of_birth, String year_of_birth) {
		
		Users user = new Users();
		user.setUsername(username);
		user.setPassword(password);
		user.setFirstName(first_name);
		user.setLastName(last_name);
		
//		Date date = constructDateFromString(day_of_birth, month_of_birth, year_of_birth);
//		user.setDateOfBirth(date);
//		
		return user;
	}

	private static Date constructDateFromString(String day_of_birth, String month_of_birth, String year_of_birth) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
		 
		String dateInString = day_of_birth + "-" + month_of_birth + "-" + year_of_birth;
		Date date = null;
		try {
			date = formatter.parse(dateInString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}
}
