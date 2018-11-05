package bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.component.datalist.DataList;

import entity.Offerersale;

@ManagedBean
@SessionScoped
public class OfferersaleMB
{
	private Offerersale offerer;
	private DataList listaOfertas;
	
	public Offerersale getOfferer()
	{
		return offerer;
	}
	public void setOfferer(Offerersale offerer)
	{
		this.offerer = offerer;
	}
	public DataList getListaOfertas()
	{
		return listaOfertas;
	}
	public void setListaOfertas(DataList listaOfertas)
	{
		this.listaOfertas = listaOfertas;
	}
	
	
}
