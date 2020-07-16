# Spring WebSocket Holla mundo

Este proyecto es una implementación “Hola mundo” de una app que envía mensajes de ida y vuelta entre un navegador y un servidor. 

Lo que se creara es un servidor que acepte un mensaje que lleve el nombre de un usuario. Es respuesta, el servidor enviará un saludo a una cola a la que está suscripto el/los cliente/s.

## Creación y configuración de dependencias

Creamos un proyecto de spring boot con utilizando la versión 2.2.2.RELEASE y a continuación le agregamos en el pom.xml las siguientes dependencias.

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-websocket</artifactId>
    </dependency>
    <dependency>
        <groupId>org.webjars</groupId>
        <artifactId>webjars-locator-core</artifactId>
    </dependency>
    <dependency>
        <groupId>org.webjars</groupId>
        <artifactId>sockjs-client</artifactId>
        <version>1.0.2</version>
    </dependency>
    <dependency>
        <groupId>org.webjars</groupId>
        <artifactId>stomp-websocket</artifactId>
        <version>2.3.3</version>
    </dependency>
    <dependency>
        <groupId>org.webjars</groupId>
        <artifactId>bootstrap</artifactId>
        <version>3.3.7</version>
    </dependency>
    <dependency>
        <groupId>org.webjars</groupId>
        <artifactId>jquery</artifactId>
        <version>3.1.1-1</version>
    </dependency>

También incluiremos a nuestro proyecto la dependencia de lombok, la cual no es necesaria para el funcionamiento de nuestro proyecto, pero nos ahora mucho trabajo y nos mantiene el código más limpio.

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

## Creación de DTO
Lo primero que haremos es crear la estructura de mensaje de entrada que recibirá nuestro servidor de mensajería STOMP.

Nuestro servidor acepta mensajes que contengan un nombre en un mensaje STOMP, cuyo cuerpo sea un objeto JSON. A continuación, se muestra un ejemplo del objeto JSON de entrada:

    {
        "name" : "Perez"
    }

La representación de nuestro JSON en una clase Java es la siguiente:

    package ar.com.jlv.gs.spring.websocket.messaging;

    import lombok.Data;

    @Data
    public class HelloMessage {
        private String name;
    }

Como se puede observar es un simple **java bean** con el atributo **name**.

Al igual que el mensaje de entrada, la respuesta estará representada por un JSON, cuya estructura es la siguiente:

    {
        "content" : "Hello, Perez"
    }

La representación de nuestro JSON de salida en una clase Java es la siguiente:

    package ar.com.jlv.gs.spring.websocket.messaging;

    import lombok.Data;

    @Data
    public class Greeting {
        private String content;
    
        public Greeting(String content) {
            this.content = content;
          }
    }

Spring usará la biblioteca **Jackson JSON** para reunir automáticamente las instancias del  tipo Greeting en JSON

## Creación del manejador de mensajes
Para el enrutamiento de los mensajes **STOMP** nos valdremos de los **@Controller**. Para esto crearemos nuestro controlador llamado **GreetingController**, al cual le indicaremos que acepte los mensajes enviados a **/hello** utilizando la anotación **@MessageMapping** y lo reenviaremos a **/topic/greetings** utilizando la anotación **@SendTo**.

Nuestro controller nos quedara como se muestra a continuación

    package ar.com.jlv.gs.spring.websocket.messaging;

    import org.springframework.messaging.handler.annotation.MessageMapping;
    import org.springframework.messaging.handler.annotation.SendTo;
    import org.springframework.stereotype.Controller;
    import org.springframework.web.util.HtmlUtils;

    @Controller
    public class GreetingController {
	    @MessageMapping("/hello")
	    @SendTo("/topic/greetings")
	    public Greeting greeting(HelloMessage message) throws Exception {
		    Thread.sleep(1000);
		    return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
	    }
    }

Entendamos un poco lo que hace esta pequeño controller. 

Lo primero a entender es que mediante la anotación **@MessageMapping** establecemos cual va hacer vuestra fuente de datos **/hello**. La carga útil de esta fuente se mapea con el Objeto **HelloMessage**.

