
++++++++++++++++++++++++++++++++++++++++++++++++
++                                            ++
+  Octopus - Configuration Management Utility  +
++                                            ++
++++++++++++++++++++++++++++++++++++++++++++++++


User's Guide
============

Octopus is a simple tool that aims to automate repetitive system configuration 
tasks. It provides an abstraction for common sysadmin tasks i.e. updating a
config file, installing a package, etc. The advantage of using Octopus over
doing the same manually is the actions can be repeated over multiple servers.

Starting Octopus
----------------
Octopus is a java command line utility, can be started with the following command:

java info.vizhanyo.octopus.App

command line arguments:
short   long        default         description
-s      --system    system.json     json file describing the server envinronment commands needs to be executed
-r      --runbook   runbook.json    json file describing the actions needs to be performed by Octopus

The system configuration file
-----------------------------
system.json describes the runtime envinronment. An example runtime envinronment file:

[   {"name": "34.230.11.137", "port": 22, "user": "root", "password": "SQ2KBDUCAIYB4SKFLWE4R5MO"},
    {"name": "3.91.253.2", "port": 22, "user": "root", "password": "SQ2KBDUCAIYB4SKFLWE4R5MO"}
]

Servers listed as a json array. For an individual system, the following
information needs to be provided:

name: network name/ip address of the machine.
port: ssh port
user: name of a user with root privileges on the target machine
password: the uesr's password

The runbook
-----------
runbook.json describes the actions Octopus needs to perform on all hosts.
A runbook is essentially an (ordered) list of actions. Each action has zero or
more arguments (or args for sort). The action determines the command(s) being
executed, while the arguments determine its args.
The different actions and args described below:

action/arg          Description

CopyFile            Copies a file from the local machine to the remote server
- source            The local path to the source file
- destination       The remote path file being copied

InstallPackage      Installs a package on the server (with i.e. apt-get)
- package           The package name

MoveFile            Moves/renames a remote file (i.e. with mv)
- source            The remote path to the source file
- destination       The remote path to destination

RemovePackage       Uninstalls a package on the server (with i.e. apt-get)
- package           The package name

RestartService      Restarts a service on the remote server (i.e. service xxx restart)
- service           The service name

SetPermissions      Sets the owner, group and permissions on a file. It uses
                    chown, chgrp and chmod in the background
- file              File being repermissioned
- owner             The new owner (an user)
- group             The new group
- mode              File permissions. Any form of permissions chmod allows should work here


Developer's Guide
=================

Octopus' code base is split into 3 main parts:
1.  Value objects representing the different config files are located in the
    info.vizhanyo.octopus main package (along with the main app executable)
2.  Connector interface and its implementatons are in the
    info.vizhanyo.octopus.connect package. As of now there is only an
    SSHConnector class available, but it is possible to implement more using the
    Executor interface.
3.  The execute package has the Executor interface, the basis for all runbook
    actions. implementations of this class are responsible for the runbook
    actions available. The connection point between the implementation class and the
    runbook is the action name in the runbook.json file: Octopus will load the
    class with the same name.

Extending Octopus
-----------------
Current implementation makes it failry easy to extend the action "palette"
of the system. A new implementation of the Executor interface needs to be
created in the info.vizhanyo.octopus.execute package. The apply method
receives a connection to the remote server (in form of a Connector class
reference) and all the arguments spcified in the runbook (in the form of a 
hashmap). Based on the arguments the Executor implementation can issue multiple
commands over the Connector.
Extending Octopus with a new type of connection (i.e. WinRM) is certainly
possible, but require some changes in the configuration files (and their
representations) as well as the main app. Dwelwing towards this direction
can have more difficulties as different types of connections likely require
different implementations on the Executor side as well.
