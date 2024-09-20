package com.aliuken.jobvacanciesapp.model.dto;

import java.io.Serializable;
import java.util.Objects;

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

	String predefinedFilterName,

	String predefinedFilterValue,

	@NotNull(message="{tableFieldCode.notEmpty}")
	String tableFieldCode,

	@NotNull(message="{tableFieldValue.notEmpty}")
	String tableFieldValue,

	@NotEmpty(message="{tableOrderCode.notEmpty}")
	String tableOrderCode,

	@NotNull(message="{pageSize.notEmpty}")
	Integer pageSize,

	@NotNull(message="{pageNumber.notEmpty}")
	Integer pageNumber
) implements Serializable {

	private static final TableSearchDTO NO_ARGS_INSTANCE = new TableSearchDTO(null, null, null, null, null, null, null, null);

	public TableSearchDTO {
		if(languageParam == null) {
			languageParam = Language.ENGLISH.getCode();
		}
		if(pageNumber == null) {
			pageNumber = 0;
		}
	}

	public static TableSearchDTO getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	//If not all pagination URL parameters -> empty table (in Java)
	public boolean hasAllParameters() {
		final boolean hasAllParameters = (LogicalUtils.isNotNullNorEmptyString(languageParam) && !Language.BY_DEFAULT.getCode().equals(languageParam) && tableFieldCode != null && tableFieldValue != null && LogicalUtils.isNotNullNorEmptyString(tableOrderCode) && pageSize != null && pageNumber != null);
		return hasAllParameters;
	}

	@Override
	public String toString() {
		final String pageSizeString = Objects.toString(pageSize);
		final String pageNumberString = Objects.toString(pageNumber);
		final String result = StringUtils.getStringJoined("TableSearchDTO [languageParam=", languageParam, ", predefinedFilterName=", predefinedFilterName, ", predefinedFilterValue=", predefinedFilterValue, ", tableFieldCode=", tableFieldCode, ", tableFieldValue=", tableFieldValue, ", tableOrderCode=", tableOrderCode, ", pageSize=", pageSizeString, ", pageNumber=", pageNumberString, "]");
		return result;
	}
}
