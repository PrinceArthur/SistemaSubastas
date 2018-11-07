package bean;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import dao.AuditDAO;
import dao.AuditDAOImpl;
import entity.Audit;
import service.AuditService;

@ManagedBean
@SessionScoped
public class AuditMB {
	
	private Audit auditoria;
	private DataModel listaAudit;
	
	public AuditMB()
	{
		auditoria = new Audit();
	}
	
	public void adicionarAudit(String userName, String operationCrud, String tableName, int idUsuario) 
	{
		AuditService service = new AuditService();
		Date date = new Date();
		auditoria.setCreateDate(date);
		auditoria.setUserName(userName);
		auditoria.setOperationCrud(operationCrud);
		auditoria.setTableId(idUsuario);
		auditoria.setTableName(tableName);
		try
		{
			auditoria.setAddressIP(InetAddress.getLocalHost().getHostAddress());
			AuditService audit = new AuditService();
			service.nuevo(auditoria);
		} catch (UnknownHostException e)
		{

			e.getMessage();
		}
	}
	
	public Audit getAuditoria() {
		return auditoria;
	}

	public void setAuditoria(Audit auditoria) {
		this.auditoria = auditoria;
	}

	public DataModel getListarAuditorias() {
		List<Audit> lista = new AuditDAOImpl().lista();
		listaAudit = new ListDataModel(lista);
		return listaAudit;
	}

}
