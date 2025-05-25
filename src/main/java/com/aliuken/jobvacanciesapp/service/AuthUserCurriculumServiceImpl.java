package com.aliuken.jobvacanciesapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aliuken.jobvacanciesapp.annotation.ServiceMethod;
import com.aliuken.jobvacanciesapp.model.entity.AuthUser;
import com.aliuken.jobvacanciesapp.model.entity.AuthUserCurriculum;
import com.aliuken.jobvacanciesapp.repository.AuthUserCurriculumRepository;
import com.aliuken.jobvacanciesapp.repository.superinterface.UpgradedJpaRepository;

@Service
@Transactional
public class AuthUserCurriculumServiceImpl extends AuthUserCurriculumService {

	@Autowired
	private AuthUserCurriculumRepository authUserCurriculumRepository;

	@Override
	public UpgradedJpaRepository<AuthUserCurriculum> getEntityRepository() {
		return authUserCurriculumRepository;
	}

	@Override
	@ServiceMethod
	public AuthUserCurriculum findByAuthUserAndFileName(final AuthUser authUser, final String fileName) {
		final AuthUserCurriculum authUserCurriculum = authUserCurriculumRepository.findByAuthUserAndFileName(authUser, fileName);
		return authUserCurriculum;
	}

	@Override
	public AuthUserCurriculum getNewEntityForSearchByExample(final Long id, final AuthUser firstRegistrationAuthUser, final AuthUser lastModificationAuthUser) {
		final AuthUserCurriculum authUserCurriculum = new AuthUserCurriculum();
		authUserCurriculum.setId(id);
		authUserCurriculum.setFirstRegistrationAuthUser(firstRegistrationAuthUser);
		authUserCurriculum.setLastModificationAuthUser(lastModificationAuthUser);

		return authUserCurriculum;
	}

	@Override
	public AuthUserCurriculum getNewEntityWithAuthUserEmail(final String authUserEmail) {
		final AuthUser authUser = new AuthUser();
		authUser.setEmail(authUserEmail);

		final AuthUserCurriculum authUserCurriculum = new AuthUserCurriculum();
		authUserCurriculum.setAuthUser(authUser);

		return authUserCurriculum;
	}

	@Override
	public AuthUserCurriculum getNewEntityWithAuthUserName(final String authUserName) {
		final AuthUser authUser = new AuthUser();
		authUser.setName(authUserName);

		final AuthUserCurriculum authUserCurriculum = new AuthUserCurriculum();
		authUserCurriculum.setAuthUser(authUser);

		return authUserCurriculum;
	}

	@Override
	public AuthUserCurriculum getNewEntityWithAuthUserSurnames(String authUserSurnames) {
		final AuthUser authUser = new AuthUser();
		authUser.setSurnames(authUserSurnames);

		final AuthUserCurriculum authUserCurriculum = new AuthUserCurriculum();
		authUserCurriculum.setAuthUser(authUser);

		return authUserCurriculum;
	}
}
