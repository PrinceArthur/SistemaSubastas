package dao;

import java.util.List;

import entity.Salesueb;

public interface SalesuebDAO
{
	public void nuevo(Salesueb salesueb);
	public Salesueb getSalesueb(String userSales);
	public void actualizar(Salesueb salesueb);
	public void eliminar(Salesueb salesueb);
	public List<Salesueb> lista();
}
