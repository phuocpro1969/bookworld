package pq.jdev.b001.bookstore.cart.validator;

import pq.jdev.b001.bookstore.cart.dto.CustomerDTO;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
 
@Component
public class CustomerDTOValidator implements Validator {
 
   private EmailValidator emailValidator = EmailValidator.getInstance();
 
   // This validator only checks for the CustomerDTO.
   @Override
   public boolean supports(Class<?> clazz) {
      return clazz == CustomerDTO.class;
   }
 
   @Override
   public void validate(Object target, Errors errors) {
        CustomerDTO custDTO = (CustomerDTO) target;
 
        // Check the fields of CustomerDTO.
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "NotEmpty.customerDTO.name");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty.customerDTO.email");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address", "NotEmpty.customerDTO.address");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phone", "NotEmpty.customerDTO.phone");
    
        if (!emailValidator.isValid(custDTO.getEmail())) {
            errors.rejectValue("email", "Pattern.customerDTO.email");
        }
   }
}
