package ec.gob.ambiente.suia.control.denuncia.service;

import java.util.Date;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Denuncia;
import ec.gob.ambiente.suia.domain.Denunciante;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;

@Stateless
public class DenunciaService {

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private ProcesoFacade procesoFacade;

	public void guardarDenuncia(Denuncia denuncia) {
		if (denuncia.getId() == null) {
			denuncia.setFechaRegistro(new Date());
			crudServiceBean.saveOrUpdate(denuncia);
		} else
			crudServiceBean.saveOrUpdate(denuncia);
	}

	public void iniciarProceso(Map<String, Object> parametros, Usuario usuario) throws JbpmException {
		/*
		 * procesoFacade.iniciarProceso(Constantes.NOMBRE_PROCESO_DENUNCIAS, "tramite",
		 * Constantes.getDeploymentId()_DENUNCIAS, parametros, Constantes.URL_BUSINESS_CENTRAL, nombreUsuario,
		 * password);
		 */
		procesoFacade.iniciarProceso(usuario, "Pruebas.DenunciaResponsable", "tramite", parametros);

	}

	public void guardarDenunciante(Denunciante denunciante) {
		if (denunciante.getId() == null)
			crudServiceBean.saveOrUpdate(denunciante);
		else
			crudServiceBean.saveOrUpdate(denunciante);
	}

	public Denuncia getDenuncia(Integer idDenuncia) {
		return crudServiceBean.find(Denuncia.class, idDenuncia);
	}
}
