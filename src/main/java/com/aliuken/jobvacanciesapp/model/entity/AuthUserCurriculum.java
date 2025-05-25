package com.aliuken.jobvacanciesapp.model.entity;

import java.util.Objects;

import com.aliuken.jobvacanciesapp.Constants;
import com.aliuken.jobvacanciesapp.model.entity.superclass.AbstractEntityWithAuthUser;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import com.aliuken.jobvacanciesapp.util.persistence.pdf.util.StyleApplier;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name="auth_user_curriculum", indexes={
		@Index(name="auth_user_curriculum_unique_key_1", columnList="auth_user_id, file_name", unique=true),
		@Index(name="auth_user_curriculum_key_1", columnList="first_registration_auth_user_id"),
		@Index(name="auth_user_curriculum_key_2", columnList="last_modification_auth_user_id"),
		@Index(name="auth_user_curriculum_key_3", columnList="auth_user_id")})
@Data
public class AuthUserCurriculum extends AbstractEntityWithAuthUser {
	private static final long serialVersionUID = -8095503029320669346L;

	@NotNull
	@ManyToOne
	@JoinColumn(name="auth_user_id", nullable=false)
	private AuthUser authUser;

	@NotNull
	@Size(max=255)
	@Column(name="file_name", length=255, nullable=false)
	private String fileName;

	@NotNull
	@Size(max=500)
	@Column(name="description", length=500, nullable=false)
	private String description;

	public AuthUserCurriculum() {
		super();
	}

	public String getFilePath() {
		final AuthUser authUser = this.getAuthUser();

		final String authUserIdString;
		if(authUser != null) {
			Long authUserId = authUser.getId();
			authUserIdString = (authUserId != null) ? authUserId.toString() : "temp";
		} else {
			authUserIdString = "temp";
		}

		final String filePath = StringUtils.getStringJoined(authUserIdString, "/", fileName);
		return filePath;
	}

	public String getSelectionName() {
		final String idString = this.getIdString();
		final String firstRegistrationDateTimeString = this.getFirstRegistrationDateTimeString();
		final String selectionName = StringUtils.getStringJoined("CV ", idString, Constants.SPACE, firstRegistrationDateTimeString);
		return selectionName;
	}

	@Override
	public boolean isPrintableEntity() {
		return true;
	}

	@Override
	public String getKeyFields() {
		final String idString = this.getIdString();
		final String authUserIdString = this.getAuthUserId();

		final String result = StringUtils.getStringJoined(
			StyleApplier.getBoldString("id: "), idString, Constants.NEWLINE,
			StyleApplier.getBoldString("[authUserId, fileName]: "), "[", authUserIdString, ", ", fileName, "]");
		return result;
	}

	@Override
	public String getOtherFields() {
		final String result = StringUtils.getStringJoined(
			StyleApplier.getBoldString("description: "), description);
		return result;
	}

	@Override
	public String toString() {
		final String idString = this.getIdString();
		final String authUserEmail = this.getAuthUserEmail();
		final String firstRegistrationDateTimeString = this.getFirstRegistrationDateTimeString();
		final String firstRegistrationAuthUserEmail = this.getFirstRegistrationAuthUserEmail();
		final String lastModificationDateTimeString = this.getLastModificationDateTimeString();
		final String lastModificationAuthUserEmail = this.getLastModificationAuthUserEmail();

		final String result = StringUtils.getStringJoined("AuthUserCurriculum [id=", idString, ", authUser=", authUserEmail, ", fileName=", fileName,
			", firstRegistrationDateTime=", firstRegistrationDateTimeString, ", firstRegistrationAuthUser=", firstRegistrationAuthUserEmail, ", lastModificationDateTime=", lastModificationDateTimeString, ", lastModificationAuthUser=", lastModificationAuthUserEmail, "]");

		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(this.getAuthUser(), fileName);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(!super.equals(obj)) {
			return false;
		}
		AuthUserCurriculum other = (AuthUserCurriculum) obj;
		return Objects.equals(this.getAuthUser(), other.getAuthUser()) && Objects.equals(fileName, other.fileName);
	}
}
