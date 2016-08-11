# Akka Rest API

## Dependency

- [VirtualBox](https://www.virtualbox.org/wiki/Downloads)
- [Vagrant](https://www.vagrantup.com/downloads.html)

## Usage

To start the VM, open a terminal in the directory with the Vagrantfile and run:

	vagrant up

Then, you can log onto the virtual machine by running:

	vagrant ssh 

Finally, move to app directory:

	cd app	

### Run Application

	sbt run

### Run Test

	sbt test