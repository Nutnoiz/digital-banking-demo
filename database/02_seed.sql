INSERT INTO customers
    (customer_no, first_name, last_name, email, phone, status)
VALUES
    ('C00001', 'Somchai', 'Demo', 'somchai.demo@example.com', '0800000001', 'ACTIVE'),
    ('C00002', 'Somsri', 'Demo', 'somsri.demo@example.com', '0800000002', 'ACTIVE'),
    ('C00003', 'Anan', 'Demo', 'anan.demo@example.com', '0800000003', 'ACTIVE');


INSERT INTO accounts
    (account_no, customer_id, account_type, balance, currency, status)
VALUES
    ('1000000001', 1, 'SAVINGS', 100000.00, 'THB', 'ACTIVE'),
    ('1000000002', 1, 'SAVINGS', 25000.00, 'THB', 'ACTIVE'),
    ('1000000003', 2, 'SAVINGS', 75000.00, 'THB', 'ACTIVE'),
    ('1000000004', 3, 'SAVINGS', 50000.00, 'THB', 'ACTIVE');