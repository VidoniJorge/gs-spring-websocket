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

## Creación de Controller

## Creación de WebSocket Config

## Creación del cliente
