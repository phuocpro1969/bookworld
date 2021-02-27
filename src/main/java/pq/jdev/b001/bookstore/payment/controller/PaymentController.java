package pq.jdev.b001.bookstore.payment.controller;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pq.jdev.b001.bookstore.cart.model.CartInfo;
import pq.jdev.b001.bookstore.cart.service.CartService;
import pq.jdev.b001.bookstore.payment.config.PaypalPaymentIntent;
import pq.jdev.b001.bookstore.payment.config.PaypalPaymentMethod;
import pq.jdev.b001.bookstore.payment.service.PaypalService;
import pq.jdev.b001.bookstore.payment.util.Utils;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import java.sql.Timestamp;

import javax.validation.Valid;

import org.springframework.beans.support.PagedListHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import pq.jdev.b001.bookstore.category.model.Category;
import pq.jdev.b001.bookstore.category.service.CategoryService;
import pq.jdev.b001.bookstore.publishers.model.Publishers;
import pq.jdev.b001.bookstore.publishers.service.PublisherService;
import pq.jdev.b001.bookstore.users.service.UserService;


@Controller
public class PaymentController {
	
	@Autowired
	private CartService cartService;
	public static final String URL_PAYPAL_SUCCESS = "pay/paymentSuccess";
	public static final String URL_PAYPAL_CANCEL = "pay/paymentCancel";
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private PaypalService paypalService;
	
	
	@PostMapping("/pay")
	public String pay(HttpServletRequest request,@RequestParam("price") double price ){
		String cancelUrl = Utils.getBaseURL(request) + "/" + URL_PAYPAL_CANCEL;
		String successUrl = Utils.getBaseURL(request) + "/" + URL_PAYPAL_SUCCESS;
		try {
			Payment payment = paypalService.createPayment(
					price, 
					"USD", 
					PaypalPaymentMethod.paypal, 
					PaypalPaymentIntent.sale,
					"payment description", 
					cancelUrl, 
					successUrl);
			for(Links links : payment.getLinks()){
				if(links.getRel().equals("approval_url")){
					return "redirect:" + links.getHref();
				}
			}
			
		} catch (PayPalRESTException e) {
			log.error(e.getMessage());
		}

		return "redirect:/";
	}

	@GetMapping(URL_PAYPAL_CANCEL)
	public String cancelPay(HttpServletRequest request, Model model, ModelMap map, Authentication authentication){
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
        CartInfo myCart = pq.jdev.b001.bookstore.cart.utils.Utils.getCartInSession(request);
			model.addAttribute("cartForm", myCart);
			model.addAttribute("myCart", myCart);
		return "paymentCancel";
	}

	@GetMapping(URL_PAYPAL_SUCCESS)
	public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId, HttpServletRequest request, Model model, Authentication authentication, ModelMap map){
        
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
			Payment payment = paypalService.executePayment(paymentId, payerId);
			if(payment.getState().equals("approved")){
                CartInfo myCart = pq.jdev.b001.bookstore.cart.utils.Utils.getCartInSession(request);
                model.addAttribute("cartForm", myCart);
				model.addAttribute("myCart", myCart);
				if (myCart == null) {
					return "redirect:/shoppingCart";
				}

				try {
					cartService.saveOrder(myCart);
				} catch (Exception e) {
					return "redirect:/";
				}

				 // Remove Cart from Session.
				 pq.jdev.b001.bookstore.cart.utils.Utils.removeCartInSession(request);
				 // Store last cart.
				 pq.jdev.b001.bookstore.cart.utils.Utils.storeLastOrderedCartInSession(request, myCart);

				CartInfo lastOrderedCart = pq.jdev.b001.bookstore.cart.utils.Utils.getLastOrderedCartInSession(request);
				model.addAttribute("lastOrderedCart", lastOrderedCart);
				
				return "paymentSuccess";
			}
		} catch (PayPalRESTException e) {
			log.error(e.getMessage());
		}

		// CartInfo lastOrderedCart = pq.jdev.b001.bookstore.cart.utils.Utils.getLastOrderedCartInSession(request);
 
		// if (lastOrderedCart == null) {
		// 	return "redirect:/shoppingCart";
		// }
		// model.addAttribute("lastOrderedCart", lastOrderedCart);
		
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
	
}

