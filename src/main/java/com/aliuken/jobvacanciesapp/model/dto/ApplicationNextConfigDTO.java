package com.aliuken.jobvacanciesapp.model.dto;

import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;

public record ApplicationNextConfigDTO(
	@NotEmpty(message="{nextDefaultLanguageCode.notEmpty}")
	String nextDefaultLanguageCode,

	@NotEmpty(message="{nextDefaultAnonymousAccessPermissionValue.notEmpty}")
	String nextDefaultAnonymousAccessPermissionValue,

	@NotEmpty(message="{nextDefaultInitialTableSortingDirectionCode.notEmpty}")
	String nextDefaultInitialTableSortingDirectionCode,

	@NotEmpty(message="{nextDefaultInitialTablePageSizeValue.notEmpty}")
	String nextDefaultInitialTablePageSizeValue,

	@NotEmpty(message="{nextDefaultColorModeCode.notEmpty}")
	String nextDefaultColorModeCode,

	@NotEmpty(message="{nextDefaultUserInterfaceFrameworkCode.notEmpty}")
	String nextDefaultUserInterfaceFrameworkCode,

	@NotEmpty(message="{nextDefaultPdfDocumentPageFormatCode.notEmpty}")
	String nextDefaultPdfDocumentPageFormatCode
) implements Serializable {

	private static final ApplicationNextConfigDTO NO_ARGS_INSTANCE = new ApplicationNextConfigDTO(null, null, null, null, null, null, null);

	public ApplicationNextConfigDTO {

	}

	public static ApplicationNextConfigDTO getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	@Override
	public String toString() {
		final String result = StringUtils.getStringJoined(
			"ApplicationNextConfigDTO [nextDefaultLanguageCode=", nextDefaultLanguageCode,
			", nextDefaultAnonymousAccessPermissionValue=", nextDefaultAnonymousAccessPermissionValue,
			", nextDefaultInitialTableSortingDirectionCode=", nextDefaultInitialTableSortingDirectionCode,
			", nextDefaultInitialTablePageSizeValue=", nextDefaultInitialTablePageSizeValue,
			", nextDefaultColorModeCode=", nextDefaultColorModeCode,
			", nextDefaultUserInterfaceFrameworkCode=", nextDefaultUserInterfaceFrameworkCode,
			", nextDefaultPdfDocumentPageFormatCode=", nextDefaultPdfDocumentPageFormatCode, "]");
		return result;
	}
}
