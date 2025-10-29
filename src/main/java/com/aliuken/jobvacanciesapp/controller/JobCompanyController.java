package com.aliuken.jobvacanciesapp.controller;

import com.aliuken.jobvacanciesapp.Constants;
import com.aliuken.jobvacanciesapp.config.ConfigPropertiesBean;
import com.aliuken.jobvacanciesapp.controller.superclass.AbstractEntityControllerWithoutPredefinedFilter;
import com.aliuken.jobvacanciesapp.enumtype.FileType;
import com.aliuken.jobvacanciesapp.model.dto.*;
import com.aliuken.jobvacanciesapp.model.dto.converter.JobCompanyConverter;
import com.aliuken.jobvacanciesapp.model.entity.JobCompany;
import com.aliuken.jobvacanciesapp.model.entity.JobCompanyLogo;
import com.aliuken.jobvacanciesapp.model.entity.JobVacancy;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.PageEntityEnum;
import com.aliuken.jobvacanciesapp.service.JobCompanyLogoService;
import com.aliuken.jobvacanciesapp.service.JobCompanyService;
import com.aliuken.jobvacanciesapp.service.JobRequestService;
import com.aliuken.jobvacanciesapp.service.JobVacancyService;
import com.aliuken.jobvacanciesapp.util.i18n.I18nUtils;
import com.aliuken.jobvacanciesapp.util.javase.LogicalUtils;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import com.aliuken.jobvacanciesapp.util.javase.ThrowableUtils;
import com.aliuken.jobvacanciesapp.util.persistence.file.FileUtils;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Controller
@Slf4j
public class JobCompanyController extends AbstractEntityControllerWithoutPredefinedFilter<JobCompany> {

	@Autowired
	private ConfigPropertiesBean configPropertiesBean;

	@Autowired
	private JobCompanyService jobCompanyService;

	@Autowired
	private JobCompanyLogoService jobCompanyLogoService;

	@Autowired
	private JobVacancyService jobVacancyService;

	@Autowired
	private JobRequestService jobRequestService;

	private static String jobCompanyLogosPath;
	private static boolean useAjaxToRefreshJobCompanyLogos;

	@PostConstruct
	private void postConstruct() {
		jobCompanyLogosPath = configPropertiesBean.getJobCompanyLogosPath();
		useAjaxToRefreshJobCompanyLogos = configPropertiesBean.isUseAjaxToRefreshJobCompanyLogos();
	}

