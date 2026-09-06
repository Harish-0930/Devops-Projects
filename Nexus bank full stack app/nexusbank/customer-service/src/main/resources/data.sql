INSERT IGNORE INTO customers (customer_id, first_name, last_name, email, phone_number, username, password, status, created_date, address_id, unique_id) VALUES
(1, 'John', 'Doe', 'john@example.com', '9876543210', 'johndoe', '$2b$10$GKVe3HMouNrEk92U/IRDUu5uCYCyP7HsAwOyVpIacE0JsTiTlga4K', 'ACTIVE', CURRENT_TIMESTAMP, NULL, '1000000000000001');
