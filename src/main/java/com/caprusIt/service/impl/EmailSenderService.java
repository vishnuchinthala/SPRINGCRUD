package com.caprusIt.service.impl;
import java.io.File;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class EmailSenderService {
	private static final Logger Logger=LoggerFactory.getLogger(EmailSenderService.class);
@Autowired
private  JavaMailSender mailSender;

public void sendEmail(String toEmail[],
		String subject,
		String body) {
	SimpleMailMessage message=new SimpleMailMessage();
	message.setFrom("vishnuchinthala5462@gmail.com");
	message.setTo(toEmail);
	message.setText(body);
	message.setSubject(subject);
	mailSender.send(message);
Logger.info("Mail send Sucessfully....");
}

public void sendMailWithAttachment(String toEmail[],
		
        String body,
        String subject,
        String attachment) throws MessagingException {
	
      MimeMessage mimeMessage=mailSender.createMimeMessage();
      
      MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage,true);
      
      mimeMessageHelper.setFrom("vishnuchinthala5462@gmail.com");
      mimeMessageHelper.setTo(toEmail);
      mimeMessageHelper.setText(body);
      mimeMessageHelper.setSubject(subject);
      
      
     FileSystemResource fileSystemResource=new FileSystemResource(new File(attachment));
    mimeMessageHelper.addAttachment(fileSystemResource.getFilename(),fileSystemResource);
    
    mailSender.send(mimeMessage);
    Logger.info("Mail with attachment sent successfully..");


}
}


