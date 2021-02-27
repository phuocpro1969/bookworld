package pq.jdev.b001.bookstore.cart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;

import pq.jdev.b001.bookstore.books.model.Book;
import pq.jdev.b001.bookstore.books.model.BookInfo;
import pq.jdev.b001.bookstore.books.service.BookService;
import pq.jdev.b001.bookstore.cart.dto.CustomerDTO;
import pq.jdev.b001.bookstore.cart.model.CartInfo;
import pq.jdev.b001.bookstore.cart.model.CustomerInfo;
import pq.jdev.b001.bookstore.cart.model.Order;
import pq.jdev.b001.bookstore.cart.model.OrderDetailInfo;
import pq.jdev.b001.bookstore.cart.model.OrderInfo;
import pq.jdev.b001.bookstore.cart.pagination.PaginationResult;
import pq.jdev.b001.bookstore.cart.repository.CartRepository;
import pq.jdev.b001.bookstore.cart.service.CartService;
import pq.jdev.b001.bookstore.cart.utils.Utils;
import pq.jdev.b001.bookstore.cart.validator.CustomerDTOValidator;
import pq.jdev.b001.bookstore.export.OrderExcelExporter;
import pq.jdev.b001.bookstore.export.OrderPDFExporter;
import pq.jdev.b001.bookstore.payment.config.PaypalPaymentIntent;
import pq.jdev.b001.bookstore.payment.config.PaypalPaymentMethod;
import pq.jdev.b001.bookstore.payment.service.PaypalService;
import pq.jdev.b001.bookstore.users.model.Person;

import java.util.List;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.DocumentException;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

@Controller
public class CartController {

   @Autowired
   private CartService cartService;

   @Autowired
   private BookService bookService;

   @Autowired
   private CustomerDTOValidator customerDTOValidator;

   @InitBinder
   public void myInitBinder(WebDataBinder dataBinder) {
      Object target = dataBinder.getTarget();
      if (target == null) {
         return;
      }
      System.out.println("Target=" + target);

      // Case update quantity in cart
      // (@ModelAttribute("cartForm") @Validated CartInfo cartForm)
      if (target.getClass() == CartInfo.class) {

      }

      // Case save customer information.
      // (@ModelAttribute @Validated CustomerInfo customerForm)
      else if (target.getClass() == CustomerDTO.class) {
         dataBinder.setValidator(customerDTOValidator);
      }

   }

   @RequestMapping({ "/buyBook" })
   public String listProductHandler(HttpServletRequest request, Model model, Authentication authentication,
         ModelMap map, //
         @RequestParam(value = "bookId", defaultValue = "") Long bookId) {

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

      Book book = null;
      if (bookId != null) {
         book = bookService.findBookByID(bookId);
      }
      if (book != null) {

         //
         CartInfo cartInfo = Utils.getCartInSession(request);

         BookInfo bookInfo = new BookInfo(book);

         cartInfo.addBook(bookInfo, 1);
      }

      return "redirect:/shoppingCart";
   }

   @RequestMapping({ "/shoppingCartRemoveBook" })
   public String removeProductHandler(HttpServletRequest request, Model model, //
         @RequestParam(value = "bookId", defaultValue = "") Long bookId) {
      Book book = null;
      if (bookId != null) {
         book = bookService.findBookByID(bookId);
      }
      if (book != null) {

         CartInfo cartInfo = Utils.getCartInSession(request);

         BookInfo bookInfo = new BookInfo(book);

         cartInfo.removeProduct(bookInfo);

      }

      return "redirect:/shoppingCart";
   }

   // POST: Update quantity for product in cart
   @RequestMapping(value = { "/shoppingCart" }, method = RequestMethod.POST)
   public String shoppingCartUpdateQty(HttpServletRequest request, Authentication authentication, ModelMap map, //
         Model model, //
         @ModelAttribute("cartForm") CartInfo cartForm) {

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

      CartInfo cartInfo = Utils.getCartInSession(request);
      cartInfo.updateQuantity(cartForm);

      return "redirect:/shoppingCart";
   }

