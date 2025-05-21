package com.gorkem.livebettingapi.application;

import com.gorkem.livebettingapi.domain.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @InjectMocks
    private CustomerService sut;

    @Test
    void testGetCustomerById_whenUserExists_shouldPass() {
        String existingUserId = "1";
        assertDoesNotThrow(() -> sut.getCustomerById(existingUserId));
    }

    @Test
    void testGetCustomerById_whenUserDoesNotExist_shouldThrowException() {
        String nonExistentUserId = "999";
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> sut.getCustomerById(nonExistentUserId));

        assertEquals(1001, exception.getErrorCode().getCode());
        assertTrue(exception.getMessage().contains(nonExistentUserId));
    }

}