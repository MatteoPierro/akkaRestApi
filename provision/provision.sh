#!/bin/bash

echo "-------------------- updating package lists"

apt-get update

echo "-------------------- install utilities"

sudo apt-get install -y wget
sudo apt-get install -y curl
sudo apt-get install -y vim
sudo apt-get install -y git
sudo apt-get install -y build-essential
sudo apt-get install -y unzip

echo "-------------------- Install Java 8"

cd /opt
sudo wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u45-b14/jdk-8u45-linux-x64.tar.gz"
sudo tar -xzvf jdk-8u45-linux-x64.tar.gz
sudo rm -rf jdk-8u45-linux-x64.tar.gz


echo "-------------------- Install Scala 2.11.7"

sudo mkdir /home/vagrant/bin
cd /home/vagrant/bin/
sudo wget http://downloads.typesafe.com/scala/2.11.7/scala-2.11.7.tgz
sudo tar -xzvf scala-2.11.7.tgz
sudo rm -rf scala-2.11.7.tgz

echo "-------------------- Install SBT 0.13"
    
sudo wget https://repo.typesafe.com/typesafe/ivy-releases/org.scala-sbt/sbt-launch/0.13.9/sbt-launch.jar
sudo cp /home/vagrant/app/provision/conf/sbt.sh /home/vagrant/bin/sbt
sudo chmod u+x /home/vagrant/bin/sbt
sudo chmod +x /home/vagrant/bin/sbt

echo "-------------------- Update bashrc"

sudo cat /home/vagrant/app/provision/conf/bashrc.txt >> /home/vagrant/.bashrc
source /home/vagrant/.bashrc

echo "-------------------- installing postgres"

sudo add-apt-repository "deb https://apt.postgresql.org/pub/repos/apt/ trusty-pgdg main"
wget --quiet -O - https://postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add - 
sudo apt-get update
sudo apt-get -y install postgresql-9.4

echo "-------------------- fixing listen_addresses on postgresql.conf"

sudo sed -i "s/#listen_address.*/listen_addresses '*'/" /etc/postgresql/9.4/main/postgresql.conf

echo "-------------------- creating postgres vagrant role with password vagrant"

sudo su postgres -c "psql -c \"CREATE ROLE vagrant SUPERUSER LOGIN PASSWORD 'vagrant'\" "

echo "-------------------- creating app database"

sudo su postgres -c "createdb -E UTF8 -T template0 --locale=en_US.utf8 -O vagrant app"

echo "-------------------- upgrading packages to latest"

apt-get upgrade -y