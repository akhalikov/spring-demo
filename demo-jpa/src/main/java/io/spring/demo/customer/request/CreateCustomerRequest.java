package io.spring.demo.customer.request;

import static io.spring.demo.util.Checks.isValidEmail;
import static io.spring.demo.util.Checks.validate;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public record CreateCustomerRequest(String name, String email) {

    public CreateCustomerRequest {
        validate(isNotBlank(name), "name must not be empty");
        validate(isNotBlank(email), "email must not be empty");
        validate(isValidEmail(email), "email is invalid");
    }
}
