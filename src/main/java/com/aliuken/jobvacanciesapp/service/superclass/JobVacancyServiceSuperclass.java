package com.aliuken.jobvacanciesapp.service.superclass;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.aliuken.jobvacanciesapp.annotation.ServiceMethod;
import com.aliuken.jobvacanciesapp.model.dto.AbstractEntityPageWithExceptionDTO;
import com.aliuken.jobvacanciesapp.model.dto.TableSearchDTO;
import com.aliuken.jobvacanciesapp.model.entity.AuthUser;
import com.aliuken.jobvacanciesapp.model.entity.JobCategory;
import com.aliuken.jobvacanciesapp.model.entity.JobCompany;
import com.aliuken.jobvacanciesapp.model.entity.JobVacancy;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.TableField;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.TableSortingDirection;
import com.aliuken.jobvacanciesapp.util.javase.LogicalUtils;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import com.aliuken.jobvacanciesapp.util.javase.ThrowableUtils;
import com.aliuken.jobvacanciesapp.util.persistence.database.DatabaseUtils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Slf4j
public abstract class JobVacancyServiceSuperclass extends AbstractEntityWithJobCompanyServiceSuperclass<JobVacancy> {

	private static final ExampleMatcher JOB_CATEGORY_ID_EXAMPLE_MATCHER = DatabaseUtils.getExampleMatcherWithExactOneField("jobCategory.id");
	private static final ExampleMatcher JOB_CATEGORY_ID_AND_ID_EXAMPLE_MATCHER = DatabaseUtils.getExampleMatcherWithExactTwoFields("jobCategory.id", "id");
	private static final ExampleMatcher JOB_CATEGORY_ID_AND_FIRST_REGISTRATION_AUTH_USER_EMAIL_EXAMPLE_MATCHER = DatabaseUtils.getExampleMatcherWithContainsTwoFields("jobCategory.id", "firstRegistrationAuthUser.email");
	private static final ExampleMatcher JOB_CATEGORY_ID_AND_LAST_MODIFICATION_AUTH_USER_EMAIL_EXAMPLE_MATCHER = DatabaseUtils.getExampleMatcherWithContainsTwoFields("jobCategory.id", "lastModificationAuthUser.email");
	private static final ExampleMatcher JOB_CATEGORY_ID_AND_JOB_COMPANY_NAME_EXAMPLE_MATCHER = DatabaseUtils.getExampleMatcherWithContainsTwoFields("jobCategory.id", "jobCompany.name");

	private static final ExampleMatcher JOB_COMPANY_ID_EXAMPLE_MATCHER = DatabaseUtils.getExampleMatcherWithExactOneField("jobCompany.id");
	private static final ExampleMatcher JOB_COMPANY_ID_AND_ID_EXAMPLE_MATCHER = DatabaseUtils.getExampleMatcherWithExactTwoFields("jobCompany.id", "id");
	private static final ExampleMatcher JOB_COMPANY_ID_AND_FIRST_REGISTRATION_AUTH_USER_EMAIL_EXAMPLE_MATCHER = DatabaseUtils.getExampleMatcherWithContainsTwoFields("jobCompany.id", "firstRegistrationAuthUser.email");
	private static final ExampleMatcher JOB_COMPANY_ID_AND_LAST_MODIFICATION_AUTH_USER_EMAIL_EXAMPLE_MATCHER = DatabaseUtils.getExampleMatcherWithContainsTwoFields("jobCompany.id", "lastModificationAuthUser.email");

	@ServiceMethod
	public AbstractEntityPageWithExceptionDTO<JobVacancy> getJobCategoryJobVacanciesPage(final long jobCategoryId, final TableSearchDTO tableSearchDTO, final Pageable pageable) {
		Page<JobVacancy> page;
		Exception exception;
		try {
			if(tableSearchDTO != null) {
				final TableField filterTableField = tableSearchDTO.getFilterTableField();
				final String filterValue = tableSearchDTO.filterValue();
				final TableField tableSortingField = tableSearchDTO.getTableSortingField();
				final TableSortingDirection tableSortingDirection = tableSearchDTO.getTableSortingDirection();

				page = this.getJobCategoryJobVacanciesPage(jobCategoryId, filterTableField, filterValue, tableSortingField, tableSortingDirection, pageable);
			} else {
				final Example<JobVacancy> example = this.getJobCategoryIdExample(jobCategoryId);
				page = this.findAll(example, pageable);
			}
			exception = null;
		} catch(final Exception e) {
			if(log.isErrorEnabled()) {
				final String stackTrace = ThrowableUtils.getStackTrace(e);
				log.error(StringUtils.getStringJoined("An exception happened when trying to get an entity page. Exception: ", stackTrace));
			}
			page = Page.empty();
			exception = e;
		}

		final AbstractEntityPageWithExceptionDTO<JobVacancy> pageWithExceptionDTO = new AbstractEntityPageWithExceptionDTO<>(page, exception);
		return pageWithExceptionDTO;
	}

