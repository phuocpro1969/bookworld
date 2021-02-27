package pq.jdev.b001.bookstore.users.web;

import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DemoControllerAdvice {
	 @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	 
	 public String handleIOException(HttpRequestMethodNotSupportedException ex) {
	        return "redirect:/";
	    }
}
