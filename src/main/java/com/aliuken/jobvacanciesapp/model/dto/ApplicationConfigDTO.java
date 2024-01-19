package com.aliuken.jobvacanciesapp.model.dto;

import java.io.Serializable;

import com.aliuken.jobvacanciesapp.util.javase.StringUtils;

import jakarta.validation.constraints.NotEmpty;

public record ApplicationConfigDTO(
	@NotEmpty(message="{nextDefaultLanguageCode.notEmpty}")
	String nextDefaultLanguageCode,

	@NotEmpty(message="{nextAnonymousAccessPermissionName.notEmpty}")
	String nextAnonymousAccessPermissionName,

	@NotEmpty(message="{nextDefaultInitialTablePageSizeValue.notEmpty}")
	String nextDefaultInitialTablePageSizeValue,

	@NotEmpty(message="{nextDefaultColorModeCode.notEmpty}")
	String nextDefaultColorModeCode,

	@NotEmpty(message="{nextUserInterfaceFrameworkCode.notEmpty}")
	String nextUserInterfaceFrameworkCode
) implements Serializable {

	private static final ApplicationConfigDTO NO_ARGS_INSTANCE = new ApplicationConfigDTO(null, null, null, null, null);

	public ApplicationConfigDTO {

	}

	public static ApplicationConfigDTO getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	@Override
	public String toString() {
		final String result = StringUtils.getStringJoined("ApplicationConfigDTO [nextDefaultLanguageCode=", nextDefaultLanguageCode, ", nextAnonymousAccessPermissionName=", nextAnonymousAccessPermissionName, ", nextDefaultInitialTablePageSizeValue=", nextDefaultInitialTablePageSizeValue, ", nextDefaultColorModeCode=", nextDefaultColorModeCode, ", nextUserInterfaceFrameworkCode=", nextUserInterfaceFrameworkCode, "]");
		return result;
	}
}
