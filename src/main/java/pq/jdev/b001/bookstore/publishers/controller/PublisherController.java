package pq.jdev.b001.bookstore.publishers.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
//import java.util.concurrent.Flow.Publisher;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pq.jdev.b001.bookstore.books.service.BookService;
import pq.jdev.b001.bookstore.cart.model.CartInfo;
import pq.jdev.b001.bookstore.cart.utils.Utils;
import pq.jdev.b001.bookstore.category.model.Category;
import pq.jdev.b001.bookstore.category.service.CategoryService;
import pq.jdev.b001.bookstore.publishers.model.Publishers;
import pq.jdev.b001.bookstore.publishers.service.PublisherService;
import pq.jdev.b001.bookstore.users.service.UserService;

@Controller
public class PublisherController {
	// private final PublisherRepository publisherReponsitory;

	/*
	 * @Autowired public PublisherController(PublisherRepository
	 * publisherReponsitory) { this.publisherReponsitory = publisherReponsitory; }
	 */

	/*
	 * @GetMapping("list") public String showUpdateForm(Model model) {
	 * model.addAttribute("publishers", publisherReponsitory.findAll()); return
	 * "index"; }
	 */

	// private static List<Publishers> publishers = new ArrayList<Publishers>();

	/*
	 * @RequestMapping(value = {"/index" }, method = RequestMethod.GET) public
	 * String index(Model model) {
	 * 
	 * return "index"; }
	 */
	
	@Autowired
	private PublisherService publisherService;

	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private BookService bookService;

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/publishersList")
	public String index(Model model, HttpServletRequest request, RedirectAttributes redirect) {
		request.getSession().setAttribute("listPublisher", null);

		if (model.asMap().get("success") != null)
			redirect.addFlashAttribute("success", model.asMap().get("success").toString());
		return "redirect:/publishersList/page/1";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/publisher/add")
	public String create(Model model,ModelMap map, Authentication authentication, HttpServletRequest request) {
		if (authentication != null) {
			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
			List<String> roles = new ArrayList<String>();
			for (GrantedAuthority a : authorities) {
				roles.add(a.getAuthority());
			}
			
			if (isAdmin(roles)) {
				map.addAttribute("header", "header_admin");
				map.addAttribute("footer", "footer_admin");
			}
			else if (isUser(roles)){
				map.addAttribute("header", "header_user");
				map.addAttribute("footer", "footer_user");
			} 
		}
		else {
				map.addAttribute("header", "header_login");
				map.addAttribute("footer", "footer_login");
		}
		
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
		request.getSession().setAttribute("idC", idP);
		request.getSession().setAttribute("idU", idP);
		model.addAttribute("publisher", new Publishers(idP, idP));
		java.util.Date date= new java.util.Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);
		map.addAttribute("cd", ts);
		map.addAttribute("ud", ts);

		CartInfo myCart = Utils.getCartInSession(request);
		model.addAttribute("cartForm", myCart);
		model.addAttribute("myCart", myCart);
		return "publisherAdd";
	}
	
	@PostMapping("/publisher/save")
	public String savePublisher(@Valid Publishers publishers, BindingResult result, RedirectAttributes redirect, HttpServletRequest request) {
		if (result.hasErrors()) {
			return "publisherAdd";
			}
		Long idPub = (Long) request.getSession().getAttribute("idPub");
		if (idPub == publishers.getId())
			publishers.setCreateDate((Timestamp)request.getSession().getAttribute("cd"));
		publishers.setCreateId((Long)request.getSession().getAttribute("idC"));
		publishers.setUpdateId((Long)request.getSession().getAttribute("idU"));
		publisherService.save(publishers);
		return "redirect:/publishersList";
	}
	
