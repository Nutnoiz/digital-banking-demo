package com.unb.digitalbanking.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.http.MediaType;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthSecurityIntegrationTest {

    private static final String LOGIN_JSON = """
            {
                "username": "demo",
                "password": "Demo123!"
            }
            """;

    private static final String TRANSFER_JSON = """
            {
                "sourceAccountId": 1,
                "destinationAccountId": 2,
                "amount": 1000.00,
                "description": "JWT secured transfer test"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void resetBankingState() {

        /*
         * Keep every security test independent from test execution order.
         */

        jdbcTemplate.update("DELETE FROM transactions");

        jdbcTemplate.update("""
                UPDATE accounts
                SET
                    balance = CASE id
                        WHEN 1 THEN 100000.00
                        WHEN 2 THEN 25000.00
                        WHEN 3 THEN 75000.00
                        WHEN 4 THEN 50000.00
                    END,
                    currency = CASE id
                        WHEN 1 THEN 'THB'
                        WHEN 2 THEN 'THB'
                        WHEN 3 THEN 'THB'
                        WHEN 4 THEN 'THB'
                    END,
                    status = 'ACTIVE'
                WHERE id IN (1, 2, 3, 4)
                """);

        jdbcTemplate.update("""
                INSERT INTO transactions
                    (
                        account_id,
                        transaction_type,
                        amount,
                        balance_before,
                        balance_after,
                        reference_no,
                        description
                    )
                VALUES
                    (
                        1,
                        'DEPOSIT',
                        100000.00,
                        0.00,
                        100000.00,
                        'DEP-000001',
                        'Initial demo deposit'
                    ),
                    (
                        2,
                        'DEPOSIT',
                        25000.00,
                        0.00,
                        25000.00,
                        'DEP-000002',
                        'Initial demo deposit'
                    ),
                    (
                        3,
                        'DEPOSIT',
                        75000.00,
                        0.00,
                        75000.00,
                        'DEP-000003',
                        'Initial demo deposit'
                    ),
                    (
                        4,
                        'DEPOSIT',
                        50000.00,
                        0.00,
                        50000.00,
                        'DEP-000004',
                        'Initial demo deposit'
                    )
                """);
    }

    @Test
    void protectedAccountsWithoutTokenReturns401() throws Exception {

        mockMvc.perform(
                get("/api/accounts")
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void loginReturnsBearerJwt() throws Exception {

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").isNotEmpty())
        .andExpect(jsonPath("$.tokenType").value("Bearer"))
        .andExpect(jsonPath("$.expiresIn").value(3600))
        .andExpect(jsonPath("$.username").value("demo"))
        .andExpect(jsonPath("$.roles").isArray());
    }

    @Test
    void validJwtAllowsAccessToAccounts() throws Exception {

        String token = loginAndGetToken();

        mockMvc.perform(
                get("/api/accounts")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(4))
        .andExpect(
                jsonPath("$[0].accountNo")
                        .value("1000000001")
        )
        .andExpect(
                jsonPath("$[0].balance")
                        .value(100000.00)
        );
    }

    @Test
    void malformedJwtReturns401() throws Exception {

        mockMvc.perform(
                get("/api/accounts")
                        .header(
                                "Authorization",
                                "Bearer invalid.jwt.token"
                        )
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void validJwtAllowsAccessToTransactions() throws Exception {

        String token = loginAndGetToken();

        mockMvc.perform(
                get("/api/accounts/1/transactions")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(
                jsonPath("$[0].transactionType")
                        .value("DEPOSIT")
        )
        .andExpect(
                jsonPath("$[0].accountId")
                        .value(1)
        )
        .andExpect(
                jsonPath("$[0].amount")
                        .value(100000.00)
        );
    }

    @Test
    void protectedTransferWithoutTokenReturns401() throws Exception {

        mockMvc.perform(
                post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TRANSFER_JSON)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void validJwtAllowsTransfer() throws Exception {

        String token = loginAndGetToken();

        mockMvc.perform(
                post("/api/transfers")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TRANSFER_JSON)
        )
        .andExpect(status().isCreated())
        .andExpect(
                jsonPath("$.status")
                        .value("COMPLETED")
        )
        .andExpect(
                jsonPath("$.sourceAccountId")
                        .value(1)
        )
        .andExpect(
                jsonPath("$.destinationAccountId")
                        .value(2)
        )
        .andExpect(
                jsonPath("$.amount")
                        .value(1000.00)
        )
        .andExpect(
                jsonPath("$.sourceBalance")
                        .value(99000.00)
        )
        .andExpect(
                jsonPath("$.destinationBalance")
                        .value(26000.00)
        )
        .andExpect(
                jsonPath("$.referenceNo")
                        .isNotEmpty()
        );

        /*
         * Verify balances through protected REST API.
         */
        mockMvc.perform(
                get("/api/accounts")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
        )
        .andExpect(status().isOk())
        .andExpect(
                jsonPath("$[0].balance")
                        .value(99000.00)
        )
        .andExpect(
                jsonPath("$[1].balance")
                        .value(26000.00)
        );

        /*
         * Verify transaction history through protected REST API.
         *
         * Every test starts with one initial deposit for account 1,
         * so after this transfer there must be exactly two records.
         */
        mockMvc.perform(
                get("/api/accounts/1/transactions")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(
                jsonPath("$[0].transactionType")
                        .value("TRANSFER_OUT")
        )
        .andExpect(
                jsonPath("$[0].amount")
                        .value(1000.00)
        )
        .andExpect(
                jsonPath("$[1].transactionType")
                        .value("DEPOSIT")
        );
    }

    private String loginAndGetToken() throws Exception {

        MvcResult result = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_JSON)
        )
        .andExpect(status().isOk())
        .andReturn();

        String responseBody =
                result.getResponse().getContentAsString();

        JsonParser parser =
                JsonParserFactory.getJsonParser();

        Map<String, Object> response =
                parser.parseMap(responseBody);

        Object accessToken =
                response.get("accessToken");

        if (accessToken == null) {
            throw new IllegalStateException(
                    "Login response does not contain accessToken"
            );
        }

        return accessToken.toString();
    }
}