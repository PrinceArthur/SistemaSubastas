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
	
	public List<Audit> auditUser(String userName)
	{
		return audit.getAuditUser(userName);
	}
	
	public List<Audit> auditCrud(String operationCrud)
	{
		return audit.getAuditCrud(operationCrud);
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
	
	public List<Audit> listaFecha(String fecha)
	{
		return audit.listaRegistrosPostor(fecha);
	}
}
