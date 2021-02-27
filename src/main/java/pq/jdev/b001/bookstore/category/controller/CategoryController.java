package pq.jdev.b001.bookstore.category.controller;

import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import pq.jdev.b001.bookstore.cart.model.CartInfo;
import pq.jdev.b001.bookstore.cart.utils.Utils;
import pq.jdev.b001.bookstore.category.model.Category;
import pq.jdev.b001.bookstore.category.service.CategoryService;
import pq.jdev.b001.bookstore.publishers.model.Publishers;
import pq.jdev.b001.bookstore.publishers.service.PublisherService;
import pq.jdev.b001.bookstore.users.service.UserService;


@Controller
public class CategoryController {
	@Autowired
	private CategoryService categoryService;

	@Autowired
	private PublisherService publisherService;

	@Autowired
	private UserService userService;

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/category/add")
	public String AddEditForm(Model model, ModelMap map, Authentication authentication, HttpServletRequest request) {
		map.addAttribute("header", "header_admin");
		map.addAttribute("footer", "footer_admin");

		int pagesizeCP = 15;
		PagedListHolder<?> pagePubs = null;
		PagedListHolder<?> pageCates = null;
		List<Publishers> listPub = (List<Publishers>) publisherService.findAll();
		List<Category> categoryList = categoryService.findAll();
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

		Long idP = userService.findByUsername(authentication.getName()).getId();
		request.getSession().setAttribute("cd", null);
		request.getSession().setAttribute("idCate", idP);
		request.getSession().setAttribute("idC", idP);
		request.getSession().setAttribute("idU", idP);
		model.addAttribute("category", new Category(idP, idP));
		java.util.Date date = new java.util.Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);
		map.addAttribute("cd", ts);
		map.addAttribute("ud", ts);

		CartInfo myCart = Utils.getCartInSession(request);
		model.addAttribute("cartForm", myCart);
		model.addAttribute("myCart", myCart);

		return "categoryadd";
	}

	@PostMapping("/category/save")
	public String Add(@Valid Category category, BindingResult result, HttpServletRequest request) {
		if (result.hasErrors()) {
			return "categoryadd";
		}
		Long idCate = (Long) request.getSession().getAttribute("idCate");
		if (idCate == category.getId())
			category.setCreateDate((Timestamp) request.getSession().getAttribute("cd"));
		category.setCreateId((Long) request.getSession().getAttribute("idC"));
		category.setUpdateId((Long) request.getSession().getAttribute("idU"));
		categoryService.save(category);

		return "redirect:/categoryList";
	}
}