   // GET: Show cart.
   @RequestMapping(value = { "/shoppingCart" }, method = RequestMethod.GET)
   public String shoppingCartHandler(HttpServletRequest request, Model model, Authentication authentication,
         ModelMap map) {

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
      CartInfo myCart = Utils.getCartInSession(request);

      model.addAttribute("cartForm", myCart);
      return "shoppingCart";
   }

   // GET: Enter customer information.
   @RequestMapping(value = { "/checkout" }, method = RequestMethod.GET)
   public String shoppingCartCustomerForm(HttpServletRequest request, Model model, Authentication authentication,
         ModelMap map) {

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

      CartInfo cartInfo = Utils.getCartInSession(request);

      if (cartInfo.isEmpty()) {

         return "redirect:/shoppingCart";
      }
      CustomerInfo customerInfo = cartInfo.getCustomerInfo();

      CustomerDTO customerDTO = new CustomerDTO(customerInfo);

      model.addAttribute("customerDTO", customerDTO);
      model.addAttribute("myCart", cartInfo);
      model.addAttribute("cartForm", cartInfo);

      return "checkout";
   }

   // POST: Save customer information.
   @RequestMapping(value = { "/checkout" }, method = RequestMethod.POST)
   public String shoppingCartCustomerSave(HttpServletRequest request, Authentication authentication, ModelMap map, //
         Model model, //
         @ModelAttribute("customerDTO") @Validated CustomerDTO customerForm, //
         BindingResult result, //
         final RedirectAttributes redirectAttributes) {
      CartInfo cartInfo = Utils.getCartInSession(request);
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

      if (result.hasErrors()) {
         customerForm.setValid(false);
         model.addAttribute("myCart", cartInfo);
         model.addAttribute("cartForm", cartInfo);
         // Forward to reenter customer info.
         return "checkout";
      }

      customerForm.setValid(true);

      CustomerInfo customerInfo = new CustomerInfo(customerForm);
      cartInfo.setCustomerInfo(customerInfo);

      return "redirect:/checkoutComfirmation";
   }

   // GET: Show information to confirm.
   @RequestMapping(value = { "/checkoutComfirmation" }, method = RequestMethod.GET)
   public String shoppingCartConfirmationReview(HttpServletRequest request, Model model, Authentication authentication,
         ModelMap map) {

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
      CartInfo cartInfo = Utils.getCartInSession(request);

      if (cartInfo == null || cartInfo.isEmpty()) {

         return "redirect:/shoppingCart";
      } else if (!cartInfo.isValidCustomer()) {

         return "redirect:/checkout";
      }
      model.addAttribute("myCart", cartInfo);
      model.addAttribute("cartForm", cartInfo);

      return "checkoutComfirmation";
   }

   // POST: Submit Cart (Save)
   @RequestMapping(value = { "/checkoutComfirmation" }, method = RequestMethod.POST)

   public String shoppingCartConfirmationSave(HttpServletRequest request, Model model, Authentication authentication,
         ModelMap map) {
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

      CartInfo cartInfo = Utils.getCartInSession(request);

      if (cartInfo.isEmpty()) {

         return "redirect:/shoppingCart";
      } else if (!cartInfo.isValidCustomer()) {

         return "redirect:/checkout";
      }

      try {
         cartService.saveOrder(cartInfo);
      } catch (Exception e) {
         return "checkoutComfirmation";
      }

      // Remove Cart from Session.
      Utils.removeCartInSession(request);
      // Store last cart.
      Utils.storeLastOrderedCartInSession(request, cartInfo);

      return "redirect:/shoppingCartFinalize";
   }

