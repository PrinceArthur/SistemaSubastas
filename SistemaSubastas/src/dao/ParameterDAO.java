package dao;

import java.util.List;

import entity.Parameter;

/**
 * 
 * @author Guillermo Marcano, Richard Mora y Estefanía Pérez
 * Interface del DAO de parámetros
 */
public interface ParameterDAO
{
	/**
	 * Método para crear un parámetro
	 * @param parameter
	 */
	public void nuevo(Parameter parameter);
	
	/**
	 * Método que devuelve un parámetro específico
	 * @param parameterCode
	 * @return parametro
	 */
	public Parameter getParameter(String parameterCode);
	
	/**
	 * Método para modificar un parámetro
	 * @param parameter
	 */
	public void actualizar(Parameter parameter);
	
	/**
	 * Método para eliminar un parámetro
	 * @param parameter
	 */
	public void eliminar(Parameter parameter);
	
	/**
	 * Método que devuelve la lista de los parámetros
	 * @return lista
	 */
	public List<Parameter> lista();
}
