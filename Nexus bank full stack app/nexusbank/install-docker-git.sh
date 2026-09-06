#!/bin/bash

set -e

echo "======================================"
echo " Installing Docker and Git"
echo "======================================"

# Update packages
sudo apt-get update -y
sudo apt-get upgrade -y

# Install prerequisites
sudo apt-get install -y \
    ca-certificates \
    curl \
    gnupg \
    lsb-release \
    git

echo "======================================"
echo " Installing Docker"
echo "======================================"

# Create Docker GPG key directory
sudo install -m 0755 -d /etc/apt/keyrings

# Add Docker GPG key
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg \
    -o /etc/apt/keyrings/docker.asc

sudo chmod a+r /etc/apt/keyrings/docker.asc

# Add Docker repository
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] \
  https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Update repository
sudo apt-get update -y

# Install Docker Engine + Compose
sudo apt-get install -y \
    docker-ce \
    docker-ce-cli \
    containerd.io \
    docker-buildx-plugin \
    docker-compose-plugin

echo "======================================"
echo " Configuring Docker"
echo "======================================"

# Start Docker
sudo systemctl enable docker
sudo systemctl start docker

# Add current user to docker group
sudo usermod -aG docker "$USER"

echo "======================================"
echo " Versions"
echo "======================================"

git --version
sudo docker --version
sudo docker compose version

echo "======================================"
echo " Installation completed"
echo "======================================"

echo ""
echo "IMPORTANT:"
echo "Log out and log back in for Docker group permissions to take effect."
echo ""
echo "Then verify with:"
echo "docker ps"
echo "docker compose version"