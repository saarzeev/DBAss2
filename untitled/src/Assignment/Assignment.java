package Assignment;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import hib.*;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.hibernate.*;

public class Assignment
{

//	public static void main(String[] args)
//	{
//
//		// System.out.println(insertUser("ilay", "ilayPass", "ilay", "firedman", "13",
//		// "03", "1991"));
//		// List<Mediaitems> nu = getTopNItems(5);
//		// for (Mediaitems m : nu) {
//		// System.out.println(m.getMid());
//		// }
//
//		// System.out.println(validateUser("ilay", "ilayPass"));
//		// System.out.println(validateUser("sss", "ilayPass"));
//		//System.out.println(getUser("2"));
//		System.out.println(getHistory("2"));
//		// insertToHistory("99", "10");
//		// System.out.println(getUsers());
//		// insertToLog("2");
//		// System.out.println(getNumberOfRegistredUsers(3));
//
//	}

	public static boolean isExistUsername(String username)
	{
		Query query = createQuery("FROM Users WHERE username = :username");
		query.setParameter("username", username);
		List<Users> results = query.list();

		return (results.size() > 0);
	}

	private static Query createQuery(String hql)
	{
		Session session = HibernateUtil.currentSession();
		Query query = session.createQuery(hql);
		return query;
	}

	public static String insertUser(String username, String password, String first_name, String last_name,
			String day_of_birth, String month_of_birth, String year_of_birth)
	{
		if (isExistUsername(username))
		{
			return null;
		}

		String userID = "";
		Users newUser = initializeNewUser(username, password, first_name, last_name, day_of_birth, month_of_birth,
				year_of_birth);

		userID = insertUserIntoDB(userID, newUser);

		return userID;
	}

	private static String insertUserIntoDB(String userID, Users newUser)
	{
		Session session = HibernateUtil.currentSession();
		try
		{
			Transaction tx = session.beginTransaction();
			userID = session.save(newUser).toString();
			tx.commit();
		} catch (HibernateException e)
		{
			e.printStackTrace();
		}

		return userID;
	}

	private static Users initializeNewUser(String username, String password, String first_name, String last_name,
			String day_of_birth, String month_of_birth, String year_of_birth)
	{

		Users user = new Users();
		user.setUsername(username);
		user.setPassword(password);
		user.setFirstName(first_name);
		user.setLastName(last_name);

		Date date = constructDateFromString(day_of_birth, month_of_birth, year_of_birth);
		user.setDateOfBirth(date);

		return user;
	}

	private static Date constructDateFromString(String day_of_birth, String month_of_birth, String year_of_birth)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

		String dateInString = day_of_birth + "-" + month_of_birth + "-" + year_of_birth;
		Date date = null;
		try
		{
			date = formatter.parse(dateInString);
		} catch (ParseException e)
		{
			e.printStackTrace();
		}

		return date;
	}

	public static List<Mediaitems> getTopNItems(int top_n)
	{
		Query query = createQuery("FROM Mediaitems ORDER BY mid DESC").setMaxResults(top_n);

		List<Mediaitems> results = query.list();

		return results;
	}

	public static String validateUser(String username, String password)
	{
		Query q = createQuery("FROM Users WHERE username = :username AND password = :password");
		q.setParameter("username", username);
		q.setParameter("password", password);
		List<Users> results = q.list();

		if (results.isEmpty())
		{
			return ("Not Found");
		}
		return Integer.toString(results.get(0).getUserid());
	}

	public static String validateAdministrator(String username, String password)
	{
		Query q = createQuery("FROM Administrators WHERE username = :username AND password = :password");
		q.setParameter("username", username);
		q.setParameter("password", password);
		List<Administrators> results = q.list();

		if (results.isEmpty())
		{
			return ("Not Found");
		}
		return Integer.toString(results.get(0).getAdminid());
	}

	public static void insertToHistory(String userid, String mid)
	{
		Date date = new Date(System.currentTimeMillis());
		Users user = getUsersByID(userid);
		Mediaitems mediaItem = getMediaitemByID(mid);

		if (user == null || mediaItem == null)
		{
			return; // Printing of errors was already done.
		}

		HistoryId historyID = new HistoryId(Integer.parseInt(userid), Integer.parseInt(mid), date);

		History history = new History(historyID, mediaItem, user);
		insertHistoryToDB(history);

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
		System.out.println("The insertion to history table was successful " + formatter.format(date));
	}

	private static void insertHistoryToDB(History history) throws HibernateException
	{
		Session session = HibernateUtil.currentSession();
		Transaction tx = session.beginTransaction();
		session.save(history);
		tx.commit();
	}

	private static Mediaitems getMediaitemByID(String mid)
	{
		Query query = createQuery("FROM Mediaitems WHERE mid = :mid");
		query.setParameter("mid", Integer.parseInt(mid));
		List<Mediaitems> results = query.list();
		if (results.isEmpty())
		{
			System.out.println("Could not find mid: " + mid);
			return null;
		}
		return results.get(0);
	}

	private static Users getUsersByID(String userid)
	{
		Query query = createQuery("FROM Users WHERE userid = :userid");
		query.setParameter("userid", Integer.parseInt(userid));
		List<Users> results = query.list();
		if (results.isEmpty())
		{
			System.out.println("Could not find userid: " + userid);
			return null;
		}
		return results.get(0);
	}

	public static void insertToLog(String userid)
	{
		Users user = getUsersByID(userid);
		if (user == null)
		{
			return;
		}
		Date date = new Date(System.currentTimeMillis());
		LoginlogId logingId = new LoginlogId(Integer.parseInt(userid), date);
		Loginlog loginlog = new Loginlog(logingId, user);
		insertLoginLogToDB(loginlog);
		System.out.println("The insertion to log table was successful " + date);
	}

	private static void insertLoginLogToDB(Loginlog loginlog)
	{
		Session session = HibernateUtil.currentSession();
		Transaction tx = session.beginTransaction();
		session.save(loginlog);
		tx.commit();
	}

	public static List<?> getHistory(String userid)
	{
		Query query = createQuery("SELECT h.id.viewtime, m.title FROM History h, Mediaitems m join h.mediaitems WHERE userid = :userid ORDER BY h.id.viewtime ASC");
		query.setParameter("userid", Integer.parseInt(userid));
		List<?> histories = query.list();

		return histories;
	}

	public static int getNumberOfRegistredUsers(int n)
	{
		long now = System.currentTimeMillis();
		long nowMinusNDays = now - Duration.ofDays(n).toMillis();
		Date nowMinusNDaysAsDate = new Date(nowMinusNDays);

		Session session = HibernateUtil.currentSession();
		Query query = session.createQuery("FROM Users WHERE REGISTRATION_DATE > :limit");
		query.setParameter("limit", nowMinusNDaysAsDate);
		List<Users> result = query.list();

		return result.size();
	}

	public static List<Users> getUsers()
	{
		Query q = createQuery("FROM Users");
		List<Users> results = q.list();
		return results;
	}

	public static Users getUser(String userid)
	{
		return getUsersByID(userid);
	}
}
