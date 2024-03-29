package Assignment;
//package com.tsystems.casou.hibernate;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.*;
import org.hibernate.cfg.*;


/**
 * @author tsikinov
 *  
 */
public class HibernateUtil {

    private static Log log = LogFactory.getLog(HibernateUtil.class);
    private static final SessionFactory sessionFactory;

    static {
        try {
            // Create the SessionFactory                
            sessionFactory = new Configuration().configure().buildSessionFactory();
           
        } catch (Throwable ex) {
            
            log.error("Initial SessionFactory creation failed.", ex);
            throw new ExceptionInInitializerError(ex);
            
        } // end of the try - catch block
        
    } // end of static block

    public static final ThreadLocal<Session> session = new ThreadLocal<Session>();

    public static Session currentSession() throws HibernateException {
        
        Session s = (Session) session.get();
        
        // Open a new Session, if this Thread has none yet
        if (s == null) {
            s = sessionFactory.openSession();
            session.set(s);
        }
        return s;
    }

    public static void closeSession() throws HibernateException {
        Session s = (Session) session.get();
        session.set(null);
        if (s != null)
            s.close();
    } // end of the method

} // end of the class 