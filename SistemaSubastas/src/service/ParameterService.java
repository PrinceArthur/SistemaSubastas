package service;

import java.util.List;

import dao.ParameterDAO;
import dao.ParameterDAOImpl;
import entity.Parameter;

/**
 * 
 * @author Guillermo Marcano, Richard Mora y Estefan�a P�rez
 * Service DAO
 */
public class ParameterService
{
	private ParameterDAO par = new ParameterDAOImpl();
	
	/**
	 * M�todo para crear un par�metro
	 * @param parameter
	 */
	public void nuevo(Parameter parameter)
	{
		par.nuevo(parameter);
	}
	
	/**
	 * M�todo que devuelve un par�metro espec�fico
	 * @param parameterCode
	 * @return parametro
	 */
	public Parameter getParameter(String parameterCode)
	{
		return par.getParameter(parameterCode);
	}
	
	/**
	 * M�todo para modificar un par�metro
	 * @param parameter
	 */
	public void actualizar(Parameter parameter)
	{
		par.actualizar(parameter);
	}
	
	/**
	 * M�todo para eliminar un par�metro
	 * @param parameter
	 */
	public void eliminar(Parameter parameter)
	{
		par.eliminar(parameter);
	}
	
	/**
	 * M�todo que devuelve la lista de los par�metros
	 * @return lista
	 */
	public List<Parameter> lista()
	{
		return par.lista();
	}
}
