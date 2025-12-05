package com.aliuken.jobvacanciesapp.model.dto;

import com.aliuken.jobvacanciesapp.model.dto.superinterface.AbstractEntityDTO;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

public record AuthUserDTO(
	@NotNull(message="{id.notNull}")
	Long id,

	@NotEmpty(message="{email.notEmpty}")
	@Size(max=255, message="{email.maxSize}")
	@Email(message="{email.validFormat}")
	String email,

	@NotEmpty(message="{name.notEmpty}")
	@Size(max=25, message="{name.maxSize25}")
	String name,

	@NotEmpty(message="{surnames.notEmpty}")
	@Size(max=35, message="{surnames.maxSize}")
	String surnames,

	@NotEmpty(message="{language.notEmpty}")
	@Size(min=2, max=2, message="{language.minAndMaxSize}")
	String languageCode,

	@NotNull(message="{enabled.notNull}")
	Boolean enabled,

	@NotEmpty(message="{colorModeCode.notEmpty}")
	String colorModeCode,

	@NotNull(message="{initialCurrencySymbol.notEmpty}")
	String initialCurrencySymbol,

	@NotNull(message="{initialTableSortingDirectionCode.notNull}")
	String initialTableSortingDirectionCode,

	@NotNull(message="{initialTablePageSizeValue.notNull}")
	Integer initialTablePageSizeValue,

	@NotEmpty(message="{pdfDocumentPageFormatCode.notEmpty}")
	String pdfDocumentPageFormatCode,

	@NotEmpty(message="{maxPriorityAuthRoleName.notEmpty}")
	@Size(max=20, message="{maxPriorityAuthRoleName.maxSize}")
	String maxPriorityAuthRoleName,

	@NotEmpty(message="{authRoleNames.notEmpty}")
	Set<String> authRoleNames
) implements AbstractEntityDTO, Serializable {

	private static final AuthUserDTO NO_ARGS_INSTANCE = new AuthUserDTO(null, null, null, null, null, null, null, null, null, null, null, null, null);

	public AuthUserDTO {

	}

	public static AuthUserDTO getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	@Override
	public String toString() {
		final String idString = Objects.toString(id);
		final String enabledString = Objects.toString(enabled);
		final String initialTablePageSizeValueString = Objects.toString(initialTablePageSizeValue);
		final String authRoleNamesString = authRoleNames.toString();

		final String result = StringUtils.getStringJoined("AuthUserDTO [id=", idString, ", email=", email, ", name=", name, ", surnames=", surnames, ", languageCode=", languageCode, ", enabled=", enabledString,
				", initialTableSortingDirectionCode=", initialTableSortingDirectionCode, ", initialTablePageSizeValue=", initialTablePageSizeValueString, ", colorModeCode=", colorModeCode, ", pdfDocumentPageFormatCode=", pdfDocumentPageFormatCode, ", maxPriorityAuthRoleName=", maxPriorityAuthRoleName, ", authRoleNames=", authRoleNamesString, "]");
		return result;
	}
}
