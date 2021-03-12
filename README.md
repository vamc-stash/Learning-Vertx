# Learning-Vertx

What is Vert.x?
//definition goes here

Vert.x is not a framework but a toolkit: the core library defines the fundamental APIs for writing asynchronous networked applications, and then you can pick the useful modules for your application (e.g., database connection, monitoring, authentication, logging, service discovery, clustering support, etc).
Vert.x does not impose any packaging or build environment. Since Vert.x core itself is just a regular Jar library it can be embedded inside applications packaged as a set of Jars, a single Jar with all dependencies, or it can even be deployed inside popular component and application containers.
Because Vert.x was designed for asynchronous communications it can deal with more concurrent network connections with less threads than synchronous APIs such as Java servlets or java.net socket classes.

1. What is a verticle? 
   - The unit of deployment in Vert.x is called a Verticle. A verticle processes incoming events over an event-loop, where events can be anything like receiving network buffers, timing events, or messages sent by other verticles. Each event shall be processed in a reasonable amount of time to not block the event loop.
   - Incoming network data are being received from accepting threads then passed as events to the corresponding verticles. When a verticle opens a network server and is deployed more than once, then the events are being distributed to the verticle instances in a round-robin fashion which is very useful for maximizing CPU usage with lots of concurrent networked requests. Finally, verticles have a simple start / stop life-cycle, and verticles can deploy other verticles.
2. What an event loop is
Event loop continuously check for new events, and each time a new event comes in, to quickly dispatch it to someone who knows how to handle it.
Every event loop is attached to a thread. By default Vert.x attaches 2 event loops per CPU core thread. One is running the application, and another is called `vertx-blocked-thread-checker`, which is used for debugging i.e., it is useful to know if a handler is blocking longer than we want.
Each verticle is assigned to a specific thread, and all handlers of that verticle are executed on that thread sequentially.  Multiple instances of the same verticle, however, can have their handlers executed at the same time. In fact, this holds for any two verticles. This means that if two verticles share a resource, you might still have to worry about concurrent access to that resource.
`Vert.x is multithreaded, so it has to create a thread for each verticle` (however, maximum value depends on the cores of the machine, not on number of veritcles deployed) and `Vert.x has EventLoop, so it’s single threaded and is using only one CPU`(This means that thread blocking operations shall not be performed while executed on the event loop.)

3. Event Bus
Event bus is the main tool for different verticles to communicate through asynchronous message passing. The event-bus allows passing any kind of data, although JSON is the preferred exchange format since it allows verticles written in different languages to communicate. The supported communication patterns are: 1. point-to-point messaging(direct messages), 2.request-response messaging and 3. publish / subscribe for broadcasting messages.
The event bus allows verticles to transparently communicate not just within the same JVM process:
- when network clustering is activated, the event bus is distributed so that messages can be sent to verticles running on other application nodes, meaning Verticles that are not necessarily running on the same machine can communicate with each other.
- the event-bus can be accessed through a simple TCP protocol for third-party applications to communicate,
- the event-bus can also be exposed over general-purpose messaging bridges (e.g, AMQP, Stomp),
- a SockJS bridge allows web applications to seamlessly communicate over the event bus from JavaScript running in the browser by receiving and publishing messages just like any verticle would do.
4. Future Objects and callbacks
The Vert.x core APIs are based on callbacks to notify of asynchronous events. 
There are two ways to deal with async calls in Vert.x.
- i) Pass a callback that will be executed on call completion. (-> Leads to Callback Hell problem)
- ii) Handle a future returned from the function/method call.
A future represents the result of some computation that is potentially available at some later stage. A future can either succeed or fail. When it succeed, its result will be available. When it fails, a throwable representing the cause of failure will be available. You can set a handler for a future, which will be called with the asynchronous result when the future has succeeded or failed.
Promise represents the writable side of an action that may, or may not, have occurred yet. Promise are for defining non-blocking operations, and it's future() method returns the Future associated with a promise, to get notified of the promise completion and retrieve its value.
Future: reference, read-only, at a value yet to be calculated
Promise: variable, assignable only once, to which Future refers
In other words, a Future shows the value previously written in a Promise in read-only mode.
