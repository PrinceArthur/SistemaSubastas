package daoImpl;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import dao.AuditDAO;
import entities.Audit;
import util.HibernateUtil;

public class AuditDAOImpl implements AuditDAO{

	@Override
	public void save(Audit audi) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.save(audi);
		t.commit();
		
	}

	@Override
	public List<Audit> list() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		List lista = session.createQuery("from Audit").list();
		t.commit();
		return lista;
	}

}