	private Page<JobVacancy> getJobCategoryJobVacanciesPage(final long jobCategoryId, final TableField filterTableField, final String filterValue, final TableField tableSortingField, final TableSortingDirection tableSortingDirection, final Pageable pageable) {
		final Page<JobVacancy> page;
		if(filterTableField != null && LogicalUtils.isNotNullNorEmptyString(filterValue)) {
			switch(filterTableField) {
				case ID -> {
					final JobCategory jobCategory = new JobCategory();
					jobCategory.setId(jobCategoryId);

					final Long entityId;
					try {
						entityId = Long.valueOf(filterValue);
					} catch(final NumberFormatException exception) {
						if(log.isErrorEnabled()) {
							final String stackTrace = ThrowableUtils.getStackTrace(exception);
							log.error(StringUtils.getStringJoined("An exception happened when trying to get an entity page. Exception: ", stackTrace));
						}
						throw new IllegalArgumentException(StringUtils.getStringJoined("The id '", filterValue, "' is not a number"));
					}

					final JobVacancy jobVacancySearch = new JobVacancy();
					jobVacancySearch.setId(entityId);
					jobVacancySearch.setJobCategory(jobCategory);

					final Example<JobVacancy> example = Example.of(jobVacancySearch, JOB_CATEGORY_ID_AND_ID_EXAMPLE_MATCHER);
					page = this.findAll(example, pageable, tableSortingField, tableSortingDirection);
				}
				case FIRST_REGISTRATION_DATE_TIME -> {
					final Specification<JobVacancy> specification = this.equalsJobCategoryIdAndFirstRegistrationDateTime(jobCategoryId, filterValue);
					page = this.findAll(pageable, tableSortingField, tableSortingDirection, specification);
				}
				case FIRST_REGISTRATION_AUTH_USER_EMAIL -> {
					final AuthUser firstRegistrationAuthUser = new AuthUser();
					firstRegistrationAuthUser.setEmail(filterValue);

					final JobCategory jobCategory = new JobCategory();
					jobCategory.setId(jobCategoryId);

					final JobVacancy jobVacancySearch = new JobVacancy();
					jobVacancySearch.setFirstRegistrationAuthUser(firstRegistrationAuthUser);
					jobVacancySearch.setJobCategory(jobCategory);

					final Example<JobVacancy> example = Example.of(jobVacancySearch, JOB_CATEGORY_ID_AND_FIRST_REGISTRATION_AUTH_USER_EMAIL_EXAMPLE_MATCHER);
					page = this.findAll(example, pageable, tableSortingField, tableSortingDirection);
				}
				case LAST_MODIFICATION_DATE_TIME -> {
					final Specification<JobVacancy> specification = this.equalsJobCategoryIdAndLastModificationDateTime(jobCategoryId, filterValue);
					page = this.findAll(pageable, tableSortingField, tableSortingDirection, specification);
				}
				case LAST_MODIFICATION_AUTH_USER_EMAIL -> {
					final AuthUser lastModificationAuthUser = new AuthUser();
					lastModificationAuthUser.setEmail(filterValue);

					final JobCategory jobCategory = new JobCategory();
					jobCategory.setId(jobCategoryId);

					final JobVacancy jobVacancySearch = new JobVacancy();
					jobVacancySearch.setLastModificationAuthUser(lastModificationAuthUser);
					jobVacancySearch.setJobCategory(jobCategory);

					final Example<JobVacancy> example = Example.of(jobVacancySearch, JOB_CATEGORY_ID_AND_LAST_MODIFICATION_AUTH_USER_EMAIL_EXAMPLE_MATCHER);
					page = this.findAll(example, pageable, tableSortingField, tableSortingDirection);
				}
				case JOB_COMPANY_NAME -> {
					final JobCategory jobCategory = new JobCategory();
					jobCategory.setId(jobCategoryId);

					final JobVacancy jobVacancySearch = this.getNewEntityWithJobCompanyName(filterValue);
					jobVacancySearch.setJobCategory(jobCategory);

					final Example<JobVacancy> example = Example.of(jobVacancySearch, JOB_CATEGORY_ID_AND_JOB_COMPANY_NAME_EXAMPLE_MATCHER);
					page = this.findAll(example, pageable, tableSortingField, tableSortingDirection);
				}
				default -> {
					throw new IllegalArgumentException(StringUtils.getStringJoined("TableField '", filterTableField.name(), "' not supported"));
				}
			}
		} else {
			final Example<JobVacancy> example = this.getJobCategoryIdExample(jobCategoryId);
			page = this.findAll(example, pageable, tableSortingField, tableSortingDirection);
		}

		return page;
	}

