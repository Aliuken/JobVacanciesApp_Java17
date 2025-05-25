package com.aliuken.jobvacanciesapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aliuken.jobvacanciesapp.annotation.ServiceMethod;
import com.aliuken.jobvacanciesapp.model.entity.AuthRole;
import com.aliuken.jobvacanciesapp.model.entity.AuthUser;
import com.aliuken.jobvacanciesapp.model.entity.AuthUserRole;
import com.aliuken.jobvacanciesapp.repository.AuthUserRoleRepository;
import com.aliuken.jobvacanciesapp.repository.superinterface.UpgradedJpaRepository;

@Service
@Transactional
public class AuthUserRoleServiceImpl extends AuthUserRoleService {

	@Autowired
	private AuthUserRoleRepository authUserRoleRepository;

	@Override
	public UpgradedJpaRepository<AuthUserRole> getEntityRepository() {
		return authUserRoleRepository;
	}

	@Override
	@ServiceMethod
	public AuthUserRole findByAuthUserAndAuthRole(final AuthUser authUser, final AuthRole authRole) {
		final AuthUserRole authUserRole = authUserRoleRepository.findByAuthUserAndAuthRole(authUser, authRole);
		return authUserRole;
	}

	@Override
	public AuthUserRole getNewEntityForSearchByExample(final Long id, final AuthUser firstRegistrationAuthUser, final AuthUser lastModificationAuthUser) {
		final AuthUserRole authUserRole = new AuthUserRole();
		authUserRole.setId(id);
		authUserRole.setFirstRegistrationAuthUser(firstRegistrationAuthUser);
		authUserRole.setLastModificationAuthUser(lastModificationAuthUser);

		return authUserRole;
	}

	@Override
	public AuthUserRole getNewEntityWithAuthUserEmail(final String authUserEmail) {
		final AuthUser authUser = new AuthUser();
		authUser.setEmail(authUserEmail);

		final AuthUserRole authUserRole = new AuthUserRole();
		authUserRole.setAuthUser(authUser);

		return authUserRole;
	}

	@Override
	public AuthUserRole getNewEntityWithAuthUserName(final String authUserName) {
		final AuthUser authUser = new AuthUser();
		authUser.setName(authUserName);

		final AuthUserRole authUserRole = new AuthUserRole();
		authUserRole.setAuthUser(authUser);

		return authUserRole;
	}

	@Override
	public AuthUserRole getNewEntityWithAuthUserSurnames(final String authUserSurnames) {
		final AuthUser authUser = new AuthUser();
		authUser.setSurnames(authUserSurnames);

		final AuthUserRole authUserRole = new AuthUserRole();
		authUserRole.setAuthUser(authUser);

		return authUserRole;
	}
}
