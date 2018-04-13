package com.financEng.service;

import com.financEng.entity.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {
    private final Log log = LogFactory.getLog(this.getClass());

	@Value("${spring.mail.username}")
	private String MESSAGE_FROM;

	private JavaMailSender mailSender;

	@Autowired
	public void setJavaMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendMessage(User user) {

        MimeMessagePreparator preparator = new MimeMessagePreparator() {

            public void prepare(MimeMessage mimeMessage) throws Exception {

                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
                mimeMessage.setSubject("Sikeres regisztrálás");
                mimeMessage.setFrom(new InternetAddress(MESSAGE_FROM));
                mimeMessage.setText(
                        "Dear " + user.getfName()+" "+user.getsName() + " "
                                + user.getEmail()
                                + ", thank you for your registration. \n Your registration number is "
                                + user.getActivation()
                + "\n http://localhost:8080/activation/" + user.getActivation());
            }
        };
        try {
            this.mailSender.send(preparator);
        }
        catch (MailException ex) {
            log.error("Hiba e-mail küldéskor az alábbi címre: " + user.getEmail() + "  " + ex);
        }

	}

}