	/**
	 * Method to show the list of companies with pagination
	 */
	@GetMapping("/job-companies/index")
	public String index(Model model, Pageable pageable,
			@Validated TableSearchDTO tableSearchDTO, BindingResult bindingResult) {
		final String operation = "GET /job-companies/index";

		try {
			if(tableSearchDTO == null || !tableSearchDTO.hasAllParameters()) {
				if(log.isDebugEnabled()) {
					final String tableSearchDtoString = Objects.toString(tableSearchDTO);
					log.debug(StringUtils.getStringJoined("Some table search parameters were empty: ", tableSearchDtoString));
				}

				final Page<JobCompany> jobCompanies = Page.empty();
				model.addAttribute("jobCompanies", jobCompanies);
				model.addAttribute("paginationUrl", this.getPaginationUrl());
				model.addAttribute("exportToPdfUrl", EXPORT_TO_PDF_DISABLED_VALUE);

				return ControllerNavigationUtils.getNextViewWithTable("jobCompany/listJobCompanies.html", model, operation, tableSearchDTO, false);
			}

			final String firstBindingErrorString = ControllerValidationUtils.getFirstBindingErrorString(bindingResult);
			if(firstBindingErrorString != null) {
				final Page<JobCompany> jobCompanies = Page.empty();
				model.addAttribute("jobCompanies", jobCompanies);
				model.addAttribute("paginationUrl", this.getPaginationUrl());
				model.addAttribute("exportToPdfUrl", EXPORT_TO_PDF_DISABLED_VALUE);
				//model.addAttribute("org.springframework.validation.BindingResult.tableSearchDTO", bindingResult);
				model.addAttribute("errorMsg", firstBindingErrorString);

				return ControllerNavigationUtils.getNextViewWithTable("jobCompany/listJobCompanies.html", model, operation, tableSearchDTO, true);
			}

			final AbstractEntityPageWithExceptionDTO<JobCompany> pageWithExceptionDTO = jobCompanyService.getEntityPage(tableSearchDTO, pageable);
			final Page<JobCompany> jobCompanies = pageWithExceptionDTO.page();
			final Throwable throwable = pageWithExceptionDTO.throwable();

			model.addAttribute("jobCompanies", jobCompanies);
			model.addAttribute("paginationUrl", this.getPaginationUrl());
			model.addAttribute("exportToPdfUrl", this.getExportToPdfUrl());

			if(throwable != null) {
				final String rootCauseMessage = ThrowableUtils.getRootCauseMessage(throwable);
				model.addAttribute("errorMsg", rootCauseMessage);
			}

			return ControllerNavigationUtils.getNextViewWithTable("jobCompany/listJobCompanies.html", model, operation, tableSearchDTO, true);

		} catch(final Exception exception) {
			if(log.isErrorEnabled()) {
				final String stackTrace = ThrowableUtils.getStackTrace(exception);
				log.error(StringUtils.getStringJoined("An exception happened when executing a controller method. Exception: ", stackTrace));
			}

			final Page<JobCompany> jobCompanies = Page.empty();
			model.addAttribute("jobCompanies", jobCompanies);
			model.addAttribute("paginationUrl", this.getPaginationUrl());
			model.addAttribute("exportToPdfUrl", EXPORT_TO_PDF_DISABLED_VALUE);

			final String rootCauseMessage = ThrowableUtils.getRootCauseMessage(exception);
			model.addAttribute("errorMsg", rootCauseMessage);

			return ControllerNavigationUtils.getNextViewWithTable("jobCompany/listJobCompanies.html", model, operation, tableSearchDTO, true);
		}
	}

