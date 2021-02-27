package pq.jdev.b001.bookstore.books.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pq.jdev.b001.bookstore.books.model.Book;
import pq.jdev.b001.bookstore.books.service.BookService;
import pq.jdev.b001.bookstore.books.web.dto.UploadInformationDTO;
import pq.jdev.b001.bookstore.cart.model.CartInfo;
import pq.jdev.b001.bookstore.cart.utils.Utils;
import pq.jdev.b001.bookstore.category.model.Category;
import pq.jdev.b001.bookstore.category.service.CategoryService;
import pq.jdev.b001.bookstore.publishers.model.Publishers;
import pq.jdev.b001.bookstore.publishers.service.PublisherService;
import pq.jdev.b001.bookstore.users.model.Person;
import pq.jdev.b001.bookstore.users.service.UserService;

@Controller
public class BookController {

	@Autowired
	private BookService bookService;

	// @Autowired
	// private BookDAO bookDAO;

	@Autowired
	private UserService userService;

	@Autowired
	private PublisherService publisherService;

	@Autowired
	private CategoryService categoryService;

	@ModelAttribute("dto")
	public UploadInformationDTO dTO() {
		return new UploadInformationDTO();
	}

	@GetMapping("/bookview")
	public String loadGuestView(Model model) {
		model.addAttribute("books", bookService.viewAllBooks());
		return "/book/page/1";
	}

