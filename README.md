# EmailServer
The Email Client Server is a locally hosted email server that allows users to freely send messages to one another similar to the processes performed by Outlook and Gmail. 

How it Works:
The server runs on Java and works to create ports and sockets to allow the flow of messages from one user to another. The program presents a user friendly pane where they can lay out the receiver, subject and body of the email. The server then separates the information into nodes containing 76 character long strings containing identifiers so that the program can reassemble the message when ready. Finally, the message appears on both the receiver and sender panes in their respective mailboxes: Outbox, Inbox.

Guided by Mario Portoraro
