package dao;

import java.util.List;

import entity.Audit;

/**
 * 
 * @author Guillermo Marcano, Richard Mora y Estefanía Pérez
 * Interface del DAO de Auditorías
 *
 */
public interface AuditDAO {
	
	/**
	 * Método para crear auditoría
	 * @param auditoria
	 */
	public void nuevo(Audit auditoria);
	
	/**
	 * Método para obtener la lista de auditorías de un usuario específico
	 * @param userName
	 * @return lista
	 */
	public List<Audit> getAuditUser(String userName);
	
	/**
	 * Método para obtener la lista de auditorías de una operación CRUD específica
	 * @param operationCrud
	 * @return lista
	 */
	public List<Audit> getAuditCrud(String operationCrud);
	
	/**
	 * Método para modificar
	 * @param auditoria
	 */
	public void actualizar(Audit auditoria);
	
	/**
	 * Método para eliminar
	 * @param auditoria
	 */
	public void eliminar(Audit auditoria);
	
	/**
	 * Método que devuelve una lista con todas las auditorías
	 * @return lista
	 */
	public List<Audit> lista();
	
	/**
	 * Método con el que podemos filtrar cualquier lista
	 * @param filtro
	 * @return lista por el filtro correspondiente
	 */
	public List<Audit> getAuditFilter(String filtro);

}