	// @PreAuthorize("hasRole('ADMIN')")
	// @GetMapping("/publishersList")
	// public String viewPublishersList() {
	// 	return "redirect:/publishersList/page/1";
	// }

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/publisher/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes redirect, Authentication authentication) {
		if (authentication != null && id != (long) 1)
		{
			bookService.changePublisher(id, (long) 1);
			publisherService.delete(id);
		}
		return "redirect:/publishersList";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/publisher/{id}/edit")
	public String edit(@PathVariable int id, Model model, ModelMap map, HttpServletRequest request, Authentication authentication) {
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
		Publishers pub = publisherService.find(id);
		request.getSession().setAttribute("cd", pub.getCreateDate());
		request.getSession().setAttribute("idPub", pub.getId());
		model.addAttribute("publisher", pub);
		
		Long idP = userService.findByUsername(authentication.getName()).getId();
		request.getSession().setAttribute("idC", pub.getCreateId());
		request.getSession().setAttribute("idU", idP);
		java.util.Date date= new java.util.Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);
		map.addAttribute("cd", pub.getCreateDate());
		map.addAttribute("ud", ts);
		CartInfo myCart = Utils.getCartInSession(request);
		model.addAttribute("cartForm", myCart);
		model.addAttribute("myCart", myCart);
		return "publisherAdd";
	}

	/*
	 * @GetMapping("/publisher/search") public String search(@RequestParam("s")
	 * String s, Model model) { if (s.equals("")) { return
	 * "redirect:/publishersList"; }
	 * 
	 * model.addAttribute("publisher", publisherService.search(s)); return
	 * "publishersList"; }
	 */
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/publishersList/page/{pageNumber}")
	public String showPage(HttpServletRequest request, @PathVariable int pageNumber, Model model, ModelMap map) {
		map.addAttribute("header", "header_admin");
		map.addAttribute("footer", "footer_admin");
		PagedListHolder<?> pageLs = (PagedListHolder<?>) request.getSession().getAttribute("listPublisher");
		int pagesize = 8;
		List<Publishers> list = publisherService.findAll();
		if (pageLs == null) {
			pageLs = new PagedListHolder<>(list);
			pageLs.setPageSize(pagesize);
		} else {
			final int goToPage = pageNumber - 1;
			if (goToPage <= pageLs.getPageCount() && goToPage >= 0) {
				pageLs.setPage(goToPage);
			}
		}

		request.getSession().setAttribute("listPublisher", pageLs);
		int current = pageLs.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pageLs.getPageCount());
		int totalPageCount = pageLs.getPageCount();
		String baseUrl = "/publishersList/page/";

		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("publishersL", pageLs);
		
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
		CartInfo myCart = Utils.getCartInSession(request);
		model.addAttribute("cartForm", myCart);
		model.addAttribute("myCart", myCart);
		return "publishersList";
	}

	@GetMapping("/publishersList/search/{pageNumber}")
	public String search(@RequestParam("s") String s, Model model, HttpServletRequest request,
			@PathVariable int pageNumber, ModelMap map) {
		map.addAttribute("header", "header_admin");
		map.addAttribute("footer", "footer_admin");
		int pagesizeCP = 15;
		PagedListHolder<?> pagePubs = null;
		PagedListHolder<?> pageCates = null;
		List<Publishers> listPub = (List<Publishers>) publisherService.findAll();
		List<Category> listCate = categoryService.findAll();
		if (pageCates == null) {
			pageCates = new PagedListHolder<>(listCate);
			pageCates.setPageSize(pagesizeCP);
		}
		if (pagePubs == null) {
			pagePubs = new PagedListHolder<>(listPub);
			pagePubs.setPageSize(pagesizeCP);
		}
		model.addAttribute("publishers", pagePubs);
		model.addAttribute("categories", pageCates);
		if (s.equals("")) {
			return "redirect:/publishersList";
		}

		List<Publishers> publishersList = publisherService.findByPublisher(s);
		if (publishersList == null) {
			return "redirect:/publishersList";
		}
		PagedListHolder<?> pageLs = (PagedListHolder<?>) request.getSession().getAttribute("listPublisher");
		int pagesize = 4;

		pageLs = new PagedListHolder<>(publishersList);
		pageLs.setPageSize(pagesize);

		final int goToPage = pageNumber - 1;
		if (goToPage <= pageLs.getPageCount() && goToPage >= 0) {
			pageLs.setPage(goToPage);
		}

		request.getSession().setAttribute("listPublisher", pageLs);
		int current = pageLs.getPage() + 1;
		int begin = Math.max(1, current - publishersList.size());
		int end = Math.min(begin + 5, pageLs.getPageCount());
		int totalPageCount = pageLs.getPageCount();
		String baseUrl = "/publishersList/search/";

		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("publishersL", pageLs);
		CartInfo myCart = Utils.getCartInSession(request);
		model.addAttribute("cartForm", myCart);
		model.addAttribute("myCart", myCart);

		return "publishersList";
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
}
