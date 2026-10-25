#----------------------------#
#-------- VPC Creation ------#
#----------------------------#

resource "aws_vpc" "nexusbank_vpc" {
  cidr_block           = var.vpc_cidr
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = merge(local.common_tags, {
    Name = "nexusbank-vpc"
  })
}

#--------- Subnet Creation --------#

resource "aws_subnet" "public_subnet_a" {
  vpc_id                  = aws_vpc.nexusbank_vpc.id
  cidr_block              = local.public_subnet_a_cidr
  availability_zone       = local.azs[0]
  map_public_ip_on_launch = true

  tags = merge(local.common_tags, {
    Name = "nexusbank-public-subnet-a"
  })
}

resource "aws_subnet" "public_subnet_b" {
  vpc_id                  = aws_vpc.nexusbank_vpc.id
  cidr_block              = local.public_subnet_b_cidr
  availability_zone       = local.azs[1]
  map_public_ip_on_launch = true

  tags = merge(local.common_tags, {
    Name = "nexusbank-public-subnet-b"
  })
}

resource "aws_subnet" "private_subnet_a" {
  vpc_id            = aws_vpc.nexusbank_vpc.id
  cidr_block        = local.private_subnet_a_cidr
  availability_zone = local.azs[0]

  tags = merge(local.common_tags, {
    Name = "nexusbank-private-subnet-a"
  })
}

resource "aws_subnet" "private_subnet_b" {
  vpc_id            = aws_vpc.nexusbank_vpc.id
  cidr_block        = local.private_subnet_b_cidr
  availability_zone = local.azs[1]

  tags = merge(local.common_tags, {
    Name = "nexusbank-private-subnet-b"
  })
}

#------ Internet Gateway Creation ------#
resource "aws_internet_gateway" "nexusbank_igw" {
  vpc_id = aws_vpc.nexusbank_vpc.id

  tags = merge(local.common_tags, {
    Name = "nexusbank-igw"
  })
}

#------ Nat Gateway Creation ------#
resource "aws_eip" "nexusbank_nat_eip" {
  domain = "vpc"

  tags = merge(local.common_tags, {
    Name = "nexusbank-nat-eip"
  })
}
resource "aws_nat_gateway" "nexusbank_nat_gw" {
  allocation_id = aws_eip.nexusbank_nat_eip.id
  subnet_id     = aws_subnet.public_subnet_a.id
  depends_on    = [aws_internet_gateway.nexusbank_igw]
  tags = merge(local.common_tags, {
    Name = "nexusbank-nat-gw"
  })
}

#------ Route Table Creation ------#
resource "aws_route_table" "nexusbank_public_rt" {
  vpc_id = aws_vpc.nexusbank_vpc.id

  tags = merge(local.common_tags, {
    Name = "nexusbank-public-rt"
  })
}

resource "aws_route" "public_default_route" {
  route_table_id         = aws_route_table.nexusbank_public_rt.id
  destination_cidr_block = "0.0.0.0/0"
  gateway_id             = aws_internet_gateway.nexusbank_igw.id
}

resource "aws_route_table_association" "public_subnet_a_association" {
  subnet_id      = aws_subnet.public_subnet_a.id
  route_table_id = aws_route_table.nexusbank_public_rt.id
}

resource "aws_route_table_association" "public_subnet_b_association" {
  subnet_id      = aws_subnet.public_subnet_b.id
  route_table_id = aws_route_table.nexusbank_public_rt.id
}

resource "aws_route_table" "nexusbank_private_rt" {
  vpc_id = aws_vpc.nexusbank_vpc.id

  tags = merge(local.common_tags, {
    Name = "nexusbank-private-rt"
  })
}

resource "aws_route" "private_default_route" {
  route_table_id         = aws_route_table.nexusbank_private_rt.id
  destination_cidr_block = "0.0.0.0/0"
  nat_gateway_id         = aws_nat_gateway.nexusbank_nat_gw.id
}

resource "aws_route_table_association" "private_a" {
  subnet_id      = aws_subnet.private_subnet_a.id
  route_table_id = aws_route_table.nexusbank_private_rt.id
}

resource "aws_route_table_association" "private_b" {
  subnet_id      = aws_subnet.private_subnet_b.id
  route_table_id = aws_route_table.nexusbank_private_rt.id
}

# -------------------------
# Security groups
# -------------------------

