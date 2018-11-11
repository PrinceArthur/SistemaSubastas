package service;

import java.util.List;

import dao.OfferersaleDAO;
import dao.OfferersaleDAOImpl;
import entity.Offerersale;

public class OfferersaleService
{
	private OfferersaleDAO offer = new OfferersaleDAOImpl();
	
	public void nuevo(Offerersale offerersale)
	{
		offer.nuevo(offerersale);
	}
	
	public List<Offerersale> getOfferersale(String userName)
	{
		return offer.getOfferersale(userName);
	}
	
	public void actualizar(Offerersale offerersale)
	{
		offer.actualizar(offerersale);
	}
	
	public void eliminar(Offerersale oferrersale)
	{
		offer.eliminar(oferrersale);
	}
	
	public List<Offerersale> lista()
	{
		return offer.lista();
	}
}
