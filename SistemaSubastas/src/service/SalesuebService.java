package service;

import java.util.List;

import dao.SalesuebDAOImpl;
import dao.SalesuebDAO;
import entity.Salesueb;

public class SalesuebService
{
	private SalesuebDAO sales = new SalesuebDAOImpl();
	
	public void nuevo(Salesueb salesueb)
	{
		sales.nuevo(salesueb);
	}
	
	public List<Salesueb> getSalesueb(String userSales)
	{
		return sales.getSalesueb(userSales);
	}
	
	public void actualizar(Salesueb salesueb)
	{
		sales.actualizar(salesueb);
	}
	
	public void eliminar(Salesueb salesueb)
	{
		sales.eliminar(salesueb);
	}
	
	public List<Salesueb> lista()
	{
		return sales.lista();
	}
}
