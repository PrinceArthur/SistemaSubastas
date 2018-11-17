package dao;

import java.util.List;

import entity.Offerersale;

/**
 * 
 * @author Guillermo Marcano, Richard Mora y Estefanía Pérez
 * Interface del DAO de ofertas
 */
public interface OfferersaleDAO
{
	/**
	 * Método que crear una oferta
	 * @param offerersale
	 */
	public void nuevo(Offerersale offerersale);
	
	/**
	 * Métod que devuelve una lista de las ofertas de un usuario específico
	 * @param userName
	 * @return lista
	 */
	public List<Offerersale> getOfferersale(String userName);
	
	/**
	 * Método que devuelve una lista de las ofertas hechas en una subasta específica
	 * @param idSubasta
	 * @return list
	 */
	public List<Offerersale> getOfertaDeSubasta(int idSubasta);
	
	/**
	 * Métdo para modificar una oferta
	 * @param offerersale
	 */
	public void actualizar(Offerersale offerersale);
	
	/**
	 * Método para eiminar una oferta
	 * @param oferrersale
	 */
	public void eliminar(Offerersale oferrersale);
	
	/**
	 * Método que devuelve una lista de todas las ofertas
	 * @return
	 */
	public List<Offerersale> lista();
	
	public List<Offerersale> listaFiltrada(String filtro);
}
