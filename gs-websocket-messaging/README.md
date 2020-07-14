# Spring WebSocket

El protocolo **WebSocket** [RFC 6455](https://tools.ietf.org/html/rfc6455), proporciona una forma estandarizada de establecer un canal de comunicación **bidireccional full-duplex** entre el cliente y el servidor a través de una única conexión TCP. 

Una interacción de WebSocket comienza con una solicitud HTTP que utiliza el campo _Upgrade_ del **HEADER HTTP** para actualizar, o en este caso, para cambiar al protocolo WebSocket.

    GET /spring-websocket-portfolio/portfolio HTTP/1.1
    Host: localhost:8080
    Upgrade: websocket 
    Connection: Upgrade 
    Sec-WebSocket-Key: Uc9l9TMkWGbHFD2qnFHltg==
    Sec-WebSocket-Protocol: v10.stomp, v11.stomp
    Sec-WebSocket-Version: 13
    Origin: http://localhost:8080
 
En lugar del código de estado 200 habitual, un servidor con soporte WebSocket devuelve resultados similares a los siguientes

    HTTP/1.1 101 Switching Protocols 
    Upgrade: websocket
    Connection: Upgrade
    Sec-WebSocket-Accept: 1qVdfYHU9hPOl4JYYNXF623Gzn0=
    Sec-WebSocket-Protocol: v10.stomp
 
Después de que se establece el cambio de protocolo de forma exitosa, el socket TCP subyacente a la permanece abierto para que el cliente y el servidor continúen enviando y recibiendo mensajes.

Para más detalles sobre cómo funciona el protocolo WebSocket consultar el [RFC 6455](https://tools.ietf.org/html/rfc6455).

## HTTP versus WebSocket
Aunque WebSocket está diseñado para ser compatible con HTTP y comienza con una solicitud HTTP, es importante comprender que estos dos protocolos conducen una arquitectura y modelo de programación muy diferentes.

En HTTP y REST, una aplicación se modela a través de varias URLs. Para interactuar con la aplicación, los clientes acceden a las URL, siguiendo el patrón request-response. Los servidores enrutan las solicitudes al controlador apropiado en función de la URL, en método y los encabezados HTTP.

Por el contrario, en WebSocket, generalmente solo hay una URL para la conexión inicial.  Posteriormente, todos los mensajes de la aplicación fluyen sobre la misma conexión TCP. Esto apunta a una arquitectura de mensajería asincrónica, controlada por eventos.

WebSocket también es un protocolo de transporte de bajo nivel que, a diferencia de HTTP, no prescribe ninguna semántica al contenido de los mensajes. Eso significa que no hay forma de enrutar o procesar un mensaje a menos que el cliente y el servidor acuerden la semántica del mensaje.

Los clientes y servidores de WebSocket pueden negociar el uso de un protocolo de mensajería de un nivel superior (por ejemplo, STOMP), a través del encabezado **Sec-WebSocket-Protocol** en la solicitud inicial o de enlace HTTP. En ausencia de eso, necesitan crear sus propias convenciones.





















### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.8.RELEASE/maven-plugin/)

https://docs.spring.io/spring/docs/5.0.0.BUILD-SNAPSHOT/spring-framework-reference/html/websocket.html
https://spring.io/guides/gs/messaging-stomp-websocket/
https://www.baeldung.com/websockets-spring
http://stomp.github.io/stomp-specification-1.2.html
https://docs.spring.io/spring/docs/5.2.7.RELEASE/spring-framework-reference/web.html#websocket

¿Te imaginas trabajando en el app que gestiona el inbox de millones de usuarios y es responsable de lidiar con el envío de millones notificarlos via push ?
 ¿Te apasiona que tu aplicación no se caiga nunca, aplicar SRE y tener 100% de uptime?