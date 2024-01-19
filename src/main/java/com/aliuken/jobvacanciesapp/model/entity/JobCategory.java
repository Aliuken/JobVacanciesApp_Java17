package com.aliuken.jobvacanciesapp.model.entity;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.aliuken.jobvacanciesapp.Constants;
import com.aliuken.jobvacanciesapp.annotation.LazyEntityRelationGetter;
import com.aliuken.jobvacanciesapp.model.entity.superclass.AbstractEntity;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name="job_category", indexes={
		@Index(name="job_category_unique_key_1", columnList="name", unique=true),
		@Index(name="job_category_key_1", columnList="first_registration_auth_user_id"),
		@Index(name="job_category_key_2", columnList="last_modification_auth_user_id")})
@Data
public class JobCategory extends AbstractEntity {

	private static final long serialVersionUID = -1716013269189038906L;

	@NotNull
	@Size(max=35)
	@Column(name="name", length=35, nullable=false)
	private String name;

	@NotNull
	@Size(max=500)
	@Column(name="description", length=500, nullable=false)
	private String description;

	@OneToMany(mappedBy="jobCategory", fetch=FetchType.LAZY)
	@Fetch(value = FetchMode.SUBSELECT)
	@OrderBy("id DESC")
	private Set<JobVacancy> jobVacancies;

	public JobCategory() {
		super();
	}

	@LazyEntityRelationGetter
	public Set<JobVacancy> getJobVacancies() {
		return jobVacancies;
	}

	@LazyEntityRelationGetter
	public Set<Long> getJobVacancyIds() {
		final Set<Long> jobVacancyIds = Constants.PARALLEL_STREAM_UTILS.ofNullableCollection(jobVacancies)
				.map(jv -> jv.getId())
				.collect(Collectors.toCollection(LinkedHashSet::new));

		return jobVacancyIds;
	}

	@LazyEntityRelationGetter
	public Set<String> getJobVacancyNames() {
		final Set<String> jobVacancyNames = Constants.PARALLEL_STREAM_UTILS.ofNullableCollection(jobVacancies)
				.map(jv -> jv.getName())
				.collect(Collectors.toCollection(LinkedHashSet::new));

		return jobVacancyNames;
	}

	@Override
	public String toString() {
		final String idString = Objects.toString(this.getId());
		final String firstRegistrationDateTimeString = Constants.DATE_TIME_UTILS.convertToString(this.getFirstRegistrationDateTime());
		final String firstRegistrationAuthUserEmail = this.getFirstRegistrationAuthUserEmail();
		final String lastModificationDateTimeString = Constants.DATE_TIME_UTILS.convertToString(this.getLastModificationDateTime());
		final String lastModificationAuthUserEmail = this.getLastModificationAuthUserEmail();
		final String jobVacancyNames = this.getJobVacancyNames().toString();

		final String result = StringUtils.getStringJoined("JobCategory [id=", idString, ", name=", name, ", description=", description,
			", firstRegistrationDateTime=", firstRegistrationDateTimeString, ", firstRegistrationAuthUser=", firstRegistrationAuthUserEmail, ", lastModificationDateTime=", lastModificationDateTimeString, ", lastModificationAuthUser=", lastModificationAuthUserEmail,
			", jobVacancies=", jobVacancyNames, "]");

		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(name, description);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(!super.equals(obj)) {
			return false;
		}
		JobCategory other = (JobCategory) obj;
		return Objects.equals(name, other.name) && Objects.equals(description, other.description);
	}

}
