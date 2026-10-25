output "vpc_id" {
  value = aws_vpc.nexusbank_vpc.id
}

output "public_subnet_a_id" {
  value = aws_subnet.public_subnet_a.id
}

output "public_subnet_b_id" {
  value = aws_subnet.public_subnet_b.id
}

output "private_subnet_a_id" {
  value = aws_subnet.private_subnet_a.id
}

output "private_subnet_b_id" {
  value = aws_subnet.private_subnet_b.id
}

output "internet_gateway_id" {
  value = aws_internet_gateway.nexusbank_igw.id
}

output "nat_gateway_id" {
  value = aws_nat_gateway.nexusbank_nat_gw.id
}

output "nat_gateway_eip" {
  value = aws_eip.nexusbank_nat_eip.public_ip
}

output "security_group_ids" {
  value = {
    internet_alb = aws_security_group.nexus_internet_alb_sg.id
    frontend     = aws_security_group.nexus_frontend_sg.id
    internal_alb = aws_security_group.nexus_internal_alb_sg.id
    backend      = aws_security_group.nexus_backend_sg.id
    jump         = aws_security_group.nexus_jump_sg.id
  }
}

output "jump_host_eip" {
  value = aws_eip.nexusbank_jump_eip.public_ip
}

output "jump_host_private_ip" {
  value = aws_instance.nexusbank_jump_host.private_ip
}

output "jump_host_instance_id" {
  value = aws_instance.nexusbank_jump_host.id
}

output "nexusbank_frontend_host01_private_ip" {
  value = aws_instance.nexusbank_frontend[0].private_ip
}

output "nexusbank_frontend_host02_private_ip" {
  value = aws_instance.nexusbank_frontend[1].private_ip
}

output "nexusbank_frontend_public_dns" {

  value = {
    Host01 = aws_instance.nexusbank_frontend[0].public_dns
    Host02 = aws_instance.nexusbank_frontend[1].public_dns
  }

}

output "nexusbank_backend_host_private_ip" {
  value = aws_instance.nexusbank_backend[0].private_ip
}

output "nexusbank-frontend-target-group-details" {
  value = {
    arn  = aws_lb_target_group.nexusbank_frontend_tg.arn
    name = aws_lb_target_group.nexusbank_frontend_tg.name
  }
}

output "nexusbank-backend-target-group-details" {
  value = {
    arn  = aws_lb_target_group.nexusbank_backend_tg.arn
    name = aws_lb_target_group.nexusbank_backend_tg.name
  }
}


output "internet_alb_dns_name" {
  description = "Public DNS name of the NexusBank internet ALB"
  value       = aws_lb.nexusbank_internet_alb.dns_name
}

output "internal_alb_dns_name" {
  description = "Private DNS name of the NexusBank internal ALB"
  value       = aws_lb.nexusbank_internal_alb.dns_name
}
