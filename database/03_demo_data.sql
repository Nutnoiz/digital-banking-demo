INSERT INTO transactions
    (account_id, transaction_type, amount,
     balance_before, balance_after,
     reference_no, description)
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
    );