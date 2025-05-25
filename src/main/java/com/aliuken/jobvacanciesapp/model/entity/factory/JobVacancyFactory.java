package com.aliuken.jobvacanciesapp.model.entity.factory;

import com.aliuken.jobvacanciesapp.model.entity.AuthUser;
import com.aliuken.jobvacanciesapp.model.entity.JobCompany;
import com.aliuken.jobvacanciesapp.model.entity.JobVacancy;
import com.aliuken.jobvacanciesapp.model.entity.factory.superclass.AbstractEntityFactory;

public class JobVacancyFactory extends AbstractEntityFactory<JobVacancy> {
	public JobVacancyFactory() {
		super();
	}

	@Override
	protected JobVacancy createInstance() {
		return new JobVacancy();
	}

	public static JobVacancy createForSearchByExample(final Long id, final AuthUser firstRegistrationAuthUser, final AuthUser lastModificationAuthUser) {
		final JobVacancy jobVacancy = new JobVacancy();
		jobVacancy.setId(id);
		jobVacancy.setFirstRegistrationAuthUser(firstRegistrationAuthUser);
		jobVacancy.setLastModificationAuthUser(lastModificationAuthUser);
		return jobVacancy;
	}

	public static JobVacancy createWithJobCompanyName(final String jobCompanyName) {
		final JobVacancy jobVacancy = new JobVacancy();
		final JobCompany jobCompany = new JobCompany();
		jobCompany.setName(jobCompanyName);
		jobVacancy.setJobCompany(jobCompany);
		return jobVacancy;
	}
}
