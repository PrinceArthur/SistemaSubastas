package entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the salesueb database table.
 * 
 */
@Entity
@NamedQuery(name="Salesueb.findAll", query="SELECT s FROM Salesueb s")
public class Salesueb implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date dateEnd;

	@Temporal(TemporalType.TIMESTAMP)
	private Date dateStart;

	private String descriptionProduct;

	private String userSales;

	private String photoProduct;

	private String state;

	private int valueBase;

	private int valueCurrent;
	
	private String name;

	public Salesueb() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDateEnd() {
		return this.dateEnd;
	}

	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}

	public Date getDateStart() {
		return this.dateStart;
	}

	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
	}

	public String getDescriptionProduct() {
		return this.descriptionProduct;
	}

	public void setDescriptionProduct(String descriptionProduct) {
		this.descriptionProduct = descriptionProduct;
	}

	public String getIdentificationSales() {
		return this.userSales;
	}

	public void setIdentificationSales(String userSales) {
		this.userSales = userSales;
	}

	public String getPhotoProduct() {
		return this.photoProduct;
	}

	public void setPhotoProduct(String photoProduct) {
		this.photoProduct = photoProduct;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getValueBase() {
		return this.valueBase;
	}

	public void setValueBase(int valueBase) {
		this.valueBase = valueBase;
	}

	public int getValueCurrent() {
		return this.valueCurrent;
	}

	public void setValueCurrent(int valueCurrent) {
		this.valueCurrent = valueCurrent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}