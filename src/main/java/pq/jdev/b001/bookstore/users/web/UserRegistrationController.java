package pq.jdev.b001.bookstore.users.web;

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
import pq.jdev.b001.bookstore.users.model.Person;
import pq.jdev.b001.bookstore.users.service.UserService;
import pq.jdev.b001.bookstore.users.web.dto.UserDto;

@PreAuthorize("!(hasRole('EMPLOYEE') OR hasRole('ADMIN'))")
@Controller
@RequestMapping(value = "/registration")
public class UserRegistrationController {

	@Autowired
	private UserService userService;

	@ModelAttribute("person")
	public UserDto userRegistrationDto() {
		return new UserDto();
	}
	
	@ModelAttribute("singleSelectAllValues")
    public String[] getSingleSelectAllValues() {
        return new String[] {"Male", "Female"};
    }

	@GetMapping
	public String showRegistrationForm(ModelMap map, HttpServletRequest request, Model model) {
		map.addAttribute("header", "header_login");
		map.addAttribute("footer", "footer_login");
		CartInfo myCart = Utils.getCartInSession(request);
		model.addAttribute("cartForm", myCart);
		model.addAttribute("myCart", myCart);
		return "registration";
	}

	@PostMapping
	public String registerUserAccount(@ModelAttribute("person") @Valid UserDto userDto,
			BindingResult result, ModelMap map, HttpServletRequest request, Model model) throws Exception {
		
		Person existingUserName = userService.findByUsername(userDto.getUserName());
		Person existingEmail = userService.findByEmail(userDto.getEmail());
		if (existingEmail != null || existingUserName != null) {
			result.rejectValue("email", null, "There is email or username already an account registered");
		}

		if (result.hasErrors()) {
			map.addAttribute("header", "header_login");
			map.addAttribute("footer", "footer_login");
			CartInfo myCart = Utils.getCartInSession(request);
			model.addAttribute("cartForm", myCart);
			model.addAttribute("myCart", myCart);
			return "registration";
		}

		userService.save(userDto);
		return "redirect:/registration?success";
	}
}