	@ServiceMethod
	public AbstractEntityPageWithExceptionDTO<JobVacancy> getJobCompanyJobVacanciesPage(final long jobCompanyId, final TableSearchDTO tableSearchDTO, final Pageable pageable) {
		Page<JobVacancy> page;
		Exception exception;
		try {
			if(tableSearchDTO != null) {
				final TableField filterTableField = tableSearchDTO.getFilterTableField();
				final String filterValue = tableSearchDTO.filterValue();
				final TableField tableSortingField = tableSearchDTO.getTableSortingField();
				final TableSortingDirection tableSortingDirection = tableSearchDTO.getTableSortingDirection();

				page = this.getJobCompanyJobVacanciesPage(jobCompanyId, filterTableField, filterValue, tableSortingField, tableSortingDirection, pageable);
			} else {
				final Example<JobVacancy> example = this.getJobCompanyIdExample(jobCompanyId);
				page = this.findAll(example, pageable);
			}
			exception = null;
		} catch(final Exception e) {
			if(log.isErrorEnabled()) {
				final String stackTrace = ThrowableUtils.getStackTrace(e);
				log.error(StringUtils.getStringJoined("An exception happened when trying to get an entity page. Exception: ", stackTrace));
			}
			page = Page.empty();
			exception = e;
		}

		final AbstractEntityPageWithExceptionDTO<JobVacancy> pageWithExceptionDTO = new AbstractEntityPageWithExceptionDTO<>(page, exception);
		return pageWithExceptionDTO;
	}

	private Page<JobVacancy> getJobCompanyJobVacanciesPage(final long jobCompanyId, final TableField filterTableField, final String filterValue, final TableField tableSortingField, final TableSortingDirection tableSortingDirection, final Pageable pageable) {
		final Page<JobVacancy> page;
		if(filterTableField != null && LogicalUtils.isNotNullNorEmptyString(filterValue)) {
			switch(filterTableField) {
				case ID -> {
					final JobCompany jobCompany = new JobCompany();
					jobCompany.setId(jobCompanyId);

					final Long entityId;
					try {
						entityId = Long.valueOf(filterValue);
					} catch(final NumberFormatException exception) {
						if(log.isErrorEnabled()) {
							final String stackTrace = ThrowableUtils.getStackTrace(exception);
							log.error(StringUtils.getStringJoined("An exception happened when trying to get an entity page. Exception: ", stackTrace));
						}
						throw new IllegalArgumentException(StringUtils.getStringJoined("The id '", filterValue, "' is not a number"));
					}

					final JobVacancy jobVacancySearch = new JobVacancy();
					jobVacancySearch.setId(entityId);
					jobVacancySearch.setJobCompany(jobCompany);

					final Example<JobVacancy> example = Example.of(jobVacancySearch, JOB_COMPANY_ID_AND_ID_EXAMPLE_MATCHER);
					page = this.findAll(example, pageable, tableSortingField, tableSortingDirection);
				}
				case FIRST_REGISTRATION_DATE_TIME -> {
					final Specification<JobVacancy> specification = this.equalsJobCompanyIdAndFirstRegistrationDateTime(jobCompanyId, filterValue);
					page = this.findAll(pageable, tableSortingField, tableSortingDirection, specification);
				}
				case FIRST_REGISTRATION_AUTH_USER_EMAIL -> {
					final AuthUser firstRegistrationAuthUser = new AuthUser();
					firstRegistrationAuthUser.setEmail(filterValue);

					final JobCompany jobCompany = new JobCompany();
					jobCompany.setId(jobCompanyId);

					final JobVacancy jobVacancySearch = new JobVacancy();
					jobVacancySearch.setFirstRegistrationAuthUser(firstRegistrationAuthUser);
					jobVacancySearch.setJobCompany(jobCompany);

					final Example<JobVacancy> example = Example.of(jobVacancySearch, JOB_COMPANY_ID_AND_FIRST_REGISTRATION_AUTH_USER_EMAIL_EXAMPLE_MATCHER);
					page = this.findAll(example, pageable, tableSortingField, tableSortingDirection);
				}
				case LAST_MODIFICATION_DATE_TIME -> {
					final Specification<JobVacancy> specification = this.equalsJobCompanyIdAndLastModificationDateTime(jobCompanyId, filterValue);
					page = this.findAll(pageable, tableSortingField, tableSortingDirection, specification);
				}
				case LAST_MODIFICATION_AUTH_USER_EMAIL -> {
					final AuthUser lastModificationAuthUser = new AuthUser();
					lastModificationAuthUser.setEmail(filterValue);

					final JobCompany jobCompany = new JobCompany();
					jobCompany.setId(jobCompanyId);

					final JobVacancy jobVacancySearch = new JobVacancy();
					jobVacancySearch.setLastModificationAuthUser(lastModificationAuthUser);
					jobVacancySearch.setJobCompany(jobCompany);

					final Example<JobVacancy> example = Example.of(jobVacancySearch, JOB_COMPANY_ID_AND_LAST_MODIFICATION_AUTH_USER_EMAIL_EXAMPLE_MATCHER);
					page = this.findAll(example, pageable, tableSortingField, tableSortingDirection);
				}
				default -> {
					throw new IllegalArgumentException(StringUtils.getStringJoined("TableField '", filterTableField.name(), "' not supported"));
				}
			}
		} else {
			final Example<JobVacancy> example = this.getJobCompanyIdExample(jobCompanyId);
			page = this.findAll(example, pageable, tableSortingField, tableSortingDirection);
		}

		return page;
	}

