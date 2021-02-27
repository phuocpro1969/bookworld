package pq.jdev.b001.bookstore.users.web;

import java.security.Principal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import pq.jdev.b001.bookstore.books.model.Book;
import pq.jdev.b001.bookstore.cart.utils.Utils;
import pq.jdev.b001.bookstore.cart.model.CartInfo;
import pq.jdev.b001.bookstore.category.model.Category;
import pq.jdev.b001.bookstore.category.service.CategoryService;
import pq.jdev.b001.bookstore.listbooks.service.ListBookService;
import pq.jdev.b001.bookstore.publishers.model.Publishers;
import pq.jdev.b001.bookstore.publishers.service.PublisherService;
import pq.jdev.b001.bookstore.users.model.Person;
import pq.jdev.b001.bookstore.users.service.UserService;

@Controller
public class LoginController {

	@Autowired
	private ListBookService listBookService;

	@Autowired
	private UserService userService;

	@Autowired
	private PublisherService publisherService;

	@Autowired
	private CategoryService categoryservice;

	@GetMapping({ "/" })
	public String root(Authentication authentication, ModelMap map, Model model, HttpServletRequest request,
			Principal principal) {

		PagedListHolder<?> pages = null;

		int pagesize = 10;
		List<Book> listH = null;
		if (principal == null) {
			listH = (List<Book>) listBookService.findAll();
		} else {
			Person per = userService.findByUsername(principal.getName());
			listH = getList(per);
		}

		if (pages == null) {
			pages = new PagedListHolder<>(listH);
			pages.setPageSize(pagesize);
		}
		if (principal == null)
			request.getSession().setAttribute("bookListC", pages);
		else
			request.getSession().setAttribute("bookListR", pages);

		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - listH.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		String baseUrl = "/page/";

		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("books", pages);

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

		if (authentication != null) {
			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
			List<String> roles = new ArrayList<String>();
			for (GrantedAuthority a : authorities) {
				roles.add(a.getAuthority());
			}
			if (isUser(roles)) {
				map.addAttribute("header", "header_user");
				map.addAttribute("footer", "footer_user");
				map.addAttribute("ok", "FALSE");
			} else if (isAdmin(roles)) {
				map.addAttribute("header", "header_admin");
				map.addAttribute("footer", "footer_admin");
				map.addAttribute("ok", "TRUE");
			}
		} else {
			map.addAttribute("header", "header_login");
			map.addAttribute("footer", "footer_login");
			map.addAttribute("ok", "FALSE");
		}

		CartInfo myCart = Utils.getCartInSession(request);
  
		model.addAttribute("cartForm", myCart);
		model.addAttribute("myCart", myCart);
		return "indexcontainer";
	}

	@GetMapping("/page/{pageNumber}")
	public String showBookPage(Authentication authentication, HttpServletRequest request, @PathVariable int pageNumber,
			Model model, ModelMap map, Principal principal) {

		PagedListHolder<?> pages = null;
		int pagesize = 10;
		List<Book> list = null;
		if (principal == null) {
			list = (List<Book>) listBookService.findAll();
			pages = (PagedListHolder<?>) request.getSession().getAttribute("bookListC");
		} else {
			Person per = userService.findByUsername(principal.getName());
			pages = (PagedListHolder<?>) request.getSession().getAttribute("bookListR");
			list = getList(per);
		}

		if (pages == null) {
			pages = new PagedListHolder<>(list);
			pages.setPageSize(pagesize);
		} else {
			final int goToPage = pageNumber - 1;
			if (goToPage <= pages.getPageCount() && goToPage >= 0) {
				pages.setPage(goToPage);
			}
		}
		if (principal == null)
			request.getSession().setAttribute("bookListC", pages);
		else
			request.getSession().setAttribute("bookListR", pages);
		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		String baseUrl = "/page/";
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("books", pages);

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

		if (authentication != null) {
			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
			List<String> roles = new ArrayList<String>();
			for (GrantedAuthority a : authorities) {
				roles.add(a.getAuthority());
			}
			if (isUser(roles)) {
				map.addAttribute("header", "header_user");
				map.addAttribute("footer", "footer_user");
				map.addAttribute("ok", "FALSE");
			} else if (isAdmin(roles)) {
				map.addAttribute("header", "header_admin");
				map.addAttribute("footer", "footer_admin");
				map.addAttribute("ok", "TRUE");
			}
		} else {
			map.addAttribute("header", "header_login");
			map.addAttribute("footer", "footer_login");
			map.addAttribute("ok", "FALSE");
		}

		CartInfo myCart = Utils.getCartInSession(request);
  
		model.addAttribute("cartForm", myCart);
		model.addAttribute("myCart", myCart);
		return "indexcontainer";
	}

	// tao list
	@ModelAttribute("list")
	public List<Book> getList(Person p) {
		List<Book> oldList = listBookService.findAll();
		List<Book> newList = new ArrayList<Book>();
		Long id = p.getId();
		for (Book b : oldList) {
			b.setOk(0);
			if (b.getPerson().getId() == id)
				b.setOk(1);
			newList.add(b);
		}
		return newList;
	}

