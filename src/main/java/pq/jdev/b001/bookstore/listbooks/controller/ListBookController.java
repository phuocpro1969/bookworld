package pq.jdev.b001.bookstore.listbooks.controller;

import java.security.Principal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.google.gson.Gson;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pq.jdev.b001.bookstore.books.model.Book;
import pq.jdev.b001.bookstore.books.service.BookService;
import pq.jdev.b001.bookstore.books.service.UploadPathService;
import pq.jdev.b001.bookstore.books.web.dto.UploadInformationDTO;
import pq.jdev.b001.bookstore.category.model.Category;
import pq.jdev.b001.bookstore.category.service.CategoryService;
import pq.jdev.b001.bookstore.listbooks.service.ListBookService;
import pq.jdev.b001.bookstore.publishers.model.Publishers;
import pq.jdev.b001.bookstore.publishers.service.PublisherService;
import pq.jdev.b001.bookstore.users.model.Person;
import pq.jdev.b001.bookstore.users.service.UserService;
import pq.jdev.b001.bookstore.cart.model.CartInfo;
import pq.jdev.b001.bookstore.cart.utils.Utils;

@Controller
public class ListBookController {
	@Autowired
	private ListBookService listBookService;

	@Autowired
	private BookService bookService;

	@Autowired
	private UserService userService;

	@Autowired
	private PublisherService publisherService;

	@Autowired
	private CategoryService categoryservice;

	@Autowired
	private UploadPathService uploadPathService;

	@GetMapping("/book")
	public String index(Authentication authentication, ModelMap map, Model model, HttpServletRequest request,
			RedirectAttributes redirect, Principal principal) {

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
			request.getSession().setAttribute("bookListCC", pages);
		else
			request.getSession().setAttribute("bookListCR", pages);

		if (model.asMap().get("success") != null)
			redirect.addFlashAttribute("success", model.asMap().get("success").toString());

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

		
		return "redirect:/book/page/1";

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

	@GetMapping("/book/page/{pageNumber}")
	public String showBookPage(Authentication authentication, HttpServletRequest request, @PathVariable int pageNumber,
			Model model, ModelMap map, Principal principal) {

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

		PagedListHolder<?> pages = null;
		int pagesize = 10;
		List<Book> list = null;
		if (principal == null) {
			list = (List<Book>) listBookService.findAll();
			pages = (PagedListHolder<?>) request.getSession().getAttribute("bookListCC");
		} else {
			Person per = userService.findByUsername(principal.getName());
			pages = (PagedListHolder<?>) request.getSession().getAttribute("bookListCR");
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
			request.getSession().setAttribute("bookListCC", pages);
		else
			request.getSession().setAttribute("bookListCR", pages);
		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		String baseUrl = "/book/page/";

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

		return "listbook";
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
	@GetMapping("/book/create")
	public String create(Model model, ModelMap map, Authentication authentication) {
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
		model.addAttribute("book", new Book());
		return "savebook";
	}

	@GetMapping("/book/{id}/edit")
	public String edit(@PathVariable Long id, Model model, ModelMap map, Authentication authentication) {
		if (authentication != null) {
			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
			List<String> roles = new ArrayList<String>();
			for (GrantedAuthority a : authorities) {
				roles.add(a.getAuthority());
			}

			if (isAdmin(roles)) {
				map.addAttribute("header", "header_admin");
				map.addAttribute("footer", "footer_admin");
			} else if (isUser(roles)) {
				Long personId = userService.findByUsername(authentication.getName()).getId();
				Long personIdInBook = listBookService.findOne(id).getPerson().getId();
				if (personIdInBook != personId)
					return "redirect:/";
				map.addAttribute("header", "header_user");
				map.addAttribute("footer", "footer_user");
			}
		} else {
			map.addAttribute("header", "header_login");
			map.addAttribute("footer", "footer_login");
			return "redirect:/";
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
		model.addAttribute("book", listBookService.findOne(id));
		return "savebook";
	}

	@PostMapping("/book/save")
	public String save(@Valid Book book, BindingResult result, RedirectAttributes redirect, ModelMap map,
			Principal principal) {
		if (result.hasErrors()) {
			map.addAttribute("header", "header_admin");
			map.addAttribute("footer", "footer_admin");
			return "savebook";
		}

		if (book.getPerson() == null)
			book.setPerson(userService.findByUsername(principal.getName()));
		listBookService.save(book);
		redirect.addFlashAttribute("success", "Saved book successfully!");
		return "redirect:/book";
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
	@GetMapping("/book/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes redirect, Authentication authentication) {
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		List<String> roles = new ArrayList<String>();
		for (GrantedAuthority a : authorities) {
			roles.add(a.getAuthority());
		}
		if ((authentication != null) && ((userService.findByUsername(authentication.getName()).getId() 
			== listBookService.findOne(id).getPerson().getId()) || (isAdmin(roles)))) {
			listBookService.delete(id);
			uploadPathService.deleteAllUploadByIdBook(id);
			redirect.addFlashAttribute("success", "Deleted book successfully!");
		}
		return "redirect:/book";
	}

	@GetMapping("/book/search/{pageNumber}")
	public String search(@RequestParam("s") String s, @RequestParam("category") String category, Authentication authentication, Model model,
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
			return "redirect:/book";
		}

//		List<Book> list = listBookService.search(s);
//		if (list == null) {
//			return "redirect:/book";
//		}

		PagedListHolder<?> pages = null;
		int pagesize = 10;

		List<Book> listBookGet = null;
		if (principal == null) {
			listBookGet = (List<Book>) listBookService.findAll();
		} else {
			Person per = userService.findByUsername(principal.getName());
			listBookGet = getList(per);
		}

		List<Book> list = new ArrayList<Book>();

		for (Book a : listBookGet) {
			if (is(String.valueOf(a.getId()), s) || is(a.getTitle(), s) || is(a.getDomain(), s) || is(a.getAuthors(), s)
					|| is(publisherService.findOne(a.getPublisher().getId()).getPublisher(), s))
				list.add(a);
		}

		for (Book a : listBookGet) {
			if (error(String.valueOf(a.getId()), s) || error(a.getTitle(), s) || error(a.getDomain(), s)
					|| error(a.getAuthors(), s)
					|| error(publisherService.find(a.getPublisher().getId()).getPublisher(), s))
				if (!list.contains(a))
					list.add(a);
		}

		pages = new PagedListHolder<>(list);
		pages.setPageSize(pagesize);

		final int goToPage = pageNumber - 1;
		if (goToPage <= pages.getPageCount() && goToPage >= 0) {
			pages.setPage(goToPage);
		}

		if (principal == null)
			request.getSession().setAttribute("bookListCC", pages);
		else
			request.getSession().setAttribute("bookListCR", pages);
		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		String baseUrl = "/book/page/";

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

		return "listbook";
	}

	@GetMapping("/searchAutocomplete")
	// @RequestMapping(value="/searchAutocomplete")
	@ResponseBody
	public List<UploadInformationDTO> searchAutocomplete(HttpServletRequest request) {
		
		// Gson gson = new Gson();
		// System.out.println("Term ====>" + gson.toJson(request.getParameter("term")));
		return this.bookService.searchAutocomplete(request.getParameter("term"));
		 
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
		System.out.println(a);
		System.out.println(b);
		b.replace("%20", "+");
		b = b.toLowerCase();
		a = a.toLowerCase();
		String[] arr = b.split("\\+");
		System.out.println(a);
		System.out.println(arr.toString());
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
