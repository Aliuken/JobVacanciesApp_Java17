package com.aliuken.jobvacanciesapp.model.dto;

import com.aliuken.jobvacanciesapp.enumtype.AnonymousAccessPermission;
import com.aliuken.jobvacanciesapp.enumtype.UserInterfaceFramework;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.ColorMode;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.Language;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.PdfDocumentPageFormat;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.TablePageSize;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.TableSortingDirection;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;

import java.io.Serializable;
import java.util.Objects;

public record ApplicationDefaultConfigDTO(
	//Non-overwritable properties
	String authUserCurriculumFilesPath,
	String authUserEntityQueryFilesPath,
	String jobCompanyLogosPath,
	Boolean useAjaxToRefreshJobCompanyLogos,
	Boolean useEntityManagerCache,
	Boolean useParallelStreams,
	Long signupConfirmationLinkExpirationHours,
	Long resetPasswordLinkExpirationHours,

	//Overwritable properties
	Language defaultLanguage,
	AnonymousAccessPermission defaultAnonymousAccessPermission,
	TableSortingDirection defaultInitialTableSortingDirection,
	TablePageSize defaultInitialTablePageSize,
	ColorMode defaultColorMode,
	UserInterfaceFramework defaultUserInterfaceFramework,
	PdfDocumentPageFormat defaultPdfDocumentPageFormat
) implements Serializable {

	private static final ApplicationDefaultConfigDTO NO_ARGS_INSTANCE = new ApplicationDefaultConfigDTO(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

	public ApplicationDefaultConfigDTO {

	}

	public static ApplicationDefaultConfigDTO getNewInstance() {
		return NO_ARGS_INSTANCE;
	}

	@Override
	public String toString() {
		final String useAjaxToRefreshJobCompanyLogosString = Objects.toString(useAjaxToRefreshJobCompanyLogos);
		final String useEntityManagerCacheString = Objects.toString(useEntityManagerCache);
		final String useParallelStreamsString = Objects.toString(useParallelStreams);
		final String signupConfirmationLinkExpirationHoursString = Objects.toString(signupConfirmationLinkExpirationHours);
		final String resetPasswordLinkExpirationHoursString = Objects.toString(resetPasswordLinkExpirationHours);
		final String defaultLanguageName = Objects.toString(defaultLanguage);
		final String defaultAnonymousAccessPermissionName = Objects.toString(defaultAnonymousAccessPermission);
		final String defaultInitialTableSortingDirectionName = Objects.toString(defaultInitialTableSortingDirection);
		final String defaultInitialTablePageSizeName = Objects.toString(defaultInitialTablePageSize);
		final String defaultColorModeName = Objects.toString(defaultColorMode);
		final String defaultUserInterfaceFrameworkName = Objects.toString(defaultUserInterfaceFramework);
		final String defaultPdfDocumentPageFormatName = Objects.toString(defaultPdfDocumentPageFormat);

		final String result = StringUtils.getStringJoined(
			"ApplicationDefaultConfigDTO [authUserCurriculumFilesPath=", authUserCurriculumFilesPath,
			", authUserEntityQueryFilesPath=", authUserEntityQueryFilesPath,
			", jobCompanyLogosPath=", jobCompanyLogosPath,
			", useAjaxToRefreshJobCompanyLogos=", useAjaxToRefreshJobCompanyLogosString,
			", useEntityManagerCache=", useEntityManagerCacheString,
			", useParallelStreams=", useParallelStreamsString,
			", signupConfirmationLinkExpirationHours=", signupConfirmationLinkExpirationHoursString,
			", resetPasswordLinkExpirationHours=", resetPasswordLinkExpirationHoursString,
			", defaultLanguage=", defaultLanguageName,
			", defaultAnonymousAccessPermission=", defaultAnonymousAccessPermissionName,
			", defaultInitialTableSortingDirection=", defaultInitialTableSortingDirectionName,
			", defaultInitialTablePageSize=", defaultInitialTablePageSizeName,
			", defaultColorMode=", defaultColorModeName,
			", defaultUserInterfaceFramework=", defaultUserInterfaceFrameworkName,
			", defaultPdfDocumentPageFormat=", defaultPdfDocumentPageFormatName, "]");
		return result;
	}
}
