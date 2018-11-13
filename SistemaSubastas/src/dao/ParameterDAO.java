package dao;

import java.util.List;

import entity.Parameter;

/**
 * 
 * @author Guillermo Marcano, Richard Mora y Estefan�a P�rez
 * Interface del DAO de par�metros
 */
public interface ParameterDAO
{
	/**
	 * M�todo para crear un par�metro
	 * @param parameter
	 */
	public void nuevo(Parameter parameter);
	
	/**
	 * M�todo que devuelve un par�metro espec�fico
	 * @param parameterCode
	 * @return parametro
	 */
	public Parameter getParameter(String parameterCode);
	
	/**
	 * M�todo para modificar un par�metro
	 * @param parameter
	 */
	public void actualizar(Parameter parameter);
	
	/**
	 * M�todo para eliminar un par�metro
	 * @param parameter
	 */
	public void eliminar(Parameter parameter);
	
	/**
	 * M�todo que devuelve la lista de los par�metros
	 * @return lista
	 */
	public List<Parameter> lista();
}
