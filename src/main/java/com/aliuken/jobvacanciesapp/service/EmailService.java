package com.aliuken.jobvacanciesapp.service;

import com.aliuken.jobvacanciesapp.model.dto.EmailDataDTO;
import com.aliuken.jobvacanciesapp.model.entity.enumtype.Language;

import jakarta.mail.MessagingException;

public interface EmailService {

	public abstract EmailDataDTO getAccountConfirmationEmailData(String destinationEmailAddress, String accountActivationLink, Language language);

	public abstract void sendMail(EmailDataDTO emailDataDTO) throws MessagingException;

}
