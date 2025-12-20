package com.aliuken.jobvacanciesapp.model.dto;

import com.aliuken.jobvacanciesapp.model.dto.superinterface.AbstractEntityDTO;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.Objects;

public record AuthUserCurriculumDTO(
	Long id,
	AuthUserDTO authUser,

	@NotNull(message="{curriculumFile.notNull}")
	MultipartFile curriculumFile,

	String fileName,

	@NotEmpty(message="{description.notEmpty}")
	@Size(max=500, message="{description.maxSize}")
	String description
) implements AbstractEntityDTO, Serializable {

	private static final AuthUserCurriculumDTO NO_ARGS_INSTANCE = new AuthUserCurriculumDTO(null, null, null, null, null);

	public AuthUserCurriculumDTO {
		if(authUser == null) {
			authUser = AuthUserDTO.getNewInstance();
		}
	}

	public static AuthUserCurriculumDTO getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	@Override
	public String toString() {
		final String idString = Objects.toString(id);
		final String authUserEmail = (authUser != null) ? Objects.toString(authUser.email()) : null;

		final String result = StringUtils.getStringJoined("AuthUserCurriculumDTO [id=", idString, ", authUserEmail=", authUserEmail, ", fileName=", fileName, ", description=", description, "]");
		return result;
	}
}
