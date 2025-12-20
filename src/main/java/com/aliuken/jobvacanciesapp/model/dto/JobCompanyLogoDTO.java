package com.aliuken.jobvacanciesapp.model.dto;

import com.aliuken.jobvacanciesapp.model.dto.superinterface.AbstractEntityDTO;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Objects;

public record JobCompanyLogoDTO(
	@NotNull(message="{id.notNull}")
	Long id,

	@NotEmpty(message="{fileName.notEmpty}")
	@Size(max=255, message="{fileName.maxSize}")
	String fileName,

	@NotEmpty(message="{filePath.notEmpty}")
	String filePath,

	@NotEmpty(message="{selectionName.notEmpty}")
	String selectionName
) implements AbstractEntityDTO, Serializable {

	private static final JobCompanyLogoDTO NO_ARGS_INSTANCE = new JobCompanyLogoDTO(null, null, null, null);

	public JobCompanyLogoDTO {

	}

	public static JobCompanyLogoDTO getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	@Override
	public String toString() {
		final String idString = Objects.toString(id);

		final String result = StringUtils.getStringJoined("JobCompanyLogoDTO [id=", idString, ", fileName=", fileName, ", filePath=", filePath, ", selectionName=", selectionName, "]");
		return result;
	}
}
