package com.aliuken.jobvacanciesapp.model.dto;

import com.aliuken.jobvacanciesapp.model.dto.superinterface.AbstractEntityDTO;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

public record JobCategoryDTO(
	Long id,

	@NotEmpty(message="{name.notEmpty}")
	@Size(max=35, message="{name.maxSize35}")
	String name,

	@NotEmpty(message="{description.notEmpty}")
	@Size(max=500, message="{description.maxSize}")
	String description,

	@NotNull(message="{jobVacancyIds.notNull}")
	Set<Long> jobVacancyIds
) implements AbstractEntityDTO, Serializable {

	private static final JobCategoryDTO NO_ARGS_INSTANCE = new JobCategoryDTO(null, null, null, null);

	public JobCategoryDTO {

	}

	public static JobCategoryDTO getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	public static JobCategoryDTO getNewInstance(final Long jobCategoryId) {
		final JobCategoryDTO jobCategoryDTO = new JobCategoryDTO(jobCategoryId, null, null, null);
		return jobCategoryDTO;
	}

	@Override
	public String toString() {
		final String idString = Objects.toString(id);
		final String jobVacancyIdsString = jobVacancyIds.toString();

		final String result = StringUtils.getStringJoined("JobCategoryDTO [id=", idString, ", name=", name, ", description=", description, ", jobVacancyIds=", jobVacancyIdsString, "]");
		return result;
	}
}
