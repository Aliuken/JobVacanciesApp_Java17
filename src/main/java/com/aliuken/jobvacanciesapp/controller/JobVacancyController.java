package com.aliuken.jobvacanciesapp.controller;

import com.aliuken.jobvacanciesapp.Constants;
import com.aliuken.jobvacanciesapp.config.ConfigPropertiesBean;
import com.aliuken.jobvacanciesapp.controller.superclass.AbstractEntityControllerWithoutPredefinedFilter;
import com.aliuken.jobvacanciesapp.model.dto.*;
import com.aliuken.jobvacanciesapp.model.dto.converter.JobCompanyConverter;
import com.aliuken.jobvacanciesapp.model.dto.converter.JobVacancyConverter;
import com.aliuken.jobvacanciesapp.model.entity.*;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.Currency;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.JobVacancyStatus;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.Language;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.PageEntityEnum;
import com.aliuken.jobvacanciesapp.service.*;
import com.aliuken.jobvacanciesapp.util.i18n.I18nUtils;
import com.aliuken.jobvacanciesapp.util.javase.LogicalUtils;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import com.aliuken.jobvacanciesapp.util.javase.ThrowableUtils;
import com.aliuken.jobvacanciesapp.util.spring.di.BeanFactoryUtils;
import com.aliuken.jobvacanciesapp.util.spring.mvc.ControllerNavigationUtils;
import com.aliuken.jobvacanciesapp.util.spring.mvc.ControllerServletUtils;
import com.aliuken.jobvacanciesapp.util.spring.mvc.ControllerValidationUtils;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

@Controller
@Slf4j
public class JobVacancyController extends AbstractEntityControllerWithoutPredefinedFilter<JobVacancy> {

	@Autowired
	private ConfigPropertiesBean configPropertiesBean;

	@Autowired
	private JobVacancyService jobVacancyService;

	@Autowired
	private JobCategoryService jobCategoryService;

	@Autowired
	private JobCompanyService jobCompanyService;

	@Autowired
	private JobRequestService jobRequestService;

	@Autowired
	private AuthUserService authUserService;

	private static boolean useAjaxToRefreshJobCompanyLogos;

	@PostConstruct
	private void postConstruct() {
		useAjaxToRefreshJobCompanyLogos = configPropertiesBean.isUseAjaxToRefreshJobCompanyLogos();
	}

	/**
	 * Method to show the list of job vacancies with pagination
	 */
	@GetMapping("/job-vacancies/index")
	public String index(Model model, Pageable pageable,
			@Validated TableSearchDTO tableSearchDTO, BindingResult bindingResult) {
		final String operation = "GET /job-vacancies/index";

		try {
			if(tableSearchDTO == null || !tableSearchDTO.hasAllParameters()) {
				if(log.isDebugEnabled()) {
					final String tableSearchDtoString = Objects.toString(tableSearchDTO);
					log.debug(StringUtils.getStringJoined("Some table search parameters were empty: ", tableSearchDtoString));
				}

				final Page<JobVacancy> jobVacancies = Page.empty();
				model.addAttribute("jobVacancies", jobVacancies);
				model.addAttribute("paginationUrl", this.getPaginationUrl());
				model.addAttribute("exportToPdfUrl", EXPORT_TO_PDF_DISABLED_VALUE);

				return ControllerNavigationUtils.getNextViewWithTable("jobVacancy/listJobVacancies.html", model, operation, tableSearchDTO, false);
			}

			final String firstBindingErrorString = ControllerValidationUtils.getFirstBindingErrorString(bindingResult);
			if(firstBindingErrorString != null) {
				final Page<JobVacancy> jobVacancies = Page.empty();
				model.addAttribute("jobVacancies", jobVacancies);
				model.addAttribute("paginationUrl", this.getPaginationUrl());
				model.addAttribute("exportToPdfUrl", EXPORT_TO_PDF_DISABLED_VALUE);
				//model.addAttribute("org.springframework.validation.BindingResult.tableSearchDTO", bindingResult);
				model.addAttribute("errorMsg", firstBindingErrorString);

				return ControllerNavigationUtils.getNextViewWithTable("jobVacancy/listJobVacancies.html", model, operation, tableSearchDTO, true);
			}

			final AbstractEntityPageWithExceptionDTO<JobVacancy> pageWithExceptionDTO = jobVacancyService.getEntityPage(tableSearchDTO, pageable);
			final Page<JobVacancy> jobVacancies = pageWithExceptionDTO.page();
			final Throwable throwable = pageWithExceptionDTO.throwable();

			model.addAttribute("jobVacancies", jobVacancies);
			model.addAttribute("paginationUrl", this.getPaginationUrl());
			model.addAttribute("exportToPdfUrl", this.getExportToPdfUrl());

			if(throwable != null) {
				final String rootCauseMessage = ThrowableUtils.getRootCauseMessage(throwable);
				model.addAttribute("errorMsg", rootCauseMessage);
			}

			return ControllerNavigationUtils.getNextViewWithTable("jobVacancy/listJobVacancies.html", model, operation, tableSearchDTO, true);

		} catch(final Exception exception) {
			if(log.isErrorEnabled()) {
				final String stackTrace = ThrowableUtils.getStackTrace(exception);
				log.error(StringUtils.getStringJoined("An exception happened when executing a controller method. Exception: ", stackTrace));
			}

			final Page<JobVacancy> jobVacancies = Page.empty();
			model.addAttribute("jobVacancies", jobVacancies);
			model.addAttribute("paginationUrl", this.getPaginationUrl());
			model.addAttribute("exportToPdfUrl", EXPORT_TO_PDF_DISABLED_VALUE);

			final String rootCauseMessage = ThrowableUtils.getRootCauseMessage(exception);
			model.addAttribute("errorMsg", rootCauseMessage);

			return ControllerNavigationUtils.getNextViewWithTable("jobVacancy/listJobVacancies.html", model, operation, tableSearchDTO, true);
		}
	}