Internamente, el método contiene un sleep generar un pequeño retraso y de esta forma simular el procesamiento de datos. Este pequeño retraso también nos da la posibilidad de probar desde el lado del cliente, que podemos seguir trabajando con total normalidad y cuando el server termine de procesas la información esta nos será enviada y se nos mostrara por pantalla.

Por último, el método crea un Objeto Greeting y lo devuelve. Este Objeto es enviado a todos los suscritores del topic** /topic/Greetings**, el cual fue especificado con la anotación **@SendTo**. 

## Creación de WebSocket Config

Llego el momento de configurar Spring para habilitar la mensajería WebSocket y STOMP.

Para esto crearemos la siguiente clase **WebSocketConfig**:

    package ar.com.jlv.gs.spring.websocket.messaging;

    import org.springframework.context.annotation.Configuration;
    import org.springframework.messaging.simp.config.MessageBrokerRegistry;
    import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
    import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
    import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

    @Configuration
    @EnableWebSocketMessageBroker
    public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	    @Override
        public void configureMessageBroker(MessageBrokerRegistry config) {
            config.enableSimpleBroker("/topic");
            config.setApplicationDestinationPrefixes("/app");
        }

        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            registry.addEndpoint("/gs-guide-websocket").withSockJS();
        }
    }

Lo primero que se hace en nuestra clase WebSocketConfig es declararla como un componente de configuración a través de la anotación @Configuration y habilitar el manejo de mensajes de WebSocket con la anotación @EnableWebSocketMessageBroker.

Nuestra clase config consta de 2 métodos:
* **configureMessageBroker**: Es la implementación del método predeterminado para configurar el intermediario de mensajes. Comienza llamando al método **enableSimpleBroker()** para permitir que un simple agente de mensajes basado en memoria lleve los mensajes de saludo a los clientes que estén escuchando el destinos con el prefijo **/topic**. También designamos, con el método **setApplicationDestinationPrefexes()**, el prefijo **/app** para los mensajes de entradas los cuales se leerán con la anotación @MessageMapping. Este prefijo se utilizará para definir todas las asignaciones de mensajes. En nuestro caso **/app/hello**
* **registerStompEndpoints:** este método registra el **/gs-guide-websocket** como punto final, habilitando las opciones de respaldo de **SockJS** para que se pueda utilizar transportes alternativos si WebSocket no está disponible. El cliente **SockJS** intentara conectarse al punto final **/gs-guide-websocket** y utilizar el mejor trasporte disponible (WebSocket, xhr-streaming, xhr-polling, etc).


## Creación del cliente
Con las piezas del lado del servidor en su lugar, puede dirigir su atención al cliente de JavaScript que enviará y recibirá mensajes del lado del servidor.

Crearemos el siguiente **index.html**, dentro de src/main/resources/static/index.html.

    <!DOCTYPE html>
    <html>
    <head>
        <title>Hello WebSocket</title>
        <link href="/webjars/bootstrap/css/bootstrap.min.css" rel="stylesheet">
        <link href="/main.css" rel="stylesheet">
        <script src="/webjars/jquery/jquery.min.js"></script>
        <script src="/webjars/sockjs-client/sockjs.min.js"></script>
        <script src="/webjars/stomp-websocket/stomp.min.js"></script>
        <script src="/app.js"></script>
    </head>
    <body>
    <noscript><h2 style="color: #ff0000">Seems your browser doesn't support Javascript! Websocket relies on Javascript being
        enabled. Please enable
        Javascript and reload this page!</h2></noscript>
    <div id="main-content" class="container">
        <div class="row">
            <div class="col-md-6">
                <form class="form-inline">
                    <div class="form-group">
                        <label for="connect">WebSocket connection:</label>
                        <button id="connect" class="btn btn-default" type="submit">Connect</button>
                        <button id="disconnect" class="btn btn-default" type="submit" disabled="disabled">Disconnect
                        </button>
                    </div>
                </form>
            </div>
            <div class="col-md-6">
                <form class="form-inline">
                    <div class="form-group">
                        <label for="name">What is your name?</label>
                        <input type="text" id="name" class="form-control" placeholder="Your name here...">
                    </div>
                    <button id="send" class="btn btn-default" type="submit">Send</button>
                </form>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12">
                <table id="conversation" class="table table-striped">
                    <thead>
                    <tr>
                        <th>Greetings</th>
                    </tr>
                    </thead>
                    <tbody id="greetings">
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    </body>
    </html>

