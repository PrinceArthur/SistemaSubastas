package service;

import java.util.List;

import dao.SalesuebDAOImpl;
import dao.SalesuebDAO;
import entity.Salesueb;

/**
 * 
 * @author Guillermo Marcano, Richard Mora y Estefan�a P�rez
 * Service
 */
public class SalesuebService
{
	private SalesuebDAO sales = new SalesuebDAOImpl();
	
	/**
	 * M�todo que crea subastas
	 * @param salesueb
	 */
	public void nuevo(Salesueb salesueb)
	{
		sales.nuevo(salesueb);
	}
	
	/**
	 * M�todo que devuelve una lista con las subastas de un proveedor espec�fico
	 * @param userSales
	 * @return lista
	 */
	public List<Salesueb> getSalesueb(String userSales)
	{
		return sales.getSalesueb(userSales);
	}
	
	/**
	 * M�todo para modificar uan subasta
	 * @param salesueb
	 */
	public void actualizar(Salesueb salesueb)
	{
		sales.actualizar(salesueb);
	}
	
	/**
	 * M�todo para eliminar una subasta
	 * @param salesueb
	 */
	public void eliminar(Salesueb salesueb)
	{
		sales.eliminar(salesueb);
	}
	
	/**
	 * M�todo que devuelve una lista con todas las subastas
	 * @return lista
	 */
	public List<Salesueb> lista()
	{
		return sales.lista();
	}
	
	/**
	 * M�todo que devuelve una lista con las subastas activas
	 * @return
	 */
	public List<Salesueb> listaActivas()
	{
		return sales.listaActivas();
	}
}
