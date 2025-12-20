package com.aliuken.jobvacanciesapp.model.dto;

import com.aliuken.jobvacanciesapp.model.dto.superinterface.AbstractEntityDTO;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Objects;

public record JobRequestDTO(
	Long id,
	AuthUserDTO authUser,

	@NotNull(message="{jobVacancy.notNull}")
	JobVacancyDTO jobVacancy,

	//In jobRequestForm.html we cannot use *{jobVacancy.id} because JobVacancyDTO is a Java record, so we use *{jobVacancyId} instead
	Long jobVacancyId,

	@NotEmpty(message="{comments.notEmpty}")
	@Size(max=1000, message="{comments.maxSize}")
	String comments,

	@NotEmpty(message="{curriculumFileName.notEmpty}")
	@Size(max=255, message="{curriculumFileName.maxSize}")
	String curriculumFileName
) implements AbstractEntityDTO, Serializable {

	private static final JobRequestDTO NO_ARGS_INSTANCE = new JobRequestDTO(null, null, null, null, null, null);

	public JobRequestDTO {
		if(authUser == null) {
			authUser = AuthUserDTO.getNewInstance();
		}
		if(jobVacancy == null) {
			jobVacancy = JobVacancyDTO.getNewInstance(jobVacancyId);
		} else if(jobVacancyId == null) {
			jobVacancyId = jobVacancy.id();
		}
	}

	public static JobRequestDTO getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	public static JobRequestDTO getNewInstance(JobRequestDTO jobRequestDTO, final JobVacancyDTO jobVacancyDTO) {
		if(jobRequestDTO != null) {
			jobRequestDTO = new JobRequestDTO(
				jobRequestDTO.id(),
				jobRequestDTO.authUser(),
				jobVacancyDTO,
				(jobVacancyDTO != null) ? jobVacancyDTO.id() : null,
				jobRequestDTO.comments(),
				jobRequestDTO.curriculumFileName()
			);
		} else {
			jobRequestDTO = new JobRequestDTO(
				null,
				null,
				jobVacancyDTO,
				(jobVacancyDTO != null) ? jobVacancyDTO.id() : null,
				null,
				null
			);
		}
		return jobRequestDTO;
	}

	@Override
	public String toString() {
		final String idString = Objects.toString(id);
		final String authUserEmail = (authUser != null) ? Objects.toString(authUser.email()) : null;
		final String jobVacancyIdString = (jobVacancy != null) ? Objects.toString(jobVacancy.id()) : null;

		final String result = StringUtils.getStringJoined("JobRequestDTO [id=", idString, ", authUserEmail=", authUserEmail, ", jobVacancyId=", jobVacancyIdString, ", comments=", comments, ", curriculumFileName=", curriculumFileName, "]");
		return result;
	}
}
