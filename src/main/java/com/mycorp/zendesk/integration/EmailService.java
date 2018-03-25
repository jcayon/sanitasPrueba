package com.mycorp.zendesk.integration;

import org.springframework.stereotype.Component;

import com.mycorp.support.CorreoElectronico;
import com.mycorp.support.MensajeriaService;

@Component("emailService")
public class EmailService implements MensajeriaService {

	@Override
	public void enviar(CorreoElectronico correo) {
		// TODO Auto-generated method stub
		
	}

}
