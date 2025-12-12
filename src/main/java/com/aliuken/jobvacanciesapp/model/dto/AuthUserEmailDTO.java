package com.aliuken.jobvacanciesapp.model.dto;

import com.aliuken.jobvacanciesapp.model.dto.superinterface.AbstractEntityDTO;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Objects;

public record AuthUserEmailDTO(
	Long id,

	@NotEmpty(message="{email.notEmpty}")
	@Size(max=255, message="{email.maxSize}")
	@Email(message="{email.validFormat}")
	String email
) implements AbstractEntityDTO, Serializable {

	private static final AuthUserEmailDTO NO_ARGS_INSTANCE = new AuthUserEmailDTO(null, null);

	public static AuthUserEmailDTO getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	@Override
	public String toString() {
		final String idString = Objects.toString(id);

		final String result = StringUtils.getStringJoined("AuthUserEmailDTO [id=", idString, ", email=", email, "]");
		return result;
	}
}
