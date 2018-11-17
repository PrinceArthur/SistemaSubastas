package dao;

import java.util.List;

import entity.Offerersale;

/**
 * 
 * @author Guillermo Marcano, Richard Mora y Estefan�a P�rez
 * Interface del DAO de ofertas
 */
public interface OfferersaleDAO
{
	/**
	 * M�todo que crear una oferta
	 * @param offerersale
	 */
	public void nuevo(Offerersale offerersale);
	
	/**
	 * M�tod que devuelve una lista de las ofertas de un usuario espec�fico
	 * @param userName
	 * @return lista
	 */
	public List<Offerersale> getOfferersale(String userName);
	
	/**
	 * M�todo que devuelve una lista de las ofertas hechas en una subasta espec�fica
	 * @param idSubasta
	 * @return list
	 */
	public List<Offerersale> getOfertaDeSubasta(int idSubasta);
	
	/**
	 * M�tdo para modificar una oferta
	 * @param offerersale
	 */
	public void actualizar(Offerersale offerersale);
	
	/**
	 * M�todo para eiminar una oferta
	 * @param oferrersale
	 */
	public void eliminar(Offerersale oferrersale);
	
	/**
	 * M�todo que devuelve una lista de todas las ofertas
	 * @return
	 */
	public List<Offerersale> lista();
	
	public List<Offerersale> listaFiltrada(String filtro);
}
