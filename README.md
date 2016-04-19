# SddlChat

SddlChat is a very simple chat application developed with the [ContextNet] SDDL Middleware for study purposes. 

### Purpose
SddlChat use the ContextNet SDDL Middleware for exchange messages through the Groups structure and private messages.

It are developed for know the dificulties of use the middleware.

### Architecture
* Generic Client
        > The client. Can be runned by the SddlChat.GenericClient.InterfaceGrafica.Main class
* Processing Node

     The server. Manage the group informations. SddlChat can be runned without the Processing Node, but the group informations will not be shown.
    * Node Server
        > It is a Processing Node type, run a server instance using the Node API, like the Generic Client. Can be runned by the SddlChat.ProcessingNode.NodeServer class

    * UDI Server
        > It is a other type, run a server instance using the UDI API. Can be runned by the SddlChat.GenericClient.InterfaceGrafica.Main class

### How to run
* [Download] the SDDL middleware JARs (gateway.jar, clientlib.jar, udilib.jar)
* Configure the middleware with a DDS implementation
        > http://www.lac-rio.com/dokuwiki/doku.php?id=installingdds
* Configure the library middleware JARs on your IDE or be a spartan
* Start a SDDL Gateway instance

     ``` $ java -jar gateway.jar 127.0.0.1 5500 OpenSplice```
     
* Start a Processing Node instance (UDI Server or Node Server, not both)
* Start a Generic Client 
* Insert your gateway address, choose the type of the used processingNode
* Click "Conectar"
* Choose a group and click "Criar"
* Make the same with other Generic Client instance
* Start chating :)

[ContextNet]: <http://www.lac-rio.com/dokuwiki>
[Download]: <http://www.lac-rio.com/dokuwiki/doku.php?id=download>
