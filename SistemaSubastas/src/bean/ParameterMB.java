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
	
	public void prepararModificarParametro()
	{
		
	}
	
	public void modificarParameter()
	{
		
	}
	
	public void eliminarParameter()
	{
		
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
