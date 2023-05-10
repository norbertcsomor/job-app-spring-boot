package com.example.jobapp.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Value("${spring.mail.username}")
	private String MESSAGE_FROM;
	
	private JavaMailSender javaMailSender;

    Logger logger = LoggerFactory.getLogger(EmailService.class);

	@Autowired
	public void setJavaMailSender(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	public void sendMessage(String email) {
		SimpleMailMessage message = null;
		
		try {
			message = new SimpleMailMessage();
			message.setFrom(MESSAGE_FROM);
			message.setTo(email);
			message.setSubject("");
			message.setText("");
			javaMailSender.send(message);
			
		} catch (Exception e) {
			logger.error("Hiba e-mail küldéskor az alábbi címre: " + email + "  " + e);
		}
		

	}
}
