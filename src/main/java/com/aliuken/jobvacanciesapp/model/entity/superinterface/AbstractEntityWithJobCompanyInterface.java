package com.aliuken.jobvacanciesapp.model.entity.superinterface;

import java.io.Serializable;

import com.aliuken.jobvacanciesapp.model.entity.JobCompany;

public interface AbstractEntityWithJobCompanyInterface extends Serializable {
	public abstract JobCompany getJobCompany();
	public abstract void setJobCompany(JobCompany jobCompany);

	public default String getJobCompanyId() {
		final JobCompany jobCompany = getJobCompany();
		final String jobCompanyId = (jobCompany != null) ? jobCompany.getIdString() : null;
		return jobCompanyId;
	}

	public default String getJobCompanyName() {
		final JobCompany jobCompany = getJobCompany();
		final String jobCompanyName = (jobCompany != null) ? jobCompany.getName() : null;
		return jobCompanyName;
	}
}