# Internet-facing ALB: 80/443 from internet
resource "aws_security_group" "nexus_internet_alb_sg" {
  name        = "nexus-internet-alb-sg"
  description = "Internet-facing ALB SG"
  vpc_id      = aws_vpc.nexusbank_vpc.id

  tags = merge(local.common_tags, {
    Name = "nexus-internet-alb-sg"
  })
}

# Frontend EC2: allow HTTP only from internet ALB, SSH only from trusted admin IP
resource "aws_security_group" "nexus_frontend_sg" {
  name        = "nexus-frontend-sg"
  description = "Frontend EC2 SG"
  vpc_id      = aws_vpc.nexusbank_vpc.id

  tags = merge(local.common_tags, {
    Name = "nexus-frontend-sg"
  })
}

# Internal ALB: only frontend SG can reach 8080
resource "aws_security_group" "nexus_internal_alb_sg" {
  name        = "nexus-internal-alb-sg"
  description = "Internal ALB SG"
  vpc_id      = aws_vpc.nexusbank_vpc.id

  tags = merge(local.common_tags, {
    Name = "nexus-internal-alb-sg"
  })
}

# Backend EC2: allow 8080 from internal ALB, SSH only from jump host SG
resource "aws_security_group" "nexus_backend_sg" {
  name        = "nexus-backend-sg"
  description = "Backend EC2 SG"
  vpc_id      = aws_vpc.nexusbank_vpc.id

  tags = merge(local.common_tags, {
    Name = "nexus-backend-sg"
  })
}

# Optional jump host / bastion SG
resource "aws_security_group" "nexus_jump_sg" {
  name        = "nexus-jump-sg"
  description = "Bastion/jump host SG"
  vpc_id      = aws_vpc.nexusbank_vpc.id


  tags = merge(local.common_tags, {
    Name = "nexus-jump-sg"
  })
}

#------- EC2 Creation --------#

#----- tls private key for EC2 instances -----#
resource "tls_private_key" "nexusbank_key" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

#----- Register the private key with AWS -----#
resource "aws_key_pair" "nexusbank_key_pair" {
  key_name   = "nexusbank-key"
  public_key = tls_private_key.nexusbank_key.public_key_openssh
}

#----- local block to save the private key to a file -----#
resource "local_file" "nexusbank_private_key" {
  content         = tls_private_key.nexusbank_key.private_key_pem
  filename        = "${path.module}/nexusbank-key.pem"
  file_permission = "0600"
}

#--- EIP for jump host ---#
resource "aws_eip" "nexusbank_jump_eip" {
  domain = "vpc"

  tags = merge(local.common_tags, {
    Name = "nexusbank-jump-eip"
  })
}

#------ EC2 Jump Host Creation ------#
resource "aws_instance" "nexusbank_jump_host" {
  ami                    = var.ami_id
  instance_type          = var.Ec2_instance_type
  subnet_id              = aws_subnet.public_subnet_a.id
  vpc_security_group_ids = [aws_security_group.nexus_jump_sg.id]
  key_name               = aws_key_pair.nexusbank_key_pair.key_name

  tags = merge(local.common_tags, {
    Name = "nexusbank-jump-host"
  })
}

#---- EIP association for jump host ----#
resource "aws_eip_association" "nexusbank_jump_eip_assoc" {
  instance_id   = aws_instance.nexusbank_jump_host.id
  allocation_id = aws_eip.nexusbank_jump_eip.id
}

#------ Ec2 Creation for frontend(Public) ---------#

resource "aws_instance" "nexusbank_frontend" {
  count                       = 2
  ami                         = var.ami_id
  instance_type               = var.Ec2_instance_type
  subnet_id                   = element([aws_subnet.public_subnet_a.id, aws_subnet.public_subnet_b.id], count.index)
  vpc_security_group_ids      = [aws_security_group.nexus_frontend_sg.id]
  key_name                    = aws_key_pair.nexusbank_key_pair.key_name
  associate_public_ip_address = true
  root_block_device {
  volume_size           = 30
  volume_type           = "gp3"
  delete_on_termination = true
  encrypted             = true
}
  tags = merge(local.common_tags, {
    Name = "nexusbank-frontend-${count.index + 1}"
  })
}

