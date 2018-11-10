package bean;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;


import entity.Parameter;
import service.ParameterService;

@ManagedBean
@SessionScoped
public class ParameterMB
{
	private Parameter parameter;
	private DataModel<Parameter> listaParametros;
	
	public String prepararModificarParametro()
	{
		parameter = (Parameter)(listaParametros.getRowData());
		return "/administrador/modificarParametro";
	}
	
	public String modificarParameter()
	{
		ParameterService servicio = new ParameterService();
		servicio.actualizar(parameter);
		audit.adicionarAudit("Admin", "UPDATE", "Parameter", parameter.getId());
		return "/administrador/indexParameter";
	}
	
	public void eliminarParameter()
	{
		Parameter parametroTemp = (Parameter) (listaParametros.getRowData());
		ParameterService servicio = new ParameterService();
		
		if(parametroTemp.getState().equalsIgnoreCase("ACTIVE"))
		{
			parametroTemp.setState("INACTIVE");
			audit.adicionarAudit("Admin", "DELETE", "Parameter", parametroTemp.getId());
		}
		else if(parametroTemp.getState().equalsIgnoreCase("INACTIVE"))
		{
			parametroTemp.setState("ACTIVE");
		}
	}
	
	public Parameter getParameter()
	{
		return parameter;
	}
	public void setParameter(Parameter parameter)
	{
		this.parameter = parameter;
	}
	public DataModel<Parameter> getListaParametros()
	{
		List<Parameter> lista = new ParameterService().lista();
		listaParametros = new ListDataModel(lista);
		return listaParametros;
	}
	public void setListaParametros(DataModel<Parameter> listaParametros)
	{
		this.listaParametros = listaParametros;
	}
	
	
}
