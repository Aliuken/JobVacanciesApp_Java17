package com.aliuken.jobvacanciesapp.model.dto;

import java.io.Serializable;

import com.aliuken.jobvacanciesapp.model.entity.enumtype.Language;
import com.aliuken.jobvacanciesapp.util.javase.LogicalUtils;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TableSearchDTO(
	@NotEmpty(message="{language.notEmpty}")
	@Size(min=2, max=2, message="{language.minAndMaxSize}")
	String languageParam,

	@NotNull(message="{tableFieldCode.notEmpty}")
	String tableFieldCode,

	@NotNull(message="{tableFieldValue.notEmpty}")
	String tableFieldValue,

	@NotEmpty(message="{tableOrderCode.notEmpty}")
	String tableOrderCode,

	@NotEmpty(message="{pageSize.notEmpty}")
	String pageSize,

	@NotEmpty(message="{pageNumber.notEmpty}")
	String pageNumber
) implements Serializable {

	private static final TableSearchDTO NO_ARGS_INSTANCE = new TableSearchDTO(null, null, null, null, null, null);

	public TableSearchDTO {
		if(languageParam == null) {
			languageParam = Language.ENGLISH.getCode();
		}
		if(pageNumber == null) {
			pageNumber = String.valueOf(0);
		}
	}

	public static TableSearchDTO getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	public boolean hasEmptyAttribute() {
		final boolean hasEmptyAttribute = (LogicalUtils.isNullOrEmpty(languageParam) || tableFieldCode == null || tableFieldValue == null || LogicalUtils.isNullOrEmpty(tableOrderCode) || LogicalUtils.isNullOrEmpty(pageSize) || LogicalUtils.isNullOrEmpty(pageNumber));
		return hasEmptyAttribute;
	}

	@Override
	public String toString() {
		final String result = StringUtils.getStringJoined("TableSearchDTO [languageParam=", languageParam, ", tableFieldCode=", tableFieldCode, ", tableFieldValue=", tableFieldValue, ", tableOrderCode=", tableOrderCode, ", pageSize=", pageSize, ", pageNumber=", pageNumber, "]");
		return result;
	}

}
