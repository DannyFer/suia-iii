package ec.gob.ambiente.rcoa.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;

@Path("/RestServices/iniciarProcesoEIAWS")
@Singleton
public class IniciarProcesoEIAWS {
	
	@GET
	@Path("/iniciar/{id}-{us}")
	@Produces("text/plain")
	public void iniciar(@PathParam("id") String idProceso,@PathParam("us") String us) {

		UsuarioFacade usuarioFacade = (UsuarioFacade) BeanLocator.getInstance(UsuarioFacade.class);
		ProcesoFacade procesoFacade = (ProcesoFacade) BeanLocator.getInstance(ProcesoFacade.class);

		Long processoInstanceId = Long.valueOf(idProceso);
		Usuario usuario = usuarioFacade.buscarUsuario(us);
		Map<String, Object> variables;
		try {
			variables = procesoFacade.recuperarVariablesProceso(usuario,processoInstanceId);
			String tramite = (String) variables.get("u_tramite");
			String idProyecto = (String) variables.get("u_idProyecto");
			String operador = (String) variables.get("u_operador");
			String tecnicoResponsable = (String) variables.get("u_tecnicoResponsable");

			Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
			parametros.put("tramite",tramite);
			parametros.put("idProyecto",idProyecto);
			parametros.put("operador",operador);
			parametros.put("tecnicoResponsable",tecnicoResponsable);

			procesoFacade.iniciarProceso(usuario, Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL_v2, tramite, parametros);

		} catch (JbpmException e) {
			e.printStackTrace();
		}
	}
}
