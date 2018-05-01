package com.financEng.service;

import com.financEng.entity.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class EmailService {

    private final Log log = LogFactory.getLog(this.getClass());

    private String pathPrefixToFiles = "./src/main/resources/";

    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
	private String MESSAGE_FROM;

	@Autowired
	public void setJavaMailSender(JavaMailSender mailSender) {
	    this.mailSender = mailSender;
	}

    /*==================================================================================================================
     || Email Service Declarations
     ==================================================================================================================*/

    /*************************************
     * Send Activation Email
     * ***********************************/
	public void sendActivationEmail(User user) {

        MimeMessagePreparator preparator = mimeMessage -> {

            log.info(">> [sendActivationEmail] - Creating activation email for: "+user.getEmail()+" user.");

            String emailTitle = "FAPP - Activation Email";
            String firstPrupose = "This is an activation email. Please follow the instructions to activate your account.";
            String lastPrupose = "Thank you for registering to the Fapp.";

            // Attach a file to the mail.
            FileSystemResource file = new FileSystemResource(new File(pathPrefixToFiles+"static/images/fapp/fapp_Logo2.png"));

            // Mail To whom ?
            mimeMessage.setRecipient(Message.RecipientType.TO,new InternetAddress(user.getEmail()));

            // Mail from ?
            mimeMessage.setFrom(new InternetAddress(MESSAGE_FROM));

            // Mail Subject
            mimeMessage.setSubject("FAPP - Successfully registration.");

            // Mime Message Helper -> Use the true flag to indicate you need a multipart message
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            // Swapping email content with my details -> Set key values
            // emailTitle, lastName, firstName, firstPurpose, activationCode, lastPurpose,
            Map<String, String> input = new HashMap<>();
            input.put("{{emailTitle}}",emailTitle);
            input.put("{{lastName}}", user.getsName());
            input.put("{{firstName}}", user.getsName());
            input.put("{{firstPurpose}}", firstPrupose);
            input.put("{{lastPurpose}}", lastPrupose);
            input.put("{{activationCode}}", user.getActivation());

            //HTML mail content swapping process call.
            String htmlText = readHtml(input);

            // Set the html text (content) -> Use the true flag to indicate the text included is HTML
            helper.setText(htmlText, true);

            // Set the image to the CID in the HTML file.
            helper.addInline("myLogo", file);

            // Attach a file if I want
            //helper.addAttachment("fapp_Logo.png", file);

        };
        try {
            mailSender.send(preparator);
            log.info(">> [sendActivationEmail] - Send was successful ! Email: "+user.getEmail()+" !");
        }
        catch (MailException ex) {
            log.error(">> [sendActivationEmail] - Something went wrong meanwhile sending email to: "+user.getEmail()+" !");
            log.error(">> [sendActivationEmail] - Error: "+ex.getMessage());
        }

	}


    /*************************************
     * Send ReActivation Email
     * ***********************************/
    public void sendReActivationEmail(User user) {

    }

    /*************************************
     * Send Password Reset Email
     * ***********************************/
    public void sendPasswordResetEmail(User user) {

    }

    /*************************************
     * Send Notification Email
     * ***********************************/
    public void sendNotificationEmail(User user) {

    }

    /*************************************
     * Send News Letter Email
     * ***********************************/
    public void sendNewsLetterEmail(User user) {

    }

    /*==================================================================================================================
     || Email Service Private Methods
     ==================================================================================================================*/

    /*************************************
     * Read Html Template File
     * ***********************************/
    private static String readFile() throws IOException {
        String fileName = "./src/main/resources/static/emailTemps/activationTemp.html";
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        StringBuilder contents = new StringBuilder();

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                contents.append(line);
                contents.append(System.getProperty("line.separator"));
            }
        } finally {
            reader.close();
        }
        System.out.println("READFILE DONE");
        return contents.toString();
    }

    /*************************************
     * Read Html Template File
     * ***********************************/
    private static String readHtml(Map<String, String> input) throws IOException {
        String msg = readFile();

        Set<Map.Entry<String, String>> entries = input.entrySet();

        for (Map.Entry<String, String> entry : entries) {
            msg = msg.replace(entry.getKey().trim(), entry.getValue().trim());
        }
        System.out.println("Read HTML and Swapping...");
        return msg;
    }
}
