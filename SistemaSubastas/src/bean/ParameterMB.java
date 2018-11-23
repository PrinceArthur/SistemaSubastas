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
 * @author Guillermo Marcano, Richard Mora y Estefan�a P�rez
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
	 * M�todo para preprarar la modificaci�n del parametro
	 * 
	 * @return la p�gina del formulario para la modificaci�n
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
	 * M�otodo para modificar el par�metro
	 * 
	 * @return nos devuelve a la p�gina de la lista de par�metros
	 */
	public String modificarParameter()
	{
		ParameterService servicio = new ParameterService();
		servicio.actualizar(parameter);
		audit.adicionarAudit("Admin", "ACTUALIZAR", "Parameter", parameter.getId());
		return "/administrador/indexParameter";
	}

	/**
	 * M�todo para desactivar un par�metro
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
	 * Da el par�metro
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
	 * Modifica la lista de los par�metros
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
