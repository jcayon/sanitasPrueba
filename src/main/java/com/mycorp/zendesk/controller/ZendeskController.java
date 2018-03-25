package com.mycorp.zendesk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mycorp.zendesk.service.ZendeskService;

import util.datos.UsuarioAlta;

@RestController
public class ZendeskController {

	@Autowired
	private ZendeskService zendeskService;
	
	@RequestMapping(value="/ticket", method=RequestMethod.POST)
	public String altaTicket(
			@RequestBody UsuarioAlta usuarioAlta, 
			@RequestHeader(value="User-Agent") String userAgent) {
        return zendeskService.altaTicketZendesk(usuarioAlta, userAgent);
    }
	
}
