El protocolo **WebSocket** [RFC 6455](https://tools.ietf.org/html/rfc6455), proporciona una forma estandarizada de establecer un canal de comunicación **bidireccional full-duplex** entre el cliente y el servidor a través de una única conexión TCP. 

Una interacción de WebSocket comienza con una solicitud HTTP que utiliza el campo **_Upgrade_** del **HEADER HTTP** para actualizar, o en este caso, para cambiar al protocolo WebSocket.

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

## Cuándo usar WebSockets
De forma simple podríamos decir que el protocolo WebSockets, es un candidato utilizar cuando estamos desarrollando una **aplicación interna**, la cual necesite una comunicación entre cliente y servido con **baja latencia, alta frecuencia y alto volumen de datos**.

Para aplicaciones donde el intercambio de datos no cumpla con algunas de las condicione mencionadas, posiblemente plantear una solución con Ajax y HTTP sea una mejor alternativa.

Evaluemos el otro requerimiento planteado, “Trabajar sobre una sea una aplicación interna”. Esto no significa que no se pueda plantear para aplicaciones públicas, pero tendremos que tener en cuenta que podríamos tener problemas debido a servidores proxis restrictivos (que están fuera de nuestro control) que pueden impedir las interacciones de WebSocket, ya sea porque no están configurados para pasar el **_Upgrade_** o porque cierran conexiones de larga duración que parecen inactivas.

