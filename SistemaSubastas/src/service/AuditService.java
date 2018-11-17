package service;

import java.util.List;

import dao.AuditDAO;
import dao.AuditDAOImpl;
import entity.Audit;

/**
 * 
 * @author Guillermo Marcano, Richard Mora y Estefan�a P�rez
 *
 */
public class AuditService
{
	/**
	 * DAO
	 */
	AuditDAO audit = new AuditDAOImpl();
	
	/**
	 * M�todo para crear auditor�a
	 * @param auditoria
	 */
	public void nuevo(Audit auditoria)
	{
		audit.nuevo(auditoria);
	}
	
	/**
	 * M�todo para obtener la lista de auditor�as de un usuario espec�fico
	 * @param userName
	 * @return lista
	 */
	public List<Audit> auditUser(String userName)
	{
		return audit.getAuditUser(userName);
	}
	
	/**
	 * M�todo para obtener la lista de auditor�as de una operaci�n CRUD espec�fica
	 * @param operationCrud
	 * @return lista
	 */
	public List<Audit> auditCrud(String operationCrud)
	{
		return audit.getAuditCrud(operationCrud);
	}
	
	/**
	 * M�todo para modificar
	 * @param auditoria
	 */
	public void actualizar(Audit auditoria)
	{
		audit.actualizar(auditoria);
	}
	
	/**
	 * M�todo para eliminar
	 * @param auditoria
	 */
	public void eliminar(Audit auditoria)
	{
		audit.eliminar(auditoria);
	}
	
	/**
	 * M�todo que devuelve una lista con todas las auditor�as
	 * @return lista
	 */
	public List<Audit> lista()
	{
		return audit.lista();
	}
	
	/**
	 * M�todo con el que podemos filtrar cualquier lista
	 * @param filtro
	 * @return lista por el filtro correspondiente
	 */
	public List<Audit> getAuditFilter(String filtro)
	{
		return audit.getAuditFilter(filtro);
	}
}
