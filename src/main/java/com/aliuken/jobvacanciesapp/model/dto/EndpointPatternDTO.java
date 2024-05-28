package com.aliuken.jobvacanciesapp.model.dto;

import java.io.Serializable;
import java.util.regex.Pattern;

import org.springframework.http.HttpMethod;

import com.aliuken.jobvacanciesapp.util.javase.StringUtils;

import jakarta.validation.constraints.NotNull;

public record EndpointPatternDTO(
	@NotNull
	String httpMethod,

	@NotNull
	Pattern pathPattern
) implements Serializable {

	private static final EndpointPatternDTO NO_ARGS_INSTANCE = new EndpointPatternDTO(null, null);

	public EndpointPatternDTO {

	}

	public static EndpointPatternDTO getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	public static EndpointPatternDTO getNewInstance(final HttpMethod httpMethod, final Pattern pathPattern) {
		final EndpointPatternDTO endpointPatternDTO = new EndpointPatternDTO(httpMethod.name(), pathPattern);
		return endpointPatternDTO;
	}

	public String getEndpointPatternAsString() {
		final String result = StringUtils.getStringJoined(httpMethod, " ", pathPattern.toString());
		return result;
	}

	@Override
	public String toString() {
		final String result = StringUtils.getStringJoined("EndpointPatternDTO [httpMethod=", httpMethod, ", pathPattern=", pathPattern.toString(), "]");
		return result;
	}
}