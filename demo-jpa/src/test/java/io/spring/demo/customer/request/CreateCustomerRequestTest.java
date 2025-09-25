package io.spring.demo.customer.request;

import io.spring.demo.error.ValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateCustomerRequestTest {

    @Test
    void does_not_create_request_when_name_is_blank() {
        // given
        var name = "   ";

        // when / then
        assertThatThrownBy(() -> new CreateCustomerRequest(name, "test@test.com"))
            .isInstanceOf(ValidationException.class)
            .hasMessage("name must not be empty");
    }
}