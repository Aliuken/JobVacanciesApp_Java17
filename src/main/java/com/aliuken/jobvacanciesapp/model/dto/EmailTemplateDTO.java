package com.aliuken.jobvacanciesapp.model.dto;

import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;

public record EmailTemplateDTO(
	@NotEmpty(message="{originEmailAddress.notEmpty}")
	String originEmailAddress,

	@NotEmpty(message="{textTemplate.notEmpty}")
	String textTemplate
) implements Serializable {

	private static final EmailTemplateDTO NO_ARGS_INSTANCE = new EmailTemplateDTO(null, null);

	public EmailTemplateDTO {

	}

	public static EmailTemplateDTO getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	@Override
	public String toString() {
		final String result = StringUtils.getStringJoined("EmailTemplateDTO [originEmailAddress=", originEmailAddress, ", textTemplate=", textTemplate, "]");
		return result;
	}
}
