package com.mycorp.zendesk.service.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mycorp.support.CorreoElectronico;
import com.mycorp.support.DatosCliente;
import com.mycorp.support.MensajeriaService;
import com.mycorp.support.Poliza;
import com.mycorp.support.PolizaBasicoFromPolizaBuilder;
import com.mycorp.support.Ticket;
import com.mycorp.support.ValueCode;
import com.mycorp.zendesk.integration.Zendesk;
import com.mycorp.zendesk.model.mapper.TicketMapper;
import com.mycorp.zendesk.service.ZendeskService;

import portalclientesweb.ejb.interfaces.PortalClientesWebEJBRemote;
import util.datos.DetallePoliza;
import util.datos.PolizaBasico;
import util.datos.UsuarioAlta;

/**
 * The Class ZendeskServiceImpl.
 */
@Service
public class ZendeskServiceImpl implements ZendeskService {

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(ZendeskServiceImpl.class);

	/** The Constant ESPACIO. */
	private static final String ESPACIO = " ";

	/** The Constant ESCAPED_LINE_SEPARATOR. */
	private static final String ESCAPED_LINE_SEPARATOR = "\\n";
	/** The Constant ESCAPE_ER. */
	private static final String ESCAPE_ER = "\\";
	/** The Constant HTML_BR. */
	private static final String HTML_BR = "<br/>";

	/** The token zendesk. */
	@Value("${zendesk.token}")
	public String tokenZendesk;

	/** The url zendesk. */
	@Value("${zendesk.url}")
	public String urlZendesk;

	/** The user zendesk. */
	@Value("${zendesk.user}")
	public String userZendesk;

	/** The url get datos tarjetas. */
	@Value("${tarjetas.getDatos}")
	public String urlGetDatosTarjetas;

	/** The url gat datos clientes. */
	@Value("${cliente.getDatos}")
	public String urlGatDatosClientes;

	/** The func erro mail. */
	@Value("${zendesk.error.mail.funcionalidad}")
	public String funcErroMail;

	/** The email dest error. */
	@Value("${zendesk.error.destinatario}")
	public String emailDestError;

	/** The portalclientes web ejb remote. */
	@Autowired
	@Qualifier("portalclientesWebEJB")
	private PortalClientesWebEJBRemote portalclientesWebEJBRemote;

	/** The rest template. */
	@Autowired
	@Qualifier("restTemplateUTF8")
	private RestTemplate restTemplate;

	/** The email service. */
	@Autowired
	@Qualifier("emailService")
	MensajeriaService emailService;

