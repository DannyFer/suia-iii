package ec.gob.ambiente.suia.webservicesclientes.facade;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class RestfulClientFacade {

	Logger LOG = Logger.getLogger(RestfulClientFacade.class);

	@SuppressWarnings("unchecked")
	public Object callServicePostFile(Map<String, Object> parameters, String urlService)
			throws Exception {
		ClientRequest clientRequest = null;
		ClientResponse<?> clientResponse = null;
		MultipartFormDataOutput multipartFormDataOutput = null;

		try {
			// invoke service after setting necessary parameters
			clientRequest = new ClientRequest(urlService);
			clientRequest.setHttpMethod(HttpMethod.POST);
			clientRequest.header("Content-Type", MediaType.MULTIPART_FORM_DATA);

			// set file upload values
			multipartFormDataOutput = new MultipartFormDataOutput();

			List<String> nombreParametros = (List<String>) parameters
					.get(Constantes.REST_MAP_PARAMETER_NAME);
			List<Object> objetoParametros = (List<Object>) parameters
					.get(Constantes.REST_MAP_PARAMETER_OBJECT);

			if (nombreParametros != null) {
				int i = 0;
				for (String nombreParametro : nombreParametros) {
					multipartFormDataOutput.addFormData(nombreParametro,
							objetoParametros.get(i),
							MediaType.MULTIPART_FORM_DATA_TYPE);
					i++;
				}

				// set POST request body and invoke the service
				clientRequest.body(MediaType.MULTIPART_FORM_DATA_TYPE,
						multipartFormDataOutput);
			}
		
			clientResponse = clientRequest.post();

			// get response code
			int responseCode = clientResponse.getResponseStatus()
					.getStatusCode();
			if (clientResponse.getResponseStatus().getStatusCode() != 200) {
				throw new RuntimeException("Failed with HTTP error code : "
						+ responseCode);
			}

			// get response
			return clientResponse.getEntity(File.class);

		} catch (Exception e) {
			LOG.error("Error en la llamada al Servicio", e);
			throw new Exception(e);
		}
	}
}
