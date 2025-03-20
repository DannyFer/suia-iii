/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade;

import java.net.MalformedURLException;
import java.net.URL;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.services.MaeLicenseResponse;
import ec.gob.ambiente.services.MaeLicenseService;
import ec.gob.ambiente.services.MaeLicenseService_Service;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class BuscarProyectoFacade {

	@EJB
	private DocumentosFacade documentosFacade;
	@EJB
	private CrudServiceBean crudServiceBean;

	public byte[] getDocumentoInformativo(String nombre) throws ServiceException, CmisAlfrescoException {

		try {
			return documentosFacade.descargarDocumentoPorNombre(nombre);
		} catch (RuntimeException e) {
			throw new ServiceException("Error al recuperar la firma para el certificado", e);
		}
	}

	private URL getUrlProjectSearch() throws MalformedURLException {
		return new URL(Constantes.getUrlProjectSearch());
	}

	public MaeLicenseResponse buscarProyecto(String numeroPermisoAmbiental, String nombreUsuario)
			throws ServiceException {
		try {
			return invocarWSBuscarProyecto(numeroPermisoAmbiental, nombreUsuario);
		} catch (Exception e) {
			throw new ServiceException("Error en el ws al buscar el proyecto.", e);
		}
	}

	private MaeLicenseResponse invocarWSBuscarProyecto(String numeroPermisoAmbiental, String nombreUsuario)/* throws Exception*/ {

		MaeLicenseService_Service wsBuscarProyecto = null;
		try {
			wsBuscarProyecto = new MaeLicenseService_Service(getUrlProjectSearch());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		MaeLicenseService servicioWebBuscarProyecto = wsBuscarProyecto.getMaeLicenseServicePort();
		MaeLicenseResponse respuestaServicioWebBuscarProyecto = servicioWebBuscarProyecto.getPermisoAmbiental(
				numeroPermisoAmbiental, nombreUsuario, null);

		return respuestaServicioWebBuscarProyecto;
	}

	public boolean existeProcesoProyecto(String proyecto) {
		Long num = (Long) crudServiceBean.getEntityManager()
				.createQuery("Select count(art) from AprobacionRequisitosTecnicos art where art.proyecto=:proyecto")
				.setParameter("proyecto", proyecto).getSingleResult();
		return num > 0;
	}
}
