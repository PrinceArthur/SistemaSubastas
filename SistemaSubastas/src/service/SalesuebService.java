package service;

import java.util.List;

import dao.SalesuebDAOImpl;
import dao.SalesuebDAO;
import entity.Salesueb;

/**
 * 
 * @author Guillermo Marcano, Richard Mora y Estefanía Pérez
 * Service
 */
public class SalesuebService
{
	private SalesuebDAO sales = new SalesuebDAOImpl();
	
	/**
	 * Método que crea subastas
	 * @param salesueb
	 */
	public void nuevo(Salesueb salesueb)
	{
		sales.nuevo(salesueb);
	}
	
	/**
	 * Método que devuelve una lista con las subastas de un proveedor específico
	 * @param userSales
	 * @return lista
	 */
	public List<Salesueb> getSalesueb(String userSales)
	{
		return sales.getSalesueb(userSales);
	}
	
	/**
	 * Método para modificar uan subasta
	 * @param salesueb
	 */
	public void actualizar(Salesueb salesueb)
	{
		sales.actualizar(salesueb);
	}
	
	/**
	 * Método para eliminar una subasta
	 * @param salesueb
	 */
	public void eliminar(Salesueb salesueb)
	{
		sales.eliminar(salesueb);
	}
	
	/**
	 * Método que devuelve una lista con todas las subastas
	 * @return lista
	 */
	public List<Salesueb> lista()
	{
		return sales.lista();
	}
	
	/**
	 * Método que devuelve una lista con las subastas activas
	 * @return
	 */
	public List<Salesueb> listaActivas()
	{
		return sales.listaActivas();
	}
}
