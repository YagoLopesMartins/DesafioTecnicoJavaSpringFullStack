package com.desafiotecnico.matera.account.api;

import com.desafiotecnico.matera.account.domain.Account;
import com.desafiotecnico.matera.account.dto.CreateAccountRequest;
import com.desafiotecnico.matera.account.service.AccountService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@ActiveProfiles("test")
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    void should_create_account_and_return_201() throws Exception {
        Account account = Account.builder()
                .id("id-123")
                .number("12345-0")
                .balance(BigDecimal.valueOf(1000L))
                .build();

        Mockito.when(accountService.createAccount(any(CreateAccountRequest.class)))
                .thenReturn(account);

        mockMvc.perform(post("/api/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "number": "12345-0",
                  "initialBalance": 1000.00
                }
                """))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/accounts/id-123"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is("id-123")))
            .andExpect(jsonPath("$.number", is("12345-0")))
            .andExpect(jsonPath("$.balance").value(1000.0));
    }

    @Test
    void should_return_400_when_payload_is_invalid() throws Exception {
        mockMvc.perform(post("/api/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "number": "",
                  "initialBalance": -10
                }
                """))
            .andExpect(status().isBadRequest());
    }
}
