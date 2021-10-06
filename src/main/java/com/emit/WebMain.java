package com.emit;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;

public class WebMain {

	public WebMain() {

	}

	public static void main(String[] args) throws InterruptedException {
		ParameterizedTypeReference<ServerSentEvent<String>> type = new ParameterizedTypeReference<ServerSentEvent<String>>() {};
	     
		WebClient.create("http://127.0.0.1:8080")
			.get()
			.uri("/receive")
			.accept(MediaType.TEXT_EVENT_STREAM)
			.retrieve()
			.bodyToFlux(type)
			.subscribe(e -> System.out.println(e.data()));
		
		Thread.sleep(60 * 1000 * 1000);
	}
}