	/**
	 * Method to export the list of job vacancies with pagination to pdf
	 */
	@GetMapping("/job-vacancies/index/export-to-pdf")
	@ResponseBody
	public byte[] exportToPdf(Model model, Pageable pageable,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			@RequestParam(name="languageParam", required=false) String languageCode,
			@RequestParam(name="filterName", required=false) String filterName,
			@RequestParam(name="filterValue", required=false) String filterValue,
			@RequestParam(name="sortingField", required=false) String sortingField,
			@RequestParam(name="sortingDirection", required=false) String sortingDirection,
			@RequestParam(name="pageSize", required=false) Integer pageSize,
			@RequestParam(name="pageNumber", required=false) Integer pageNumber) {

		final PredefinedFilterDTO predefinedFilterDTO = null;
		final TableSearchDTO tableSearchDTO = new TableSearchDTO(languageCode, filterName, filterValue, sortingField, sortingDirection, pageSize, pageNumber);
		final BindingResult bindingResult = null;

		this.index(model, pageable, tableSearchDTO, bindingResult);
		final byte[] pdfByteArray = this.storeAndDownloadPdf(predefinedFilterDTO, tableSearchDTO, model, PageEntityEnum.JOB_VACANCY, httpServletRequest, httpServletResponse);
		return pdfByteArray;
	}

	/**
	 * Method to show the detail of a job vacancy
	 */
	@GetMapping("/job-vacancies/view/{jobVacancyId}")
	public String view(Model model, @PathVariable("jobVacancyId") long jobVacancyId,
			@RequestParam(name="languageParam", required=false) String languageCode) {
		final String operation = "GET /job-vacancies/view/{jobVacancyId}";

		final JobVacancy jobVacancy = jobVacancyService.findByIdNotOptional(jobVacancyId);
		model.addAttribute("jobVacancy", jobVacancy);

		return ControllerNavigationUtils.getNextView("jobVacancy/jobVacancyDetail.html", model, operation, languageCode);
	}

