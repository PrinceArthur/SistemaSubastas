package service;

import java.util.List;

import dao.ParameterDAO;
import dao.ParameterDAOImpl;
import entity.Parameter;

/**
 * 
 * @author Guillermo Marcano, Richard Mora y Estefanía Pérez
 * Service DAO
 */
public class ParameterService
{
	private ParameterDAO par = new ParameterDAOImpl();
	
	/**
	 * Método para crear un parámetro
	 * @param parameter
	 */
	public void nuevo(Parameter parameter)
	{
		par.nuevo(parameter);
	}
	
	/**
	 * Método que devuelve un parámetro específico
	 * @param parameterCode
	 * @return parametro
	 */
	public Parameter getParameter(String parameterCode)
	{
		return par.getParameter(parameterCode);
	}
	
	/**
	 * Método para modificar un parámetro
	 * @param parameter
	 */
	public void actualizar(Parameter parameter)
	{
		par.actualizar(parameter);
	}
	
	/**
	 * Método para eliminar un parámetro
	 * @param parameter
	 */
	public void eliminar(Parameter parameter)
	{
		par.eliminar(parameter);
	}
	
	/**
	 * Método que devuelve la lista de los parámetros
	 * @return lista
	 */
	public List<Parameter> lista()
	{
		return par.lista();
	}
}
