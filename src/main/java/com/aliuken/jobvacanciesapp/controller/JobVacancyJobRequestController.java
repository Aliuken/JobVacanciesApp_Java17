package com.aliuken.jobvacanciesapp.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aliuken.jobvacanciesapp.Constants;
import com.aliuken.jobvacanciesapp.controller.superclass.AbstractEntityControllerWithPredefinedFilter;
import com.aliuken.jobvacanciesapp.model.dto.AbstractEntityPageWithExceptionDTO;
import com.aliuken.jobvacanciesapp.model.dto.TableSearchDTO;
import com.aliuken.jobvacanciesapp.model.entity.JobRequest;
import com.aliuken.jobvacanciesapp.model.entity.JobVacancy;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.PageEntityEnum;
import com.aliuken.jobvacanciesapp.service.JobRequestService;
import com.aliuken.jobvacanciesapp.service.JobVacancyService;
import com.aliuken.jobvacanciesapp.util.i18n.I18nUtils;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import com.aliuken.jobvacanciesapp.util.javase.ThrowableUtils;
import com.aliuken.jobvacanciesapp.util.web.MvcUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class JobVacancyJobRequestController extends AbstractEntityControllerWithPredefinedFilter<JobRequest> {

	@Autowired
	private JobVacancyService jobVacancyService;

	@Autowired
	private JobRequestService jobRequestService;

	/**
	 * Method to show the list of job requests of a job vacancy with pagination
	 */
	@GetMapping("/job-vacancies/job-requests/{jobVacancyId}")
	public String getJobRequests(Model model, Pageable pageable, @PathVariable("jobVacancyId") long jobVacancyId,
			@Validated TableSearchDTO tableSearchDTO, BindingResult bindingResult) {
		final String operation = "GET /job-vacancies/job-requests/{jobVacancyId}";

		final JobVacancy jobVacancy = jobVacancyService.findByIdNotOptional(jobVacancyId);
		final String jobVacancyName = (jobVacancy != null) ? jobVacancy.getName() : null;

		try {
			if(tableSearchDTO == null || !tableSearchDTO.hasAllParameters()) {
				if(log.isDebugEnabled()) {
					final String tableSearchDtoString = Objects.toString(tableSearchDTO);
					log.debug(StringUtils.getStringJoined("Some table search parameters were empty: ", tableSearchDtoString));
				}

				final Page<JobRequest> jobRequests = Page.empty();
				model.addAttribute("jobVacancyId", jobVacancyId);
				model.addAttribute("jobVacancyName", jobVacancyName);
				model.addAttribute("jobRequests", jobRequests);
				model.addAttribute("paginationUrl", this.getPaginationUrl(jobVacancyId));
				model.addAttribute("exportToPdfUrl", EXPORT_TO_PDF_DISABLED_VALUE);

				return MvcUtils.getNextViewWithTable("jobVacancy/jobVacancyJobRequests.html", model, operation, tableSearchDTO, false);
			}

			if(bindingResult.hasErrors()) {
				final String errors = bindingResult.getAllErrors().toString();

				if(log.isErrorEnabled()) {
					log.error(StringUtils.getStringJoined("Some errors were detected when binding some parameters to a model object: ", errors));
				}

				final Page<JobRequest> jobRequests = Page.empty();
				model.addAttribute("jobVacancyId", jobVacancyId);
				model.addAttribute("jobVacancyName", jobVacancyName);
				model.addAttribute("jobRequests", jobRequests);
				model.addAttribute("paginationUrl", this.getPaginationUrl(jobVacancyId));
				model.addAttribute("exportToPdfUrl", EXPORT_TO_PDF_DISABLED_VALUE);
				model.addAttribute("org.springframework.validation.BindingResult.tableSearchDTO", bindingResult);

				return MvcUtils.getNextViewWithTable("jobVacancy/jobVacancyJobRequests.html", model, operation, tableSearchDTO, true);
			}

			final AbstractEntityPageWithExceptionDTO<JobRequest> pageWithExceptionDTO = jobRequestService.getJobVacancyJobRequestsPage(jobVacancyId, tableSearchDTO, pageable);
			final Page<JobRequest> jobRequests = pageWithExceptionDTO.page();
			final Exception exception = pageWithExceptionDTO.exception();

			model.addAttribute("jobVacancyId", jobVacancyId);
			model.addAttribute("jobVacancyName", jobVacancyName);
			model.addAttribute("jobRequests", jobRequests);
			model.addAttribute("paginationUrl", this.getPaginationUrl(jobVacancyId));
			model.addAttribute("exportToPdfUrl", this.getExportToPdfUrl(jobVacancyId));

			if(exception != null) {
				final String rootCauseMessage = ThrowableUtils.getRootCauseMessage(exception);
				model.addAttribute("errorMsg", rootCauseMessage);
			}

			return MvcUtils.getNextViewWithTable("jobVacancy/jobVacancyJobRequests.html", model, operation, tableSearchDTO, true);

		} catch(final Exception exception) {
			if(log.isErrorEnabled()) {
				final String stackTrace = ThrowableUtils.getStackTrace(exception);
				log.error(StringUtils.getStringJoined("An exception happened when executing a controller method. Exception: ", stackTrace));
			}

			final Page<JobRequest> jobRequests = Page.empty();
			model.addAttribute("jobVacancyId", jobVacancyId);
			model.addAttribute("jobVacancyName", jobVacancyName);
			model.addAttribute("jobRequests", jobRequests);
			model.addAttribute("paginationUrl", this.getPaginationUrl(jobVacancyId));
			model.addAttribute("exportToPdfUrl", EXPORT_TO_PDF_DISABLED_VALUE);

			final String rootCauseMessage = ThrowableUtils.getRootCauseMessage(exception);
			model.addAttribute("errorMsg", rootCauseMessage);

			return MvcUtils.getNextViewWithTable("jobVacancy/jobVacancyJobRequests.html", model, operation, tableSearchDTO, true);
		}
	}

	/**
	 * Method to export the list of job vacancy requests with pagination to pdf
	 */
	@GetMapping("/job-vacancies/job-requests/{jobVacancyId}/export-to-pdf")
	@ResponseBody
	public byte[] exportToPdf(Model model, Pageable pageable, @PathVariable("jobVacancyId") long jobVacancyId,
			@Validated TableSearchDTO tableSearchDTO, BindingResult bindingResult,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			@RequestParam(name="languageParam", required=false) String languageCode,
			@RequestParam(name="tableFieldCode", required=false) String tableFieldCode,
			@RequestParam(name="tableFieldValue", required=false) String tableFieldValue,
			@RequestParam(name="tableOrderCode", required=false) String tableOrderCode,
			@RequestParam(name="pageSize", required=false) Integer pageSize,
			@RequestParam(name="pageNumber", required=false) Integer pageNumber) {

		final String jobVacancyIdString = String.valueOf(jobVacancyId);
		tableSearchDTO = new TableSearchDTO(languageCode, Constants.JOB_VACANCY_PREDEFINED_FILTER_NAME, jobVacancyIdString, tableFieldCode, tableFieldValue, tableOrderCode, pageSize, pageNumber);

		this.getJobRequests(model, pageable, jobVacancyId, tableSearchDTO, bindingResult);
		final byte[] pdfByteArray = this.storeAndDownloadPdf(tableSearchDTO, model, PageEntityEnum.JOB_REQUEST, httpServletRequest, httpServletResponse);
		return pdfByteArray;
	}

	/**
	 * Method to delete a job request of a job vacancy
	 */
	@GetMapping("/job-vacancies/job-requests/delete/{jobVacancyId}/{jobRequestId}")
	public String delete(RedirectAttributes redirectAttributes, @PathVariable("jobVacancyId") long jobVacancyId, @PathVariable("jobRequestId") long jobRequestId,
			@RequestParam(name="languageParam", required=false) String languageCode,
			@RequestParam(name="tableFieldCode", required=false) String tableFieldCode,
			@RequestParam(name="tableFieldValue", required=false) String tableFieldValue,
			@RequestParam(name="tableOrderCode", required=false) String tableOrderCode,
			@RequestParam(name="pageSize", required=false) String pageSize,
			@RequestParam(name="pageNumber", required=false) String pageNumber) {

		jobRequestService.deleteByIdAndFlush(jobRequestId);

		final String successMsg = I18nUtils.getInternationalizedMessage(languageCode, "deleteJobRequest.successMsg", null);
		redirectAttributes.addFlashAttribute("successMsg", successMsg);

		return MvcUtils.getNextRedirectWithTable("/job-vacancies/job-requests/" + jobVacancyId, languageCode, tableFieldCode, tableFieldValue, tableOrderCode, pageSize, pageNumber);
	}

	@Override
	public String getPaginationUrl(final String predefinedFilterIdString) {
		return StringUtils.getStringJoined("/job-vacancies/job-requests/", predefinedFilterIdString);
	}
}
