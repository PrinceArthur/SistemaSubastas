package bean;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;


import entity.Salesueb;
import service.SalesuebService;

@ManagedBean
@SessionScoped
public class SalesuebMB
{
	private Salesueb sale;
	private DataModel<Salesueb> listaSubastas;
	
	public String prepararAdicionarSubasta()
	{
		sale = new Salesueb();
		return "/usuarios/adicionarSubasta";
	}
	
	public String prepararModificarSubasta()
	{
		sale = (Salesueb)(listaSubastas.getRowData());
		return "/usuarios/modificarSubasta";
	}
	
	public String agregarSubasta()
	{
		SalesuebService service = new SalesuebService();
		service.nuevo(sale);
		return "/usuarios/subasta";
	}
	
	public String modificarSubasta()
	{
		SalesuebService service = new SalesuebService();
		service.actualizar(sale);
		return "/usuarios/subasta";
	}
	
	public String eliminarSubasta()
	{
		Salesueb subastaTemp = (Salesueb) (listaSubastas.getRowData());
		SalesuebService service = new SalesuebService();
		
		subastaTemp.setState("INACTIVE");
		service.actualizar(subastaTemp);
		return "/usuarios/indexProveedor";
	}
	
	public Salesueb getSale()
	{
		return sale;
	}
	public void setSale(Salesueb sale)
	{
		this.sale = sale;
	}
	public DataModel<Salesueb> getListaSubastas()
	{
		List<Salesueb> lista = new SalesuebService().lista();
		listaSubastas = new ListDataModel(lista);
		return listaSubastas;
	}
	public void setListaSubastas(DataModel<Salesueb> listaSubastas)
	{
		this.listaSubastas = listaSubastas;
	}
	
	
}
