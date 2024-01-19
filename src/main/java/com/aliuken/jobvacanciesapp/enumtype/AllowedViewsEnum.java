package com.aliuken.jobvacanciesapp.enumtype;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.aliuken.jobvacanciesapp.Constants;

import jakarta.validation.constraints.NotEmpty;

public enum AllowedViewsEnum implements Serializable {
	ANONYMOUS_ACCESS_ALLOWED(true),
	ANONYMOUS_ACCESS_NOT_ALLOWED(false);

	private final AntPathRequestMatcher[] VARIABLE_VIEWS_ARRAY = new AntPathRequestMatcher[]{
		new AntPathRequestMatcher("/"),
		new AntPathRequestMatcher("/search"),
		new AntPathRequestMatcher("/auth-users/view/**"),
		new AntPathRequestMatcher("/job-categories/view/**"),
		new AntPathRequestMatcher("/job-companies/view/**"),
		new AntPathRequestMatcher("/job-vacancies/view/**")
	};

	private final AntPathRequestMatcher[] FIXED_ANONYMOUS_VIEWS_ARRAY = new AntPathRequestMatcher[]{
		new AntPathRequestMatcher("/login"),
		new AntPathRequestMatcher("/logout"),
		new AntPathRequestMatcher("/signup"),
		new AntPathRequestMatcher("/signup-confirmed"),
		new AntPathRequestMatcher("/about")
	};

	private final AntPathRequestMatcher[] FIXED_USER_VIEWS_ARRAY = new AntPathRequestMatcher[]{
		new AntPathRequestMatcher("/my-user/auth-users/**"),
		new AntPathRequestMatcher("/my-user/auth-user-curriculums/**"),
		new AntPathRequestMatcher("/job-requests/create/**"),
		new AntPathRequestMatcher("/job-requests/save/**"),
		new AntPathRequestMatcher("/job-requests/view/**")
	};

	private final AntPathRequestMatcher[] SUPERVISOR_VIEWS_ARRAY = new AntPathRequestMatcher[]{
		new AntPathRequestMatcher("/job-requests/**"),
		new AntPathRequestMatcher("/job-vacancies/**"),
		new AntPathRequestMatcher("/job-categories/**"),
		new AntPathRequestMatcher("/job-companies/**")
	};

	private final AntPathRequestMatcher[] ADMINISTRATOR_VIEWS_ARRAY = new AntPathRequestMatcher[]{
		new AntPathRequestMatcher("/auth-users/**"),
		new AntPathRequestMatcher("/my-user/application/**")
	};

	private final boolean anonymousAccessAllowed;

	@NotEmpty
	private final AntPathRequestMatcher[] anonymousViewsArray;

	@NotEmpty
	private final AntPathRequestMatcher[] userViewsArray;

	@NotEmpty
	private final AntPathRequestMatcher[] supervisorViewsArray;

	@NotEmpty
	private final AntPathRequestMatcher[] administratorViewsArray;

	private static final Map<Boolean, AllowedViewsEnum> ALLOWED_VIEWS_MAP = AllowedViewsEnum.getAllowedViewsMap();

	private AllowedViewsEnum(final boolean anonymousAccessAllowed) {
		this.anonymousAccessAllowed = anonymousAccessAllowed;

		if(anonymousAccessAllowed) {
			this.anonymousViewsArray = Constants.PARALLEL_STREAM_UTILS.ofArrays(AntPathRequestMatcher[]::new, FIXED_ANONYMOUS_VIEWS_ARRAY, VARIABLE_VIEWS_ARRAY);
			this.userViewsArray = FIXED_USER_VIEWS_ARRAY;
		} else {
			this.anonymousViewsArray = FIXED_ANONYMOUS_VIEWS_ARRAY;
			this.userViewsArray = Constants.PARALLEL_STREAM_UTILS.ofArrays(AntPathRequestMatcher[]::new, FIXED_USER_VIEWS_ARRAY, VARIABLE_VIEWS_ARRAY);
		}

		this.supervisorViewsArray = SUPERVISOR_VIEWS_ARRAY;
		this.administratorViewsArray = ADMINISTRATOR_VIEWS_ARRAY;
	}

	public boolean isAnonymousAccessAllowed() {
		return anonymousAccessAllowed;
	}

	public AntPathRequestMatcher[] getAnonymousViewsArray() {
		return anonymousViewsArray;
	}

	public AntPathRequestMatcher[] getUserViewsArray() {
		return userViewsArray;
	}

	public AntPathRequestMatcher[] getSupervisorViewsArray() {
		return supervisorViewsArray;
	}

	public AntPathRequestMatcher[] getAdministratorViewsArray() {
		return administratorViewsArray;
	}

	public static AllowedViewsEnum getInstance(final boolean anonymousAccessAllowed) {
		final AllowedViewsEnum allowedViewsEnum = ALLOWED_VIEWS_MAP.get(anonymousAccessAllowed);
		return allowedViewsEnum;
	}

	private static Map<Boolean, AllowedViewsEnum> getAllowedViewsMap() {
		final AllowedViewsEnum[] allowedViewsEnums = AllowedViewsEnum.values();

		final Map<Boolean, AllowedViewsEnum> allowedViewsMap = new HashMap<>();
		final Consumer<AllowedViewsEnum> allowedViewsEnumConsumer = (allowedViewsEnum -> {
			if(allowedViewsEnum != null) {
				final boolean anonymousAccessAllowed = allowedViewsEnum.isAnonymousAccessAllowed();
				allowedViewsMap.put(anonymousAccessAllowed, allowedViewsEnum);
			}
		});

		final Stream<AllowedViewsEnum> allowedViewsEnumStream = Constants.PARALLEL_STREAM_UTILS.ofNullableArray(allowedViewsEnums);
		allowedViewsEnumStream.forEach(allowedViewsEnumConsumer);

		return allowedViewsMap;
	}

}