	/**
	 * Method to show the creation form of a job vacancy
	 */
	@GetMapping("/job-vacancies/create")
	public String create(HttpServletRequest httpServletRequest, Model model,
			@RequestParam(name="languageParam", required=false) String languageCode, @RequestParam(name="jobCompanyLogo", required=false) String jobCompanyLogoUrlParam) {
		final String operation = "GET /job-vacancies/create";

		JobCompanyDTO jobCompanyDTO = JobCompanyDTO.getNewInstance();

		if(!useAjaxToRefreshJobCompanyLogos) {
			jobCompanyDTO = JobVacancyController.setSelectedJobCompanyLogoForJobVacancyForm(jobCompanyDTO, jobCompanyLogoUrlParam, true);
		}

		final String initialCurrencySymbol = configPropertiesBean.getInitialCurrencySymbol();

		final Map<String, ?> inputFlashMap = ControllerServletUtils.getInputFlashMap(httpServletRequest);

		JobVacancyDTO jobVacancyDTO;
		if(inputFlashMap != null) {
			jobVacancyDTO = (JobVacancyDTO) inputFlashMap.get("jobVacancyDTO");
			if(jobVacancyDTO == null) {
				jobVacancyDTO = JobVacancyDTO.getNewInstance();
			}
		} else {
			jobVacancyDTO = JobVacancyDTO.getNewInstance();
		}

		jobVacancyDTO = JobVacancyDTO.getNewInstance(jobVacancyDTO, jobCompanyDTO, initialCurrencySymbol);

		model.addAttribute("jobVacancyDTO", jobVacancyDTO);
		model.addAttribute("jobCompanyLogo", jobVacancyDTO.jobCompany().selectedLogoId());
		model.addAttribute("useAjaxToRefreshJobCompanyLogos", useAjaxToRefreshJobCompanyLogos);

		return ControllerNavigationUtils.getNextView("jobVacancy/jobVacancyForm.html", model, operation, languageCode);
	}


	/**
	 * Method to show the edition form of a job vacancy
	 */
	@GetMapping("/job-vacancies/edit/{jobVacancyId}")
	public String edit(HttpServletRequest httpServletRequest, Model model, @PathVariable("jobVacancyId") long jobVacancyId,
			@RequestParam(name="languageParam", required=false) String languageCode, @RequestParam(name="jobCompanyLogo", required=false) String jobCompanyLogoUrlParam) {
		final String operation = "GET /job-vacancies/edit/{jobVacancyId}";

		final Map<String, ?> inputFlashMap = ControllerServletUtils.getInputFlashMap(httpServletRequest);

		JobVacancyDTO jobVacancyDTO;
		if(inputFlashMap != null) {
			jobVacancyDTO = (JobVacancyDTO) inputFlashMap.get("jobVacancyDTO");
		} else {
			jobVacancyDTO = null;
		}

		if(jobVacancyDTO == null) {
			final JobVacancy jobVacancy = jobVacancyService.findByIdNotOptional(jobVacancyId);
			jobVacancyDTO = JobVacancyConverter.getInstance().convertEntityElement(jobVacancy);
		}

		JobCompanyDTO jobCompanyDTO = jobVacancyDTO.jobCompany();

		if(!useAjaxToRefreshJobCompanyLogos) {
			jobCompanyDTO = JobVacancyController.setSelectedJobCompanyLogoForJobVacancyForm(jobCompanyDTO, jobCompanyLogoUrlParam, false);
		}

		final String currencySymbol = jobVacancyDTO.currencySymbol();

		jobVacancyDTO = JobVacancyDTO.getNewInstance(jobVacancyDTO, jobCompanyDTO, currencySymbol);

		final Function<Language, String> conversionErrorFunction = jobVacancyDTO.getConversionErrorFunction();
		if(conversionErrorFunction != null) {
			final Language language = Language.findByCode(languageCode);
			final String conversionError = conversionErrorFunction.apply(language);
			model.addAttribute("errorMsg", conversionError);
		}

		model.addAttribute("jobVacancyDTO", jobVacancyDTO);
		model.addAttribute("jobCompanyLogo", jobVacancyDTO.jobCompany().selectedLogoId());
		model.addAttribute("useAjaxToRefreshJobCompanyLogos", useAjaxToRefreshJobCompanyLogos);

		return ControllerNavigationUtils.getNextView("jobVacancy/jobVacancyForm.html", model, operation, languageCode);
	}

	/**
	 * Method to refresh the logo of the company of a job vacancy
	 */
	@GetMapping("/job-vacancies/refresh-logo")
	public String refreshLogo(Model model,
			@RequestParam(name="jobCompanyId", required=false) Long jobCompanyId, @RequestParam(name="jobCompanyLogo", required=false) String jobCompanyLogoUrlParam) {
		JobCompanyDTO jobCompanyDTO;
		if(jobCompanyId != null) {
			final JobCompany jobCompany = jobCompanyService.findByIdNotOptional(jobCompanyId);
			jobCompanyDTO = JobCompanyConverter.getInstance().convertEntityElement(jobCompany);
		} else {
			jobCompanyDTO = JobCompanyDTO.getNewInstance();
		}

		jobCompanyDTO = JobVacancyController.setSelectedJobCompanyLogoForJobVacancyForm(jobCompanyDTO, jobCompanyLogoUrlParam, false);

		model.addAttribute("jobCompanyDTO", jobCompanyDTO);
		model.addAttribute("isJobCompanyForm", false);

		return "fragments/optional/jobCompanyLogoFragment :: jobCompanyLogoFragment";
	}