	@GetMapping("/search/{pageNumber}")
	public String search(@RequestParam("s") String s, Authentication authentication, Model model,
			HttpServletRequest request, @PathVariable int pageNumber, ModelMap map, Principal principal) {

		if (authentication != null) {
			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
			List<String> roles = new ArrayList<String>();
			for (GrantedAuthority a : authorities) {
				roles.add(a.getAuthority());
			}
			if (isUser(roles)) {
				map.addAttribute("header", "header_user");
				map.addAttribute("footer", "footer_user");
				map.addAttribute("ok", "FALSE");
			} else if (isAdmin(roles)) {
				map.addAttribute("header", "header_admin");
				map.addAttribute("footer", "footer_admin");
				map.addAttribute("ok", "TRUE");
			}
		} else {
			map.addAttribute("header", "header_login");
			map.addAttribute("footer", "footer_login");
			map.addAttribute("ok", "FALSE");
		}

		if (s.equals("")) {
			return "redirect:/";
		}

//		List<Book> list = listBookService.search(s);
//		if (list == null) {
//			return "redirect:/book";
//		}
		PagedListHolder<?> pages = null;
		List<Book> listBookGet = null;
		if (principal == null) {
			listBookGet = (List<Book>) listBookService.findAll();
			pages = (PagedListHolder<?>) request.getSession().getAttribute("bookListC");
		} else {
			Person per = userService.findByUsername(principal.getName());
			pages = (PagedListHolder<?>) request.getSession().getAttribute("bookListR");
			listBookGet = getList(per);
		}
		List<Book> list = new ArrayList<Book>();
		for (Book a : listBookGet) {
			if (is(String.valueOf(a.getId()), s) || is(a.getTitle(), s) || is(a.getDomain(), s)
					|| is(a.getAuthors(), s) || is(publisherService.findOne(a.getPublisher().getId()).getPublisher(), s))
				list.add(a);
		}
		
		for (Book a : listBookGet) {
			if (error(String.valueOf(a.getId()), s) || error(a.getTitle(), s) || error(a.getDomain(), s)
					|| error(a.getAuthors(), s) || error(publisherService.find(a.getPublisher().getId()).getPublisher(),s))
				if (!list.contains(a))
					list.add(a);
		}

		int pagesize = 10;

		pages = new PagedListHolder<>(list);
		pages.setPageSize(pagesize);

		final int goToPage = pageNumber - 1;
		if (goToPage <= pages.getPageCount() && goToPage >= 0) {
			pages.setPage(goToPage);
		}

		if (principal == null)
			request.getSession().setAttribute("bookListC", pages);
		else
			request.getSession().setAttribute("bookListR", pages);

		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		String baseUrl = "/page/";

		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("books", pages);

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

		CartInfo cartInfo = Utils.getCartInSession(request);
		model.addAttribute("myCart", cartInfo);
		model.addAttribute("cartForm", cartInfo);

		return "indexcontainer";
	}

	@PreAuthorize("!(hasRole('EMPLOYEE') OR hasRole('ADMIN'))")
	@GetMapping("/login")
	public String login(ModelMap map, HttpServletRequest request, Model model) {
		map.addAttribute("header", "header_login");
		map.addAttribute("footer", "footer_login");

		CartInfo cartInfo = Utils.getCartInSession(request);
		model.addAttribute("myCart", cartInfo);
		model.addAttribute("cartForm", cartInfo);
		return "signin";
	}

	@PreAuthorize("hasRole('EMPLOYEE')")
	@GetMapping("/user")
	public String userPage(ModelMap map) {
		map.addAttribute("header", "header_user");
		map.addAttribute("footer", "footer_user");
		return "redirect:/";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/admin")
	public String getAdmin(ModelMap map) {
		map.addAttribute("user", getPrincipal());
		map.addAttribute("header", "header_admin");
		map.addAttribute("footer", "footer_admin");
		return "redirect:/";
	}

	@GetMapping(value = "/403")
	public String accessDeniedPage(Authentication authentication, ModelMap map, Model model, HttpServletRequest request){
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
		
		return "403";
	}

	private String getPrincipal() {
		String userName = null;
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof UserDetails) {
			userName = ((UserDetails) principal).getUsername();
		} else {
			userName = principal.toString();
		}
		return userName;
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

	boolean is(String a, String b) {
		a = unAccent(a);
		b = unAccent(b);
		b.replace("+", " ");
		b.replace("%20", " ");
		b = b.toLowerCase();
		a = a.toLowerCase();
		return b.equalsIgnoreCase(a);
	}

	boolean error(String a, String b) {
		a = unAccent(a);
		b = unAccent(b);
		b.replace("%20", "+");
		b = b.toLowerCase();
		a = a.toLowerCase();
		String[] arr = b.split("\\+");
		for (String item : arr)
			if (a.contains(item))
				return true;
		return false;
	}

	public static String unAccent(String s) {
		String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replace("đ", "d");
	}

}
