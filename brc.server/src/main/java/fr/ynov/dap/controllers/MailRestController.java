package fr.ynov.dap.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.ynov.dap.google.data.AppUser;
import fr.ynov.dap.google.data.AppUserRepostory;
import fr.ynov.dap.google.service.GmailService;
import fr.ynov.dap.microsoft.service.OutlookService;
import fr.ynov.dap.models.NbMailResponse;

/**
 * The Class MailRestController.
 */
@RestController
public class MailRestController {


	/** The app user repository. */
	@Autowired
	AppUserRepostory appUserRepository;
	
	/** The outlook service. */
	@Autowired
	OutlookService outlookService;
	
	/** The gmail service. */
	@Autowired 
	GmailService gmailService;
	

	
	/**
	 * Mail.
	 *
	 * @param model the model
	 * @param request the request
	 * @param redirectAttributes the redirect attributes
	 * @param userKey the user key
	 * @return the nb mail response
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@RequestMapping("/microsoft/nbUnreadMails")
	public NbMailResponse mail(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes,
			@RequestParam final String userKey) throws IOException {
		
        AppUser currentUser = appUserRepository.findByUserkey(userKey);
        
        Integer nbUnreadMails = outlookService.getNbUnreadEmailsForAccount(currentUser);
        
		return new NbMailResponse(nbUnreadMails);
	}
	
	/**
	 * Gets the number of unread message.
	 *
	 * @param userKey the user key
	 * @return the number of unread message
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws Exception the exception
	 */
	@RequestMapping("/mail/nbUnread")
    public final NbMailResponse getNumberOfUnreadMessage(@RequestParam final String userKey) throws IOException, Exception{

		AppUser appUser = appUserRepository.findByUserkey(userKey);

        Integer googleNbUnreadMail = gmailService.getNbUnreadMailForAccount(appUser);

        Integer microsoftNbUnreadMail = outlookService.getNbUnreadEmailsForAccount(appUser);

        return new NbMailResponse(googleNbUnreadMail + microsoftNbUnreadMail);
    }
	
	/**
	 * Gets the unread mail.
	 *
	 * @param model the model
	 * @param userKey the user key
	 * @return the gmail response
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws Exception the exception
	 */
	@RequestMapping("/google/nbUnreadMails")
	public NbMailResponse GetUnreadMail (ModelMap model, @RequestParam final String userKey) throws IOException, Exception {
		
		NbMailResponse response = new NbMailResponse(0);
		AppUser appUser = appUserRepository.findByUserkey(userKey);
		if(appUser != null) {
			Integer nbUnreadMail = gmailService.getNbUnreadMailForAccount(appUser);
			response.setNbUnreadMail(nbUnreadMail);
		}	
		return response;
	}
}