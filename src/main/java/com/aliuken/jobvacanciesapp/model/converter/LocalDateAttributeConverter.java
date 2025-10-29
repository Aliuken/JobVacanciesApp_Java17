package com.aliuken.jobvacanciesapp.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.sql.Date;
import java.time.LocalDate;

@Converter(autoApply=true)
public class LocalDateAttributeConverter implements AttributeConverter<LocalDate, Date> {

	@Override
	public Date convertToDatabaseColumn(final LocalDate localDate) {
		if(localDate == null) {
			return null;
		}

		final Date sqlDate = Date.valueOf(localDate);
		return sqlDate;
	}

	@Override
	public LocalDate convertToEntityAttribute(final Date sqlDate) {
		if(sqlDate == null) {
			return null;
		}

		final LocalDate localDate = sqlDate.toLocalDate();
		return localDate;
	}
}
