package util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import entity.Audit;
import entity.Offerersale;
import entity.Parameter;
import entity.Salesueb;
import entity.User;

/**
 * Clase de Hibernate
 * @author Guillermo Marcano, Richard Mora y Estefanía Pérez
 *
 */
public class HibernateUtil {

	/**
	 * Sesión 
	 */
	private static SessionFactory sessionFactory;

	/**
	 * Constructor
	 */
	private HibernateUtil() {

	}

	/**
	 * Configura el hibernate
	 * @return sessionFactory
	*/
	
	@SuppressWarnings("deprecation")
	public static SessionFactory getSessionFactory() {

		if (sessionFactory == null) {
			try {
				// Create the SessionFactory from standard (hibernate.cfg.xml)
				// config file.
				@SuppressWarnings("deprecation")
				AnnotationConfiguration ac = new AnnotationConfiguration();
				ac.addAnnotatedClass(User.class);
				ac.addAnnotatedClass(Audit.class);
				ac.addAnnotatedClass(Parameter.class);
				ac.addAnnotatedClass(Offerersale.class);
				ac.addAnnotatedClass(Salesueb.class);
				sessionFactory = ac.configure().buildSessionFactory();
				

			} catch (Throwable ex) {
				// Log the exception.
				System.err.println("Initial SessionFactory creation failed." + ex);
				throw new ExceptionInInitializerError(ex);
			}

			return sessionFactory;

		} else {
			return sessionFactory;
		}

	}

}