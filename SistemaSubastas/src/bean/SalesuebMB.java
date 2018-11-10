package bean;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import java.io.File;
import java.util.Base64;
import java.util.Date;

import entity.Salesueb;
import entity.User;
import service.SalesuebService;

@ManagedBean
@SessionScoped
public class SalesuebMB {
	private Salesueb sale;
	private DataModel listaSubastas;
	private DataModel listaProveedor;
	private User proveedor = new User();

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
		sale.setState("W");
		service.nuevo(sale);
	}

	public String modificarSubasta() {
		SalesuebService service = new SalesuebService();
		service.actualizar(sale);
		return "/usuarios/subasta";
	}

	public String eliminarSubasta() {
		Salesueb subastaTemp = (Salesueb) (listaSubastas.getRowData());
		SalesuebService service = new SalesuebService();

		subastaTemp.setState("INACTIVE");
		service.actualizar(subastaTemp);
		return "/usuarios/indexProveedor";
	}

	public Salesueb getSale() {
		return sale;
	}

	public void setSale(Salesueb sale) {
		this.sale = sale;
	}

	public DataModel getListaSubastas() {
		List<Salesueb> lista = new SalesuebService().lista();
		listaSubastas = new ListDataModel(lista);
		return listaSubastas;
	}

	public void setListaSubastas(DataModel<Salesueb> listaSubastas) {
		this.listaSubastas = listaSubastas;
	}


	public DataModel getListaProveedor(String proveedorLista) {
		List<Salesueb> lista = new SalesuebService().listaPro(proveedorLista);
		listaProveedor = new ListDataModel(lista);
		return listaProveedor;
	}

	public void setListaProveedor(DataModel listaProveedor) {
		this.listaProveedor = listaProveedor;
	}

}
