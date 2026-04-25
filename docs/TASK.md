Assignment 3: Middleware for Remote Procedure Call/Remote Method Invocation. The Remote Proxy Pattern
Objectives

The goal of this assigment is to understand the main architectural components of RPC/RMI middleware and their common architectural pattern.
Standard requirements

Implement a simple banking client-server application that supports the following operations:

    Add an amount to an account
    Withdraw an amount from an account
    Transfer an amount from one account to another
    Query the balance of an account

Provide two implementations of the banking scenario: one using Java RMI and one using gRPC
Bonus requirements

This part should be adresed only after you answered the standard requirments.

Design and implement a simplified Remote Method Invocation Framework that enables clients to invoke methods on remote objects as if they were local. As a simplifying assumptions,the remote methods can have parameter and return types limited to Integer and String (can have multiple parameters, but only of these types).

You can choose to implement generation of stubs either statically or dynamically. (Each variant is 1 bonus point. If your implementation supports both methods for stub generation then you can accumulate 2 bonus points).

For details on the architectural components of RPC/RMI middleware and their responsabilities see the slides and discussion in lectures week 6 and 7.

The system must use a custom protocol over TCP sockets and implement explicit marshalling and unmarshalling of requests and responses. You can start from the provided code for a Requestor-Replier which sends and receives bytes over TCP,  given in SocketRequestReply.zip This archive contains the implementation of the RequestReply and a simple Echo client-server example using it. You do not have to dig into socket communication, you can use the Requestor sendRequestAndWaitResponse and the Replier start methods while providing a customized implementation of a ByteStreamTransformer.

Demonstrate the usability of your framework by developing two different applications on top of it. One of the applications can be the banking application.
Deadlines and Grading

The deadline for this assignment is in Week 10. See the Grading Policy on CV.
Resources

    Slides - Lecture notes on Reflection (weeks 6 and 7) link
    Source code of examples from lecture 7: Example with JavaRMI, Example with gRPC
    Code to use as start: Requestor-Replyer Bytes over TCP socket:  SocketRequestReply.zip
    Java RMI tutorial
    gRPC
    Java Dynamic Proxy doc
