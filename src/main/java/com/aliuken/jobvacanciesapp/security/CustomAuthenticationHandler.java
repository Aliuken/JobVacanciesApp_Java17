package com.aliuken.jobvacanciesapp.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.aliuken.jobvacanciesapp.Constants;
import com.aliuken.jobvacanciesapp.annotation.ServiceMethod;
import com.aliuken.jobvacanciesapp.config.ConfigPropertiesBean;
import com.aliuken.jobvacanciesapp.enumtype.AnonymousAccessPermission;
import com.aliuken.jobvacanciesapp.enumtype.UserInterfaceFramework;
import com.aliuken.jobvacanciesapp.model.entity.AuthRole;
import com.aliuken.jobvacanciesapp.model.entity.AuthUser;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.ColorMode;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.Language;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.PdfDocumentPageFormat;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.TablePageSize;
import com.aliuken.jobvacanciesapp.service.AuthUserService;
import com.aliuken.jobvacanciesapp.util.javase.StringUtils;
import com.aliuken.jobvacanciesapp.util.javase.ThrowableUtils;
import com.aliuken.jobvacanciesapp.util.security.SessionUtils;
import com.aliuken.jobvacanciesapp.util.security.SpringSecurityUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Transactional
@Slf4j
public class CustomAuthenticationHandler extends SavedRequestAwareAuthenticationSuccessHandler implements LogoutHandler {

	private static final int LOGOUT_STATUS = HttpStatus.OK.value();
	private static final String LOGOUT_REDIRECT_LANGUAGE = "?languageParam=";
	private static final String LOGOUT_REDIRECT_ACCOUNT_DELETED = "&accountDeleted=";
	private static final String LOGOUT_RESTART_APP_WITH_DEFAULT_LANGUAGE = "&restartWithDefaultLanguage=";
	private static final String LOGOUT_RESTART_APP_WITH_DEFAULT_ANONYMOUS_ACCESS_PERMISSION = "&restartWithDefaultAnonymousAccessPermission=";
	private static final String LOGOUT_RESTART_APP_WITH_DEFAULT_INITIAL_PAGE_SIZE = "&restartWithDefaultInitialPageSize=";
	private static final String LOGOUT_RESTART_APP_WITH_DEFAULT_COLOR_MODE = "&restartWithDefaultColorMode=";
	private static final String LOGOUT_RESTART_APP_WITH_DEFAULT_USER_INTERFACE_FRAMEWORK = "&restartWithDefaultUserInterfaceFramework=";
	private static final String LOGOUT_RESTART_APP_WITH_DEFAULT_PDF_DOCUMENT_PAGE_FORMAT = "&restartWithDefaultPdfDocumentPageFormat=";

	@Autowired
	private AuthUserService authUserService;

	public CustomAuthenticationHandler() {
		this.setDefaultTargetUrl("/");
		this.setAlwaysUseDefaultTargetUrl(true);
	}

