package com.aliuken.jobvacanciesapp.model.dto;

import com.aliuken.jobvacanciesapp.Constants;
import com.aliuken.jobvacanciesapp.model.dto.superinterface.AbstractEntityDTO;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public record JobCompanyDTO(
	Long id,

	@NotEmpty(message="{name.notEmpty}")
	@Size(max=35, message="{name.maxSize35}")
	String name,

	@NotEmpty(message="{description.notEmpty}")
	@Size(max=500, message="{description.maxSize}")
	String description,

	@NotNull
	Boolean isSelectedLogo,

	Long selectedLogoId,

	@NotEmpty(message="{selectedLogoFilePath.notEmpty}")
	String selectedLogoFilePath,

	Set<JobCompanyLogoDTO> jobCompanyLogos
) implements AbstractEntityDTO, Serializable {

	private static final JobCompanyDTO NO_ARGS_INSTANCE = new JobCompanyDTO(null, null, null, Boolean.FALSE, null, null, null);

	public JobCompanyDTO {
		if(selectedLogoId == null || Constants.NO_SELECTED_LOGO_ID.equals(selectedLogoId)) {
			selectedLogoFilePath = Constants.NO_SELECTED_LOGO_FILE_PATH;
		}
	}

	public static JobCompanyDTO getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	public static JobCompanyDTO getNewInstance(final Long jobCompanyId) {
		final JobCompanyDTO jobCompanyDTO = new JobCompanyDTO(jobCompanyId, null, null, Boolean.FALSE, null, null, null);
		return jobCompanyDTO;
	}

	public static JobCompanyDTO getNewInstance(JobCompanyDTO jobCompanyDTO, final Boolean isSelectedLogo, final Long selectedLogoId, final String selectedLogoFilePath) {
		if(jobCompanyDTO != null) {
			jobCompanyDTO = new JobCompanyDTO(
				jobCompanyDTO.id(),
				jobCompanyDTO.name(),
				jobCompanyDTO.description(),
				isSelectedLogo,
				selectedLogoId,
				(selectedLogoFilePath != null) ? selectedLogoFilePath : jobCompanyDTO.selectedLogoFilePath(),
				jobCompanyDTO.jobCompanyLogos()
			);
		} else {
			jobCompanyDTO = new JobCompanyDTO(
				null,
				null,
				null,
				isSelectedLogo,
				selectedLogoId,
				selectedLogoFilePath,
				null
			);
		}
		return jobCompanyDTO;
	}

	public List<String> getJobCompanyLogoFilePaths() {
		final List<String> jobCompanyLogoFilePaths = Constants.PARALLEL_STREAM_UTILS.ofNullableCollection(jobCompanyLogos)
				.map(jcl -> jcl.filePath())
				.collect(Collectors.toList());

		return jobCompanyLogoFilePaths;
	}

	@Override
	public String toString() {
		final String idString = Objects.toString(id);
		final String isSelectedLogoString = Objects.toString(isSelectedLogo);
		final String selectedLogoIdString = Objects.toString(selectedLogoId);
		final String jobCompanyLogoFilePaths = this.getJobCompanyLogoFilePaths().toString();

		final String result = StringUtils.getStringJoined("JobCompanyDTO [id=", idString, ", name=", name, ", description=", description, ", isSelectedLogo=", isSelectedLogoString, ", selectedLogoId=", selectedLogoIdString, ", selectedLogoFilePath=", selectedLogoFilePath, ", jobCompanyLogos=", jobCompanyLogoFilePaths, "]");
		return result;
	}
}
