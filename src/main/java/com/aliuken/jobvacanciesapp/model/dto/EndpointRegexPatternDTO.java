package com.aliuken.jobvacanciesapp.model.dto;

import com.aliuken.jobvacanciesapp.Constants;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpMethod;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record EndpointRegexPatternDTO(
	@NotNull
	HttpMethod httpMethod,

	@NotNull
	String httpMethodName,

	@NotNull
	Pattern pathRegexPattern,

	@NotNull
	String pathRegex,

	@NotNull
	String description
) implements Serializable {

	private static final EndpointRegexPatternDTO NO_ARGS_INSTANCE = new EndpointRegexPatternDTO(null, null, null, null, null);

	public EndpointRegexPatternDTO {

	}

	public static EndpointRegexPatternDTO getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	public static EndpointRegexPatternDTO getNewInstance(final HttpMethod httpMethod, final String pathRegex, final String description) {
		final String httpMethodName = httpMethod.name();
		final Pattern pathRegexPattern = Pattern.compile(pathRegex);
		final EndpointRegexPatternDTO endpointRegexPatternDTO = new EndpointRegexPatternDTO(httpMethod, httpMethodName, pathRegexPattern, pathRegex, description);
		return endpointRegexPatternDTO;
	}

	public boolean matches(final String httpMethodNameToMatch, final String pathToMatch) {
		final boolean result;
		if(httpMethodName.equals(httpMethodNameToMatch)) {
			final Matcher matcher = pathRegexPattern.matcher(pathToMatch);
			result = matcher.matches();
		} else {
			result = false;
		}
		return result;
	}

	public String getEndpointRegexPatternAsString() {
		final String result = StringUtils.getStringJoined(httpMethodName, Constants.SPACE, pathRegex);
		return result;
	}

	@Override
	public String toString() {
		final String result = StringUtils.getStringJoined("EndpointRegexPatternDTO [httpMethod=", httpMethodName, ", pathRegex=", pathRegex, ", description=", description, "]");
		return result;
	}
}