	/** The ticket mapper. */
	@Autowired
	private TicketMapper ticketMapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycorp.zendesk.service.ZendeskServiceI#altaTicketZendesk(util.datos.
	 * UsuarioAlta, java.lang.String)
	 */
	@Override
	public String altaTicketZendesk(UsuarioAlta usuarioAlta, String userAgent) {

		// Instance Data
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		String idCliente = null;
		StringBuilder clientName = new StringBuilder();
		String detallePoliza = "";

		// get idCliente and name
		if (StringUtils.isNotBlank(usuarioAlta.getNumTarjeta())) {
			try {
				ResponseEntity<String> res = getDatosTarjetas(usuarioAlta);
				if (res.getStatusCode() == HttpStatus.OK) {
					idCliente = res.getBody();
					clientName.append(idCliente);
					detallePoliza = mapper.writeValueAsString(idCliente);
				}
			} catch (Exception e) {
				LOG.error("Error al obtener los datos de la tarjeta", e);
			}
		} else if (StringUtils.isNotBlank(usuarioAlta.getNumPoliza())) {
			try {
				DetallePoliza detallePolizaResponse = getDatosPoliza(usuarioAlta);

				clientName = (new StringBuilder()).append(detallePolizaResponse.getTomador().getNombre())
						.append(ESPACIO).append(detallePolizaResponse.getTomador().getApellido1()).append(ESPACIO)
						.append(detallePolizaResponse.getTomador().getApellido2());

				detallePoliza = mapper.writeValueAsString(detallePolizaResponse);

				idCliente = detallePolizaResponse.getTomador().getIdentificador();
			} catch (Exception e) {
				LOG.error("Error al obtener los datos de la poliza", e);
			}
		}

		// get client Data
		DatosCliente cliente = getDatosCliente(idCliente);

		// get Ticket Data and call create Ticket
		String ticket = ticketMapper.getTicket(usuarioAlta, clientName.toString(), userAgent, detallePoliza, cliente,
				getTiposDocumentosRegistro());
		try (Zendesk zendesk = new Zendesk.Builder(urlZendesk).setUsername(userZendesk).setToken(tokenZendesk).build()) {
			// Ticket
			Ticket petiZendesk = mapper.readValue(ticket, Ticket.class);
			zendesk.createTicket(petiZendesk);

		} catch (Exception e) {
			LOG.error("Error al crear ticket ZENDESK", e);
			
			// If Error send mail
			CorreoElectronico correo = new CorreoElectronico(Long.parseLong(funcErroMail), "es").addParam(ticketMapper.getDatosUsuario(usuarioAlta, userAgent)
							.replaceAll(ESCAPE_ER + ESCAPED_LINE_SEPARATOR, HTML_BR))
					.addParam(ticketMapper.getDatosBravo(cliente, getTiposDocumentosRegistro())
							.replaceAll(ESCAPE_ER + ESCAPED_LINE_SEPARATOR, HTML_BR));
			correo.setEmailA(emailDestError);
			try {
				emailService.enviar(correo);
			} catch (Exception ex) {
				LOG.error("Error al enviar mail", ex);
			}

		}

		return ticketMapper.getDatosUsuario(usuarioAlta, userAgent) + ticketMapper.getDatosBravo(cliente, getTiposDocumentosRegistro());
	}

	/**
	 * Gets the datos poliza.
	 *
	 * @param usuarioAlta the usuario alta
	 * @return the datos poliza
	 */
	private util.datos.DetallePoliza getDatosPoliza(UsuarioAlta usuarioAlta) {
		Poliza poliza = new Poliza();
		poliza.setNumPoliza(Integer.valueOf(usuarioAlta.getNumPoliza()));
		poliza.setNumColectivo(Integer.valueOf(usuarioAlta.getNumDocAcreditativo()));
		poliza.setCompania(1);

		PolizaBasico polizaBasicoConsulta = new PolizaBasicoFromPolizaBuilder().withPoliza(poliza).build();

		return portalclientesWebEJBRemote.recuperarDatosPoliza(polizaBasicoConsulta);
	}

	/**
	 * Gets the datos tarjetas.
	 *
	 * @param usuarioAlta the usuario alta
	 * @return the datos tarjetas
	 */
	private ResponseEntity<String> getDatosTarjetas(UsuarioAlta usuarioAlta) {
		String urlToRead = urlGetDatosTarjetas + usuarioAlta.getNumTarjeta();
		return restTemplate.getForEntity(urlToRead, String.class);
	}

	/**
	 * Gets the datos cliente.
	 *
	 * @param idCliente the id cliente
	 * @return the datos cliente
	 */
	private DatosCliente getDatosCliente(String idCliente) {
		DatosCliente cliente = null;
		try {
			cliente = restTemplate.getForObject(urlGetDatosTarjetas, DatosCliente.class, idCliente);
		} catch (Exception e) {
			LOG.error("Error al obtener los datos en BRAVO del cliente", e);
		}
		return cliente;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycorp.zendesk.service.ZendeskServiceI#getTiposDocumentosRegistro()
	 */
	@Override
	public List<ValueCode> getTiposDocumentosRegistro() {
		return Arrays.asList(new ValueCode(), new ValueCode()); // simulacion servicio externo
	}
}