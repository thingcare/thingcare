package io.thingcare.api.errors;

import java.util.List;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 */
@ControllerAdvice
public class ExceptionTranslator {

	@ExceptionHandler(ConcurrencyFailureException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	@ResponseBody
	public ErrorDto processConcurencyError(ConcurrencyFailureException ex) {
		return new ErrorDto(ErrorConstants.ERR_CONCURRENCY_FAILURE);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorDto processValidationError(MethodArgumentNotValidException ex) {
		BindingResult result = ex.getBindingResult();
		List<FieldError> fieldErrors = result.getFieldErrors();

		return processFieldErrors(fieldErrors);
	}

	@ExceptionHandler(CustomParameterizedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ParameterizedErrorDto processParameterizedValidationError(CustomParameterizedException ex) {
		return ex.getErrorDTO();
	}

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ResponseBody
	public ErrorDto processAccessDeniedExcpetion(AccessDeniedException e) {
		return new ErrorDto(ErrorConstants.ERR_ACCESS_DENIED, e.getMessage());
	}

	private ErrorDto processFieldErrors(List<FieldError> fieldErrors) {
		ErrorDto dto = new ErrorDto(ErrorConstants.ERR_VALIDATION);

		for (FieldError fieldError : fieldErrors) {
			dto.add(fieldError.getObjectName(), fieldError.getField(), fieldError.getCode());
		}

		return dto;
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public ErrorDto processMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
		return new ErrorDto(ErrorConstants.ERR_METHOD_NOT_SUPPORTED, exception.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDto> processRuntimeException(Exception ex) throws Exception {
		BodyBuilder builder;
		ErrorDto errorDTO;
		ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
		if (responseStatus != null) {
			builder = ResponseEntity.status(responseStatus.value());
			errorDTO = new ErrorDto("error." + responseStatus	.value()
																.value(),
					responseStatus.reason());
		} else {
			builder = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
			errorDTO = new ErrorDto(ErrorConstants.ERR_INTERNAL_SERVER_ERROR, "Internal server error");
		}
		return builder.body(errorDTO);
	}
}
