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

	private String mensajeError;

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

	public String prepararAgregarParametro()
	{
		parameter = new Parameter();
		parameter.setState("ACTIVO");
		return "/administrador/crearParametro";
	}
	
	public boolean validaciion()
	{
		boolean valido = true;
		
		if (!parameter.getParameterType().equals("T") || !parameter.getParameterType().equals("N"))
		{
			valido = false;
			mensajeError = "El tipo del parametro solo puede ser T o N";
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
		}
		
		
		
		return valido;
	}

	public String nuevoParametro()
	{

		if(validaciion()==true)
		{
					
		ParameterService servicio = new ParameterService();
		servicio.nuevo(parameter);
		}
		return "/administrador/indexParameter";
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
		audit.adicionarAudit("Admin", "ACTUALIZAR", "Parameter", parameter.getId());
		return "/administrador/indexParameter";
	}

	/**
	 * Método para desactivar un parámetro
	 */
	public void eliminarParameter()
	{
		Parameter parametroTemp = (Parameter) (listaParametros.getRowData());
		ParameterService servicio = new ParameterService();

		if (parametroTemp.getState().equalsIgnoreCase("ACTIVO"))
		{
			parametroTemp.setState("INACTIVO");
			servicio.actualizar(parametroTemp);
			audit.adicionarAudit("Admin", "BORRAR", "Parameter", parametroTemp.getId());
		} else if (parametroTemp.getState().equalsIgnoreCase("INACTIVO"))
		{
			parametroTemp.setState("ACTIVO");
			servicio.actualizar(parametroTemp);
		}
	}

	/**
	 * Da el parámetro
	 * 
	 * @return parameter
	 */
	public Parameter getParameter()
	{
		return parameter;
	}

	/**
	 * Modifica el parametro
	 * 
	 * @param parameter
	 */
	public void setParameter(Parameter parameter)
	{
		this.parameter = parameter;
	}

	/**
	 * Da la lista de parametros inicializada
	 * 
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
	 * 
	 * @param listaParametros
	 */
	public void setListaParametros(DataModel<Parameter> listaParametros)
	{
		this.listaParametros = listaParametros;
	}

	public String getMensajeError()
	{
		return mensajeError;
	}

	public void setMensajeError(String mensajeError)
	{
		this.mensajeError = mensajeError;
	}

}