	/**
	 * Method to save a job vacancy in the database
	 */
	@PostMapping("/job-vacancies/save")
	public String save(RedirectAttributes redirectAttributes,
			@Validated JobVacancyDTO jobVacancyDTO, BindingResult bindingResult,
			@RequestParam(name="id", required=false) Long id, @RequestParam(name="languageParam", required=false) String languageCode) {
		final JobCompanyDTO jobCompanyDTO = (jobVacancyDTO != null) ? jobVacancyDTO.jobCompany() : null;
		final Long jobCompanyLogoId = (jobCompanyDTO != null) ? jobCompanyDTO.selectedLogoId() : null;

		try {
			final String firstBindingErrorString = ControllerValidationUtils.getFirstBindingErrorString(bindingResult);
			if(firstBindingErrorString != null) {
				redirectAttributes.addFlashAttribute("jobCompanyLogo", jobCompanyLogoId);
				redirectAttributes.addFlashAttribute("useAjaxToRefreshJobCompanyLogos", useAjaxToRefreshJobCompanyLogos);
				redirectAttributes.addFlashAttribute("jobVacancyDTO", jobVacancyDTO);
				//redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.jobVacancyDTO", bindingResult);
				redirectAttributes.addFlashAttribute("errorMsg", firstBindingErrorString);

				if(id == null) {
					return ControllerNavigationUtils.getNextRedirect("/job-vacancies/create", languageCode);
				} else {
					return ControllerNavigationUtils.getNextRedirect("/job-vacancies/edit/" + id, languageCode);
				}
			}

			final Function<Language, String> conversionErrorFunction = jobVacancyDTO.getConversionErrorFunction();
			if(conversionErrorFunction != null) {
				final Language language = Language.findByCode(languageCode);
				final String conversionError = conversionErrorFunction.apply(language);
				redirectAttributes.addFlashAttribute("jobCompanyLogo", jobCompanyLogoId);
				redirectAttributes.addFlashAttribute("useAjaxToRefreshJobCompanyLogos", useAjaxToRefreshJobCompanyLogos);
				redirectAttributes.addFlashAttribute("jobVacancyDTO", jobVacancyDTO);
				redirectAttributes.addFlashAttribute("errorMsg", conversionError);

				if(id == null) {
					return ControllerNavigationUtils.getNextRedirect("/job-vacancies/create", languageCode);
				} else {
					return ControllerNavigationUtils.getNextRedirect("/job-vacancies/edit/" + id, languageCode);
				}
			}

			final String name = jobVacancyDTO.name();
			final String description = jobVacancyDTO.description();
			final JobCategoryDTO jobCategoryDTO = jobVacancyDTO.jobCategory();
			final String statusCode = jobVacancyDTO.statusCode();
			final LocalDateTime publicationDateTime = jobVacancyDTO.publicationDateTime();
			final BigDecimal salary = jobVacancyDTO.getSalary();
			final String currencySymbol = jobVacancyDTO.currencySymbol();
			final Boolean highlighted = jobVacancyDTO.highlighted();
			final String details = jobVacancyDTO.details();

			final JobCategory jobCategory;
			if(jobCategoryDTO != null) {
				final Long jobCategoryDTOId = jobCategoryDTO.id();
				jobCategory = jobCategoryService.findByIdNotOptional(jobCategoryDTOId);
			} else {
				jobCategory = null;
			}

			final JobCompany jobCompany;
			if(jobCompanyDTO != null) {
				final Long jobCompanyDTOId = jobCompanyDTO.id();
				jobCompany = jobCompanyService.findByIdNotOptional(jobCompanyDTOId);
			} else {
				jobCompany = null;
			}

			final JobVacancyStatus status = JobVacancyStatus.findByCode(statusCode);
			final Currency currency = Currency.findBySymbol(currencySymbol);

			JobVacancy jobVacancy = jobVacancyService.findByIdOrNewEntity(id);
			jobVacancy.setName(name);
			jobVacancy.setDescription(description);
			jobVacancy.setJobCategory(jobCategory);
			jobVacancy.setJobCompany(jobCompany);
			jobVacancy.setStatus(status);
			jobVacancy.setPublicationDateTime(publicationDateTime);
			jobVacancy.setSalary(salary);
			jobVacancy.setCurrency(currency);
			jobVacancy.setHighlighted(highlighted);
			jobVacancy.setDetails(details);

			jobVacancy = jobVacancyService.saveAndFlush(jobVacancy);

			final String successMsg = I18nUtils.getInternationalizedMessage(languageCode, "saveJobVacancy.successMsg", null);
			redirectAttributes.addFlashAttribute("successMsg", successMsg);

			return ControllerNavigationUtils.getNextRedirect("/job-vacancies/view/" + jobVacancy.getId(), languageCode);

		} catch(final Exception exception) {
			if(log.isErrorEnabled()) {
				final String stackTrace = ThrowableUtils.getStackTrace(exception);
				log.error(StringUtils.getStringJoined("An exception happened when executing a controller method. Exception: ", stackTrace));
			}

			final String rootCauseMessage = ThrowableUtils.getRootCauseMessage(exception);

			redirectAttributes.addFlashAttribute("jobCompanyLogo", jobCompanyLogoId);
			redirectAttributes.addFlashAttribute("useAjaxToRefreshJobCompanyLogos", useAjaxToRefreshJobCompanyLogos);
			redirectAttributes.addFlashAttribute("jobVacancyDTO", jobVacancyDTO);
			redirectAttributes.addFlashAttribute("errorMsg", rootCauseMessage);

			if(id == null) {
				return ControllerNavigationUtils.getNextRedirect("/job-vacancies/create", languageCode);
			} else {
				return ControllerNavigationUtils.getNextRedirect("/job-vacancies/edit/" + id, languageCode);
			}
		}
	}

