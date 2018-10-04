package cz.metacentrum.perun.spRegistration.persistence.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.metacentrum.perun.spRegistration.persistence.models.attributes.Attribute;

import java.util.HashMap;
import java.util.Map;

/**
 * Representation of Perun Facility.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public class Facility extends PerunEntity {

	private String name;
	private String description;
	@JsonProperty private final String beanName = "Facility";
	@JsonIgnore private Map<String, Attribute> attrs = new HashMap<>();

	public Facility(Long id) {
		super(id);
	}

	public Facility(Long id, String name, String description) {
		super(id);
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, Attribute> getAttrs() {
		return attrs;
	}

	public void setAttrs(Map<String, Attribute> attrs) {
		this.attrs = attrs;
	}

	/**
	 * Convert object to JSON representation
	 * @return JSON String
	 */
	public String toJsonString() {
		ObjectMapper mapper = new ObjectMapper();
		String json = null;
		try {
			json = mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "{ \"facility\" : " + json + " }";
	}
}
