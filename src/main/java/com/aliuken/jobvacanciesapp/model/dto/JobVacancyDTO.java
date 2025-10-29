package com.aliuken.jobvacanciesapp.model.dto;

import com.aliuken.jobvacanciesapp.Constants;
import com.aliuken.jobvacanciesapp.model.dto.superinterface.AbstractEntityDTO;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.Language;
import com.aliuken.jobvacanciesapp.util.javase.NumericUtils;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Function;

public record JobVacancyDTO(
	Long id,

	@NotEmpty(message="{name.notEmpty}")
	@Size(max=120, message="{name.maxSize120}")
	String name,

	@NotEmpty(message="{description.notEmpty}")
	@Size(max=500, message="{description.maxSize}")
	String description,

	@NotNull(message="{jobCategory.notNull}")
	JobCategoryDTO jobCategory,

	//In jobVacancyForm.html we cannot use *{jobCategory.id} because JobCategoryDTO is a Java record, so we use *{jobCategoryId} instead
	Long jobCategoryId,

	@NotNull(message="{jobCompany.notNull}")
	JobCompanyDTO jobCompany,

	//In jobVacancyForm.html we cannot use *{jobCompany.id} because JobCompanyDTO is a Java record, so we use *{jobCompanyId} instead
	Long jobCompanyId,

	@NotEmpty(message="{statusCode.notEmpty}")
	@Size(max=1, message="{statusCode.maxSize}")
	String statusCode,

	@NotNull(message="{publicationDateTime.notNull}")
	LocalDateTime publicationDateTime,

	String salaryString,

	BigDecimalFromStringConversionResult salaryConversionResult,

	@NotEmpty(message="{currencySymbol.notEmpty}")
	@Size(max=1, message="{currencySymbol.maxSize}")
	String currencySymbol,

	@NotNull(message="{highlighted.notNull}")
	Boolean highlighted,

	@NotEmpty(message="{details.notEmpty}")
	@Size(max=10000, message="{details.maxSize}")
	String details
) implements AbstractEntityDTO, Serializable {

	private static final JobVacancyDTO NO_ARGS_INSTANCE = new JobVacancyDTO(null, null, null, null, null, null, null, null, null, null, null, null, null, null);

	public JobVacancyDTO {
		if(jobCategory == null) {
			jobCategory = JobCategoryDTO.getNewInstance(jobCategoryId);
		} else if(jobCategoryId == null) {
			jobCategoryId = jobCategory.id();
		}
		if(jobCompany == null) {
			jobCompany = JobCompanyDTO.getNewInstance(jobCompanyId);
		} else if(jobCompanyId == null) {
			jobCompanyId = jobCompany.id();
		}
		salaryConversionResult = NumericUtils.getBigDecimalFromStringConversionResult("jobVacancy.salary", salaryString, 10, 2);
	}

	public static JobVacancyDTO getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	public static JobVacancyDTO getNewInstance(final Long jobVacancyId) {
		final JobVacancyDTO jobVacancyDTO = new JobVacancyDTO(jobVacancyId, null, null, null, null, null, null, null, null, null, null, null, null, null);
		return jobVacancyDTO;
	}

	public static JobVacancyDTO getNewInstance(JobVacancyDTO jobVacancyDTO, final JobCompanyDTO jobCompanyDTO, final String initialCurrencySymbol) {
		if(jobVacancyDTO != null) {
			jobVacancyDTO = new JobVacancyDTO(
				jobVacancyDTO.id(),
				jobVacancyDTO.name(),
				jobVacancyDTO.description(),
				jobVacancyDTO.jobCategory(),
				(jobVacancyDTO.jobCategory() != null) ? jobVacancyDTO.jobCategory().id() : null,
				jobCompanyDTO,
				(jobCompanyDTO != null) ? jobCompanyDTO.id() : null,
				jobVacancyDTO.statusCode(),
				jobVacancyDTO.publicationDateTime(),
				jobVacancyDTO.salaryString(),
				jobVacancyDTO.salaryConversionResult(),
				initialCurrencySymbol,
				jobVacancyDTO.highlighted(),
				jobVacancyDTO.details()
			);
		} else {
			jobVacancyDTO = new JobVacancyDTO(
				null,
				null,
				null,
				null,
				null,
				jobCompanyDTO,
				(jobCompanyDTO != null) ? jobCompanyDTO.id() : null,
				null,
				null,
				null,
				null,
				null,
				null,
				null
			);
		}
		return jobVacancyDTO;
	}

	public Function<Language, String> getConversionErrorFunction() {
		final Function<Language, String> conversionErrorFunction;
		if(salaryConversionResult != null) {
			conversionErrorFunction = salaryConversionResult.conversionErrorFunction();
		} else {
			conversionErrorFunction = null;
		}
		return conversionErrorFunction;
	}

	public BigDecimal getSalary() {
		final BigDecimal salary;
		if(salaryConversionResult != null) {
			salary = salaryConversionResult.conversionResult();
		} else {
			salary = null;
		}
		return salary;
	}

	@Override
	public String toString() {
		final String idString = Objects.toString(id);
		final String jobCategoryIdString = (jobCategory != null) ? Objects.toString(jobCategory.id()) : null;
		final String jobCompanyIdString = (jobCompany != null) ? Objects.toString(jobCompany.id()) : null;
		final String publicationDateTimeString = Constants.DATE_TIME_UTILS.convertToString(publicationDateTime);
		final String salaryConversionResultString  = Objects.toString(salaryConversionResult);
		final String highlightedString = highlighted.toString();

		final String result = StringUtils.getStringJoined("JobVacancyDTO [id=", idString, ", name=", name, ", description=", description,
			", jobCategory=", jobCategoryIdString, ", jobCompany=", jobCompanyIdString, ", statusCode=", statusCode, ", publicationDateTime=", publicationDateTimeString, ", salary=", salaryString, ", salaryConversionResult=", salaryConversionResultString, ", currencySymbol=", currencySymbol, ", highlighted=", highlightedString, ", details=", details, "]");
		return result;
	}
}
