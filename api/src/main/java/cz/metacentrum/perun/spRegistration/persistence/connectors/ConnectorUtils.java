package cz.metacentrum.perun.spRegistration.persistence.connectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.metacentrum.perun.spRegistration.persistence.exceptions.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;

/**
 * Utils class used by connectors.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public class ConnectorUtils {

	private static final Logger log = LoggerFactory.getLogger(ConnectorUtils.class);

	public static String dealWithHttpClientErrorException(HttpClientErrorException ex, String message) throws ConnectorException {
		log.trace("dealWithHttpClientErrorException(ex: {}, message: {})", ex, message);
		MediaType contentType = null;
		if (ex.getResponseHeaders() != null) {
			contentType = ex.getResponseHeaders().getContentType();
		}
		String body = ex.getResponseBodyAsString();
		if (contentType != null && "json".equals(contentType.getSubtype())) {
			try {
				new ObjectMapper().readValue(body, JsonNode.class).path("message").asText();
			} catch (IOException e) {
				log.error("cannot parse error message from JSON", e);
			}
		} else {
			log.error(ex.getMessage());
		}

		throw new ConnectorException(message + ' ' + ex.getMessage(), ex);
	}
}