	/**
	 * Method to delete a job vacancy
	 */
	@GetMapping("/job-vacancies/delete/{jobVacancyId}")
	public String delete(RedirectAttributes redirectAttributes, @PathVariable("jobVacancyId") long jobVacancyId,
			@RequestParam(name="languageParam", required=false) String languageCode,
			@RequestParam(name="filterName", required=false) String filterName,
			@RequestParam(name="filterValue", required=false) String filterValue,
			@RequestParam(name="sortingField", required=false) String sortingField,
			@RequestParam(name="sortingDirection", required=false) String sortingDirection,
			@RequestParam(name="pageSize", required=false) String pageSize,
			@RequestParam(name="pageNumber", required=false) String pageNumber) {

		final JobVacancy jobVacancy = jobVacancyService.findByIdNotOptional(jobVacancyId);

		final Set<Long> jobRequestIds = jobVacancy.getJobRequestIds();
		for(final Long jobRequestId : jobRequestIds) {
			jobRequestService.deleteByIdAndFlush(jobRequestId);
		}

		jobVacancyService.deleteByIdAndFlush(jobVacancyId);

		final String successMsg = I18nUtils.getInternationalizedMessage(languageCode, "deleteJobVacancy.successMsg", null);
		redirectAttributes.addFlashAttribute("successMsg", successMsg);

		return ControllerNavigationUtils.getNextRedirectWithTable("/job-vacancies/index", languageCode, filterName, filterValue, sortingField, sortingDirection, pageSize, pageNumber);
	}

