package dao;

import java.util.List;

import entity.Salesueb;

/**
 * 
 * @author Guillermo Marcano, Richard Mora y Estefan�a P�rez
 * Interface del DAO de subastas
 */
public interface SalesuebDAO
{
	/**
	 * M�todo que crea subastas
	 * @param salesueb
	 */
	public void nuevo(Salesueb salesueb);
	
	/**
	 * M�todo que devuelve una lista con las subastas de un proveedor espec�fico
	 * @param userSales
	 * @return lista
	 */
	public List<Salesueb> getSalesueb(String userSales);
	
	/**
	 * M�todo para modificar uan subasta
	 * @param salesueb
	 */
	public void actualizar(Salesueb salesueb);
	
	/**
	 * M�todo para eliminar una subasta
	 * @param salesueb
	 */
	public void eliminar(Salesueb salesueb);
	
	/**
	 * M�todo que devuelve una lista con todas las subastas
	 * @return lista
	 */
	public List<Salesueb> lista();
	
	/**
	 * M�todo que devuelve una lista con las subastas activas
	 * @return
	 */
	public List<Salesueb> listaActivas();
}
