package com.aliuken.jobvacanciesapp.model.entity.factory;

import com.aliuken.jobvacanciesapp.model.entity.AuthUser;
import com.aliuken.jobvacanciesapp.model.entity.JobCompany;
import com.aliuken.jobvacanciesapp.model.entity.JobRequest;
import com.aliuken.jobvacanciesapp.model.entity.JobVacancy;
import com.aliuken.jobvacanciesapp.model.entity.factory.superclass.AbstractEntityFactory;

public class JobRequestFactory extends AbstractEntityFactory<JobRequest> {
	public JobRequestFactory() {
		super();
	}

	@Override
	protected JobRequest createInstance() {
		return new JobRequest();
	}

	public static JobRequest createForSearchByExample(final Long id, final AuthUser firstRegistrationAuthUser, final AuthUser lastModificationAuthUser) {
		final JobRequest jobRequest = new JobRequest();
		jobRequest.setId(id);
		jobRequest.setFirstRegistrationAuthUser(firstRegistrationAuthUser);
		jobRequest.setLastModificationAuthUser(lastModificationAuthUser);
		return jobRequest;
	}

	public static JobRequest createWithAuthUserEmail(final String authUserEmail) {
		final JobRequest jobRequest = new JobRequest();
		final AuthUser authUser = new AuthUser();
		authUser.setEmail(authUserEmail);
		jobRequest.setAuthUser(authUser);
		return jobRequest;
	}

	public static JobRequest createWithAuthUserName(final String authUserName) {
		final JobRequest jobRequest = new JobRequest();
		final AuthUser authUser = new AuthUser();
		authUser.setName(authUserName);
		jobRequest.setAuthUser(authUser);
		return jobRequest;
	}

	public static JobRequest createWithAuthUserSurnames(final String authUserSurnames) {
		final JobRequest jobRequest = new JobRequest();
		final AuthUser authUser = new AuthUser();
		authUser.setSurnames(authUserSurnames);
		jobRequest.setAuthUser(authUser);
		return jobRequest;
	}

	public static JobRequest createWithJobCompanyName(final String jobCompanyName) {
		final JobRequest jobRequest = new JobRequest();
		final JobVacancy jobVacancy = new JobVacancy();
		final JobCompany jobCompany = new JobCompany();
		jobCompany.setName(jobCompanyName);
		jobVacancy.setJobCompany(jobCompany);
		jobRequest.setJobVacancy(jobVacancy);
		return jobRequest;
	}
}
