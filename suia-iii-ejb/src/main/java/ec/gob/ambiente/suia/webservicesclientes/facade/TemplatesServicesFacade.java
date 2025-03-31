/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.webservicesclientes.facade;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.ConvertidorJsonUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 01/06/2015]
 *          </p>
 */
@Stateless
public class TemplatesServicesFacade {

	public Map<String, String> getTemplates() throws Exception {
		String urlServicio = Constantes.getUrlTemplatesService();
		Map<String, String> templates = new HashMap<String, String>();
		JSONArray jsonArray = consumirServicio(urlServicio);
		for (int i = 0; i < jsonArray.size(); i++) {
			String[] template = ConvertidorJsonUtil.convertirJsonTemplate((JSONObject) jsonArray.get(i));
			templates.put(template[0], template[1]);
		}
		return templates;
	}

	public JSONArray consumirServicio(String urlServicio) throws Exception {
		ClientRequest request = new ClientRequest(urlServicio);
		request.accept(MediaType.APPLICATION_JSON);
		ClientResponse<String> response = request.get(String.class);
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getEntity()
				.getBytes())));
		JSONParser parser = new JSONParser();
		return (JSONArray) parser.parse(br);
	}

}
