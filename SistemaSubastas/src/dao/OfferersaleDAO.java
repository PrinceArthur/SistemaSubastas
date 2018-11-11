package dao;

import java.util.List;

import entity.Offerersale;

public interface OfferersaleDAO
{
	public void nuevo(Offerersale offerersale);
	public List<Offerersale> getOfferersale(String userName);
	public void actualizar(Offerersale offerersale);
	public void eliminar(Offerersale oferrersale);
	public List<Offerersale> lista();
}
