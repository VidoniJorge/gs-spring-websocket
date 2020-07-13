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
		System.out.println("esperando");
		Thread.sleep(1000); // simulated delay
		System.out.println("enviando el saludo");
		return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
	}
}