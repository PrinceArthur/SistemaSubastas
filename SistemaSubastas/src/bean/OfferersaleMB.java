package bean;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.primefaces.component.datalist.DataList;

import entity.Offerersale;
import entity.Salesueb;
import service.OfferersaleService;
import service.SalesuebService;

@ManagedBean
@SessionScoped
public class OfferersaleMB
{
	private Offerersale offerer;
	private DataModel<Offerersale> listaOfertas;
	
	public String prepararAdicionarOferta()
	{
		offerer = new Offerersale();
		return "";
	}
	
	public String agregarOferta()
	{
		OfferersaleService service = new OfferersaleService();
		service.nuevo(offerer);
		return "";
	}
	
	
	public Offerersale getOfferer()
	{
		return offerer;
	}
	public void setOfferer(Offerersale offerer)
	{
		this.offerer = offerer;
	}
	public DataModel<Offerersale> getListaOfertas()
	{
		List<Offerersale> lista = new OfferersaleService().lista();
		listaOfertas = new ListDataModel(lista);
		return listaOfertas;
	}
	public void setListaOfertas(DataModel<Offerersale> listaOfertas)
	{
		this.listaOfertas = listaOfertas;
	}
	
	
}
