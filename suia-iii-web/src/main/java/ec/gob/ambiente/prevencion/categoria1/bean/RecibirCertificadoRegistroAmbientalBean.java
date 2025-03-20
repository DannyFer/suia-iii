/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.prevencion.categoria1.bean;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.suia.domain.CertificadoRegistroAmbiental;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.prevencion.categoria1.facade.Categoria1Facade;
import ec.gob.ambiente.suia.utils.Constantes;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 22/01/2015]
 *          </p>
 */
@ManagedBean
@SessionScoped
public class RecibirCertificadoRegistroAmbientalBean implements Serializable {

	private static final long serialVersionUID = -3526271287113629636L;

	@Getter
	@Setter
	private Documento guiaBuenasPracticas;

	private byte[] guiaBuenasPracticasArreglo;

	@Getter
	@Setter
	private Documento certificadoRegistroAmbiental;

	private byte[] certificadoRegistroAmbientalArreglo;

	@EJB
	private Categoria1Facade categoria1Facade;

	@Getter
	@Setter
	private String nombreProyecto;

	@Getter
	@Setter
	private String guiaBuenasPracticasCatalogoCategoriaSistema;

	private static final Logger LOG = Logger.getLogger(RecibirCertificadoRegistroAmbientalBean.class);

	public void iniciar(Integer idProyecto) {
		nombreProyecto = "";
		guiaBuenasPracticasCatalogoCategoriaSistema = "";
		try {
			if (idProyecto != null) {
				CertificadoRegistroAmbiental certificadoRegistroA = null;

				List<CertificadoRegistroAmbiental> certificadosRegistroA = categoria1Facade
						.getCertificadoRegistroAmbientalPorIdProyecto(idProyecto);

				if (certificadosRegistroA != null && !certificadosRegistroA.isEmpty())
					certificadoRegistroA = certificadosRegistroA.get(0);

				if (certificadoRegistroA != null && certificadoRegistroA.getProyecto() != null
						&& certificadoRegistroA.getProyecto().getNombre() != null) {
					nombreProyecto = certificadoRegistroA.getProyecto().getNombre();
					guiaBuenasPracticasCatalogoCategoriaSistema = certificadoRegistroA.getProyecto()
							.getCatalogoCategoria().getGuiaBuenasPracticas();
				}

				if (certificadoRegistroA == null || certificadoRegistroA.getDocumento() == null)
					certificadoRegistroAmbiental = new Documento();
				else {
					certificadoRegistroAmbiental = certificadoRegistroA.getDocumento();
					certificadoRegistroAmbientalArreglo = categoria1Facade
							.descargarDocumentoAlfresco(certificadoRegistroAmbiental.getIdAlfresco());
				}

				guiaBuenasPracticas = new Documento();
				guiaBuenasPracticas.setNombre(Constantes.GUIA_BUENAS_PRACTICAS);
				guiaBuenasPracticasArreglo = categoria1Facade
						.descargarDocumentoPorNombre(guiaBuenasPracticasCatalogoCategoriaSistema);
			}
		} catch (Exception e) {
			LOG.error("Error al inicializar el bean", e);
		}
	}

	public StreamedContent getStream(Documento documento) throws Exception {
		DefaultStreamedContent content = null;
		byte[] ficheroDescargar;
		try {
			if (documento != null && documento.getNombre() != null) {
				if (documento.getNombre().equals(Constantes.GUIA_BUENAS_PRACTICAS))
					ficheroDescargar = guiaBuenasPracticasArreglo;
				else
					ficheroDescargar = certificadoRegistroAmbientalArreglo;
				if (ficheroDescargar != null) {
                    content = new DefaultStreamedContent(new ByteArrayInputStream(ficheroDescargar));
                    content.setName(documento.getNombre());
                }else{
                    JsfUtil.addMessageError("Error al descargar el documento.");
                    throw new Exception("Error al descargar el documento.");
                }
			}
		} catch (Exception exception) {
			LOG.error("Ocurrió un error al completar la operación", exception);
		}
		return content;

	}
}
