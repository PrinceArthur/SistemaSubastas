package bean;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;


import entity.Salesueb;
import service.SalesuebService;

/**
 * ManagedBean para las subastas
 * @author Guillermo Marcano, Richard Mora y Estefanía Pérez
 *
 */
@ManagedBean
@SessionScoped
public class SalesuebMB
{
	/**
	 * Subasta
	 */
	private Salesueb sale;
	
	/**
	 * Lista de las subastas
	 */
	private DataModel listaSubastas;
	
	/**
	 * Se agrega una subasta
	 * @param xxx
	 * @param fechaInicio
	 * @param fechaFinal
	 * @param imagen
	 * @param descripcion
	 * @param nombre
	 * @param valorInicial
	 */
	public void agregarSubasta(String xxx, Date fechaInicio, Date fechaFinal, String imagen, String descripcion,
			String nombre, int valorInicial) {
		SalesuebService service = new SalesuebService();
		sale = new Salesueb();
		sale.setIdentificationSales(xxx);
		sale.setDateStart(fechaInicio);
		sale.setDateEnd(fechaFinal);
		sale.setPhotoProduct(imagen);
		sale.setDescriptionProduct(descripcion);
		sale.setName(nombre);
		sale.setValueBase(valorInicial);
		sale.setState("WAITING");
		service.nuevo(sale);
	}
	
	/**
	 * Método para modificar la subasta
	 * @return página donde aparecen los datos de la subasta
	 */
	public String modificarSubasta() {
		SalesuebService service = new SalesuebService();
		service.actualizar(sale);
		return "/usuarios/subasta";
	}

	/**
	 * Método para eliminar la subasta
	 * @return página con la lista de las subastas
	 */
	public String eliminarSubasta() {
		Salesueb subastaTemp = (Salesueb) (listaSubastas.getRowData());
		SalesuebService service = new SalesuebService();

		subastaTemp.setState("INACTIVE");
		service.actualizar(subastaTemp);
		return "/usuarios/indexProveedor";
	}
	
	/**
	 * Da la subasta
	 * @return sale
	 */
	public Salesueb getSale()
	{
		return sale;
	}
	
	/**
	 * Modifica la subasta
	 * @param sale
	 */
	public void setSale(Salesueb sale)
	{
		this.sale = sale;
	}
	
	/**
	 * Da la lista de subastas inicializada
	 * @return
	 */
	public DataModel<Salesueb> getListaSubastas()
	{
		List<Salesueb> lista = new SalesuebService().lista();
		listaSubastas = new ListDataModel(lista);
		return listaSubastas;
	}
	
	/**
	 *Modiifca la lista de subastas
	 * @param listaSubastas
	 */
	public void setListaSubastas(DataModel<Salesueb> listaSubastas)
	{
		this.listaSubastas = listaSubastas;
	}
	
	
}