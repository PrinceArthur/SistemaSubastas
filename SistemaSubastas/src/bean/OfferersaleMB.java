package bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;


import entity.Offerersale;

@ManagedBean
@SessionScoped
public class OfferersaleMB
{
	private Offerersale offerer;
	private DataModel listaOfertas;
	
	public Offerersale getOfferer()
	{
		return offerer;
	}
	public void setOfferer(Offerersale offerer)
	{
		this.offerer = offerer;
	}
	public DataModel getListaOfertas()
	{
		return listaOfertas;
	}
	public void setListaOfertas(DataModel listaOfertas)
	{
		this.listaOfertas = listaOfertas;
	}
	
	
}
