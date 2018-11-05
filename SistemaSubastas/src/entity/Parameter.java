package entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the parameter database table.
 * 
 */
@Entity
@NamedQuery(name="Parameter.findAll", query="SELECT p FROM Parameter p")
public class Parameter implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	private String descriptionParameter;

	private int numberValue;

	private String parameterCode;

	private String parameterType;

	private String state;

	private String textValue;

	public Parameter() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescriptionParameter() {
		return this.descriptionParameter;
	}

	public void setDescriptionParameter(String descriptionParameter) {
		this.descriptionParameter = descriptionParameter;
	}

	public int getNumberValue() {
		return this.numberValue;
	}

	public void setNumberValue(int numberValue) {
		this.numberValue = numberValue;
	}

	public String getParameterCode() {
		return this.parameterCode;
	}

	public void setParameterCode(String parameterCode) {
		this.parameterCode = parameterCode;
	}

	public String getParameterType() {
		return this.parameterType;
	}

	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTextValue() {
		return this.textValue;
	}

	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}

}