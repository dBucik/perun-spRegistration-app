package cz.metacentrum.perun.spRegistration.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiUtils {

	private static final Logger log = LoggerFactory.getLogger(ApiUtils.class);

	public static String normalizeRequestBodyString(String code) {
		if (code.startsWith("\"")) {
			code = code.substring(1, code.length() - 1);
		}

		log.trace("normalizeRequestBodyString() returns: {}", code);
		return code;
	}
}
