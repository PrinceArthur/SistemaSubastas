package service;

import java.util.List;

import dao.AuditDAO;
import dao.AuditDAOImpl;
import entity.Audit;

public class AuditService
{
	AuditDAO audit = new AuditDAOImpl();
	
	public void nuevo(Audit auditoria)
	{
		audit.nuevo(auditoria);
	}
	
	public Audit getAudit(int id)
	{
		return audit.getAudit(id);
	}
	
	public void actualizar(Audit auditoria)
	{
		audit.actualizar(auditoria);
	}
	
	public void eliminar(Audit auditoria)
	{
		audit.eliminar(auditoria);
	}
	
	public List<Audit> lista()
	{
		return audit.lista();
	}
}
