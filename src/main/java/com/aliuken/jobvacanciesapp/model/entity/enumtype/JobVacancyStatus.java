package com.aliuken.jobvacanciesapp.model.entity.enumtype;

import java.io.Serializable;

import com.aliuken.jobvacanciesapp.Constants;
import com.aliuken.jobvacanciesapp.superinterface.Internationalizable;
import com.aliuken.jobvacanciesapp.util.javase.LogicalUtils;

import jakarta.validation.constraints.NotNull;

public enum JobVacancyStatus implements Serializable, Internationalizable {
	CREATED ("C", "jobVacancyStatus.created"),
	APPROVED("A", "jobVacancyStatus.approved"),
	DELETED ("D", "jobVacancyStatus.deleted");

	@NotNull
	private final String code;

	@NotNull
	private final String messageName;

	private JobVacancyStatus(final String code, @NotNull final String messageName) {
		this.code = code;
		this.messageName = messageName;
	}

	public String getCode() {
		return code;
	}

	@Override
	public String getMessageName() {
		return messageName;
	}

	public static JobVacancyStatus findByCode(final String code) {
		if(LogicalUtils.isNullOrEmptyString(code)) {
			return null;
		}

		final JobVacancyStatus jobVacancyStatus = Constants.PARALLEL_STREAM_UTILS.ofEnum(JobVacancyStatus.class)
			.filter(value -> value.code.equals(code))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("JobVacancyStatus code does not exist"));

		return jobVacancyStatus;
	}
}
