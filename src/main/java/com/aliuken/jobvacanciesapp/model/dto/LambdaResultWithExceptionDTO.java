package com.aliuken.jobvacanciesapp.model.dto;

import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import com.aliuken.jobvacanciesapp.util.javase.ThrowableUtils;

import java.io.Serializable;
import java.util.Objects;

public record LambdaResultWithExceptionDTO<T>(
	T returnedValue,
	Throwable throwable
) implements Serializable {

	private static final LambdaResultWithExceptionDTO<?> NO_ARGS_INSTANCE = new LambdaResultWithExceptionDTO<>(null, null);

	public LambdaResultWithExceptionDTO {

	}

	public static LambdaResultWithExceptionDTO<?> getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	public boolean hasException() {
		final boolean result = (throwable != null);
		return result;
	}

	@Override
	public String toString() {
		final String returnedValueString = Objects.toString(returnedValue);
		final String rootCauseMessage = ThrowableUtils.getRootCauseMessage(throwable);

		final String result = StringUtils.getStringJoined("LambdaResultWithExceptionDTO [returnedValue=", returnedValueString, ", throwable=", rootCauseMessage, "]");
		return result;
	}
}
