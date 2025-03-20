package ec.gob.ambiente.suia.webservice.comun;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import ec.gob.ambiente.rcoa.service.IniciarProcesoEIAWS;
import ec.gob.ambiente.suia.certificado.ambiental.GenerarCertificadoAmbientalWs;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionServiceWS;
import ec.gob.ambiente.suia.facilitador.service.FacilitadorServiceWS;
import ec.gob.ambiente.suia.iniciadorproceso.service.IniciarProcesoServiceWS;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialServiceWS;
import ec.gob.ambiente.suia.tareas.programadas.EnvioMailServiceWS;
import ec.gob.ambiente.suia.webservice.AsignarTareaWs;


@ApplicationPath("/RestServices/*")
public class RestServices extends Application {

	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> empty = new HashSet<Class<?>>();

	public RestServices() {
		singletons.add(new AsignarTareaWs());
		singletons.add(new CertificadoInterseccionServiceWS());
		singletons.add(new FacilitadorServiceWS());
        singletons.add(new EnvioMailServiceWS());
		singletons.add(new IniciarProcesoServiceWS());
		singletons.add(new IniciarProcesoServiceWS());
		singletons.add(new ParticipacionSocialServiceWS());
		singletons.add(new IniciarProcesoEIAWS());
		singletons.add(new GenerarCertificadoAmbientalWs());
	}

	@Override
	public Set<Class<?>> getClasses() {
		return empty;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
