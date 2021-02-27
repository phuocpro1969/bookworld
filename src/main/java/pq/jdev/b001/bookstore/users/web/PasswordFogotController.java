package pq.jdev.b001.bookstore.users.web;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import pq.jdev.b001.bookstore.cart.model.CartInfo;
import pq.jdev.b001.bookstore.cart.utils.Utils;
import pq.jdev.b001.bookstore.users.model.Mail;
import pq.jdev.b001.bookstore.users.model.PasswordResetToken;
import pq.jdev.b001.bookstore.users.model.Person;
import pq.jdev.b001.bookstore.users.service.EmailService;
import pq.jdev.b001.bookstore.users.service.UserService;
import pq.jdev.b001.bookstore.users.web.dto.PasswordForgotDto;

@PreAuthorize("!(hasRole('EMPLOYEE') OR hasRole('ADMIN'))")
@Controller
@RequestMapping("/forgot-password")
public class PasswordFogotController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private EmailService emailService;

	@ModelAttribute("forgotPasswordForm")
	public PasswordForgotDto forgotPasswordDto() {
		return new PasswordForgotDto();
	}

	@GetMapping
	public String displayForgotPasswordPage(ModelMap map, HttpServletRequest request, Model model) {
		map.addAttribute("header", "header_login");
		map.addAttribute("footer", "footer_login");
		CartInfo myCart = Utils.getCartInSession(request);
  
		model.addAttribute("cartForm", myCart);
		model.addAttribute("myCart", myCart);
		return "forgot-password";
	}

	@PostMapping
	public String processForgotPasswordForm(@ModelAttribute("forgotPasswordForm") @Valid PasswordForgotDto form,
			BindingResult result, HttpServletRequest request, ModelMap map, Model mode) {
		CartInfo myCart = Utils.getCartInSession(request);
		if (result.hasErrors()) {
			map.addAttribute("header", "header_login");
			map.addAttribute("footer", "footer_login");
			
  
			mode.addAttribute("cartForm", myCart);
			mode.addAttribute("myCart", myCart);
			return "forgot-password";
		}
		
		map.addAttribute("header", "header_login");
		map.addAttribute("footer", "footer_login");
		mode.addAttribute("cartForm", myCart);
		mode.addAttribute("myCart", myCart);
		
		Person person = userService.findByEmail(form.getEmail());
		if (person == null) {
			result.rejectValue("email", null, "We could not find an account for that e-mail address.");
			return "forgot-password";
		}
		
		PasswordResetToken token = new PasswordResetToken();
		token.setToken(UUID.randomUUID().toString());
		token.setPerson(person);

		token.setExpiryDate();
		userService.saveToken(token);
		
		Mail mail = new Mail();
		mail.setFrom("user1@testmail.com");
		mail.setTo(person.getEmail());
		mail.setSubject("Password reset request");

		Map<String, Object> model = new HashMap<>();
		model.put("token", token);
		model.put("person", person);
		model.put("signature", "http://localhost:8090");
		String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		model.put("resetUrl", url + "/reset-password?token=" + token.getToken());
		mail.setModel(model);
		emailService.sendEmail(mail);

		return "redirect:/forgot-password?success";

	}
}
