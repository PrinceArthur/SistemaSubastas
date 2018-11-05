package service;

import java.util.List;

import dao.ParameterDAO;
import dao.ParameterDAOImpl;
import entity.Parameter;

public class ParameterService
{
	private ParameterDAO par = new ParameterDAOImpl();
	
	public void nuevo(Parameter parameter)
	{
		par.nuevo(parameter);
	}
	
	public Parameter getParameter(String parameterCode)
	{
		return par.getParameter(parameterCode);
	}
	
	public void actualizar(Parameter parameter)
	{
		par.actualizar(parameter);
	}
	
	public void eliminar(Parameter parameter)
	{
		par.eliminar(parameter);
	}
	
	public List<Parameter> lista()
	{
		return par.lista();
	}
}
