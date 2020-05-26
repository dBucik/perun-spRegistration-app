package cz.metacentrum.perun.spRegistration.persistence.connectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.metacentrum.perun.spRegistration.common.exceptions.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;

/**
 * PersistenceUtils class used by connectors.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>;
 */
public class ConnectorUtils {

	private static final Logger log = LoggerFactory.getLogger(ConnectorUtils.class);

	public static JsonNode dealWithHttpClientErrorException(HttpClientErrorException ex, String message) throws ConnectorException {
		log.trace("dealWithHttpClientErrorException(ex: {}, message: {})", ex, message);

		MediaType contentType = null;
		if (ex.getResponseHeaders() != null) {
			contentType = ex.getResponseHeaders().getContentType();
		}
		if (contentType != null && "json".equalsIgnoreCase(contentType.getSubtype())) {
			try {
				String body = ex.getResponseBodyAsString();
				JsonNode error = new ObjectMapper().readValue(body, JsonNode.class);
				String exErrorId = error.path("errorId").textValue();
				String exName = error.path("name").textValue();
				String exMessage = error.path("message").textValue();

				String errMessage = "Error from Perun: { id: " + exErrorId + ", name: " + exName + ", message: " + exMessage + " }";
				throw new ConnectorException(errMessage, ex);
			} catch (IOException e) {
				throw new ConnectorException("Perun RPC Exception thrown, cannot read message");
			}
		} else {
			throw new ConnectorException("Perun RPC Exception thrown, cannot read message");
		}
	}
}
