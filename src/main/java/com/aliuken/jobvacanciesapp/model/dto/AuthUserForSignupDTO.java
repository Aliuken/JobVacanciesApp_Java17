package com.aliuken.jobvacanciesapp.model.dto;

import com.aliuken.jobvacanciesapp.model.dto.superinterface.AbstractEntityDTO;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record AuthUserForSignupDTO(
	@NotEmpty(message="{email.notEmpty}")
	@Size(max=255, message="{email.maxSize}")
	@Email(message="{email.validFormat}")
	String email,

	@NotEmpty(message="{password1.notEmpty}")
	@Size(min=7, max=20, message="{password1.minAndMaxSize}")
	String password1,

	@NotEmpty(message="{password2.notEmpty}")
	@Size(min=7, max=20, message="{password2.minAndMaxSize}")
	String password2,

	@NotEmpty(message="{name.notEmpty}")
	@Size(max=25, message="{name.maxSize25}")
	String name,

	@NotEmpty(message="{surnames.notEmpty}")
	@Size(max=35, message="{surnames.maxSize}")
	String surnames,

	@NotEmpty(message="{language.notEmpty}")
	@Size(min=2, max=2, message="{language.minAndMaxSize}")
	String languageCode
) implements AbstractEntityDTO, Serializable {

	private static final AuthUserForSignupDTO NO_ARGS_INSTANCE = new AuthUserForSignupDTO(null, null, null, null, null, null);

	public AuthUserForSignupDTO {

	}

	public static AuthUserForSignupDTO getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	@Override
	public String toString() {
		final String result = StringUtils.getStringJoined("AuthUserForSignupDTO [email=", email, ", name=", name, ", surnames=", surnames, ", languageCode=", languageCode, "]");
		return result;
	}
}
