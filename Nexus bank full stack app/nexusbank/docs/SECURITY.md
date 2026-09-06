# NexusBank Security

## Authentication
- JWT-based authentication
- Tokens expire after 24 hours
- Role-based access control (RBAC)

## Roles
### CUSTOMER
- Manage own profile
- View own accounts
- Deposit, withdraw, transfer
- Apply for loans

### ADMIN
- View all customers
- Manage all accounts
- Approve/reject loans
- Monitor transactions

## Security Measures
- Passwords hashed with BCrypt
- HTTPS recommended for production
- CORS configured for frontend
- CSRF disabled for stateless JWT
- Input validation on all endpoints

## JWT Token Structure
```json
{
  "sub": "username",
  "role": "CUSTOMER|ADMIN",
  "iat": 1234567890,
  "exp": 1234654290
}
```
