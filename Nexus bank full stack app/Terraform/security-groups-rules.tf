
# ==========================================
# 1. Internet-facing ALB ingress
# ==========================================

resource "aws_vpc_security_group_ingress_rule" "internet_alb_http" {
  security_group_id = aws_security_group.nexus_internet_alb_sg.id
  description       = "HTTP from internet"
  ip_protocol       = "tcp"
  from_port         = 80
  to_port           = 80
  cidr_ipv4         = "0.0.0.0/0"
}

resource "aws_vpc_security_group_ingress_rule" "internet_alb_https" {
  security_group_id = aws_security_group.nexus_internet_alb_sg.id
  description       = "HTTPS from internet"
  ip_protocol       = "tcp"
  from_port         = 443
  to_port           = 443
  cidr_ipv4         = "0.0.0.0/0"
}

resource "aws_vpc_security_group_egress_rule" "internet_alb_to_frontend" {
  security_group_id            = aws_security_group.nexus_internet_alb_sg.id
  description                  = "Forward HTTP to frontend"
  ip_protocol                  = "tcp"
  from_port                    = 80
  to_port                      = 80
  referenced_security_group_id = aws_security_group.nexus_frontend_sg.id
}

# ==========================================
# 2. Frontend EC2 rules
# ==========================================

resource "aws_vpc_security_group_ingress_rule" "frontend_from_internet_alb" {
  security_group_id            = aws_security_group.nexus_frontend_sg.id
  description                  = "HTTP only from internet ALB"
  ip_protocol                  = "tcp"
  from_port                    = 80
  to_port                      = 80
  referenced_security_group_id = aws_security_group.nexus_internet_alb_sg.id
}

resource "aws_vpc_security_group_ingress_rule" "frontend_ssh" {
  security_group_id = aws_security_group.nexus_frontend_sg.id
  description       = "SSH from trusted admin IP"
  ip_protocol       = "tcp"
  from_port         = 22
  to_port           = 22
  referenced_security_group_id         = aws_security_group.nexus_jump_sg.id
}

resource "aws_vpc_security_group_egress_rule" "frontend_to_internal_alb" {
  security_group_id            = aws_security_group.nexus_frontend_sg.id
  description                  = "Frontend to internal ALB"
  ip_protocol                  = "tcp"
  from_port                    = 8080
  to_port                      = 8080
  referenced_security_group_id = aws_security_group.nexus_internal_alb_sg.id
}

resource "aws_vpc_security_group_egress_rule" "frontend_https" {
  security_group_id = aws_security_group.nexus_frontend_sg.id
  description       = "HTTPS for package installs and updates"
  ip_protocol       = "tcp"
  from_port         = 443
  to_port           = 443
  cidr_ipv4         = "0.0.0.0/0"
}

resource "aws_vpc_security_group_egress_rule" "frontend_http" {
  security_group_id = aws_security_group.nexus_frontend_sg.id
  description       = "HTTP for package installs and updates"
  ip_protocol       = "tcp"
  from_port         = 80
  to_port           = 80
  cidr_ipv4         = "0.0.0.0/0"
}

# ==========================================
# 3. Internal ALB rules
# ==========================================

resource "aws_vpc_security_group_ingress_rule" "internal_alb_from_frontend" {
  security_group_id            = aws_security_group.nexus_internal_alb_sg.id
  description                  = "HTTP 8080 from frontend"
  ip_protocol                  = "tcp"
  from_port                    = 8080
  to_port                      = 8080
  referenced_security_group_id = aws_security_group.nexus_frontend_sg.id
}

resource "aws_vpc_security_group_egress_rule" "internal_alb_to_backend" {
  security_group_id            = aws_security_group.nexus_internal_alb_sg.id
  description                  = "Forward traffic to backend"
  ip_protocol                  = "tcp"
  from_port                    = 8080
  to_port                      = 8080
  referenced_security_group_id = aws_security_group.nexus_backend_sg.id
}

# ==========================================
# 4. Backend EC2 rules
# ==========================================

resource "aws_vpc_security_group_ingress_rule" "backend_from_internal_alb" {
  security_group_id            = aws_security_group.nexus_backend_sg.id
  description                  = "Application traffic from internal ALB"
  ip_protocol                  = "tcp"
  from_port                    = 8080
  to_port                      = 8080
  referenced_security_group_id = aws_security_group.nexus_internal_alb_sg.id
}

resource "aws_vpc_security_group_ingress_rule" "backend_ssh_from_jump" {
  security_group_id            = aws_security_group.nexus_backend_sg.id
  description                  = "SSH from jump host"
  ip_protocol                  = "tcp"
  from_port                    = 22
  to_port                      = 22
  referenced_security_group_id = aws_security_group.nexus_jump_sg.id
}

resource "aws_vpc_security_group_egress_rule" "backend_outbound" {
  security_group_id = aws_security_group.nexus_backend_sg.id
  description       = "Outbound traffic for packages and AWS APIs"
  ip_protocol       = "-1"
  cidr_ipv4         = "0.0.0.0/0"
}

# ==========================================
# 5. Jump host rules
# ==========================================

resource "aws_vpc_security_group_egress_rule" "jump_to_private_instances" {
  security_group_id = aws_security_group.nexus_jump_sg.id
  description       = "SSH to private instances"
  ip_protocol       = "tcp"
  from_port         = 22
  to_port           = 22
  cidr_ipv4         = "10.0.0.0/16"
}

# Uncomment if the jump host must accept SSH directly
# from your trusted administrator IP.

resource "aws_vpc_security_group_ingress_rule" "jump_ssh" {
  security_group_id = aws_security_group.nexus_jump_sg.id
  description       = "SSH from trusted admin IP"
  ip_protocol       = "tcp"
  from_port         = 22
  to_port           = 22
  cidr_ipv4         = "0.0.0.0/0"
}

