package dao;

import java.util.List;

import entity.Salesueb;

/**
 * 
 * @author Guillermo Marcano, Richard Mora y Estefanía Pérez
 * Interface del DAO de subastas
 */
public interface SalesuebDAO
{
	/**
	 * Método que crea subastas
	 * @param salesueb
	 */
	public void nuevo(Salesueb salesueb);
	
	/**
	 * Método que devuelve una lista con las subastas de un proveedor específico
	 * @param userSales
	 * @return lista
	 */
	public List<Salesueb> getSalesueb(String userSales);
	
	/**
	 * Método para modificar uan subasta
	 * @param salesueb
	 */
	public void actualizar(Salesueb salesueb);
	
	/**
	 * Método para eliminar una subasta
	 * @param salesueb
	 */
	public void eliminar(Salesueb salesueb);
	
	/**
	 * Método que devuelve una lista con todas las subastas
	 * @return lista
	 */
	public List<Salesueb> lista();
	
	/**
	 * Método que devuelve una lista con las subastas activas
	 * @return
	 */
	public List<Salesueb> listaActivas();
}
