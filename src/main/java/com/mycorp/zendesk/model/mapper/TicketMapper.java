package com.mycorp.zendesk.model.mapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mycorp.support.DatosCliente;
import com.mycorp.support.ValueCode;
import com.mycorp.zendesk.exception.ZendeskException;

import util.datos.UsuarioAlta;

/**
 * The Class TicketMapper.
 */
@Component
public class TicketMapper {

	/** The Constant ESCAPED_LINE_SEPARATOR. */
	private static final String ESCAPED_LINE_SEPARATOR = "\\n";
	/** The Constant ESCAPE_ER. */
	private static final String ESCAPE_ER = "\\";
	/** The Constant HTML_BR. */
	private static final String HTML_BR = "<br/>";

	/** The formatter. */
	private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

	/** The peticion zendesk. */
	@Value("${zendesk.ticket}")
	public String peticionZendesk;

	/**
	 * Gets the ticket.
	 *
	 * @param usuarioAlta            the usuario alta
	 * @param clientName the client name
	 * @param userAgent the user agent
	 * @param detallePoliza the detalle poliza
	 * @param cliente the cliente
	 * @param tiposDocumentos the tipos documentos
	 * @return the ticket
	 */
	public String getTicket(UsuarioAlta usuarioAlta, String clientName, String userAgent, String detallePoliza,
			DatosCliente cliente, List<ValueCode> tiposDocumentos) {
		return String
				.format(peticionZendesk, clientName, usuarioAlta.getEmail(),
						getDatosUsuario(usuarioAlta, userAgent) + getDatosBravo(cliente, tiposDocumentos)
								+ parseJsonBravo(getDatosServicio(detallePoliza)))
				.replaceAll("[" + ESCAPED_LINE_SEPARATOR + "]", " ");
	}

	/**
	 * Gets the datos usuario.
	 *
	 * @param usuarioAlta            the usuario alta
	 * @param userAgent the user agent
	 * @return the datos usuario
	 */
	public String getDatosUsuario(UsuarioAlta usuarioAlta, String userAgent) {

		StringBuilder datosUsuario = new StringBuilder();

		if (StringUtils.isNotBlank(usuarioAlta.getNumPoliza())) {
			datosUsuario.append("Nº de poliza/colectivo: ").append(usuarioAlta.getNumPoliza()).append("/")
					.append(usuarioAlta.getNumDocAcreditativo()).append(ESCAPED_LINE_SEPARATOR);
		} else {
			datosUsuario.append("Nº tarjeta Sanitas o Identificador: ").append(usuarioAlta.getNumTarjeta())
					.append(ESCAPED_LINE_SEPARATOR);
		}
		datosUsuario.append("Tipo documento: ").append(usuarioAlta.getTipoDocAcreditativo())
				.append(ESCAPED_LINE_SEPARATOR);
		datosUsuario.append("Nº documento: ").append(usuarioAlta.getNumDocAcreditativo())
				.append(ESCAPED_LINE_SEPARATOR);
		datosUsuario.append("Email personal: ").append(usuarioAlta.getEmail()).append(ESCAPED_LINE_SEPARATOR);
		datosUsuario.append("Nº movil: ").append(usuarioAlta.getNumeroTelefono()).append(ESCAPED_LINE_SEPARATOR);
		datosUsuario.append("User Agent: ").append(userAgent).append(ESCAPED_LINE_SEPARATOR);

		return datosUsuario.toString();
	}

	/**
	 * Gets the datos bravo.
	 *
	 * @param cliente the cliente
	 * @param tiposDocumentos the tipos documentos
	 * @return the datos bravo
	 */
	public String getDatosBravo(DatosCliente cliente, List<ValueCode> tiposDocumentos) {

		StringBuilder datosBravo = new StringBuilder();
		datosBravo.append(ESCAPED_LINE_SEPARATOR).append("Datos recuperados de BRAVO:").append(ESCAPED_LINE_SEPARATOR)
				.append(ESCAPED_LINE_SEPARATOR);
		datosBravo.append("TelÃ©fono: ").append(cliente.getGenTGrupoTmk()).append(ESCAPED_LINE_SEPARATOR);
		try {
			datosBravo.append("Feha de nacimiento: ")
					.append(formatter.format(formatter.parse(cliente.getFechaNacimiento())))
					.append(ESCAPED_LINE_SEPARATOR);
		} catch (ParseException e) {
			throw new ZendeskException(e);
		}

		for (int i = 0; i < tiposDocumentos.size(); i++) {
			if (tiposDocumentos.get(i).getCode().equals(cliente.getGenCTipoDocumento().toString())) {
				datosBravo.append("Tipo de documento: ").append(tiposDocumentos.get(i).getValue())
						.append(ESCAPED_LINE_SEPARATOR);
			}
		}
		datosBravo.append("NÃºmero documento: ").append(cliente.getNumeroDocAcred()).append(ESCAPED_LINE_SEPARATOR);

		datosBravo.append("Tipo cliente: ");
		switch (cliente.getGenTTipoCliente()) {
		case 1:
			datosBravo.append("POTENCIAL").append(ESCAPED_LINE_SEPARATOR);
			break;
		case 2:
			datosBravo.append("REAL").append(ESCAPED_LINE_SEPARATOR);
			break;
		case 3:
			datosBravo.append("PROSPECTO").append(ESCAPED_LINE_SEPARATOR);
			break;
		}

		datosBravo.append("ID estado del cliente: ").append(cliente.getGenTStatus()).append(ESCAPED_LINE_SEPARATOR);

		datosBravo.append("ID motivo de alta cliente: ").append(cliente.getIdMotivoAlta())
				.append(ESCAPED_LINE_SEPARATOR);

		datosBravo.append("Registrado: ").append((cliente.getfInactivoWeb() == null ? "SÃ­" : "No"))
				.append(ESCAPED_LINE_SEPARATOR + ESCAPED_LINE_SEPARATOR);

		return datosBravo.toString();
	}

	/**
	 * Gets the datos servicio.
	 *
	 * @param detallePoliza the detalle poliza
	 * @return the datos servicio
	 */
	private StringBuilder getDatosServicio(String detallePoliza) {

		StringBuilder datosServicio = new StringBuilder();
		datosServicio.append("Datos recuperados del servicio de tarjeta:").append(ESCAPED_LINE_SEPARATOR)
				.append(detallePoliza);

		return datosServicio;
	}

	/**
	 * Parses the json bravo.
	 *
	 * @param resBravo
	 *            the res bravo
	 * @return the string
	 */
	private String parseJsonBravo(StringBuilder resBravo) {
		return resBravo.toString().replaceAll("[\\[\\]\\{\\}\\\"\\r]", "").replaceAll(ESCAPED_LINE_SEPARATOR,
				ESCAPE_ER + ESCAPED_LINE_SEPARATOR);
	}
}
