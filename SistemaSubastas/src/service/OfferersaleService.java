package service;

import java.util.List;

import dao.OfferersaleDAO;
import dao.OfferersaleDAOImpl;
import entity.Offerersale;

/**
 * 
 * @author Guillermo Marcano, Richard Mora y Estefanía Pérez
 * Servie del DAO
 */
public class OfferersaleService
{
	private OfferersaleDAO offer = new OfferersaleDAOImpl();
	
	/**
	 * Método que crear una oferta
	 * @param offerersale
	 */
	public void nuevo(Offerersale offerersale)
	{
		offer.nuevo(offerersale);
	}
	
	/**
	 * Métod que devuelve una lista de las ofertas de un usuario específico
	 * @param userName
	 * @return lista
	 */
	public List<Offerersale> getOfferersale(String userName)
	{
		return offer.getOfferersale(userName);
	}
	
	/**
	 * Método que devuelve una lista de las ofertas hechas en una subasta específica
	 * @param idSubasta
	 * @return list
	 */
	public void actualizar(Offerersale offerersale)
	{
		offer.actualizar(offerersale);
	}
	
	/**
	 * Métdo para modificar una oferta
	 * @param offerersale
	 */
	public void eliminar(Offerersale oferrersale)
	{
		offer.eliminar(oferrersale);
	}
	
	/**
	 * Método para eiminar una oferta
	 * @param oferrersale
	 */
	public List<Offerersale> lista()
	{
		return offer.lista();
	}
	
	/**
	 * Método que devuelve una lista de todas las ofertas
	 * @return
	 */
	public List<Offerersale> getOfertaDeSubasta(int idSales)
	{
		return offer.getOfertaDeSubasta(idSales);
	}
	
	public List<Offerersale> listaFiltrada(String filtro)
	{
		return offer.listaFiltrada(filtro);
	}
}
