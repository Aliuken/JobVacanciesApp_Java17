package com.aliuken.jobvacanciesapp.model.entity.enumtype;

import com.aliuken.jobvacanciesapp.Constants;

import java.io.Serializable;

public enum PageEntityEnum implements Serializable {
	AUTH_USER           ("authUsers"),
	AUTH_USER_CURRICULUM("authUserCurriculums"),
	JOB_CATEGORY        ("jobCategories"),
	JOB_COMPANY         ("jobCompanies"),
	JOB_COMPANY_LOGO    ("jobCompanyLogos"),
	JOB_REQUEST         ("jobRequests"),
	JOB_VACANCY         ("jobVacancies");

	private final String value;

	private PageEntityEnum(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static PageEntityEnum findByValue(final String value) {
		final PageEntityEnum pageEntity;
		if(value != null) {
			pageEntity = Constants.PARALLEL_STREAM_UTILS.ofEnum(PageEntityEnum.class)
				.filter(pageEntityAux -> value.equals(pageEntityAux.value))
				.findFirst()
				.orElse(null);
		} else {
			pageEntity = null;
		}

		return pageEntity;
	}
}
