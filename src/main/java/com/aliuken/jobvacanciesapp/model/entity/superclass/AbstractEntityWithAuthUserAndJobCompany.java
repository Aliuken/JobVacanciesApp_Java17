package com.aliuken.jobvacanciesapp.model.entity.superclass;

import com.aliuken.jobvacanciesapp.model.entity.superinterface.AbstractEntityWithAuthUserInterface;
import com.aliuken.jobvacanciesapp.model.entity.superinterface.AbstractEntityWithJobCompanyInterface;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractEntityWithAuthUserAndJobCompany extends AbstractEntity implements AbstractEntityWithAuthUserInterface, AbstractEntityWithJobCompanyInterface {
	private static final long serialVersionUID = -6502895741206864193L;

	public AbstractEntityWithAuthUserAndJobCompany() {
		super();
	}

	@Override
	public String toString() {
		final String idString = this.getIdString();
		final String authUserEmail = this.getAuthUserEmail();
		final String jobCompanyName = this.getJobCompanyName();
		final String firstRegistrationDateTimeString = this.getFirstRegistrationDateTimeString();
		final String firstRegistrationAuthUserEmail = this.getFirstRegistrationAuthUserEmail();
		final String lastModificationDateTimeString = this.getLastModificationDateTimeString();
		final String lastModificationAuthUserEmail = this.getLastModificationAuthUserEmail();

		final String result = StringUtils.getStringJoined("AbstractEntityWithAuthUser [id=", idString, ", authUserEmail=", authUserEmail, ", jobCompanyName=", jobCompanyName,
			", firstRegistrationDateTime=", firstRegistrationDateTimeString, ", firstRegistrationAuthUser=", firstRegistrationAuthUserEmail, ", lastModificationDateTime=", lastModificationDateTimeString, ", lastModificationAuthUser=", lastModificationAuthUserEmail, "]");

		return result;
	}
}
