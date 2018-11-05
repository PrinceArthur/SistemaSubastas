package dao;

import java.util.List;

import entity.Salesueb;

public interface SaleuebDAO
{
	public void nuevo(Salesueb offerersale);
	public Salesueb getSalesueb(String userName);
	public void actualizar(Salesueb offerersale);
	public void eliminar(Salesueb oferrersale);
	public List<Salesueb> lista();
}
