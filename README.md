# JLI40D_Uno

This repository contains the code for the assignment for the course distributed systems at the university KU Leuven, campus Ghent.

## The assignment

The assignment was to implemented the game Uno in Java using RMI. Clients can play on several application servers that needed to be synchronized. Currently this project contains the following modules:
- Dispatcher
- Gameclient
- Application server
- Database server

### Dispatcher
The dispatcher module is an rmi server, to which clients can connect and request a server ip/port. 

[!img](img/dashboard.png)
