package com.mycorp.zendesk.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ZendeskController {

	@RequestMapping("/")
    String getGreeting() {
        return "Hello World!";
    }
	
}