	private Example<JobVacancy> getJobCategoryIdExample(long jobCategoryId) {
		final JobCategory jobCategory = new JobCategory();
		jobCategory.setId(jobCategoryId);

		final JobVacancy jobVacancySearch = new JobVacancy();
		jobVacancySearch.setJobCategory(jobCategory);

		final Example<JobVacancy> example = Example.of(jobVacancySearch, JOB_CATEGORY_ID_EXAMPLE_MATCHER);
		return example;
	}

	private Example<JobVacancy> getJobCompanyIdExample(long jobCompanyId) {
		final JobCompany jobCompany = new JobCompany();
		jobCompany.setId(jobCompanyId);

		final JobVacancy jobVacancySearch = new JobVacancy();
		jobVacancySearch.setJobCompany(jobCompany);

		final Example<JobVacancy> example = Example.of(jobVacancySearch, JOB_COMPANY_ID_EXAMPLE_MATCHER);
		return example;
	}

	private Specification<JobVacancy> equalsJobCategoryIdAndFirstRegistrationDateTime(final Long jobCategoryId, final String dateTimeString) {
		return new Specification<JobVacancy>() {
			private static final long serialVersionUID = 5483339128935088308L;

			@Override
			public Predicate toPredicate(final Root<JobVacancy> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) {
				final String entityFieldName = "jobCategory";
				final String dateTimeFieldName = "firstRegistrationDateTime";
				final Predicate predicate = DatabaseUtils.getEqualsEntityIdAndDateTimePredicate(jobCategoryId, entityFieldName, dateTimeString, dateTimeFieldName, root, criteriaQuery, criteriaBuilder);
				return predicate;
			}
		};
	}

	private Specification<JobVacancy> equalsJobCategoryIdAndLastModificationDateTime(final Long jobCategoryId, final String dateTimeString) {
		return new Specification<JobVacancy>() {
			private static final long serialVersionUID = 8689777778809761337L;

			@Override
			public Predicate toPredicate(final Root<JobVacancy> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) {
				final String entityFieldName = "jobCategory";
				final String dateTimeFieldName = "lastModificationDateTime";
				final Predicate predicate = DatabaseUtils.getEqualsEntityIdAndDateTimePredicate(jobCategoryId, entityFieldName, dateTimeString, dateTimeFieldName, root, criteriaQuery, criteriaBuilder);
				return predicate;
			}
		};
	}

	private Specification<JobVacancy> equalsJobCompanyIdAndFirstRegistrationDateTime(final Long jobCompanyId, final String dateTimeString) {
		return new Specification<JobVacancy>() {
			private static final long serialVersionUID = -2749244145790761484L;

			@Override
			public Predicate toPredicate(final Root<JobVacancy> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) {
				final String entityFieldName = "jobCompany";
				final String dateTimeFieldName = "firstRegistrationDateTime";
				final Predicate predicate = DatabaseUtils.getEqualsEntityIdAndDateTimePredicate(jobCompanyId, entityFieldName, dateTimeString, dateTimeFieldName, root, criteriaQuery, criteriaBuilder);
				return predicate;
			}
		};
	}

	private Specification<JobVacancy> equalsJobCompanyIdAndLastModificationDateTime(final Long jobCompanyId, final String dateTimeString) {
		return new Specification<JobVacancy>() {
			private static final long serialVersionUID = -776681120938849544L;

			@Override
			public Predicate toPredicate(final Root<JobVacancy> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) {
				final String entityFieldName = "jobCompany";
				final String dateTimeFieldName = "lastModificationDateTime";
				final Predicate predicate = DatabaseUtils.getEqualsEntityIdAndDateTimePredicate(jobCompanyId, entityFieldName, dateTimeString, dateTimeFieldName, root, criteriaQuery, criteriaBuilder);
				return predicate;
			}
		};
	}
}
