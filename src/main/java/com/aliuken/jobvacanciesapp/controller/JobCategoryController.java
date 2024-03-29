package com.aliuken.jobvacanciesapp.controller;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.aliuken.jobvacanciesapp.model.dto.AbstractEntityPageWithExceptionDTO;
import com.aliuken.jobvacanciesapp.model.dto.JobCategoryDTO;
import com.aliuken.jobvacanciesapp.model.dto.TableSearchDTO;
import com.aliuken.jobvacanciesapp.model.dto.converter.JobCategoryConverter;
import com.aliuken.jobvacanciesapp.model.entity.JobCategory;
import com.aliuken.jobvacanciesapp.model.entity.JobVacancy;
import com.aliuken.jobvacanciesapp.service.JobCategoryService;
import com.aliuken.jobvacanciesapp.service.JobRequestService;
import com.aliuken.jobvacanciesapp.service.JobVacancyService;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import com.aliuken.jobvacanciesapp.util.javase.ThrowableUtils;
import com.aliuken.jobvacanciesapp.util.web.MvcUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class JobCategoryController {

	@Autowired
	private JobCategoryService jobCategoryService;

	@Autowired
	private JobVacancyService jobVacancyService;

	@Autowired
	private JobRequestService jobRequestService;

	/**
	 * Method to show the list of job categories with pagination
	 */
	@GetMapping("/job-categories/index")
	public String index(Model model, Pageable pageable,
			@Validated TableSearchDTO tableSearchDTO, BindingResult bindingResult) {
		final String operation = "GET /job-categories/index";

		try {
			if(tableSearchDTO == null || tableSearchDTO.hasEmptyAttribute()) {
				if(log.isDebugEnabled()) {
					String tableSearchDTOString = Objects.toString(tableSearchDTO);
					log.debug(StringUtils.getStringJoined("Some table search parameters were empty: ", tableSearchDTOString));
				}

				final Page<JobCategory> jobCategories = Page.empty();
				model.addAttribute("jobCategories", jobCategories);

				return MvcUtils.getNextViewWithTable("jobCategory/listJobCategories.html", model, operation, tableSearchDTO, false);
			}

			if(bindingResult.hasErrors()) {
				final String errors = bindingResult.getAllErrors().toString();

				if(log.isErrorEnabled()) {
					log.error(StringUtils.getStringJoined("Some errors were detected when binding some parameters to a model object: ", errors));
				}

				final Page<JobCategory> jobCategories = Page.empty();
				model.addAttribute("jobCategories", jobCategories);
				model.addAttribute("org.springframework.validation.BindingResult.tableSearchDTO", bindingResult);

				return MvcUtils.getNextViewWithTable("jobCategory/listJobCategories.html", model, operation, tableSearchDTO, true);
			}

			final AbstractEntityPageWithExceptionDTO<JobCategory> pageWithExceptionDTO = jobCategoryService.getEntityPage(tableSearchDTO, pageable);
			final Page<JobCategory> jobCategories = pageWithExceptionDTO.page();
			final Exception exception = pageWithExceptionDTO.exception();

			model.addAttribute("jobCategories", jobCategories);

			if(exception != null) {
				final String rootCauseMessage = ThrowableUtils.getRootCauseMessage(exception);
				model.addAttribute("errorMsg", rootCauseMessage);
			}

			return MvcUtils.getNextViewWithTable("jobCategory/listJobCategories.html", model, operation, tableSearchDTO, true);

		} catch(final Exception exception) {
			if(log.isErrorEnabled()) {
				final String stackTrace = ThrowableUtils.getStackTrace(exception);
				log.error(StringUtils.getStringJoined("An exception happened when executing a controller method. Exception: ", stackTrace));
			}

			final Page<JobCategory> jobCategories = Page.empty();
			model.addAttribute("jobCategories", jobCategories);

			final String rootCauseMessage = ThrowableUtils.getRootCauseMessage(exception);
			model.addAttribute("errorMsg", rootCauseMessage);

			return MvcUtils.getNextViewWithTable("jobCategory/listJobCategories.html", model, operation, tableSearchDTO, true);
		}
	}

	/**
	 * Method to show the detail of a job category
	 */
	@GetMapping("/job-categories/view/{jobCategoryId}")
	public String view(Model model, @PathVariable("jobCategoryId") long jobCategoryId,
			@RequestParam(name="languageParam", required=false) String languageCode) {
		final String operation = "GET /job-categories/view/{jobCategoryId}";

		final JobCategory jobCategory = jobCategoryService.findByIdNotOptional(jobCategoryId);
		model.addAttribute("jobCategory", jobCategory);

		return MvcUtils.getNextView("jobCategory/jobCategoryDetail.html", model, operation, languageCode);
	}

	/**
	 * Method to show the creation form of a job category
	 */
	@GetMapping("/job-categories/create")
	public String create(HttpServletRequest httpServletRequest, Model model,
			@RequestParam(name="languageParam", required=false) String languageCode) {
		final String operation = "GET /job-categories/create";

		final Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(httpServletRequest);

		JobCategoryDTO jobCategoryDTO;
		if(inputFlashMap != null) {
			jobCategoryDTO = (JobCategoryDTO) inputFlashMap.get("jobCategoryDTO");
			if(jobCategoryDTO == null) {
				jobCategoryDTO = JobCategoryDTO.getNewInstance();
			}
		} else {
			jobCategoryDTO = JobCategoryDTO.getNewInstance();
		}

		model.addAttribute("jobCategoryDTO", jobCategoryDTO);

		return MvcUtils.getNextView("jobCategory/jobCategoryForm.html", model, operation, languageCode);
	}

	/**
	 * Method to show the edition form of a job category
	 */
	@GetMapping("/job-categories/edit/{jobCategoryId}")
	public String edit(HttpServletRequest httpServletRequest, Model model, @PathVariable("jobCategoryId") long jobCategoryId,
			@RequestParam(name="languageParam", required=false) String languageCode) {
		final String operation = "GET /job-categories/edit/{jobCategoryId}";

		final Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(httpServletRequest);

		JobCategoryDTO jobCategoryDTO;
		if(inputFlashMap != null) {
			jobCategoryDTO = (JobCategoryDTO) inputFlashMap.get("jobCategoryDTO");
		} else {
			jobCategoryDTO = null;
		}

		if(jobCategoryDTO == null) {
			final JobCategory jobCategory = jobCategoryService.findByIdNotOptional(jobCategoryId);
			jobCategoryDTO = JobCategoryConverter.getInstance().convertEntityElement(jobCategory);
		}

		model.addAttribute("jobCategoryDTO", jobCategoryDTO);

		return MvcUtils.getNextView("jobCategory/jobCategoryForm.html", model, operation, languageCode);
	}

	/**
	 * Method to save a job category in the database
	 */
	@PostMapping("/job-categories/save")
	public String save(RedirectAttributes redirectAttributes,
			@Validated JobCategoryDTO jobCategoryDTO, BindingResult bindingResult,
			@RequestParam(name="id", required=false) Long id, @RequestParam(name="languageParam", required=false) String languageCode) {
		try {
			if(bindingResult.hasErrors()) {
				final String errors = bindingResult.getAllErrors().toString();

				if(log.isErrorEnabled()) {
					log.error(StringUtils.getStringJoined("Some errors were detected when binding some parameters to a model object: ", errors));
				}

				redirectAttributes.addFlashAttribute("jobCategoryDTO", jobCategoryDTO);
				redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.jobCategoryDTO", bindingResult);

				if(id == null) {
					return MvcUtils.getNextRedirect("/job-categories/create", languageCode);
				} else {
					return MvcUtils.getNextRedirect("/job-categories/edit/" + id, languageCode);
				}
			}

			final String name = jobCategoryDTO.name();
			final String description = jobCategoryDTO.description();
			final Set<Long> jobVacancyIds = jobCategoryDTO.jobVacancyIds();

			JobCategory jobCategory = jobCategoryService.findByIdOrNewEntity(id);
			jobCategory.setName(name);
			jobCategory.setDescription(description);

			final Set<JobVacancy> jobVacancies = new LinkedHashSet<>();
			if(jobVacancyIds != null) {
				for(final Long jobVacancyId : jobVacancyIds) {
					final JobVacancy jobVacancy = jobVacancyService.findByIdNotOptional(jobVacancyId);
					jobVacancies.add(jobVacancy);
				}
			}

			jobCategory.setJobVacancies(jobVacancies);

			jobCategory = jobCategoryService.saveAndFlush(jobCategory);

			final String successMsg = StringUtils.getInternationalizedMessage(languageCode, "saveJobCategory.successMsg", null);
			redirectAttributes.addFlashAttribute("successMsg", successMsg);

			return MvcUtils.getNextRedirect("/job-categories/view/" + jobCategory.getId(), languageCode);

		} catch(final Exception exception) {
			if(log.isErrorEnabled()) {
				final String stackTrace = ThrowableUtils.getStackTrace(exception);
				log.error(StringUtils.getStringJoined("An exception happened when executing a controller method. Exception: ", stackTrace));
			}

			final String rootCauseMessage = ThrowableUtils.getRootCauseMessage(exception);

			redirectAttributes.addFlashAttribute("jobCategoryDTO", jobCategoryDTO);
			redirectAttributes.addFlashAttribute("errorMsg", rootCauseMessage);

			if(id == null) {
				return MvcUtils.getNextRedirect("/job-categories/create", languageCode);
			} else {
				return MvcUtils.getNextRedirect("/job-categories/edit/" + id, languageCode);
			}
		}
	}

	/**
	 * Method to delete a job category
	 */
	@GetMapping("/job-categories/delete/{jobCategoryId}")
	public String delete(RedirectAttributes redirectAttributes, @PathVariable("jobCategoryId") long jobCategoryId,
			@RequestParam(name="languageParam", required=false) String languageCode, @RequestParam(name="tableFieldCode", required=false) String tableFieldCode, @RequestParam(name="tableFieldValue", required=false) String tableFieldValue, @RequestParam(name="tableOrderCode", required=false) String tableOrderCode, @RequestParam(name="pageSize", required=false) String pageSize, @RequestParam(name="pageNumber", required=false) String pageNumber) {
		final JobCategory jobCategory = jobCategoryService.findByIdNotOptional(jobCategoryId);

		final Set<Long> jobVacancyIds = jobCategory.getJobVacancyIds();
		for(final Long jobVacancyId : jobVacancyIds) {
			final JobVacancy jobVacancy = jobVacancyService.findByIdNotOptional(jobVacancyId);

			final Set<Long> jobRequestIds = jobVacancy.getJobRequestIds();
			for(final Long jobRequestId : jobRequestIds) {
				jobRequestService.deleteByIdAndFlush(jobRequestId);
			}

			jobVacancyService.deleteByIdAndFlush(jobVacancyId);
		}

		jobCategoryService.deleteByIdAndFlush(jobCategoryId);

		final String successMsg = StringUtils.getInternationalizedMessage(languageCode, "deleteJobCategory.successMsg", null);
		redirectAttributes.addFlashAttribute("successMsg", successMsg);

		return MvcUtils.getNextRedirectWithTable("/job-categories/index", languageCode, tableFieldCode, tableFieldValue, tableOrderCode, pageSize, pageNumber);
	}

}
