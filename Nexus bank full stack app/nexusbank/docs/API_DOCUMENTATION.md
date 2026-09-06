# NexusBank API Documentation

## Customer APIs

### Authentication
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | /api/customers/register | Register new customer | No |
| POST | /api/customers/login | Customer login | No |
| GET | /api/customers/profile | Get profile | Customer |
| PUT | /api/customers/update | Update profile | Customer |

### Accounts
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | /api/accounts/create | Create account | Customer |
| GET | /api/accounts/{number} | Get account | Customer/Admin |
| GET | /api/accounts/customer/{id} | Get customer accounts | Customer/Admin |

### Transactions
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | /api/transactions/deposit | Deposit funds | Customer |
| POST | /api/transactions/withdraw | Withdraw funds | Customer |
| POST | /api/transactions/transfer | Transfer funds | Customer |
| GET | /api/transactions/history/{acc} | Transaction history | Customer |

### Loans
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | /api/loans/apply | Apply for loan | Customer |
| GET | /api/loans/status/{id} | Check loan status | Customer/Admin |

## Admin APIs

### Authentication
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | /api/admin/login | Admin login | No |

### Customer Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/admin/customers | List all customers |
| GET | /api/admin/customers/{id} | Get customer by ID |
| PUT | /api/admin/customers/{id}/activate | Activate customer |
| PUT | /api/admin/customers/{id}/deactivate | Deactivate customer |
| DELETE | /api/admin/customers/{id} | Delete customer |

### Account Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/admin/accounts | List all accounts |
| PUT | /api/admin/accounts/{num}/approve | Approve account |
| PUT | /api/admin/accounts/{num}/reject | Reject account |
| PUT | /api/admin/accounts/{num}/freeze | Freeze account |
| PUT | /api/admin/accounts/{num}/unfreeze | Unfreeze account |

### Loan Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/admin/loans | List all loans |
| GET | /api/admin/loans/pending | List pending loans |
| PUT | /api/admin/loans/{id}/approve | Approve loan |
| PUT | /api/admin/loans/{id}/reject | Reject loan |

### Dashboard
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/admin/dashboard | Get admin dashboard stats |
