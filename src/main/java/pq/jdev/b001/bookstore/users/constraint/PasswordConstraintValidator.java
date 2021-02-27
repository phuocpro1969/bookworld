package pq.jdev.b001.bookstore.users.constraint;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.passay.AlphabeticalSequenceRule;
import org.passay.DigitCharacterRule;
import org.passay.LengthRule;
import org.passay.NumericalSequenceRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.QwertySequenceRule;
import org.passay.RuleResult;
import org.passay.SpecialCharacterRule;
import org.passay.UppercaseCharacterRule;
import org.passay.WhitespaceRule;


public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

	@Override
	public void initialize(ValidPassword constraintAnnotation) {

	}

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		PasswordValidator validator = new PasswordValidator(Arrays.asList(new LengthRule(8, 70),
				new UppercaseCharacterRule(1), new DigitCharacterRule(1), new SpecialCharacterRule(1),
				new NumericalSequenceRule(3, false), new AlphabeticalSequenceRule(3, false),
				new QwertySequenceRule(3, false), new WhitespaceRule()));

		RuleResult result = validator.validate(new PasswordData(password));
		if (result.isValid()) {
			return true;
		}
		List<String> messages = validator.getMessages(result);
		String messageTemplate = messages.stream().collect(Collectors.joining(","));
		context.buildConstraintViolationWithTemplate(messageTemplate)
        .addConstraintViolation()
        .disableDefaultConstraintViolation();
		return false;
	}
}
