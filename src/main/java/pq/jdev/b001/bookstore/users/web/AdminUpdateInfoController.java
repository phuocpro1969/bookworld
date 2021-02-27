package pq.jdev.b001.bookstore.users.web;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pq.jdev.b001.bookstore.cart.model.CartInfo;
import pq.jdev.b001.bookstore.cart.utils.Utils;
import pq.jdev.b001.bookstore.users.model.Person;
import pq.jdev.b001.bookstore.users.service.UserService;
import pq.jdev.b001.bookstore.users.web.dto.UserChangePassDto;
import pq.jdev.b001.bookstore.users.web.dto.UserUpdateInfoDto;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/accountAdmin")
public class AdminUpdateInfoController {

	@Autowired
	private UserService userService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@ModelAttribute("person")
	public UserUpdateInfoDto updateInfoDto(Principal principal) {
		String username = principal.getName();
		Person p = userService.findByUsername(username);
		return userService.updateInfo(p);
	}

	@ModelAttribute("person2")
	public UserChangePassDto changePass(Principal principal) {
		String username = principal.getName();
		Person p = userService.findByUsername(username);
		return userService.updateInfoP(p);
	}

	@ModelAttribute("singleSelectAllValues")
	public String[] getSingleSelectAllValues() {
		return new String[] { "Male", "Female" };
	}

	// update info

	@GetMapping
	public String showUpdateInfoForm(ModelMap map, HttpServletRequest request, Model model) {
		map.addAttribute("header", "header_admin");
		map.addAttribute("footer", "footer_admin");
		CartInfo myCart = Utils.getCartInSession(request);
  
		model.addAttribute("cartForm", myCart);
		model.addAttribute("myCart", myCart);
		
		return "/accountAdmin";
	}

	@PostMapping
	public String UpdateUserAccount(@ModelAttribute("person") @Valid UserUpdateInfoDto userDto, BindingResult result,
			ModelMap map, Authentication authentication, HttpServletRequest request, Model model) throws Exception {

		if (authentication == null)
			return "redirect:/";

		if (result.hasErrors()) {
			map.addAttribute("header", "header_admin");
			map.addAttribute("footer", "footer_admin");
			CartInfo myCart = Utils.getCartInSession(request);
  
			model.addAttribute("cartForm", myCart);
			model.addAttribute("myCart", myCart);
			return "accountAdmin";
		}
		userService.save(userDto);

		return "redirect:/accountAdmin?success";
	}

	// update pass

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/changePassAdmin", method = RequestMethod.GET)
	public String showChangePassForm(ModelMap map, HttpServletRequest request, Model model) {
		map.addAttribute("header", "header_admin");
		map.addAttribute("footer", "footer_admin");
		CartInfo myCart = Utils.getCartInSession(request);
  
		model.addAttribute("cartForm", myCart);
		model.addAttribute("myCart", myCart);
		return "/changePassAdmin";
	}

	@RequestMapping(value = "/changePassAdmin", method = RequestMethod.POST)
	public String UpdatePassUserAccount(@ModelAttribute("person2") @Valid UserChangePassDto userDto,
			BindingResult result, ModelMap map, Authentication authentication, HttpServletRequest request, Model model) {

		if (authentication == null)
			return "redirect:/";

		if (result.hasErrors()) {
			map.addAttribute("header", "header_admin");
			map.addAttribute("footer", "footer_admin");
			CartInfo myCart = Utils.getCartInSession(request);
  
			model.addAttribute("cartForm", myCart);
			model.addAttribute("myCart", myCart);
			return "/changePassAdmin";
		}

		String updatedPassword = passwordEncoder.encode(userDto.getPassword());
		userService.updatePassword(updatedPassword, userDto.getId());
		userService.loadUserByUsername(userDto.getUserName());
		return "redirect:/accountAdmin/changePassAdmin?success";
	}

	// delete user
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	public String delete(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
		if (id != 1) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
				new SecurityContextLogoutHandler().logout(request, response, auth);
			}
			userService.deleteTokenByIdPerson(id);
			userService.delete(id);
			return "redirect:/";
		} else
			return "redirect:/accountAdmin";
	}
}