Este archivo HTML importa las bibliotecas **SockJS** y **STOMP** javascript que se utilizarán para comunicarse con nuestro servidor a través de websocket. También importamos app.js, que contiene la lógica de nuestra aplicación cliente. El archivo **app.js** lo tendremos que crear dentro de src/main/resources/static/app.js

    var stompClient = null;

    function setConnected(connected) {
        $("#connect").prop("disabled", connected);
        $("#disconnect").prop("disabled", !connected);
        if (connected) {
            $("#conversation").show();
        }
        else {
            $("#conversation").hide();
        }
        $("#greetings").html("");
    }

    function connect() {
        var socket = new SockJS('/gs-guide-websocket');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            setConnected(true);
            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/greetings', function (greeting) {
                showGreeting(JSON.parse(greeting.body).content);
            });
        });
    }

    function disconnect() {
        if (stompClient !== null) {
            stompClient.disconnect();
        }
        setConnected(false);
        console.log("Disconnected");
    }

    function sendName() {
        stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
    }

    function showGreeting(message) {
        $("#greetings").append("<tr><td>" + message + "</td></tr>");
    }

    $(function () {
        $("form").on('submit', function (e) {
            e.preventDefault();
        });
        $( "#connect" ).click(function() { connect(); });
        $( "#disconnect" ).click(function() { disconnect(); });
        $( "#send" ).click(function() { sendName(); });
    });


Las piezas principales de este js para comprender son las funciones **connect()** y **sendName()**.

La función **connect()** utiliza las librerías SockjS y stomp para abrir una conexión al punto final **/gs-guide-websocket**. Tras una conexión exitosa el cliente se subscribe al destino **/topic/greeting**, donde el servidor publicara los mensajes. Cuando se reciba un mensaje se agregara un elemento de párrafo al DOM para mostrar el mensaje.

La función **sendName()** recupera el nombre ingresado por el usuario y utiliza el cliente STOMP para enviar un mensaje al destino **/app/hello**.

Por ultimo agregaremos un pequeño css para que nuestro cliente se vea un poco más elegante. El css lo agregamos en la carpeta src/main/resources/static/main.css

    body {
        background-color: #f5f5f5;
    }

    #main-content {
        max-width: 940px;
        padding: 2em 3em;
        margin: 0 auto 20px;
        background-color: #fff;
        border: 1px solid #e5e5e5;
        -webkit-border-radius: 5px;
        -moz-border-radius: 5px;
        border-radius: 5px;
    }

## Ejecutar la aplicación

Como toda aplicación creada con Spring Boot para ejecutar nuestra aplicación solo tenemos que correr nuestra clase *Application. La cual identificaremos porque lleva la anotación **@SpringBootApplication**.

### Construir un JAR ejecutable
Puede ejecutar la aplicación desde la línea de comandos Maven. También puede crear un único archivo JAR ejecutable que contenga todas las dependencias, clases y recursos necesarios y ejecutarlo. La creación de un archivo jar ejecutable facilita el envío, la versión y la implementación del servicio como una aplicación durante todo el ciclo de vida de desarrollo, en diferentes entornos, etc.

Al usar Maven, puede ejecutar la aplicación usando **./mvnw spring-boot:run**. Alternativamente, puede compilar el archivo JAR con **./mvnw clean package** y luego ejecutar el archivo JAR de la siguiente manera:

**java -jar target / gs-messaging-stomp-websocket-0.1.0.jar**

## Prueba el servicio
Ahora que el servicio se está ejecutando, apunte su navegador a http://localhost:8080 y haga clic en el botón Conectar .

Al abrir una conexión, se le solicita su nombre. Ingrese su nombre y haga clic en Enviar . Su nombre se envía al servidor como un mensaje JSON a través de STOMP. Después de un retraso simulado de un segundo, el servidor envía un mensaje de vuelta con un saludo "Hola" que se muestra en la página. En este punto, puede enviar otro nombre o puede hacer clic en el botón Desconectar para cerrar la conexión.

Una prueba adicional que puede realzar es abrir varios navegadores, conectarse al servidor y ver como se replican los mensajes.






