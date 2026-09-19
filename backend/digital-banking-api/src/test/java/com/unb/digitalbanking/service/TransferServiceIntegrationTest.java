package com.unb.digitalbanking.service;

import com.unb.digitalbanking.dto.TransferRequest;
import com.unb.digitalbanking.dto.TransferResponse;
import com.unb.digitalbanking.entity.Account;
import com.unb.digitalbanking.repository.AccountRepository;
import com.unb.digitalbanking.repository.TransactionRepository;
import com.unb.digitalbanking.exception.AccountNotFoundException;
import com.unb.digitalbanking.exception.TransferException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
class TransferServiceIntegrationTest {

    @Autowired
    private TransferService transferService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoSpyBean
    private TransactionRepository transactionRepository;

    @BeforeEach
    void resetDatabase() {

        reset(transactionRepository);

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
                    currency = 'THB',
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
                    (1, 'DEPOSIT', 100000.00, 0.00, 100000.00,
                     'DEP-000001', 'Initial demo deposit'),

                    (2, 'DEPOSIT', 25000.00, 0.00, 25000.00,
                     'DEP-000002', 'Initial demo deposit'),

                    (3, 'DEPOSIT', 75000.00, 0.00, 75000.00,
                     'DEP-000003', 'Initial demo deposit'),

                    (4, 'DEPOSIT', 50000.00, 0.00, 50000.00,
                     'DEP-000004', 'Initial demo deposit')
                """);
    }

    @Test
    void successfulTransferMovesMoneyAndCreatesTwoTransactions() {

        TransferRequest request = new TransferRequest(
                1L,
                2L,
                new BigDecimal("5000.00"),
                "Integration test transfer"
        );

        TransferResponse response =
                transferService.transfer(request);

        assertThat(response.status())
                .isEqualTo("COMPLETED");

        assertThat(response.amount())
                .isEqualByComparingTo("5000.00");

        assertThat(response.sourceBalance())
                .isEqualByComparingTo("95000.00");

        assertThat(response.destinationBalance())
                .isEqualByComparingTo("30000.00");

        assertThat(response.referenceNo())
                .startsWith("TRF-");

        Account source =
                accountRepository.findById(1L).orElseThrow();

        Account destination =
                accountRepository.findById(2L).orElseThrow();

        assertThat(source.getBalance())
                .isEqualByComparingTo("95000.00");

        assertThat(destination.getBalance())
                .isEqualByComparingTo("30000.00");

        List<com.unb.digitalbanking.entity.Transaction> sourceTransactions =
                transactionRepository
                        .findByAccountIdOrderByCreatedAtDesc(1L);

        List<com.unb.digitalbanking.entity.Transaction> destinationTransactions =
                transactionRepository
                        .findByAccountIdOrderByCreatedAtDesc(2L);

        assertThat(sourceTransactions)
                .anyMatch(transaction ->
                        "TRANSFER_OUT".equals(transaction.getTransactionType())
                                && new BigDecimal("5000.00")
                                .compareTo(transaction.getAmount()) == 0
                                && response.referenceNo()
                                .equals(transaction.getReferenceNo())
                );

        assertThat(destinationTransactions)
                .anyMatch(transaction ->
                        "TRANSFER_IN".equals(transaction.getTransactionType())
                                && new BigDecimal("5000.00")
                                .compareTo(transaction.getAmount()) == 0
                                && response.referenceNo()
                                .equals(transaction.getReferenceNo())
                );
    }

    @Test
    void insufficientBalanceIsRejected() {

        TransferRequest request = new TransferRequest(
                1L,
                2L,
                new BigDecimal("200000.00"),
                "Insufficient balance test"
        );

        assertThatThrownBy(() ->
                transferService.transfer(request)
        )
                .isInstanceOf(TransferException.class)
                .hasMessage("Insufficient balance in source account");

        BigDecimal sourceBalance = jdbcTemplate.queryForObject(
                "SELECT balance FROM accounts WHERE id = 1",
                BigDecimal.class
        );

        BigDecimal destinationBalance = jdbcTemplate.queryForObject(
                "SELECT balance FROM accounts WHERE id = 2",
                BigDecimal.class
        );

        assertThat(sourceBalance)
                .isEqualByComparingTo("100000.00");

        assertThat(destinationBalance)
                .isEqualByComparingTo("25000.00");

        Integer transactionCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM transactions",
                Integer.class
        );

        assertThat(transactionCount)
                .isEqualTo(4);
    }

    @Test
    void sourceAccountNotFoundIsRejected() {

        TransferRequest request = new TransferRequest(
                999L,
                2L,
                new BigDecimal("1000.00"),
                "Source not found test"
        );

        assertThatThrownBy(() ->
                transferService.transfer(request)
        )
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessage("Account not found: 999");

        assertDatabaseBalances(
                "100000.00",
                "25000.00"
        );
    }

    @Test
    void destinationAccountNotFoundIsRejected() {

        TransferRequest request = new TransferRequest(
                1L,
                999L,
                new BigDecimal("1000.00"),
                "Destination not found test"
        );

        assertThatThrownBy(() ->
                transferService.transfer(request)
        )
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessage("Account not found: 999");

        assertDatabaseBalances(
                "100000.00",
                "25000.00"
        );
    }

    @Test
    void selfTransferIsRejected() {

        TransferRequest request = new TransferRequest(
                1L,
                1L,
                new BigDecimal("1000.00"),
                "Self transfer test"
        );

        assertThatThrownBy(() ->
                transferService.transfer(request)
        )
                .isInstanceOf(TransferException.class)
                .hasMessage(
                        "Source and destination accounts must be different"
                );

        assertDatabaseBalances(
                "100000.00",
                "25000.00"
        );
    }

    @Test
    void inactiveSourceAccountIsRejected() {

        jdbcTemplate.update("""
                UPDATE accounts
                SET status = 'INACTIVE'
                WHERE id = 1
                """);

        TransferRequest request = new TransferRequest(
                1L,
                2L,
                new BigDecimal("1000.00"),
                "Inactive source test"
        );

        assertThatThrownBy(() ->
                transferService.transfer(request)
        )
                .isInstanceOf(TransferException.class)
                .hasMessage("Source account is not active");

        assertDatabaseBalances(
                "100000.00",
                "25000.00"
        );
    }

    @Test
    void inactiveDestinationAccountIsRejected() {

        jdbcTemplate.update("""
                UPDATE accounts
                SET status = 'INACTIVE'
                WHERE id = 2
                """);

        TransferRequest request = new TransferRequest(
                1L,
                2L,
                new BigDecimal("1000.00"),
                "Inactive destination test"
        );

        assertThatThrownBy(() ->
                transferService.transfer(request)
        )
                .isInstanceOf(TransferException.class)
                .hasMessage("Destination account is not active");

        assertDatabaseBalances(
                "100000.00",
                "25000.00"
        );
    }

    @Test
    void currencyMismatchIsRejected() {

        jdbcTemplate.update("""
                UPDATE accounts
                SET currency = 'USD'
                WHERE id = 2
                """);

        TransferRequest request = new TransferRequest(
                1L,
                2L,
                new BigDecimal("1000.00"),
                "Currency mismatch test"
        );

        assertThatThrownBy(() ->
                transferService.transfer(request)
        )
                .isInstanceOf(TransferException.class)
                .hasMessage(
                        "Source and destination currencies must match"
                );

        assertDatabaseBalances(
                "100000.00",
                "25000.00"
        );
    }

    @Test
    void invalidAmountIsRejected() {

        TransferRequest request = new TransferRequest(
                1L,
                2L,
                new BigDecimal("-1.00"),
                "Invalid amount test"
        );

        assertThatThrownBy(() ->
                transferService.transfer(request)
        )
                .isInstanceOf(TransferException.class)
                .hasMessage(
                        "Transfer amount must be greater than zero"
                );

        assertDatabaseBalances(
                "100000.00",
                "25000.00"
        );
    }

    @Test
    void tooManyDecimalPlacesAreRejected() {

        TransferRequest request = new TransferRequest(
                1L,
                2L,
                new BigDecimal("1000.001"),
                "Scale validation test"
        );

        assertThatThrownBy(() ->
                transferService.transfer(request)
        )
                .isInstanceOf(TransferException.class)
                .hasMessage(
                        "Transfer amount must have at most 2 decimal places"
                );

        assertDatabaseBalances(
                "100000.00",
                "25000.00"
        );
    }

    @Test
    void forcedFailureAfterMutationRollsBackEverything() {

        doThrow(new RuntimeException("FORCED_ROLLBACK_TEST"))
                .when(transactionRepository)
                .saveAll(anyList());

        TransferRequest request = new TransferRequest(
                1L,
                2L,
                new BigDecimal("5000.00"),
                "Rollback integration test"
        );

        assertThatThrownBy(() ->
                transferService.transfer(request)
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessage("FORCED_ROLLBACK_TEST");

        verify(transactionRepository)
                .saveAll(anyList());

        /*
         * The service changed the managed Account balances before
         * saveAll() failed.
         *
         * Because the method is @Transactional and the exception
         * escaped the transaction, those changes must be rolled back.
         */

        BigDecimal sourceBalance = jdbcTemplate.queryForObject(
                "SELECT balance FROM accounts WHERE id = 1",
                BigDecimal.class
        );

        BigDecimal destinationBalance = jdbcTemplate.queryForObject(
                "SELECT balance FROM accounts WHERE id = 2",
                BigDecimal.class
        );

        assertThat(sourceBalance)
                .isEqualByComparingTo("100000.00");

        assertThat(destinationBalance)
                .isEqualByComparingTo("25000.00");

        Integer transactionCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM transactions",
                Integer.class
        );

        assertThat(transactionCount)
                .isEqualTo(4);
    }

    private void assertDatabaseBalances(
            String expectedSource,
            String expectedDestination
    ) {

        BigDecimal sourceBalance = jdbcTemplate.queryForObject(
                "SELECT balance FROM accounts WHERE id = 1",
                BigDecimal.class
        );

        BigDecimal destinationBalance = jdbcTemplate.queryForObject(
                "SELECT balance FROM accounts WHERE id = 2",
                BigDecimal.class
        );

        assertThat(sourceBalance)
                .isEqualByComparingTo(expectedSource);

        assertThat(destinationBalance)
                .isEqualByComparingTo(expectedDestination);

        Integer transactionCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM transactions",
                Integer.class
        );

        assertThat(transactionCount)
                .isEqualTo(4);
    }
}