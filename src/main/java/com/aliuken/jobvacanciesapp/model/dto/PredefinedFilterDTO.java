package com.aliuken.jobvacanciesapp.model.dto;

import com.aliuken.jobvacanciesapp.model.entity.enumtype.PredefinedFilterEntity;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;

import java.io.Serializable;
import java.util.Objects;

public record PredefinedFilterDTO(
	String predefinedFilterName,
	String predefinedFilterValue
) implements Serializable {

	private static final PredefinedFilterDTO NO_ARGS_INSTANCE = new PredefinedFilterDTO(null, null);

	public PredefinedFilterDTO {

	}

	public static PredefinedFilterDTO getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	public PredefinedFilterEntity getPredefinedFilterEntity() {
		final PredefinedFilterEntity predefinedFilterEntity = PredefinedFilterEntity.findByEntityName(predefinedFilterName);
		return predefinedFilterEntity;
	}

	@Override
	public String toString() {
		final PredefinedFilterEntity predefinedFilterEntity = this.getPredefinedFilterEntity();
		final String predefinedFilterEntityName = Objects.toString(predefinedFilterEntity);

		final String result = StringUtils.getStringJoined("PredefinedFilterDTO [",
			", predefinedFilterName=", predefinedFilterName, ", predefinedFilterEntityName=", predefinedFilterEntityName, ", predefinedFilterValue=", predefinedFilterValue,
			"]");
		return result;
	}
}
