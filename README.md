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
- Now the client is able to connect to any external server via hostname or ip (can also connect to loopback). The client can then send HTTP requests to these servers.
- HTTP headers weren't working previously, so separated into a different class which is working nicely now. I need to fix an issue with the client listener thread because it starts reading an infinite loop of no data from the receive buffer, which is not good.

### 9/9/26

- I am starting to date the updates bc that seems like the smart thing to do. Anyways, I am unsatisfied with the architecture of the client at the moment. Currently, the client has a main thread (that the user types requests into); and a listener thread, which listens for responses from the server. This is not scalable because the user will have no way of specifying which server to send the requests to, things such as this. Here is what I propose:
    - A two-tiered architecture, where the first tier is the connection manageer. This tier will keep track of all current connections, as well as open new ones and close ones that are either stale, or the user wants to close.
    - The connection manager will also route requests to the correct write/listener pair, based on user input.
    - The second tier is the work tier, which is made up of multiple pairs of writer/listener pairs. Each of these pairs includes the client socket and the read/write buffer. The connection manager is not responsible for the state of each of writer/listeners.

### 9/10/26
- The two tiered approach appears to be working, although I am running into parsing issues with the http requests I am inputting from the command line. Part of me is tempted to use an actual parsing library that converts requests into an abstract syntax tree. I have mixed thoughts on this:
    - If we convert into an AST, it will be far easier to handle requests. This will also support a future development that I would like to support: reading in http requests from files. If I do this, I don't need to rely on users to input delimiters between headers and the body, because it will be evident from the \r\n\r\n that is inputted.
    - On the other hand, it seems like overkill to learn a parsing library solely for what is essentially having less of a headache when consuming input whitespace, etc. This is also a throwback to my compiler days.
- I think I will go with the parse, because that will also resolve the http request issue I am currently dealing with.