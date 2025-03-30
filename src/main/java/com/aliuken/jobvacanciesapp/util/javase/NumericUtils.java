package com.aliuken.jobvacanciesapp.util.javase;

import com.aliuken.jobvacanciesapp.model.dto.BigDecimalFromStringConversionResult;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.Language;
import com.aliuken.jobvacanciesapp.util.i18n.I18nUtils;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;

public class NumericUtils {
    private static final Function<Language, String> FIELD_NAME_NULL_ERROR_FUNCTION = (language) -> I18nUtils.getInternationalizedMessage(language, "fieldName.isNull", null);
    private static final Function<Language, String> FIELD_NAME_BLANK_ERROR_FUNCTION = (language) -> I18nUtils.getInternationalizedMessage(language, "fieldName.isBlank", null);

    public static BigDecimalFromStringConversionResult getBigDecimalFromStringConversionResult(final String fieldNameProperty, final String fieldValue, final int integerPartSize, final int fractionalPartSize) {
        if(fieldNameProperty == null) {
            return new BigDecimalFromStringConversionResult(FIELD_NAME_NULL_ERROR_FUNCTION, null);
        }
        final String strippedFieldNameProperty = fieldNameProperty.strip();
        if(strippedFieldNameProperty.isEmpty()) {
            return new BigDecimalFromStringConversionResult(FIELD_NAME_BLANK_ERROR_FUNCTION, null);
        } else if(fieldValue == null) {
            final Function<Language, String> conversionErrorFunction = (language) -> NumericUtils.getInternationalizedMessage(language, "fieldValue.isNull", new Object[]{strippedFieldNameProperty});
            return new BigDecimalFromStringConversionResult(conversionErrorFunction, null);
        } else if(integerPartSize <= 0) {
            final Function<Language, String> conversionErrorFunction = (language) -> NumericUtils.getInternationalizedMessage(language, "decimalFieldValue.integerPartLength.notValid", new Object[]{strippedFieldNameProperty});
            return new BigDecimalFromStringConversionResult(conversionErrorFunction, null);
        } else if(fractionalPartSize < 0) {
            final Function<Language, String> conversionErrorFunction = (language) -> NumericUtils.getInternationalizedMessage(language, "decimalFieldValue.fractionalPartLength.notValid", new Object[]{strippedFieldNameProperty});
            return new BigDecimalFromStringConversionResult(conversionErrorFunction, null);
        }

        final String decimalNumberRegex;
        final String minimumNumberString;
        final String maximumNumberString;
        if(fractionalPartSize == 0) {
            final String integerPartSizeString = Objects.toString(integerPartSize);
            decimalNumberRegex = StringUtils.getStringJoined("^\\d{1,", integerPartSizeString, "}$");
            minimumNumberString = "1";
            maximumNumberString = "9".repeat(integerPartSize);
        } else {
            final String integerPartSizeString = Objects.toString(integerPartSize);
            final String fractionalPartSizeString = Objects.toString(fractionalPartSize);
            decimalNumberRegex = StringUtils.getStringJoined("^\\d{1,", integerPartSizeString, "}\\.\\d{1,", fractionalPartSizeString, "}$");
            minimumNumberString = StringUtils.getStringJoined("0.", "0".repeat(fractionalPartSize - 1), "1");
            maximumNumberString = StringUtils.getStringJoined("9".repeat(integerPartSize), ".", "9".repeat(fractionalPartSize));
        }

        if(!fieldValue.matches(decimalNumberRegex)) {
            final Function<Language, String> conversionErrorFunction = (language) -> NumericUtils.getInternationalizedMessage(language, "decimalFieldValue.notValid", new Object[]{strippedFieldNameProperty, minimumNumberString, maximumNumberString});
            return new BigDecimalFromStringConversionResult(conversionErrorFunction, null);
        } else if(fieldValue.startsWith("00")) {
            final Function<Language, String> conversionErrorFunction = (language) -> NumericUtils.getInternationalizedMessage(language, "decimalFieldValue.leftZerosError", new Object[]{strippedFieldNameProperty});
            return new BigDecimalFromStringConversionResult(conversionErrorFunction, null);
        }

        final BigDecimal decimal = new BigDecimal(fieldValue);
        final BigDecimal minimumNumber = new BigDecimal(minimumNumberString);
        if(decimal.compareTo(minimumNumber) < 0) {
            final Function<Language, String> conversionErrorFunction = (language) -> NumericUtils.getInternationalizedMessage(language, "decimalFieldValue.tooSmall", new Object[]{strippedFieldNameProperty, minimumNumberString});
            return new BigDecimalFromStringConversionResult(conversionErrorFunction, null);
        }

        return new BigDecimalFromStringConversionResult(null, decimal);
    }

    private static String getInternationalizedMessage(final Language language, final String messageName, Object[]  messageParameters) {
        if(LogicalUtils.isNullOrEmpty(messageParameters)) {
            final String result = I18nUtils.getInternationalizedMessage(language, messageName, messageParameters);
            return result;
        }

        final String strippedFieldNameProperty = (String) messageParameters[0];
        final String fieldName = I18nUtils.getInternationalizedMessage(language, strippedFieldNameProperty, null);

        final String finalFieldName;
        if(fieldName == null) {
            finalFieldName = strippedFieldNameProperty;
        } else {
            final String strippedFieldName = fieldName.strip();
            if (strippedFieldName.isEmpty()) {
                finalFieldName = strippedFieldNameProperty;
            } else {
                finalFieldName = strippedFieldName;
            }
        }

        messageParameters[0] = finalFieldName;

        final String result = I18nUtils.getInternationalizedMessage(language, messageName, messageParameters);
        return result;
    }
}
