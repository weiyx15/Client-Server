# Client & server
## Introduction
Our class has a server which transmit event infomation. All the event information is wirtten on a file on the server. A event item includes four pieces of infomation: start time, end time, place and activity. The server will transmit the infomation to students via local area network. Every student in this class has a client, which just looks like an alarm clock. This client will listen to the server, record the event infomation and use alarm to remind the student. The client can join or leave the network at any time.
### Functions of the Server
1. GUI is not required
2. Accept TCP socket from a client and tranfer the history infomation. Then shut down the socket
3. Polling the file every second
4. If there is an update, broadcast it to all the clients in the network
### Functions of the Client
1. Current date & time display
2. Allow manually switch the time
3. Allow manually set one daily alarm
4. Words on the alarm popup are editable
5. One-time alarm can be automatedly set according to event infomation received
6. Display future event list
7. Events on the event list can be deleted manually
## Source Code Structure
### server
1. **RunServer.java**: Main class, starting two threads
2. **SingleServerThread.java**: TCP socket to transfer all the history data to client when the client connects the server. Shut down when data transfer process ends.
3. **BroadcastThread.java**: UDP socket to broadcast the newly updated data to all the clients
### client
1. **RunClient.java**: Main class, starting two threads
2. **Alarm.java**: The event list, which is shared data between two threads
3. **RunClientThread.java**: First establish TCP sockeck with server to acquire the history data and close the TCP socket. Then establish UDP socket with the server to acquire updating broadcast
4. **ClockThread.java**: Clock GUI, switch current time, set alarms and automated alarms, future event list display
### data file
**events.properties**: format>>"start_time**\t**end_time**\t**place**\t**activity"
## User Guide
1. To make the server work, RUN **RunServer**
2. To make the client work, RUN **RunClient**
3. Multi-clients allowed
4. To add events, edit **events.properties**
