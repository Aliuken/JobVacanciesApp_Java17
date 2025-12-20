package com.aliuken.jobvacanciesapp.model.dto;

import com.aliuken.jobvacanciesapp.model.entity.enumtype.Language;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;

public record BigDecimalFromStringConversionResult(
	Function<Language, String> conversionErrorFunction,
	BigDecimal conversionResult
) implements Serializable {

	private static final BigDecimalFromStringConversionResult NO_ARGS_INSTANCE = new BigDecimalFromStringConversionResult(null, null);

	public BigDecimalFromStringConversionResult {
	}

	public static BigDecimalFromStringConversionResult getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	public static BigDecimalFromStringConversionResult getNewInstanceWithError(Function<Language, String> conversionErrorFunction) {
		return new BigDecimalFromStringConversionResult(conversionErrorFunction, null);
	}

	public static BigDecimalFromStringConversionResult getNewInstanceWithoutError(BigDecimal conversionResult) {
		return new BigDecimalFromStringConversionResult(null, conversionResult);
	}

	@Override
	public String toString() {
		final String conversionErrorFunctionString = Objects.toString(conversionErrorFunction);
		final String conversionResultString = Objects.toString(conversionResult);

		final String result = StringUtils.getStringJoined("BigDecimalFromStringConversionResult [conversionErrorFunction=", conversionErrorFunctionString, ", conversionResult=", conversionResultString, "]");
		return result;
	}
}
