package entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the offerersales database table.
 * 
 */
@Entity
@Table(name="offerersales")
@NamedQuery(name="Offerersale.findAll", query="SELECT o FROM Offerersale o")
public class Offerersale implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date dateOffer;

	private String userName;

	private int idSales;

	private int valueOffer;

	private String winner;
	
	private String producto;

	public Offerersale() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDateOffer() {
		return this.dateOffer;
	}

	public void setDateOffer(Date dateOffer) {
		this.dateOffer = dateOffer;
	}

	public String getIdentification() {
		return this.userName;
	}

	public void setIdentification(String userName) {
		this.userName = userName;
	}

	public int getIdSales() {
		return this.idSales;
	}

	public void setIdSales(int idSales) {
		this.idSales = idSales;
	}

	public int getValueOffer() {
		return this.valueOffer;
	}

	public void setValueOffer(int valueOffer) {
		this.valueOffer = valueOffer;
	}

	public String getWinner() {
		return this.winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}

	public String getProducto()
	{
		return producto;
	}

	public void setProducto(String producto)
	{
		this.producto = producto;
	}
	
	

}
