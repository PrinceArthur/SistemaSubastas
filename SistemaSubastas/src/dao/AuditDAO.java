package dao;

import java.util.List;

import entity.Audit;


public interface AuditDAO {
	
	public void nuevo(Audit auditoria);
	public Audit getAudit(int id);
	public void actualizar(Audit auditoria);
	public void eliminar(Audit auditoria);
	public List<Audit> lista();

}