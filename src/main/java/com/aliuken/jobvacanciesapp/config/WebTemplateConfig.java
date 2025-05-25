package com.aliuken.jobvacanciesapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;
import org.thymeleaf.spring6.ISpringTemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import com.aliuken.jobvacanciesapp.Constants;
import com.aliuken.jobvacanciesapp.util.security.SpringSecurityUtils;
import com.aliuken.jobvacanciesapp.util.spring.di.BeanFactoryUtils;

import jakarta.servlet.ServletContext;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import nz.net.ultraq.thymeleaf.layoutdialect.decorators.strategies.GroupingStrategy;

@Configuration
public class WebTemplateConfig {

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private SpringResourceTemplateResolver springResourceTemplateResolver;

	@Value("${spring.thymeleaf.cache}")
	private boolean springThymeleafCache;

	@Bean
	UrlTemplateResolver urlTemplateResolver() {
		final UrlTemplateResolver urlTemplateResolver = new UrlTemplateResolver();
		urlTemplateResolver.setCacheable(springThymeleafCache);

		return urlTemplateResolver;
	}

	@Bean
	WebApplicationTemplateResolver webApplicationTemplateResolver() {
		final JakartaServletWebApplication jakartaServletWebApplication = JakartaServletWebApplication.buildApplication(servletContext);

		final WebApplicationTemplateResolver webApplicationTemplateResolver = new WebApplicationTemplateResolver(jakartaServletWebApplication);
		webApplicationTemplateResolver.setCacheable(false);
		webApplicationTemplateResolver.setTemplateMode("HTML");
		webApplicationTemplateResolver.setCharacterEncoding("UTF-8");

		return webApplicationTemplateResolver;
	}

	@Bean
	SpringTemplateEngine springTemplateEngine() {
		final ITemplateResolver urlTemplateResolver = urlTemplateResolver();
		final ITemplateResolver webApplicationTemplateResolver = webApplicationTemplateResolver();

		final IDialect springSecurityDialect = new SpringSecurityDialect();
		final IDialect layoutDialect = new LayoutDialect(new GroupingStrategy());
		final IDialect java8TimeDialect = new Java8TimeDialect();

		final SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();
		springTemplateEngine.addTemplateResolver(springResourceTemplateResolver);
		springTemplateEngine.addTemplateResolver(urlTemplateResolver);
		springTemplateEngine.addTemplateResolver(webApplicationTemplateResolver);
		springTemplateEngine.addDialect(springSecurityDialect);
		springTemplateEngine.addDialect(layoutDialect);
		springTemplateEngine.addDialect(java8TimeDialect);

		return springTemplateEngine;
	}

	@Bean
	ViewResolver viewResolver() {
		final ISpringTemplateEngine springTemplateEngine = springTemplateEngine();

		final ThymeleafViewResolver thymeleafViewResolver = new ThymeleafViewResolver();
		thymeleafViewResolver.setTemplateEngine(springTemplateEngine);
		thymeleafViewResolver.setCharacterEncoding("UTF-8");
		thymeleafViewResolver.setCache(false);
		thymeleafViewResolver.setOrder(1);

		// To use:		${dateUtils.convertToStringForWebPageField(localDateVar)}
		// Instead of:	${(localDateVar != null) ? #temporals.format(localDateVar, 'dd-MM-yyyy') : '-'}
		thymeleafViewResolver.addStaticVariable("dateUtils", Constants.DATE_UTILS);

		// To use:		${dateTimeUtils.convertToStringForWebPageField(localDateTimeVar)}
		// Instead of:	${(localDateTimeVar != null) ? #temporals.format(localDateTimeVar, 'dd-MM-yyyy HH:mm:ss') : '-'}
		thymeleafViewResolver.addStaticVariable("dateTimeUtils", Constants.DATE_TIME_UTILS);

		final String currentDefaultColorModeValue = ConfigPropertiesBean.CURRENT_DEFAULT_COLOR_MODE.getValue();
		thymeleafViewResolver.addStaticVariable("currentDefaultColorModeValue", currentDefaultColorModeValue);

		final String currentDefaultLanguageCode = ConfigPropertiesBean.CURRENT_DEFAULT_LANGUAGE.getCode();
		thymeleafViewResolver.addStaticVariable("currentDefaultLanguageCode", currentDefaultLanguageCode);

		final String currentDefaultUserInterfaceFrameworkCode = ConfigPropertiesBean.CURRENT_DEFAULT_USER_INTERFACE_FRAMEWORK.getCode();
		thymeleafViewResolver.addStaticVariable("currentDefaultUserInterfaceFrameworkCode", currentDefaultUserInterfaceFrameworkCode);

		final GenericApplicationContext applicationContext = BeanFactoryUtils.getGenericApplicationContext();
		thymeleafViewResolver.addStaticVariable("applicationContext", applicationContext);

		final ConfigPropertiesBean configPropertiesBean = applicationContext.getBean("configPropertiesBean", ConfigPropertiesBean.class);
		thymeleafViewResolver.addStaticVariable("configPropertiesBean", configPropertiesBean);

		final SpringSecurityUtils springSecurityUtils = SpringSecurityUtils.getInstance();
		thymeleafViewResolver.addStaticVariable("springSecurityUtils", springSecurityUtils);

		return thymeleafViewResolver;
	}
}