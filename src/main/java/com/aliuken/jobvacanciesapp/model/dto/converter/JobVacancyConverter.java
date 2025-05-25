package com.aliuken.jobvacanciesapp.model.dto.converter;

import com.aliuken.jobvacanciesapp.model.dto.BigDecimalFromStringConversionResult;
import com.aliuken.jobvacanciesapp.model.dto.JobCategoryDTO;
import com.aliuken.jobvacanciesapp.model.dto.JobCompanyDTO;
import com.aliuken.jobvacanciesapp.model.dto.JobVacancyDTO;
import com.aliuken.jobvacanciesapp.model.dto.converter.superclass.EntityToDtoConverter;
import com.aliuken.jobvacanciesapp.model.entity.JobVacancy;
import com.aliuken.jobvacanciesapp.util.javase.NumericUtils;

import java.util.Objects;

public class JobVacancyConverter extends EntityToDtoConverter<JobVacancy, JobVacancyDTO> {

	private static final JobVacancyConverter SINGLETON_INSTANCE = new JobVacancyConverter();

	private JobVacancyConverter() {
		super(JobVacancyConverter::conversionFunction, JobVacancy.class, JobVacancyDTO.class, JobVacancyDTO[]::new);
	}

	public static JobVacancyConverter getInstance() {
		return SINGLETON_INSTANCE;
	}

	private static JobVacancyDTO conversionFunction(final JobVacancy jobVacancy) {
		final JobVacancyDTO jobVacancyDTO;
		if(jobVacancy != null) {
			final JobCategoryDTO jobCategoryDTO = JobCategoryConverter.getInstance().convertEntityElement(jobVacancy.getJobCategory());
			final JobCompanyDTO jobCompanyDTO = JobCompanyConverter.getInstance().convertEntityElement(jobVacancy.getJobCompany());
			final String salaryString = Objects.toString(jobVacancy.getSalary());
			final BigDecimalFromStringConversionResult salaryConversionResult = null;

			jobVacancyDTO = new JobVacancyDTO(
				jobVacancy.getId(),
				jobVacancy.getName(),
				jobVacancy.getDescription(),
				jobCategoryDTO,
				(jobCategoryDTO != null) ? jobCategoryDTO.id() : null,
				jobCompanyDTO,
				(jobCompanyDTO != null) ? jobCompanyDTO.id() : null,
				(jobVacancy.getStatus() != null) ? jobVacancy.getStatus().getCode() : null,
				jobVacancy.getPublicationDateTime(),
				salaryString,
				salaryConversionResult,
				(jobVacancy.getCurrency() != null) ? jobVacancy.getCurrency().getSymbol() : null,
				jobVacancy.getHighlighted(),
				jobVacancy.getDetails()
			);
		} else {
			jobVacancyDTO = JobVacancyDTO.getNewInstance();
		}
		return jobVacancyDTO;
	}
}