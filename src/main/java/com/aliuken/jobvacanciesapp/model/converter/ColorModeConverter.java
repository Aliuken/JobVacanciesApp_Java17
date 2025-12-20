package com.aliuken.jobvacanciesapp.model.converter;

import com.aliuken.jobvacanciesapp.model.entity.enumtype.ColorMode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply=true)
public class ColorModeConverter implements AttributeConverter<ColorMode, String> {

	@Override
	public String convertToDatabaseColumn(final ColorMode colorMode) {
		if(colorMode == null) {
			return ColorMode.BY_DEFAULT.getCode();
		}

		final String colorModeCode = colorMode.getCode();
		return colorModeCode;
	}

	@Override
	public ColorMode convertToEntityAttribute(final String colorModeCode) {
		final ColorMode colorMode;
		if(colorModeCode != null) {
			colorMode = ColorMode.findByCode(colorModeCode);
		} else {
			colorMode = ColorMode.BY_DEFAULT;
		}

		return colorMode;
	}
}