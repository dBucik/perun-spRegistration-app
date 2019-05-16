package cz.metacentrum.perun.spRegistration.rest.controllers.common;

import cz.metacentrum.perun.spRegistration.persistence.exceptions.ConnectorException;
import cz.metacentrum.perun.spRegistration.service.exceptions.CannotChangeStatusException;
import cz.metacentrum.perun.spRegistration.service.exceptions.ExpiredCodeException;
import cz.metacentrum.perun.spRegistration.service.exceptions.InternalErrorException;
import cz.metacentrum.perun.spRegistration.service.exceptions.MalformedCodeException;
import cz.metacentrum.perun.spRegistration.service.exceptions.UnauthorizedActionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;

/**
 * Class that handles all exceptions thrown from REST API.
 *
 * @author Vojtech Sassmann <vojtech.sassmann@gmail.com>
 */
@ControllerAdvice
public class GlobalExceptionController {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionController.class);

	@ExceptionHandler(value = UnauthorizedActionException.class)
	public ResponseEntity<Object> unauthorizedException(UnauthorizedActionException exception) {
		log.error(exception.toString());
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(value = InternalErrorException.class)
	public ResponseEntity<Object> internalErrorException(InternalErrorException exception) {
		log.error(exception.toString());
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = ConnectorException.class)
	public ResponseEntity<Object> connectorException(ConnectorException exception) {
		log.error(exception.toString());
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = UnsupportedEncodingException.class)
	public ResponseEntity<Object> unsupportedEncodingException(UnsupportedEncodingException exception) {
		log.error(exception.toString());
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = InvalidKeyException.class)
	public ResponseEntity<Object> invalidKeyException(InvalidKeyException exception) {
		log.error(exception.toString());
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = IllegalBlockSizeException.class)
	public ResponseEntity<Object> illegalBlockSizeException(IllegalBlockSizeException exception) {
		log.error(exception.toString());
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = BadPaddingException.class)
	public ResponseEntity<Object> badPaddingException(BadPaddingException exception) {
		log.error(exception.toString());
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = ExpiredCodeException.class)
	public ResponseEntity<Object> expiredCodeException(ExpiredCodeException exception) {
		log.error(exception.toString());
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(value = MalformedCodeException.class)
	public ResponseEntity<Object> malformedCodeException(MalformedCodeException exception) {
		log.error(exception.toString());
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(value = IllegalArgumentException.class)
	public ResponseEntity<Object> illegalArgumentException(IllegalArgumentException exception) {
		log.error(exception.toString());
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = CannotChangeStatusException.class)
	public ResponseEntity<Object> cannotChangeStatusException(CannotChangeStatusException exception) {
		log.error(exception.toString());
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.PRECONDITION_FAILED);
	}
}
