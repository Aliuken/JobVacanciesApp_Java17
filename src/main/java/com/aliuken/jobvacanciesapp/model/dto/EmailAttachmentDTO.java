package com.aliuken.jobvacanciesapp.model.dto;

import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;

public record EmailAttachmentDTO(
	@NotEmpty(message="{attachmentFileName.notEmpty}")
	String attachmentFileName,

	@NotEmpty(message="{attachmentFilePath.notEmpty}")
	String attachmentFilePath
) implements Serializable {

	private static final EmailAttachmentDTO NO_ARGS_INSTANCE = new EmailAttachmentDTO(null, null);

	public EmailAttachmentDTO {

	}

	public static EmailAttachmentDTO getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	@Override
	public String toString() {
		final String result = StringUtils.getStringJoined("EmailAttachmentDTO [attachmentFileName=", attachmentFileName, ", attachmentFilePath=", attachmentFilePath, "]");
		return result;
	}
}
