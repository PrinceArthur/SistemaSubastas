package bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import java.util.List;

import entity.Offerersale;
import service.OfferersaleService;

/**
 * ManagedBean para las ofertas
 * @author Guillermo Marcano, Richard Mora y Estefanía Pérez
 *
 */
@ManagedBean
@SessionScoped
public class OfferersaleMB
{
	/**
	 * Oferta
	 */
	private Offerersale offerer;
	
	/**
	 * Lista de ofertas
	 */
	private DataModel listaOfertas;
	
	/**
	 * Da la oferta
	 * @return offerer
	 */
	public Offerersale getOfferer()
	{
		return offerer;
	}
	
	/**
	 * Modifica la oferta
	 * @param offerer
	 */
	public void setOfferer(Offerersale offerer)
	{
		this.offerer = offerer;
	}
	
	/**
	 * Da la lista de las ofertas
	 * @return listaOfertas
	 */
	public DataModel getListaOfertas()
	{
		List lista = new OfferersaleService().lista();
		listaOfertas = new ListDataModel(lista);
		return listaOfertas;
	}
	
	/**
	 * Modifica la lista de las ofertas
	 * @param listaOfertas
	 */
	public void setListaOfertas(DataModel listaOfertas)
	{
		this.listaOfertas = listaOfertas;
	}
	
	
}
