## Intro
- Learning java, so I figured I should build a web server. Uses java's sockets library to instantiate a server and client (though, this is optional to the user, they can act as just a client or server). Reads/writes to a shared buffer. Serializes and deserializes the http request for transport.
- The client sends an http request to the server. For now, the file to serve to the client is a readable, non-directory/non-symlink file within the server. The client specifies this and the server returns it.
- Eventually I might build a gui to ease the user experience.
- Incorporates a basic listening thread and separate user input thread (for the client). Will add multithreading to both client and server so that they can have multiple TCP connections open at once (I am currently using TCP sockets, but will eventually add a UDP option).
- The TCP connection is persistent, consistent with HTTP 1.1. I will add a non-persistent option, as well as, eventually, support for HTTP 2.

## More updates
- Upon receiving a request (currently supporting get and put requests), the server will either fetch the data from the local machine, if it exists (in the case of a get request). Or, for a put request, will write it to the system, if the user has write permissions. Put requests are idempotent so we delete the file if it already exists.
- The server is currently not very smart in that it returns either a 200 OK or 400 BAD REQUEST based on if the request succeeds or not. Expanding support for many different response codes.
- Currently the client only connects to the loopback address (localhost), going to next add support for connecting to any server.