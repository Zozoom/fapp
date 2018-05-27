package com.financEng;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@SpringBootApplication
public class FinancApplication {

	private static final Logger log = LoggerFactory.getLogger(FinancApplication.class);

	private static String myUrl = "http://localhost:8080/";

	public static void main(String[] args) {
		SpringApplication.run(FinancApplication.class, args);

        System.out.println("\n******************* Fapp Application Enviroment Variables *************************\n");
        System.out.println("qa_master_user:     ["+System.getenv("qa_master_user")+"]");
        System.out.println("qa_master_password: ["+System.getenv("qa_master_password")+"]");
        System.out.println("qa_email_address:   ["+System.getenv("qa_email_address")+"]");
        System.out.println("qa_email_password:  ["+System.getenv("qa_email_password")+"]");
        System.out.println("\n***********************************************************************************\n");

		log.info("||////////////////////////////////////////////////////////////////////////////////////////||");
		log.info("||                           FAPP (Financial Application)                                 ||");
		log.info("||                             Created by Zoltan Kiss                                     ||");
		log.info("||                                 Copyright 2018                                         ||");
		log.info("||////////////////////////////////////////////////////////////////////////////////////////||");
		log.info(">> [FinancApplication.main] - Starting Application ... Please Stand by...");
		openBrowser(myUrl);
	}

	private static void openBrowser (String url){
		log.info(">> [openBrowser.main] - Automatically open the app in a browser...");

		if(Desktop.isDesktopSupported()){
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(new URI(url));
				log.info(">> [openBrowser.main] - Browser opened at: {}",myUrl);
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		}else{
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec("rundll32 url.dll,FileProtocolHandler " + url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

