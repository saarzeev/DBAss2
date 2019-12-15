

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import org.hibernate.*;

public class Assignment {

	public static void main(String[] args) {


		//SessionFactory factory = new Configuration().configure().buildSessionFactory();
		Session session = HibernateUtil.currentSession();
		

	}
}
