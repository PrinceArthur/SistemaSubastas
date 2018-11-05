package dao;

import java.util.List;

import entity.Parameter;


public interface ParameterDAO
{
	public void nuevo(Parameter parameter);
	public Parameter getParameter(String parameterCode);
	public void actualizar(Parameter parameter);
	public void eliminar(Parameter parameter);
	public List<Parameter> lista();
}
