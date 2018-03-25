package com.mycorp.zendesk.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.mycorp.zendesk.integration.EmailService;
import com.mycorp.zendesk.integration.zendesk.Zendesk;
import com.mycorp.zendesk.model.mapper.TicketMapper;

import portalclientesweb.ejb.interfaces.PortalClientesWebEJBRemote;
import util.datos.UsuarioAlta;

@RunWith(MockitoJUnitRunner.class)
public class ZendeskServiceImplTest {

	@InjectMocks
	ZendeskServiceImpl zendeskServiceImpl;

	@Mock
	private PortalClientesWebEJBRemote portalclientesWebEJBRemote;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private EmailService emailService;

	@Mock
	private TicketMapper ticketMapper;

	@Mock
	private Zendesk zendesk;
	
	@Before
	public void setup() {
		ReflectionTestUtils.setField(zendeskServiceImpl, "tokenZendesk", "");
		ReflectionTestUtils.setField(zendeskServiceImpl, "urlZendesk", "");
		ReflectionTestUtils.setField(zendeskServiceImpl, "userZendesk", "");
		ReflectionTestUtils.setField(zendeskServiceImpl, "urlGetDatosTarjetas", "");
		ReflectionTestUtils.setField(zendeskServiceImpl, "urlGatDatosClientes", "");
		ReflectionTestUtils.setField(zendeskServiceImpl, "funcErroMail", "");
		ReflectionTestUtils.setField(zendeskServiceImpl, "emailDestError", "");
	}
	
	@Test
	public void test() {
		
		// Test Data
		UsuarioAlta usuarioAlta = new UsuarioAlta();
		String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36";
		
		// Call
		zendeskServiceImpl.altaTicketZendesk(usuarioAlta, userAgent);
	}

}
