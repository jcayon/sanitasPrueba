package com.mycorp.zendesk.service;

import java.util.List;

import com.mycorp.support.ValueCode;

import util.datos.UsuarioAlta;

public interface ZendeskService {

	/**
	 * Crea un ticket en Zendesk. Si se ha informado el nÂº de tarjeta, obtiene los datos asociados a dicha tarjeta de un servicio externo.
	 * @param usuarioAlta
	 * @param userAgent
	 */
	String altaTicketZendesk(UsuarioAlta usuarioAlta, String userAgent);

	/**
	 * Gets the tipos documentos registro.
	 *
	 * @return the tipos documentos registro
	 */
	List<ValueCode> getTiposDocumentosRegistro();

}