	@GetMapping("/bookview/books")
	public String loadInformationGuestView(Model model) {
		model.addAttribute("books", bookService.viewAllBooks());
		return "book/page/1";
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
	@GetMapping("/bookview/createform")
	public String initializeCreating(@AuthenticationPrincipal User user, UploadInformationDTO dto, Model model,
			RedirectAttributes redirectAttributes, Authentication authentication, ModelMap map, HttpServletRequest request) {
		try {
			Person currentUser = new Person();
			currentUser = userService.findByUsername(user.getUsername());
			dto.setPublishers(bookService.showAllPublishers());
			dto.setCategories(bookService.showAllCategories());
			model.addAttribute("dto", dto);
			model.addAttribute("currentUser", currentUser);

			CartInfo myCart = Utils.getCartInSession(request);
			model.addAttribute("cartForm", myCart);
			model.addAttribute("myCart", myCart);
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
			return "bookview/createform";
		} catch (Exception e) {
			CartInfo myCart = Utils.getCartInSession(request);
			model.addAttribute("cartForm", myCart);
			model.addAttribute("myCart", myCart);
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
			return "bookview/error";
		}
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
	@PostMapping("/bookview/createform")
	public String createBook(@AuthenticationPrincipal User user, UploadInformationDTO dto,
			@RequestParam(value = "idChecked", required = false) List<String> categoriesId, Model model, ModelMap map,
			Authentication authentication, HttpServletRequest request) {
		try {

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
			
			Person currentUser = userService.findByUsername(user.getUsername());
			if (!bookService.checkInput(dto)) {
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
				return "bookview/error";
			}
			bookService.save(dto, currentUser, categoriesId);
			return "bookview/success";
		} catch (Exception e) {
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
			return "bookview/error";
		}
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
	@GetMapping("/bookview/editform/{id}")
	public String initializeEditing(@AuthenticationPrincipal User user, UploadInformationDTO dto,
			@PathVariable("id") String id, Model model, Authentication authentication, ModelMap map, HttpServletRequest request) {

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
		
		try {
			dto = new UploadInformationDTO();
			Person currentUser = new Person();
			Long idEditBook = Long.parseLong(id);
			Book editBook = bookService.findBookByID(idEditBook);
			currentUser = userService.findByUsername(user.getUsername());
			if (!bookService.checkRightInteraction(user, editBook)) {
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
				return "/bookview/error";
			}
			model.addAttribute("currentUser", currentUser);
			dto.setBookId(editBook.getId());
			dto.setTitle(editBook.getTitle());
			dto.setPrice(editBook.getPrice());
			dto.setDomain(editBook.getDomain());
			dto.setAuthors(editBook.getAuthors());
			dto.setPublishers(bookService.showAllPublishers());
			String currentSelected = editBook.getPublisher().getPublisher();
			model.addAttribute("currentSelected", currentSelected);
			dto.setPublishedYear(editBook.getPublishedYear());
			dto.setSelectCategories(bookService.showAllCategoriesWithFlag(editBook));
			dto.setDescription(editBook.getDescription());
			model.addAttribute("id", editBook.getId());
			model.addAttribute("dto", dto);
			return "bookview/editform";
		} catch (Exception e) {
			
			model.addAttribute("cartForm", myCart);
			model.addAttribute("myCart", myCart);
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
			return "/bookview/error";
		}
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
	@PostMapping("/bookview/editform")
	public String editBook(@AuthenticationPrincipal User user, UploadInformationDTO dto,
			@RequestParam("id") String editBookId,
			@RequestParam(value = "idChecked", required = false) List<String> categoriesId, Model model,
			RedirectAttributes redirectAttributes, ModelMap map, Authentication authentication, HttpServletRequest request) {
		try {

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
			Person currentUser = new Person();
			Long id = Long.parseLong(editBookId);
			Book editBook = bookService.findBookByID(id);
			currentUser = userService.findByUsername(user.getUsername());
			if (!bookService.checkRightInteraction(user, editBook)) {
				return "redirect:/login";
			}
			bookService.update(dto, currentUser, categoriesId, editBook);
			return "bookview/success";
		} catch (Exception e) {
			CartInfo myCart = Utils.getCartInSession(request);
			model.addAttribute("cartForm", myCart);
			model.addAttribute("myCart", myCart);
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
			return "bookview/error";
		}
	}

	@GetMapping("/bookview/infor/{id}")
	public String showBookDetails(UploadInformationDTO dto, @PathVariable("id") String id, Model model, ModelMap map,
			Authentication authentication, HttpServletRequest request) {
		try {
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
			model.addAttribute("book", new Book());

			Long idCurrent = Long.parseLong(id);
			Book currentBook = bookService.findBookByID(idCurrent);
			dto.setTitle(currentBook.getTitle());
			dto.setPrice(currentBook.getPrice());
			dto.setDomain(currentBook.getDomain());
			dto.setAuthors(currentBook.getAuthors());
			String publisher = currentBook.getPublisher().getPublisher();
			dto.setPublishedYear(currentBook.getPublishedYear());
			dto.setUploadedDate(currentBook.getUploadedDate());
			String stringCategories = "";
			for (Category category : currentBook.getCategories()) {
				stringCategories = stringCategories + category.getName() + ", ";
			}
			model.addAttribute("stringCategories", stringCategories);
			dto.setDescription(currentBook.getDescription());
			model.addAttribute("publisher", publisher);
			model.addAttribute("book", currentBook);
			model.addAttribute("dto", dto);
			model.addAttribute("id", id);

			CartInfo myCart = Utils.getCartInSession(request);
			model.addAttribute("cartForm", myCart);
			model.addAttribute("myCart", myCart);
			return "bookview/infor";
		} catch (Exception e) {
			CartInfo myCart = Utils.getCartInSession(request);
			model.addAttribute("cartForm", myCart);
			model.addAttribute("myCart", myCart);
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
			return "bookview/error";
		}
	}

	

	@RequestMapping(value = { "/bookImage" }, method = RequestMethod.GET)
   public void productImage(HttpServletRequest request, HttpServletResponse response, Model model,
         @RequestParam("bookId") Long bookId) throws IOException {
      Book book = null;
      if (bookId != null) {
		book = this.bookService.findBookByID(bookId);
      }
      if (book != null && book.getPicture() != null) {
         response.setContentType("images/jpeg, images/jpg, images/png, images/gif");
         response.getOutputStream().write(book.getPicture());
      }
      response.getOutputStream().close();
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
