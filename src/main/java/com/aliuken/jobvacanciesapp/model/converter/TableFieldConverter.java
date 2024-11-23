package com.aliuken.jobvacanciesapp.model.converter;

import com.aliuken.jobvacanciesapp.model.entity.enumtype.TableField;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply=true)
public class TableFieldConverter implements AttributeConverter<TableField, String> {

	@Override
	public String convertToDatabaseColumn(final TableField tableField) {
		if(tableField == null) {
			return null;
		}

		final String tableFieldCode = tableField.getCode();
		return tableFieldCode;
	}

	@Override
	public TableField convertToEntityAttribute(final String tableFieldCode) {
		final TableField tableField = TableField.findByCode(tableFieldCode);
		return tableField;
	}
}