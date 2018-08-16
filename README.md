## SmartContract-DistroDB

### Introduction:
A design for distribution database quickly detect and recover the error. Among distribution database, consensus between database is important, sometimes even including the order of the transaction caused by the delay
in the network, withholding attack or deny of service. The system focus on the consensus reaching and recovering the data.

### Technology & Algorithm:
* Hyperledger Composer https://hyperledger.github.io/composer/latest/introduction/introduction.html
* Practical Byzantine Fault Tolerance http://pmg.csail.mit.edu/papers/osdi99.pdf
* Merkle tree https://en.wikipedia.org/wiki/Merkle_tree
* MySQL Database

### Consensus & Data Recovery:
Apply practical Byzantine Fault Tolerance Algorithm(simple version and a little modification) to make consensus and Merkle tree structure to check if something wrong in the database and start quickly recovery


### Structure:
#### Node Structure:
Node exsist as a manipulator, communicate with DBManager, Logger in software level. DBManager send and get interperated command from the Interperator, and use the command execute operation to the database, besides compute the Merkle tree for the database for quickly recovery and detecting disagreement. Logger is the 'Memory' for the node, record the stage, message sent by the peers, and notify the node to change stage or start requester to send message to the network. Requester and Receiver gets and receive message from the network
![](./Documentation/Image/NodeAgent.png)

### Admin Structure:
Client commnicate with the API and manipulate the amin node to execute operation to the distribute database. Same, Requester and Reciever 
get and receive message from the network
![](./Documentation/Image/AdminAgent.png)
