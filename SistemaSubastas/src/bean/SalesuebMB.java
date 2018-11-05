package bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.component.datalist.DataList;

import entity.Salesueb;

@ManagedBean
@SessionScoped
public class SalesuebMB
{
	private Salesueb sale;
	private DataList listaSubastas;
	
	public Salesueb getSale()
	{
		return sale;
	}
	public void setSale(Salesueb sale)
	{
		this.sale = sale;
	}
	public DataList getListaSubastas()
	{
		return listaSubastas;
	}
	public void setListaSubastas(DataList listaSubastas)
	{
		this.listaSubastas = listaSubastas;
	}
	
	
}