	/**
	 * Method to export the list of companies with pagination to pdf
	 */
	@GetMapping("/job-companies/index/export-to-pdf")
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
		final byte[] pdfByteArray = this.storeAndDownloadPdf(predefinedFilterDTO, tableSearchDTO, model, PageEntityEnum.JOB_COMPANY, httpServletRequest, httpServletResponse);
		return pdfByteArray;
	}

	/**
	 * Method to show the detail of a company
	 */
	@GetMapping("/job-companies/view/{jobCompanyId}")
	public String view(Model model, @PathVariable("jobCompanyId") long jobCompanyId,
			@RequestParam(name="languageParam", required=false) String languageCode) {
		final String operation = "GET /job-companies/view/{jobCompanyId}";

		final JobCompany jobCompany = jobCompanyService.findByIdNotOptional(jobCompanyId);
		model.addAttribute("jobCompany", jobCompany);

		return ControllerNavigationUtils.getNextView("jobCompany/jobCompanyDetail.html", model, operation, languageCode);
	}

	/**
	 * Method to show the creation form of a company
	 */
	@GetMapping("/job-companies/create")
	public String create(HttpServletRequest httpServletRequest, Model model,
			@RequestParam(name="languageParam", required=false) String languageCode, @RequestParam(name="jobCompanyLogo", required=false) String jobCompanyLogoUrlParam) {
		final String operation = "GET /job-companies/create";

		final Map<String, ?> inputFlashMap = ControllerServletUtils.getInputFlashMap(httpServletRequest);

		JobCompanyDTO jobCompanyDTO;
		if(inputFlashMap != null) {
			jobCompanyDTO = (JobCompanyDTO) inputFlashMap.get("jobCompanyDTO");
			if(jobCompanyDTO == null) {
				jobCompanyDTO = JobCompanyDTO.getNewInstance();
			}
		} else {
			jobCompanyDTO = JobCompanyDTO.getNewInstance();
		}

		if(!useAjaxToRefreshJobCompanyLogos) {
			jobCompanyDTO = JobCompanyController.setSelectedJobCompanyLogoForJobCompanyForm(jobCompanyDTO, jobCompanyLogoUrlParam, true);
		}

		model.addAttribute("jobCompanyDTO", jobCompanyDTO);
		model.addAttribute("jobCompanyLogo", jobCompanyDTO.selectedLogoId());
		model.addAttribute("useAjaxToRefreshJobCompanyLogos", useAjaxToRefreshJobCompanyLogos);

		return ControllerNavigationUtils.getNextView("jobCompany/jobCompanyForm.html", model, operation, languageCode);
	}

	/**
	 * Method to show the edition form of a company
	 */
	@GetMapping("/job-companies/edit/{jobCompanyId}")
	public String edit(HttpServletRequest httpServletRequest, Model model, @PathVariable("jobCompanyId") long jobCompanyId,
			@RequestParam(name="languageParam", required=false) String languageCode, @RequestParam(name="jobCompanyLogo", required=false) String jobCompanyLogoUrlParam) {
		final String operation = "GET /job-companies/edit/{jobCompanyId}";

		final Map<String, ?> inputFlashMap = ControllerServletUtils.getInputFlashMap(httpServletRequest);

		JobCompanyDTO jobCompanyDTO;
		if(inputFlashMap != null) {
			jobCompanyDTO = (JobCompanyDTO) inputFlashMap.get("jobCompanyDTO");
		} else {
			jobCompanyDTO = null;
		}

		if(jobCompanyDTO == null) {
			final JobCompany jobCompany = jobCompanyService.findByIdNotOptional(jobCompanyId);
			jobCompanyDTO = JobCompanyConverter.getInstance().convertEntityElement(jobCompany);
		}

		if(!useAjaxToRefreshJobCompanyLogos) {
			jobCompanyDTO = JobCompanyController.setSelectedJobCompanyLogoForJobCompanyForm(jobCompanyDTO, jobCompanyLogoUrlParam, false);
		}

		model.addAttribute("jobCompanyDTO", jobCompanyDTO);
		model.addAttribute("jobCompanyLogo", jobCompanyDTO.selectedLogoId());
		model.addAttribute("useAjaxToRefreshJobCompanyLogos", useAjaxToRefreshJobCompanyLogos);

		return ControllerNavigationUtils.getNextView("jobCompany/jobCompanyForm.html", model, operation, languageCode);
	}

	/**
	 * Method to refresh the logo of a company
	 */
	@GetMapping("/job-companies/refresh-logo")
	public String refreshLogo(Model model,
			@RequestParam(name="jobCompanyId", required=false) Long jobCompanyId, @RequestParam(name="jobCompanyLogo", required=false) String jobCompanyLogoUrlParam) {
		JobCompanyDTO jobCompanyDTO;
		if(jobCompanyId != null) {
			final JobCompany jobCompany = jobCompanyService.findByIdNotOptional(jobCompanyId);
			jobCompanyDTO = JobCompanyConverter.getInstance().convertEntityElement(jobCompany);
		} else {
			jobCompanyDTO = JobCompanyDTO.getNewInstance();
		}

		jobCompanyDTO = JobCompanyController.setSelectedJobCompanyLogoForJobCompanyForm(jobCompanyDTO, jobCompanyLogoUrlParam, false);

		model.addAttribute("jobCompanyDTO", jobCompanyDTO);
		model.addAttribute("isJobCompanyForm", true);

		return "fragments/optional/jobCompanyLogoFragment :: jobCompanyLogoFragment";
	}

	/**
	 * Method to save a company in the database
	 */
	@PostMapping("/job-companies/save")
	public String save(RedirectAttributes redirectAttributes,
			@Validated JobCompanyDTO jobCompanyDTO, BindingResult bindingResult,
			@RequestParam(name="jobCompanySelectedLogoFile", required=false) MultipartFile multipartFile, @RequestParam(name="id", required=false) Long id, @RequestParam(name="languageParam", required=false) String languageCode) {
		final Long selectedJobCompanyLogoId = (jobCompanyDTO != null) ? jobCompanyDTO.selectedLogoId() : null;

		try {
			final String firstBindingErrorString = ControllerValidationUtils.getFirstBindingErrorString(bindingResult);
			if(firstBindingErrorString != null) {
				redirectAttributes.addFlashAttribute("jobCompanyLogo", selectedJobCompanyLogoId);
				redirectAttributes.addFlashAttribute("useAjaxToRefreshJobCompanyLogos", useAjaxToRefreshJobCompanyLogos);
				redirectAttributes.addFlashAttribute("jobCompanyDTO", jobCompanyDTO);
				//redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.jobCompanyDTO", bindingResult);
				redirectAttributes.addFlashAttribute("errorMsg", firstBindingErrorString);

				if(id == null) {
					return ControllerNavigationUtils.getNextRedirect("/job-companies/create", languageCode);
				} else {
					return ControllerNavigationUtils.getNextRedirect("/job-companies/edit/" + id, languageCode);
				}
			}

			final String name = jobCompanyDTO.name();
			final String description = jobCompanyDTO.description();
			final Set<JobCompanyLogoDTO> jobCompanyLogoDTOs = jobCompanyDTO.jobCompanyLogos();

			final List<String> savedFileNameList;
			if(id != null) {
				final String jobCompanyId = Long.toString(id);
				final String pathWithJobCompanyId = StringUtils.getStringJoined(jobCompanyLogosPath, jobCompanyId, "/");
				savedFileNameList = FileUtils.uploadAndOptionallyUnzipFile(multipartFile, pathWithJobCompanyId, FileType.COMPANY_LOGO);
			} else {
				savedFileNameList = FileUtils.uploadAndOptionallyUnzipFile(multipartFile, jobCompanyLogosPath, FileType.COMPANY_LOGO);
			}

			final String selectedSavedFileName = (LogicalUtils.isNotNullNorEmpty(savedFileNameList)) ? savedFileNameList.get(0) : null;

			JobCompany jobCompany = jobCompanyService.findByIdOrNewEntity(id);
			jobCompany.setName(name);
			jobCompany.setDescription(description);

			if(selectedJobCompanyLogoId != null) {
				if(!Constants.NO_SELECTED_LOGO_ID.equals(selectedJobCompanyLogoId)) {
					final JobCompanyLogo selectedLogo = jobCompanyLogoService.findByIdNotOptional(selectedJobCompanyLogoId);
					final String selectedLogoFileName = selectedLogo.getFileName();
					jobCompany.setSelectedLogoFileName(selectedLogoFileName);
				} else {
					jobCompany.setSelectedLogoFileName(null);
				}
			} else {
				jobCompany.setSelectedLogoFileName(selectedSavedFileName);
			}

			final Set<JobCompanyLogo> jobCompanyLogos = new LinkedHashSet<>();
			if(jobCompanyLogoDTOs != null) {
				for(final JobCompanyLogoDTO jobCompanyDTOLogo : jobCompanyLogoDTOs) {
					final Long jobCompanyLogoId = jobCompanyDTOLogo.id();

					final JobCompanyLogo jobCompanyLogo = jobCompanyLogoService.findByIdOrNewEntity(jobCompanyLogoId);
					jobCompanyLogo.setJobCompany(jobCompany);
					jobCompanyLogo.setFileName(jobCompanyDTOLogo.fileName());

					jobCompanyLogos.add(jobCompanyLogo);
				}
			}

			if(savedFileNameList != null) {
				for(final String savedFileName : savedFileNameList) {
					if(savedFileName != null) {
						JobCompanyLogo jobCompanyLogo = new JobCompanyLogo();
						jobCompanyLogo.setJobCompany(jobCompany);
						jobCompanyLogo.setFileName(savedFileName);

						jobCompanyLogos.add(jobCompanyLogo);
					}
				}
			}

			jobCompany.setJobCompanyLogos(jobCompanyLogos);

			if(id == null) {
				jobCompany = jobCompanyService.saveAndFlush(jobCompany);

				for(JobCompanyLogo jobCompanyLogo : jobCompanyLogos) {
					jobCompanyLogo = jobCompanyLogoService.saveAndFlush(jobCompanyLogo);
				}
			} else {
				for(JobCompanyLogo jobCompanyLogo : jobCompanyLogos) {
					jobCompanyLogo = jobCompanyLogoService.saveAndFlush(jobCompanyLogo);
				}

				jobCompany = jobCompanyService.saveAndFlush(jobCompany);
			}

			if(savedFileNameList != null) {
				if(id == null) {
					id = jobCompany.getId();
					final String jobCompanyLogoFolder = jobCompanyLogosPath + id;
					for(final String savedFileName : savedFileNameList) {
						if(savedFileName != null) {
							try {
								Files.createDirectories(Path.of(jobCompanyLogoFolder));
								final Path originFilePath = Path.of(jobCompanyLogosPath, savedFileName);
								final Path destinationFilePath = Path.of(jobCompanyLogoFolder, savedFileName);
								Files.move(originFilePath, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
							} catch(final IOException exception) {
								if(log.isErrorEnabled()) {
									final String stackTrace = ThrowableUtils.getStackTrace(exception);
									log.error(StringUtils.getStringJoined("An exception happened when trying to save the logo ", savedFileName, " in the folder ", jobCompanyLogoFolder, ". Exception: ", stackTrace));
								}

								final String rootCauseMessage = ThrowableUtils.getRootCauseMessage(exception);

								redirectAttributes.addFlashAttribute("jobCompanyLogo", selectedJobCompanyLogoId);
								redirectAttributes.addFlashAttribute("useAjaxToRefreshJobCompanyLogos", useAjaxToRefreshJobCompanyLogos);
								redirectAttributes.addFlashAttribute("jobCompanyDTO", jobCompanyDTO);
								redirectAttributes.addFlashAttribute("errorMsg", rootCauseMessage);

								if(id == null) {
									return ControllerNavigationUtils.getNextRedirect("/job-companies/create", languageCode);
								} else {
									return ControllerNavigationUtils.getNextRedirect("/job-companies/edit/" + id, languageCode);
								}
							}
						}
					}
				}
			}

			final String successMsg = I18nUtils.getInternationalizedMessage(languageCode, "saveJobCompany.successMsg", null);
			redirectAttributes.addFlashAttribute("successMsg", successMsg);

			return ControllerNavigationUtils.getNextRedirect("/job-companies/view/" + jobCompany.getId(), languageCode);

		} catch(final Exception exception) {
			if(log.isErrorEnabled()) {
				final String stackTrace = ThrowableUtils.getStackTrace(exception);
				log.error(StringUtils.getStringJoined("An exception happened when executing a controller method. Exception: ", stackTrace));
			}

			final String rootCauseMessage = ThrowableUtils.getRootCauseMessage(exception);

			redirectAttributes.addFlashAttribute("jobCompanyLogo", selectedJobCompanyLogoId);
			redirectAttributes.addFlashAttribute("useAjaxToRefreshJobCompanyLogos", useAjaxToRefreshJobCompanyLogos);
			redirectAttributes.addFlashAttribute("jobCompanyDTO", jobCompanyDTO);
			redirectAttributes.addFlashAttribute("errorMsg", rootCauseMessage);

			if(id == null) {
				return ControllerNavigationUtils.getNextRedirect("/job-companies/create", languageCode);
			} else {
				return ControllerNavigationUtils.getNextRedirect("/job-companies/edit/" + id, languageCode);
			}
		}
	}

	/**
	 * Method to delete a company
	 */
	@GetMapping("/job-companies/delete/{jobCompanyId}")
	public String delete(RedirectAttributes redirectAttributes, @PathVariable("jobCompanyId") long jobCompanyId,
			@RequestParam(name="languageParam", required=false) String languageCode,
			@RequestParam(name="filterName", required=false) String filterName,
			@RequestParam(name="filterValue", required=false) String filterValue,
			@RequestParam(name="sortingField", required=false) String sortingField,
			@RequestParam(name="sortingDirection", required=false) String sortingDirection,
			@RequestParam(name="pageSize", required=false) String pageSize,
			@RequestParam(name="pageNumber", required=false) String pageNumber) {

		final JobCompany jobCompany = jobCompanyService.findByIdNotOptional(jobCompanyId);

		final Set<JobVacancy> jobVacancies = jobCompany.getJobVacancies();
		for(final JobVacancy jobVacancy : jobVacancies) {
			final Set<Long> jobRequestIds = jobVacancy.getJobRequestIds();
			for(final Long jobRequestId : jobRequestIds) {
				jobRequestService.deleteByIdAndFlush(jobRequestId);
			}

			jobVacancyService.deleteByIdAndFlush(jobVacancy.getId());
		}

		final Set<Long> jobCompanyLogoIds = jobCompany.getJobCompanyLogoIds();
		for(final Long jobCompanyLogoId : jobCompanyLogoIds) {
			jobCompanyLogoService.deleteByIdAndFlush(jobCompanyLogoId);
		}

		jobCompanyService.deleteByIdAndFlush(jobCompanyId);

		final String jobCompanyIdString = Long.toString(jobCompanyId);
		final Path finalFilePath = Path.of(jobCompanyLogosPath, jobCompanyIdString);
		FileUtils.deletePathRecursively(finalFilePath);

		final String successMsg = I18nUtils.getInternationalizedMessage(languageCode, "deleteJobCompany.successMsg", null);
		redirectAttributes.addFlashAttribute("successMsg", successMsg);

		return ControllerNavigationUtils.getNextRedirectWithTable("/job-companies/index", languageCode, filterName, filterValue, sortingField, sortingDirection, pageSize, pageNumber);
	}

	private static JobCompanyDTO setSelectedJobCompanyLogoForJobCompanyForm(JobCompanyDTO jobCompanyDTO, final String jobCompanyLogoUrlParam, final boolean forceNoLogoIfBlankUrlParam) {
		if(LogicalUtils.isNotNullNorEmptyString(jobCompanyLogoUrlParam)) {
			final Long jobCompanyLogoId = Long.valueOf(jobCompanyLogoUrlParam);

			if(!Constants.NO_SELECTED_LOGO_ID.equals(jobCompanyLogoId)) {
				final JobCompanyLogoService jobCompanyLogoService = BeanFactoryUtils.getBean(JobCompanyLogoService.class);
				final JobCompanyLogo jobCompanyLogo = jobCompanyLogoService.findByIdNotOptional(jobCompanyLogoId);
				if(jobCompanyLogo != null) {
					final String selectedLogoFilePath = jobCompanyLogo.getFilePath();
					jobCompanyDTO = JobCompanyDTO.getNewInstance(jobCompanyDTO, Boolean.TRUE, jobCompanyLogoId, selectedLogoFilePath);
				} else {
					jobCompanyDTO = JobCompanyDTO.getNewInstance(jobCompanyDTO, Boolean.FALSE, null, null);
				}
			} else {
				jobCompanyDTO = JobCompanyDTO.getNewInstance(jobCompanyDTO, Boolean.TRUE, Constants.NO_SELECTED_LOGO_ID, Constants.NO_SELECTED_LOGO_FILE_PATH);
			}
		} else if(forceNoLogoIfBlankUrlParam) {
			jobCompanyDTO = JobCompanyDTO.getNewInstance(jobCompanyDTO, Boolean.FALSE, null, null);
		} else {
			jobCompanyDTO = JobCompanyDTO.getNewInstance(jobCompanyDTO, Boolean.FALSE, jobCompanyDTO.selectedLogoId(), null);
		}

		return jobCompanyDTO;
	}

	@Override
	public String getPaginationUrl() {
		return "/job-companies/index";
	}
}
