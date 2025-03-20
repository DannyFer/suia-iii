package ec.gob.ambiente.suia.control.denuncia.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.control.denuncia.service.DenunciaService;
import ec.gob.ambiente.suia.control.denuncia.service.RemitirDenunciaService;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Denuncia;
import ec.gob.ambiente.suia.domain.Denunciante;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.RemitirDenuncia;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.persona.service.PersonaServiceBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.service.UbicacionGeograficaServiceBean;

@Stateless
public class DenunciaFacade {

	@EJB
	private UbicacionGeograficaServiceBean ubicacionGeograficaServiceBean;

	@EJB
	private DenunciaService denunciaService;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private RemitirDenunciaService remitirDenunciaService;

	@EJB
	private PersonaServiceBean personaServiceBean;

	@EJB
	private AreaFacade areaFacade;

	public void aceptarDenuncia(Denuncia denuncia, Denunciante denunciante,
			Usuario usuario) throws JbpmException {
		Map<String, Object> parametros = new HashMap<String, Object>();
		/*parametros.put("directorProvincial",
				areaFacade.getDireccionProvincial(denuncia.getUbicacion()));
		parametros.put("directorPlantaCentral",
				areaFacade.getDirectorPlantaCentral());
		denuncia.setDenunciaValida(true);
		denunciaService.guardarDenunciante(denunciante);
		denuncia.setDenunciante(denunciante);
		denunciaService.guardarDenuncia(denuncia);
		parametros.put("idDenuncia", denuncia.getId());
		parametros.put("directorPlantaCentral",
				areaFacade.getDirectorPlantaCentral());
		parametros.put("coordinadorPlantaCentral",
				areaFacade.getCoordinadorControlAmbientalPlantaCentral());
		parametros.put("subSecretaria", areaFacade.getSubsecretariaGeneral());*/
		parametros.put("tramite", "Denuncia" + denuncia.getId());
		denunciaService.iniciarProceso(parametros, usuario);
	}

	public void rechazarDenuncia(Denuncia denuncia) {
		denuncia.setDenunciaValida(false);
		denunciaService.guardarDenuncia(denuncia);
	}

	public List<UbicacionesGeografica> getProvincias() {
		return ubicacionGeograficaServiceBean.buscarUbicacionGeograficaPorId(1);
	}

	public List<UbicacionesGeografica> getCantonesParroquia(
			UbicacionesGeografica ubicacion) {
		return ubicacionGeograficaServiceBean
				.buscarUbicacionGeograficaPorId(ubicacion.getId());
	}

	public List<Area> getDireccionesProvinciales() {
		return areaFacade.getDirecciones();
	}

	public List<Area> getEntesAcreditados() {
		return areaFacade.getEntesAcreditados();
	}

	public void completarTareaRemitirDenuncia(RemitirDenuncia remitirDenuncia,
			Denuncia denuncia, Map<String, Object> parametros, Long idTarea, Long idProceso,
			Usuario usuario) throws JbpmException {
		remitirDenunciaService.guardar(remitirDenuncia);
		/*parametros.put("_coordinador",
				areaFacade.getCoordinadorProvincial(denuncia.getUbicacion()));*/
		procesoFacade.aprobarTarea(usuario, idTarea, idProceso, parametros);

	}

	public void completarTarea(Map<String, Object> parametros, Long idTarea, Long idProceso,
			Usuario usuario) throws JbpmException {
		procesoFacade.aprobarTarea(usuario, idTarea, idProceso, parametros);

	}

	public Denuncia getDenuncia(Long idTarea, Usuario usuario) throws JbpmException {
		Map<String, Object> variables = procesoFacade.recuperarVariablesTarea(usuario, 
				idTarea);
		return denunciaService.getDenuncia((Integer) variables
				.get("_idDenuncia"));
	}

	public UbicacionesGeografica buscarUbicacionGeograficaSuperior(
			String codigoInec) {
		return ubicacionGeograficaServiceBean
				.buscarUbicacionGeograficaSuperior(codigoInec);
	}

	public boolean isProvincia(String codigoInec) {
		return ubicacionGeograficaServiceBean.isProvincia(codigoInec);
	}

	public boolean isCanton(String codigoInec) {
		return ubicacionGeograficaServiceBean.isCanton(codigoInec);
	}

	public Area getVariableEnteAcreditado(Long idTarea, Usuario usuario) throws JbpmException {
		Map<String, Object> variables = procesoFacade.recuperarVariablesTarea(usuario,
				idTarea);
		return areaFacade.getArea((Integer) (variables.get("_codigoGAD")));
	}

	public Persona getPersona(Long idTarea, Usuario usuario) throws JbpmException {
		Map<String, Object> variables = procesoFacade.recuperarVariablesTarea(
				usuario, idTarea);
		String tecnicoResponsable = (String) variables
				.get("_tecnicoResponsable");
		return personaServiceBean.getPersona(tecnicoResponsable);
	}

}
