## SmartContract-DistroDB

### Introduction:
A design for distribution database quickly detect and recover the error. Among distribution database, consensus between database is important, sometimes even including the order of the transaction caused by the delay
in the network, withholding attack or deny of service. The system focus on the consensus reaching and recovering the data.

### Structure:
#### Node Structure:
Node exsist as a manipulator, communicate with DBManager, Logger in software level. DBManager send and get interperated command from the Interperator, and use the command execute operation to the database, besides compute the Merkle tree for the database for quickly recovery and detecting disagreement. Logger is the 'Memory' for the node, record the stage, message sent by the peers, and notify the node to change stage or start requester to send message to the network. Requester and Receiver gets and receive message from the network
![](./Documentation/Image/NodeAgent.png)

### Admin Structure:
Client commnicate with the API and manipulate the amin node to execute operation to the distribute database. Same, Requester and Reciever 
get and receive message from the network
![](./Documentation/Image/AdminAgent.png)
