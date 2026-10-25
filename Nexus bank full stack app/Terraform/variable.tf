variable "aws_region" {
  description = "The AWS region to deploy resources in"
  type        = string
  default     = "us-east-1"
}

variable "vpc_cidr" {
  description = "The CIDR block for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}


variable "Ec2_instance_type" {
  description = "The instance type for EC2 instances"
  type        = string
  default     = "t3.small"
}

variable "ami_id" {
  description = "The AMI ID for the EC2 instances"
  type        = string
  default     = "ami-0d97d7f07b25dd693" # Ubuntu Server 26.04 LTS (HVM), SSD Volume Type
}