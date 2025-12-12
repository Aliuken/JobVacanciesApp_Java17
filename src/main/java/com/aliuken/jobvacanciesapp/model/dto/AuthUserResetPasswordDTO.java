package com.aliuken.jobvacanciesapp.model.dto;

import com.aliuken.jobvacanciesapp.model.dto.superinterface.AbstractEntityDTO;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Objects;

public record AuthUserResetPasswordDTO(
	Long id,

	@NotEmpty(message="{email.notEmpty}")
	@Size(max=255, message="{email.maxSize}")
	@Email(message="{email.validFormat}")
	String email,

	@NotEmpty(message="{uuid.notEmpty}")
	@Size(min=36, max=36, message="{uuid.minAndMaxSize}")
	String uuid,

	@NotEmpty(message="{newPassword1.notEmpty}")
	@Size(min=7, max=20, message="{newPassword1.minAndMaxSize}")
	String newPassword1,

	@NotEmpty(message="{newPassword2.notEmpty}")
	@Size(min=7, max=20, message="{newPassword2.minAndMaxSize}")
	String newPassword2
) implements AbstractEntityDTO, Serializable {

	private static final AuthUserResetPasswordDTO NO_ARGS_INSTANCE = new AuthUserResetPasswordDTO(null, null, null, null, null);

	public static AuthUserResetPasswordDTO getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	@Override
	public String toString() {
		final String idString = Objects.toString(id);

		final String result = StringUtils.getStringJoined("AuthUserResetPasswordDTO [id=", idString, ", email=", email, "]");
		return result;
	}
}
