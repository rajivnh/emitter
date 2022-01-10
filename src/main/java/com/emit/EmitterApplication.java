package com.emit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitResult;

@RestController
@SpringBootApplication
public class EmitterApplication {
	private Sinks.Many<Employee> sink;

	public EmitterApplication() {
		this.sink = Sinks.many().multicast().onBackpressureBuffer();
	}

	public static void main(String[] args) {
		SpringApplication.run(EmitterApplication.class, args);
	}

	@GetMapping("/send")
	public void test() throws InterruptedException {
		Employee[] emps = new Employee[60];

		for (int i = 0; i < 60; i++) {
			emps[i] = new Employee();

			emps[i].setId(String.valueOf(i));

			EmitResult result = sink.tryEmitNext(emps[i]);

			if (result.isFailure()) {
				System.out.println("FAILED");
			}

			Thread.sleep(200);
		}
	}

	@GetMapping("/complete")
	public void complete() {
		sink.tryEmitComplete();
	}

	@RequestMapping(path = "/receive", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<Employee>> sse() {
		return sink.asFlux().map(e -> ServerSentEvent.builder(e).build());
	}
}

class Employee {
	private String id;

	public Employee() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}