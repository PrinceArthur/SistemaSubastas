package dao;

import java.util.List;

import entity.Audit;


public interface AuditDAO {
	
	public void nuevo(Audit auditoria);
	public List<Audit> getAuditUser(String userName);
	public List<Audit> getAuditCrud(String operationCrud);
	public void actualizar(Audit auditoria);
	public void eliminar(Audit auditoria);
	public List<Audit> lista();
	public List<Audit> listaRegistrosPostor(String createDate);

}
