package com.aliuken.jobvacanciesapp.model.dto;

import com.aliuken.jobvacanciesapp.model.entity.enumtype.Language;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

public record EmailDataDTO(
	@NotEmpty(message="{destinationEmailAddress.notEmpty}")
	String destinationEmailAddress,

	@NotEmpty(message="{subject.notEmpty}")
	String subject,

	@NotEmpty(message="{textTitle.notEmpty}")
	String textTitle,

	@NotEmpty(message="{textBody.notEmpty}")
	String textBody,

	Boolean isHtml,

	@NotNull(message="{language.notNull}")
	Language language,

	List<EmailAttachmentDTO> attachments
) implements Serializable {

	private static final EmailDataDTO NO_ARGS_INSTANCE = new EmailDataDTO(null, null, null, null, null, null, null);

	public EmailDataDTO {

	}

	public static EmailDataDTO getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	@Override
	public String toString() {
		final String languageName = language.name();
		final String attachmentsString = attachments.toString();
		final String isHtmlString = String.valueOf(isHtml);

		final String result = StringUtils.getStringJoined("EmailDataDTO [destinationEmailAddress=", destinationEmailAddress, ", subject=", subject,
			", textTitle=", textTitle, ", textBody=", textBody,  ", isHtml=", isHtmlString, ", language=", languageName,  ", attachments=", attachmentsString, "]");
		return result;
	}
}
