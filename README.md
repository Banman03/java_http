## Intro
- Learning java, so I figured I should build a web server. Uses java's sockets library to instantiate a server and client (though, this is optional to the user, they can act as just a client or server). Reads/writes to a shared buffer. Serializes and deserializes the http request for transport.
- The client sends an http request to the server. For now, the file to serve to the client is a readable, non-directory/non-symlink file within the server. The client specifies this and the server returns it.
- Eventually I might build a gui to ease the user experience.
- Incorporates a basic listening thread and separate user input thread (for the client). Will add multithreading to both client and server so that they can have multiple TCP connections open at once (I am currently using TCP sockets, but will eventually add a UDP option).
- The TCP connection is persistent, consistent with HTTP 1.1. I will add a non-persistent option, as well as, eventually, support for HTTP 2.