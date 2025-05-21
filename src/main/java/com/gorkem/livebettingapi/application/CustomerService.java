package com.gorkem.livebettingapi.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gorkem.livebettingapi.domain.constant.ErrorCodes;
import com.gorkem.livebettingapi.domain.exception.BadRequestException;
import com.gorkem.livebettingapi.domain.model.dto.CustomerDTO;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
public class CustomerService {
    @SneakyThrows
    public void getCustomerById(String userId) {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getClassLoader().getResourceAsStream("customer.json");
        List<CustomerDTO> customers = mapper.readValue(is, new TypeReference<>() {
        });
        boolean exists = customers.stream().anyMatch(customer -> customer.getId().equals(userId));
        if (!exists) {
            throw new BadRequestException(ErrorCodes.NOT_FOUND, String.format("User with id %s not found", userId));
        }
    }
}
