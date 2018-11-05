package entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the audit database table.
 * 
 */
@Entity
@NamedQuery(name="Audit.findAll", query="SELECT a FROM Audit a")
public class Audit implements Serializable {
	private static final long serialVersionUID = 1L;

	private String addressIP;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	private int id;

	private String operationCrud;

	private int tableId;

	private String tableName;

	private int userId;

	public Audit() {
	}

	public String getAddressIP() {
		return this.addressIP;
	}

	public void setAddressIP(String addressIP) {
		this.addressIP = addressIP;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOperationCrud() {
		return this.operationCrud;
	}

	public void setOperationCrud(String operationCrud) {
		this.operationCrud = operationCrud;
	}

	public int getTableId() {
		return this.tableId;
	}

	public void setTableId(int tableId) {
		this.tableId = tableId;
	}

	public String getTableName() {
		return this.tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}