	@Override
	@ServiceMethod
	public void onAuthenticationSuccess(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final Authentication authentication) throws IOException, ServletException {
		final String email = authentication.getName();
		final AuthUser sessionAuthUser = authUserService.findByEmail(email);

		httpServletRequest.getSession().setAttribute(Constants.SESSION_AUTH_USER, sessionAuthUser);

		super.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);
	}

	@Override
	@ServiceMethod
	public void logout(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final Authentication authentication) {
		final AuthUser sessionAuthUser = SessionUtils.getSessionAuthUserFromHttpServletRequest(httpServletRequest);
		httpServletRequest.getSession().removeAttribute(Constants.SESSION_AUTH_USER);

		final Boolean sessionAccountDeleted = (Boolean) httpServletRequest.getSession().getAttribute(Constants.SESSION_ACCOUNT_DELETED);
		httpServletRequest.getSession().removeAttribute(Constants.SESSION_ACCOUNT_DELETED);

		final AuthRole sessionAuthUserRole = (sessionAuthUser != null ? sessionAuthUser.getMaxPriorityAuthRole() : null);

		final Language language = this.getLanguage(httpServletRequest);

		final Language nextDefaultLanguage;
		final AnonymousAccessPermission nextDefaultAnonymousAccessPermission;
		final TablePageSize nextDefaultInitialTablePageSize;
		final ColorMode nextDefaultColorMode;
		final UserInterfaceFramework nextDefaultUserInterfaceFramework;
		final PdfDocumentPageFormat nextDefaultPdfDocumentPageFormat;
		if(sessionAuthUserRole != null && AuthRole.ADMINISTRATOR.equals(sessionAuthUserRole.getName())) {
			nextDefaultLanguage = this.getNextDefaultLanguage(httpServletRequest);
			nextDefaultAnonymousAccessPermission = this.getNextDefaultAnonymousAccessPermission(httpServletRequest);
			nextDefaultInitialTablePageSize = this.getNextDefaultInitialTablePageSize(httpServletRequest);
			nextDefaultColorMode = this.getNextDefaultColorMode(httpServletRequest);
			nextDefaultUserInterfaceFramework = this.getNextDefaultUserInterfaceFramework(httpServletRequest);
			nextDefaultPdfDocumentPageFormat = this.getNextDefaultPdfDocumentPageFormat(httpServletRequest);
		} else {
			nextDefaultLanguage = null;
			nextDefaultAnonymousAccessPermission = null;
			nextDefaultInitialTablePageSize = null;
			nextDefaultColorMode = null;
			nextDefaultUserInterfaceFramework = null;
			nextDefaultPdfDocumentPageFormat = null;
		}

		final String redirectEndpoint = this.getRedirectEndpoint(nextDefaultLanguage, nextDefaultAnonymousAccessPermission, nextDefaultInitialTablePageSize, nextDefaultColorMode, nextDefaultUserInterfaceFramework, nextDefaultPdfDocumentPageFormat);

		final String languageUrlParamValue;
		if(nextDefaultLanguage != null) {
			languageUrlParamValue = nextDefaultLanguage.getCode();
		} else if(language != null) {
			languageUrlParamValue = language.getCode();
		} else if(sessionAuthUser != null) {
			languageUrlParamValue = sessionAuthUser.getLanguage().getCode();
		} else {
			languageUrlParamValue = Language.ENGLISH.getCode();
		}

		String redirectUrl = StringUtils.getStringJoined(httpServletRequest.getContextPath(), redirectEndpoint, LOGOUT_REDIRECT_LANGUAGE, languageUrlParamValue);

		if(Boolean.TRUE.equals(sessionAccountDeleted)) {
			final String urlParamValue = sessionAccountDeleted.toString();
			redirectUrl = StringUtils.getStringJoined(redirectUrl, LOGOUT_REDIRECT_ACCOUNT_DELETED, urlParamValue);
		}

		if(nextDefaultLanguage != null) {
			final String urlParamValue = nextDefaultLanguage.getCode();
			redirectUrl = StringUtils.getStringJoined(redirectUrl, LOGOUT_RESTART_APP_WITH_DEFAULT_LANGUAGE, urlParamValue);
		}

		if(nextDefaultAnonymousAccessPermission != null) {
			final String urlParamValue = nextDefaultAnonymousAccessPermission.getValue();
			redirectUrl = StringUtils.getStringJoined(redirectUrl, LOGOUT_RESTART_APP_WITH_DEFAULT_ANONYMOUS_ACCESS_PERMISSION, urlParamValue);
		}

		if(nextDefaultInitialTablePageSize != null) {
			final String urlParamValue = String.valueOf(nextDefaultInitialTablePageSize.getValue());
			redirectUrl = StringUtils.getStringJoined(redirectUrl, LOGOUT_RESTART_APP_WITH_DEFAULT_INITIAL_PAGE_SIZE, urlParamValue);
		}

		if(nextDefaultColorMode != null) {
			final String urlParamValue = nextDefaultColorMode.getCode();
			redirectUrl = StringUtils.getStringJoined(redirectUrl, LOGOUT_RESTART_APP_WITH_DEFAULT_COLOR_MODE, urlParamValue);
		}

		if(nextDefaultUserInterfaceFramework != null) {
			final String urlParamValue = nextDefaultUserInterfaceFramework.getCode();
			redirectUrl = StringUtils.getStringJoined(redirectUrl, LOGOUT_RESTART_APP_WITH_DEFAULT_USER_INTERFACE_FRAMEWORK, urlParamValue);
		}

		if(nextDefaultPdfDocumentPageFormat != null) {
			final String urlParamValue = nextDefaultPdfDocumentPageFormat.getCode();
			redirectUrl = StringUtils.getStringJoined(redirectUrl, LOGOUT_RESTART_APP_WITH_DEFAULT_PDF_DOCUMENT_PAGE_FORMAT, urlParamValue);
		}

		SpringSecurityUtils.getInstance().setAuthenticated(false);

		httpServletResponse.setStatus(LOGOUT_STATUS);

		try {
			httpServletResponse.sendRedirect(redirectUrl);
		} catch(final Exception exception) {
			if(log.isErrorEnabled()) {
				final String stackTrace = ThrowableUtils.getStackTrace(exception);
				log.error(StringUtils.getStringJoined("An exception happened when redirecting from logout method to ", redirectUrl, ". Exception: ", stackTrace));
			}
		}
	}

	private Language getLanguage(final HttpServletRequest httpServletRequest) {
		final String languageCode = httpServletRequest.getParameter("languageParam");

		final Language language;
		if(languageCode != null) {
			language = Language.findByCode(languageCode);
		} else {
			language = null;
		}
		return language;
	}

	private Language getNextDefaultLanguage(final HttpServletRequest httpServletRequest) {
		final String nextDefaultLanguageCode = httpServletRequest.getParameter("nextDefaultLanguageCode");

		final Language nextDefaultLanguage;
		if(nextDefaultLanguageCode != null) {
			nextDefaultLanguage = Language.findByCode(nextDefaultLanguageCode);
		} else {
			nextDefaultLanguage = null;
		}
		return nextDefaultLanguage;
	}

	private AnonymousAccessPermission getNextDefaultAnonymousAccessPermission(final HttpServletRequest httpServletRequest) {
		final String nextDefaultAnonymousAccessPermissionValue = httpServletRequest.getParameter("nextDefaultAnonymousAccessPermissionValue");

		final AnonymousAccessPermission nextDefaultAnonymousAccessPermission;
		if(nextDefaultAnonymousAccessPermissionValue != null) {
			nextDefaultAnonymousAccessPermission = AnonymousAccessPermission.findByValue(nextDefaultAnonymousAccessPermissionValue);
		} else {
			nextDefaultAnonymousAccessPermission = null;
		}
		return nextDefaultAnonymousAccessPermission;
	}

	private TablePageSize getNextDefaultInitialTablePageSize(final HttpServletRequest httpServletRequest) {
		final String nextDefaultInitialTablePageSizeValue = httpServletRequest.getParameter("nextDefaultInitialTablePageSizeValue");

		final TablePageSize nextDefaultInitialTablePageSize;
		if(nextDefaultInitialTablePageSizeValue != null) {
			nextDefaultInitialTablePageSize = TablePageSize.findByValue(Integer.valueOf(nextDefaultInitialTablePageSizeValue));
		} else {
			nextDefaultInitialTablePageSize = null;
		}
		return nextDefaultInitialTablePageSize;
	}

	private ColorMode getNextDefaultColorMode(final HttpServletRequest httpServletRequest) {
		final String nextDefaultColorModeCode = httpServletRequest.getParameter("nextDefaultColorModeCode");

		final ColorMode nextDefaultColorMode;
		if(nextDefaultColorModeCode != null) {
			nextDefaultColorMode = ColorMode.findByCode(nextDefaultColorModeCode);
		} else {
			nextDefaultColorMode = null;
		}
		return nextDefaultColorMode;
	}

	private UserInterfaceFramework getNextDefaultUserInterfaceFramework(final HttpServletRequest httpServletRequest) {
		final String nextDefaultUserInterfaceFrameworkCode = httpServletRequest.getParameter("nextDefaultUserInterfaceFrameworkCode");

		final UserInterfaceFramework nextDefaultUserInterfaceFramework;
		if(nextDefaultUserInterfaceFrameworkCode != null) {
			nextDefaultUserInterfaceFramework = UserInterfaceFramework.findByCode(nextDefaultUserInterfaceFrameworkCode);
		} else {
			nextDefaultUserInterfaceFramework = null;
		}
		return nextDefaultUserInterfaceFramework;
	}

	private PdfDocumentPageFormat getNextDefaultPdfDocumentPageFormat(final HttpServletRequest httpServletRequest) {
		final String nextDefaultPdfDocumentPageFormatCode = httpServletRequest.getParameter("nextDefaultPdfDocumentPageFormatCode");

		final PdfDocumentPageFormat nextDefaultPdfDocumentPageFormat;
		if(nextDefaultPdfDocumentPageFormatCode != null) {
			nextDefaultPdfDocumentPageFormat = PdfDocumentPageFormat.findByCode(nextDefaultPdfDocumentPageFormatCode);
		} else {
			nextDefaultPdfDocumentPageFormat = null;
		}
		return nextDefaultPdfDocumentPageFormat;
	}

	private String getRedirectEndpoint(final Language nextDefaultLanguage, final AnonymousAccessPermission nextAnonymousAccessPermission, final TablePageSize nextDefaultInitialTablePageSize, final ColorMode nextDefaultColorMode, final UserInterfaceFramework nextUserInterfaceFramework, final PdfDocumentPageFormat nextDefaultPdfDocumentPageFormat) {
		final String redirectEndpoint;
		if(nextDefaultLanguage != null || nextAnonymousAccessPermission != null || nextDefaultInitialTablePageSize != null || nextDefaultColorMode != null || nextUserInterfaceFramework != null || nextDefaultPdfDocumentPageFormat != null) {
			redirectEndpoint = Constants.LOGIN_PATH;
		} else {
			final boolean anonymousAccessAllowed = (AnonymousAccessPermission.ACCESS_ALLOWED == ConfigPropertiesBean.CURRENT_DEFAULT_ANONYMOUS_ACCESS_PERMISSION);
			if(anonymousAccessAllowed) {
				redirectEndpoint = Constants.HOME_PATH;
			} else {
				redirectEndpoint = Constants.LOGIN_PATH;
			}
		}

		return redirectEndpoint;
	}
}