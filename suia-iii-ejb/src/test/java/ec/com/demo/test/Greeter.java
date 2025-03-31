package ec.com.demo.test;

import javax.ejb.Stateless;

@Stateless
public class Greeter {

	public String createGreeting(String name) {
		return "Hola_" + name;
	}

}