	/**
	 * Method to verify a job vacancy
	 */
	@GetMapping("/job-vacancies/verify/{jobVacancyId}")
	public String verify(RedirectAttributes redirectAttributes, @PathVariable("jobVacancyId") long jobVacancyId,
			@RequestParam(name="languageParam", required=false) String languageCode,
			@RequestParam(name="filterName", required=false) String filterName,
			@RequestParam(name="filterValue", required=false) String filterValue,
			@RequestParam(name="sortingField", required=false) String sortingField,
			@RequestParam(name="sortingDirection", required=false) String sortingDirection,
			@RequestParam(name="pageSize", required=false) String pageSize,
			@RequestParam(name="pageNumber", required=false) String pageNumber) {

		JobVacancy jobVacancy = jobVacancyService.findByIdNotOptional(jobVacancyId);
		if(!jobVacancy.isVerifiable()) {
			final String errorMsg = I18nUtils.getInternationalizedMessage(languageCode, "verifyJobVacancy.notVerifiable", null);
			redirectAttributes.addFlashAttribute("errorMsg", errorMsg);

			return ControllerNavigationUtils.getNextRedirectWithTable("/job-vacancies/index", languageCode, filterName, filterValue, sortingField, sortingDirection, pageSize, pageNumber);
		}

		jobVacancy.setStatus(JobVacancyStatus.APPROVED);
		jobVacancy.setHighlighted(Boolean.TRUE);
		jobVacancy = jobVacancyService.saveAndFlush(jobVacancy);

		final String successMsg = I18nUtils.getInternationalizedMessage(languageCode, "verifyJobVacancy.successMsg", null);
		redirectAttributes.addFlashAttribute("successMsg", successMsg);

		return ControllerNavigationUtils.getNextRedirectWithTable("/job-vacancies/index", languageCode, filterName, filterValue, sortingField, sortingDirection, pageSize, pageNumber);
	}

	/**
	 * Agregamos al Model la lista de Categories: De esta forma nos evitamos
	 * agregarlos en los metodos crear y editar.
	 */
	@ModelAttribute
	public void setGenerics(Model model, Authentication authentication) {
		final List<JobCategory> jobCategories = jobCategoryService.findAll();
		model.addAttribute("jobCategories", jobCategories);

		final List<JobCompany> jobCompanies = jobCompanyService.findAll();
		model.addAttribute("jobCompanies", jobCompanies);

		final Set<Long> authUserJobVacancyIds;
		if(authentication != null) {
			final String email = authentication.getName();
			final AuthUser authUser = authUserService.findByEmail(email);
			authUserJobVacancyIds = authUser.getJobVacancyIds();
		} else {
			authUserJobVacancyIds = null;
		}
		model.addAttribute("authUserJobVacancyIds", authUserJobVacancyIds);
	}

	private static JobCompanyDTO setSelectedJobCompanyLogoForJobVacancyForm(JobCompanyDTO jobCompanyDTO, final String jobCompanyLogoUrlParam, final boolean forceNoLogoIfBlankUrlParam) {
		if(LogicalUtils.isNotNullNorEmptyString(jobCompanyLogoUrlParam)) {
			final Long jobCompanyLogoId = Long.valueOf(jobCompanyLogoUrlParam);

			if(!Constants.NO_SELECTED_LOGO_ID.equals(jobCompanyLogoId)) {
				final JobCompanyLogoService jobCompanyLogoService = BeanFactoryUtils.getBean(JobCompanyLogoService.class);
				final JobCompanyLogo jobCompanyLogo = jobCompanyLogoService.findByIdNotOptional(jobCompanyLogoId);
				if(jobCompanyLogo != null) {
					final String selectedLogoFilePath = jobCompanyLogo.getFilePath();
					jobCompanyDTO = JobCompanyDTO.getNewInstance(jobCompanyDTO, Boolean.TRUE, jobCompanyLogoId, selectedLogoFilePath);
				} else {
					jobCompanyDTO = JobCompanyDTO.getNewInstance(jobCompanyDTO, Boolean.FALSE, Constants.NO_SELECTED_LOGO_ID, Constants.NO_SELECTED_LOGO_FILE_PATH);
				}
			} else {
				jobCompanyDTO = JobCompanyDTO.getNewInstance(jobCompanyDTO, Boolean.FALSE, Constants.NO_SELECTED_LOGO_ID, Constants.NO_SELECTED_LOGO_FILE_PATH);
			}
		} else if(forceNoLogoIfBlankUrlParam) {
			jobCompanyDTO = JobCompanyDTO.getNewInstance(jobCompanyDTO, Boolean.FALSE, Constants.NO_SELECTED_LOGO_ID, Constants.NO_SELECTED_LOGO_FILE_PATH);
		} else {
			jobCompanyDTO = JobCompanyDTO.getNewInstance(jobCompanyDTO, Boolean.FALSE, jobCompanyDTO.selectedLogoId(), null);
		}

		return jobCompanyDTO;
	}

	@Override
	public String getPaginationUrl() {
		return "/job-vacancies/index";
	}
}
