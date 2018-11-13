package bean;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import entity.Parameter;
import service.ParameterService;

/**
 * ManagedBean de los parametros
 * 
 * @author Guillermo Marcano, Richard Mora y Estefanía Pérez
 *
 */

@ManagedBean
@SessionScoped
public class ParameterMB
{
	/**
	 * Auditoria
	 */
	private AuditMB audit = new AuditMB();

	/**
	 * Parametro
	 */
	private Parameter parameter;

	/**
	 * Lista de los parametros
	 */
	private DataModel listaParametros;

	/**
	 * Método para preprarar la modificación del parametro
	 * 
	 * @return la página del formulario para la modificación
	 */
	public String prepararModificarParametro()
	{
		parameter = (Parameter) (listaParametros.getRowData());
		return "/administrador/modificarParametro";
	}

	/**
	 * Méotodo para modificar el parámetro
	 * 
	 * @return nos devuelve a la página de la lista de parámetros
	 */
	public String modificarParameter()
	{
		ParameterService servicio = new ParameterService();
		servicio.actualizar(parameter);
		audit.adicionarAudit("Admin", "UPDATE", "Parameter", parameter.getId());
		return "/administrador/indexParameter";
	}

	/**
	 * Método para desactivar un parámetro
	 */
	public void eliminarParameter()
	{
		Parameter parametroTemp = (Parameter) (listaParametros.getRowData());
		ParameterService servicio = new ParameterService();

		if (parametroTemp.getState().equalsIgnoreCase("ACTIVE"))
		{
			parametroTemp.setState("INACTIVE");
			audit.adicionarAudit("Admin", "DELETE", "Parameter", parametroTemp.getId());
		} else if (parametroTemp.getState().equalsIgnoreCase("INACTIVE"))
		{
			parametroTemp.setState("ACTIVE");
		}
	}

	/**
	 * método para dar un mensaje en la vista
	 * @param summary
	 * @param detail
	 */
	public void addMessage(String summary, String detail)
	{
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	/**
	 * Da el parámetro
	 * @return parameter
	 */
	public Parameter getParameter()
	{
		return parameter;
	}

	/**
	 * Modifica el parametro
	 * @param parameter
	 */
	public void setParameter(Parameter parameter)
	{
		this.parameter = parameter;
	}

	/**
	 * Da la lista de parametros inicializada
	 * @return listaParametros
	 */
	public DataModel<Parameter> getListaParametros()
	{
		List<Parameter> lista = new ParameterService().lista();
		listaParametros = new ListDataModel(lista);
		return listaParametros;
	}

	/**
	 * Modifica la lista de los parámetros
	 * @param listaParametros
	 */
	public void setListaParametros(DataModel<Parameter> listaParametros)
	{
		this.listaParametros = listaParametros;
	}

}
