ProFID (Protocol for Frequent Items Discovery)
======
This is a java source code for the implementation of a gossip based protocol to find frequent items in a network using the [PeerSim](http://peersim.sourceforge.net/) (Peer-to-Peer Simulator). As the gossip-based algorithm is not centralized, it does not have a single point of failure issue. For more details about ProFID please refer to my paper titled [ProFID: Practical frequent items discovery in peer-to-peer networks](http://dl.acm.org/citation.cfm?id=2480149) and [my poster.](http://www.utdallas.edu/~emrah.cem/papers/ACM32012.pptx)

###How to Run
==============
To start the GUI, you will simply run the profid.jar file. You can either double-click the profid.jar file or run the following command in the console

``java -jar /path/to/file/profid.jar``

After starting the GUI, you need to enter the simulation parameters. There are two ways to do this:

1) Using GUI: You can enter the simulation parameters using GUI which consists of two sets of parameters (network parameters, protocol parameters). After entering all parameters, you should click on the run button (green triangle on top).

2) Using a config file: You can also run a simulation by providing a configuration file. To provide a configuration file choose File -> Simulate From File from the top meun, and choose the configuration file from the file chooser. For the format of the configuration file and details of peersim simulator, see [this](http://peersim.sourceforge.net/#docs). You can find an example config file and its detailed explanation [here](http://peersim.sourceforge.net/tutorial1/tutorial1.html)

###User Interface
=================
User interface consists of two sets of parameters shown as two seperate tabs: 1) Network Parameters 2) ProFID Parameters. For the detailed explanations of each parameter, see the *Help -> How-to* menu.

[<img src="https://utdallas.edu/~emrah.cem/img/profid/profid_snapshot1.png" width="300">](https://utdallas.edu/~emrah.cem) [<img src="https://utdallas.edu/~emrah.cem/img/profid/profid_snapshot2.png" width="300">](https://utdallas.edu/~emrah.cem)

