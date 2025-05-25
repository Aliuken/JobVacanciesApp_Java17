package com.aliuken.jobvacanciesapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aliuken.jobvacanciesapp.annotation.ServiceMethod;
import com.aliuken.jobvacanciesapp.model.entity.AuthUser;
import com.aliuken.jobvacanciesapp.model.entity.JobCompany;
import com.aliuken.jobvacanciesapp.model.entity.JobRequest;
import com.aliuken.jobvacanciesapp.model.entity.JobVacancy;
import com.aliuken.jobvacanciesapp.model.entity.factory.JobRequestFactory;
import com.aliuken.jobvacanciesapp.repository.JobRequestRepository;
import com.aliuken.jobvacanciesapp.repository.superinterface.UpgradedJpaRepository;

@Service
@Transactional
public class JobRequestServiceImpl extends JobRequestService {

	@Autowired
	private JobRequestRepository jobRequestRepository;

	@Override
	public UpgradedJpaRepository<JobRequest> getEntityRepository() {
		return jobRequestRepository;
	}

	@Override
	@ServiceMethod
	public JobRequest findByAuthUserAndJobVacancy(final AuthUser authUser, final JobVacancy jobVacancy) {
		final JobRequest jobRequest = jobRequestRepository.findByAuthUserAndJobVacancy(authUser, jobVacancy);
		return jobRequest;
	}

	@Override
	@ServiceMethod
	public List<JobRequest> findByAuthUserAndCurriculumFileName(final AuthUser authUser, final String curriculumFileName) {
		final List<JobRequest> jobRequests = jobRequestRepository.findByAuthUserAndCurriculumFileName(authUser, curriculumFileName);
		return jobRequests;
	}

	@Override
	public JobRequest getNewEntityForSearchByExample(final Long id, final AuthUser firstRegistrationAuthUser, final AuthUser lastModificationAuthUser) {
		final JobRequest jobRequest = JobRequestFactory.createForSearchByExample(id, firstRegistrationAuthUser, lastModificationAuthUser);
		return jobRequest;
	}

	@Override
	public JobRequest getNewEntityWithAuthUserEmail(String authUserEmail) {
		final JobRequest jobRequest = JobRequestFactory.createWithAuthUserEmail(authUserEmail);
		return jobRequest;
	}

	@Override
	public JobRequest getNewEntityWithAuthUserName(String authUserName) {
		final JobRequest jobRequest = JobRequestFactory.createWithAuthUserName(authUserName);
		return jobRequest;
	}

	@Override
	public JobRequest getNewEntityWithAuthUserSurnames(String authUserSurnames) {
		final JobRequest jobRequest = JobRequestFactory.createWithAuthUserSurnames(authUserSurnames);
		return jobRequest;
	}

	@Override
	public JobRequest getNewEntityWithJobCompanyName(String jobCompanyName) {
		final JobRequest jobRequest = JobRequestFactory.createWithJobCompanyName(jobCompanyName);
		return jobRequest;
	}
}