   @RequestMapping(value = { "/shoppingCartFinalize" }, method = RequestMethod.GET)
   public String shoppingCartFinalize(HttpServletRequest request, Model model, Authentication authentication,
         ModelMap map) {

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

      CartInfo lastOrderedCart = Utils.getLastOrderedCartInSession(request);

      if (lastOrderedCart == null) {
         return "redirect:/shoppingCart";
      }
      model.addAttribute("lastOrderedCart", lastOrderedCart);
      return "shoppingCartFinalize";
   }

   @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
   @GetMapping("/orderList")
   public String index(Model model, HttpServletRequest request, RedirectAttributes redirect) {
      request.getSession().setAttribute("listOrder", null);

      if (model.asMap().get("success") != null)
         redirect.addFlashAttribute("success", model.asMap().get("success").toString());
      return "redirect:/orderList/page/1";
   }

   @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
   @GetMapping("/orderList/page/{pageNumber}")
   public String orderList(HttpServletRequest request, @PathVariable int pageNumber, Model model, ModelMap map,
         Authentication authentication) {

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

      PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("listOrder");

      int pagesize = 7;
      List<Order> list = cartService.findAll();
      if (pages == null) {
         pages = new PagedListHolder<>(list);
         pages.setPageSize(pagesize);
      } else {
         final int goToPage = pageNumber - 1;
         if (goToPage <= pages.getPageCount() && goToPage >= 0) {
            pages.setPage(goToPage);
         }
      }
      request.getSession().setAttribute("listOrder", pages);

      int current = pages.getPage() + 1;
      int begin = Math.max(1, current - list.size());
      int end = Math.min(begin + 5, pages.getPageCount());
      int totalPageCount = pages.getPageCount();
      String baseUrl = "/orderList/page/";

      model.addAttribute("beginIndex", begin);
      model.addAttribute("endIndex", end);
      model.addAttribute("currentIndex", current);
      model.addAttribute("totalPageCount", totalPageCount);
      model.addAttribute("baseUrl", baseUrl);
      model.addAttribute("listOrder", pages);

      CartInfo myCart = Utils.getCartInSession(request);
      model.addAttribute("cartForm", myCart);
      model.addAttribute("myCart", myCart);
      return "orderList";
   }
   

   @RequestMapping({ "/orderRemove" })
   public String removeOrderHandler(HttpServletRequest request, Model model,
         @RequestParam(value = "orderId", defaultValue = "") String orderId) {
      Order order = null;
      

      
      return "redirect:/orderList";
   }

   @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
   @GetMapping("/statistis")
   public String staTisTis(HttpServletRequest request, Model model, ModelMap map,
         Authentication authentication) {

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
      return "statistis";
   }

   // @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
   @RequestMapping(value = { "/order" }, method = RequestMethod.GET)
   public String orderView(Model model, @RequestParam("orderId") String orderId, HttpServletRequest request,
         ModelMap map, Authentication authentication) {

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

      OrderInfo orderInfo = null;
      if (orderId != null) {
         orderInfo = cartService.getOrderInfo(orderId);
      }
      if (orderInfo == null) {
         return "redirect:/orderList";
      }
      List<OrderDetailInfo> details = cartService.listOrderDetailInfos(orderId);
      orderInfo.setDetails(details);

      model.addAttribute("orderInfo", orderInfo);
      CartInfo myCart = Utils.getCartInSession(request);
      model.addAttribute("cartForm", myCart);
      model.addAttribute("myCart", myCart);

      return "order";
   }

   @GetMapping("/order/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Orders_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
        List<Order> listOrders = cartService.findAll();
        OrderExcelExporter excelExporter = new OrderExcelExporter(listOrders);
        excelExporter.export(response);   
        
    }  

    @GetMapping("/order/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
         
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Orders_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);
         
        List<Order> listOrders = cartService.findAll();
         
        OrderPDFExporter exporter = new OrderPDFExporter(listOrders);
        exporter.export(response);
         
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