#-------- Ec2 Creation for backend(Private) ---------#
resource "aws_instance" "nexusbank_backend" {
  count                  = 1
  ami                    = var.ami_id
  instance_type          = var.Ec2_instance_type
  subnet_id              = element([aws_subnet.private_subnet_a.id, aws_subnet.private_subnet_b.id], count.index)
  vpc_security_group_ids = [aws_security_group.nexus_backend_sg.id]
  key_name               = aws_key_pair.nexusbank_key_pair.key_name

  # Root EBS volume configuration
root_block_device {
  volume_size           = 30
  volume_type           = "gp3"
  delete_on_termination = true
  encrypted             = true
}

  tags = merge(local.common_tags, {
    Name = "nexusbank-backend-${count.index + 1}"
  })
}

# ==========================================
# Frontend Target Group
# ==========================================

resource "aws_lb_target_group" "nexusbank_frontend_tg" {
  name        = "nexus-frontend-tg"
  target_type = "instance"
  protocol    = "HTTP"
  port        = 80
  vpc_id      = aws_vpc.nexusbank_vpc.id

  health_check {
    enabled             = true
    protocol            = "HTTP"
    path                = "/"
    port                = "traffic-port"
    matcher             = "200"
    interval            = 30
    timeout             = 5
    healthy_threshold   = 2
    unhealthy_threshold = 2

  }

  tags = merge(local.common_tags, {
    Name = "nexusbank-frontend-tg"
  })
}

# ==========================================
# Backend Target Group
# ==========================================

resource "aws_lb_target_group" "nexusbank_backend_tg" {
  name        = "nexus-backend-tg"
  target_type = "instance"
  protocol    = "HTTP"
  port        = 8080
  vpc_id      = aws_vpc.nexusbank_vpc.id

  health_check {
    enabled             = true
    protocol            = "HTTP"
    path                = "/actuator/health"
    port                = "traffic-port"
    matcher             = "200"
    interval            = 30
    timeout             = 5
    healthy_threshold   = 2
    unhealthy_threshold = 2

  }

  tags = merge(local.common_tags, {
    Name = "nexusbank-backend-tg"
  })
}

# Register both frontend instances

resource "aws_lb_target_group_attachment" "frontend_tg_attachment" {
  count            = length(aws_instance.nexusbank_frontend)
  target_group_arn = aws_lb_target_group.nexusbank_frontend_tg.arn
  target_id        = aws_instance.nexusbank_frontend[count.index].id
  port             = 80
}

# Register the backend instance
# Update the instance reference if your backend resource has a different name.

resource "aws_lb_target_group_attachment" "backend_tg_attachment" {
  target_group_arn = aws_lb_target_group.nexusbank_backend_tg.arn
  target_id        = aws_instance.nexusbank_backend[0].id
  port             = 8080
}

# ==========================================
# Internal Application Load Balancer
# ==========================================

resource "aws_lb" "nexusbank_internal_alb" {
  name               = "nexus-internal-alb"
  internal           = true
  load_balancer_type = "application"
  ip_address_type    = "ipv4"
  security_groups    = [aws_security_group.nexus_internal_alb_sg.id]
  subnets            = [aws_subnet.private_subnet_a.id, aws_subnet.private_subnet_b.id]

  enable_deletion_protection = false

  tags = merge(local.common_tags, {
    Name = "nexus-internal-alb"
  })
}

# Internal ALB listener: HTTP 8080 and Target group association
resource "aws_lb_listener" "nexusbank_internal_alb_listener" {
  load_balancer_arn = aws_lb.nexusbank_internal_alb.arn
  port              = 8080
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.nexusbank_backend_tg.arn
  }
}

# ==========================================
# Internet-facing Application Load Balancer
# ==========================================

resource "aws_lb" "nexusbank_internet_alb" {
  name               = "nexus-internet-alb"
  internal           = false
  load_balancer_type = "application"
  ip_address_type    = "ipv4"
  security_groups    = [aws_security_group.nexus_internet_alb_sg.id]
  subnets            = [aws_subnet.public_subnet_a.id, aws_subnet.public_subnet_b.id]

  enable_deletion_protection = false

  tags = merge(local.common_tags, {
    Name = "nexus-internet-alb"
  })
}

## Internet-facing ALB listener: HTTP 80 and Target group association
resource "aws_lb_listener" "nexusbank_internet_alb_listener" {
  load_balancer_arn = aws_lb.nexusbank_internet_alb.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.nexusbank_frontend_tg.arn
  }
}