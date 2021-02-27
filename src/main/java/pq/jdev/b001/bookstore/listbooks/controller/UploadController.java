package pq.jdev.b001.bookstore.listbooks.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pq.jdev.b001.bookstore.cart.model.CartInfo;
import pq.jdev.b001.bookstore.cart.utils.Utils;
import pq.jdev.b001.bookstore.category.model.Category;
import pq.jdev.b001.bookstore.category.service.CategoryService;
import pq.jdev.b001.bookstore.publishers.model.Publishers;
import pq.jdev.b001.bookstore.publishers.service.PublisherService;

@Controller

public class UploadController {

	// Save the uploaded file to this folder
	private static String UPLOADED_FOLDER = "C:/Users/laptop/Desktop/bookstore/src/main/resources/static/images/";

	
	@Autowired
	private PublisherService publisherService;

	@Autowired
	private CategoryService categoryservice;
	
	@GetMapping("/upload")
	public String viewUpLoad(Authentication authentication, ModelMap map, Model model, HttpServletRequest request) {
		if (authentication != null) {
			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
			List<String> roles = new ArrayList<String>();
			for (GrantedAuthority a : authorities) {
				roles.add(a.getAuthority());
			}

			if (isUser(roles)) {
				map.addAttribute("header", "header_user");
				map.addAttribute("footer", "footer_user");

			} else if (isAdmin(roles)) {
				map.addAttribute("header", "header_admin");
				map.addAttribute("footer", "footer_admin");
			}
		} else {
			map.addAttribute("header", "header_login");
			map.addAttribute("footer", "footer_login");
		}
		int pagesizeCP = 15;
		PagedListHolder<?> pagePubs = null;
		PagedListHolder<?> pageCates = null;
		List<Publishers> listPub = (List<Publishers>) publisherService.findAll();
		List<Category> categoryList = categoryservice.findAll();
		if (pageCates == null) {
			pageCates = new PagedListHolder<>(categoryList);
			pageCates.setPageSize(pagesizeCP);
		}
		if (pagePubs == null) {
			pagePubs = new PagedListHolder<>(listPub);
			pagePubs.setPageSize(pagesizeCP);
		}
		model.addAttribute("publishers", pagePubs);
		model.addAttribute("categories", pageCates);

		CartInfo myCart = Utils.getCartInSession(request);
		model.addAttribute("cartForm", myCart);
		model.addAttribute("myCart", myCart);

		
		return "upload";
	}

	@PostMapping("/upload") // //new annotation since 4.3
	public String singleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

		try {

			// Get the file and save it somewhere
			byte[] bytes = file.getBytes();
			Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
			Files.write(path, bytes);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return "redirect:/";
	}

	private boolean isUser(List<String> roles) {
		if (roles.contains("ROLE_EMPLOYEE")) {
			return true;
		}
		return false;
	}

	private boolean isAdmin(List<String> roles) {
		if (roles.contains("ROLE_ADMIN")) {
			return true;
		}
		return false;
	}

//    @GetMapping("/uploadStatus")
//    public String uploadStatus() {
//        return "uploadStatus";
//    }

}