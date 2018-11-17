package dao;

import java.util.List;

import entity.Audit;

/**
 * 
 * @author Guillermo Marcano, Richard Mora y Estefan�a P�rez
 * Interface del DAO de Auditor�as
 *
 */
public interface AuditDAO {
	
	/**
	 * M�todo para crear auditor�a
	 * @param auditoria
	 */
	public void nuevo(Audit auditoria);
	
	/**
	 * M�todo para obtener la lista de auditor�as de un usuario espec�fico
	 * @param userName
	 * @return lista
	 */
	public List<Audit> getAuditUser(String userName);
	
	/**
	 * M�todo para obtener la lista de auditor�as de una operaci�n CRUD espec�fica
	 * @param operationCrud
	 * @return lista
	 */
	public List<Audit> getAuditCrud(String operationCrud);
	
	/**
	 * M�todo para modificar
	 * @param auditoria
	 */
	public void actualizar(Audit auditoria);
	
	/**
	 * M�todo para eliminar
	 * @param auditoria
	 */
	public void eliminar(Audit auditoria);
	
	/**
	 * M�todo que devuelve una lista con todas las auditor�as
	 * @return lista
	 */
	public List<Audit> lista();
	
	/**
	 * M�todo con el que podemos filtrar cualquier lista
	 * @param filtro
	 * @return lista por el filtro correspondiente
	 */
	public List<Audit> getAuditFilter(String filtro);

}
