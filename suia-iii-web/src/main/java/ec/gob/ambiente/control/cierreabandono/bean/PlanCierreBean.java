/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.control.cierreabandono.bean;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultUploadedFile;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.PlanCierre;
import ec.gob.ambiente.suia.domain.PlanCierreDocumentos;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.enums.PlanCierreFields;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 23/12/2014]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class PlanCierreBean implements Serializable {

	private static final long serialVersionUID = -3526371287113629686L;

	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyecto;

	@Getter
	private PlanCierre planCierre;

	@Getter
	private Map<String, List<UploadedFile>> files;

	@Getter
	@Setter
	private String fileKey;

	@Getter
	@Setter
	private Map<UploadedFile, StreamedContent> filesToDownload;

	@Getter
	private Map<String, String> labelsToStrings;

	@Getter
	private boolean viewPanelPoint10;
	@Getter
	@Setter
	private boolean viewPanelPoint;

	public void toggleviewPanelPoint10() {
		this.viewPanelPoint10 ^= true;
	}

	@PostConstruct
	public void init() {
		files = new HashMap<String, List<UploadedFile>>();
		files.put(PlanCierreFields.antecedentes.name(), new ArrayList<UploadedFile>());
		files.put(PlanCierreFields.objetivos.name(), new ArrayList<UploadedFile>());
		files.put(PlanCierreFields.datosGenerales.name(), new ArrayList<UploadedFile>());
		files.put(PlanCierreFields.alcance.name(), new ArrayList<UploadedFile>());
		files.put(PlanCierreFields.marcoLegal.name(), new ArrayList<UploadedFile>());
		files.put(PlanCierreFields.descripcionActividades.name(), new ArrayList<UploadedFile>());
		files.put(PlanCierreFields.descripcionArea.name(), new ArrayList<UploadedFile>());
		files.put(PlanCierreFields.diagnostico.name(), new ArrayList<UploadedFile>());
		files.put(PlanCierreFields.identificacion.name(), new ArrayList<UploadedFile>());
		files.put(PlanCierreFields.anexos.name(), new ArrayList<UploadedFile>());
		files.put(PlanCierreFields._10_equiposEstructuras.name(), new ArrayList<UploadedFile>());
		files.put(PlanCierreFields._10_desechos.name(), new ArrayList<UploadedFile>());
		files.put(PlanCierreFields._10_reduccionDrenajes.name(), new ArrayList<UploadedFile>());
		// files.put(PlanCierre.FILES_10_SELLAR_TAPON, new
		// ArrayList<UploadedFile>());
		// files.put(PlanCierre.FILES_10_SELLAR_COSTA_FUERA, new
		// ArrayList<UploadedFile>());
		// files.put(PlanCierre.FILES_10_INSTALACIONES, new
		// ArrayList<UploadedFile>());
		// files.put(PlanCierre.FILES_10_CONTROL_MITIGACION, new
		// ArrayList<UploadedFile>());
		// files.put(PlanCierre.FILES_10_REHABILITACION_ESCOMBRERAS, new
		// ArrayList<UploadedFile>());
		// files.put(PlanCierre.FILES_10_MANEJO_LAGOS, new
		// ArrayList<UploadedFile>());
		// files.put(PlanCierre.FILES_10_REHABILITACION_TALUDES, new
		// ArrayList<UploadedFile>());
		// files.put(PlanCierre.FILES_10_IMPACTOS_ADVERSOS, new
		// ArrayList<UploadedFile>());
		// files.put(PlanCierre.FILES_10_REMEDIACION_SUELOS, new
		// ArrayList<UploadedFile>());
		// files.put(PlanCierre.FILES_10_MANTENIMIENTO_ESTRUCTURAS, new
		// ArrayList<UploadedFile>());
		// files.put(PlanCierre.FILES_10_EMISION_POLVO, new
		// ArrayList<UploadedFile>());
		// files.put(PlanCierre.FILES_10_MANEJO_FLORA, new
		// ArrayList<UploadedFile>());
		// files.put(PlanCierre.FILES_10_DESMANTELAMIENTO, new
		// ArrayList<UploadedFile>());
		// files.put(PlanCierre.FILES_10_TAPON_MECANICO, new
		// ArrayList<UploadedFile>());
		files.put(PlanCierreFields._10_detallesMuestreo.name(), new ArrayList<UploadedFile>());
		files.put(PlanCierreFields._10_otros.name(), new ArrayList<UploadedFile>());

		labelsToStrings = new HashMap<String, String>();
		labelsToStrings.put(PlanCierreFields.antecedentes.name(), "1. Antecedentes");
		labelsToStrings.put(PlanCierreFields.objetivos.name(), "2. Objetivos");
		labelsToStrings.put(PlanCierreFields.datosGenerales.name(), "3. Datos Generales");
		labelsToStrings.put(PlanCierreFields.alcance.name(), "4. Alcance del Plan de Cierre y Abandono");
		labelsToStrings.put(PlanCierreFields.marcoLegal.name(), "5. Marco Legal");
		labelsToStrings.put(PlanCierreFields.descripcionActividades.name(),
				"6. Descripción de las actividades desarrolladas en el área");
		labelsToStrings.put(PlanCierreFields.descripcionArea.name(), "7. Descripción del Área de Infuencia");
		labelsToStrings.put(PlanCierreFields.diagnostico.name(), "8. Diagnóstico Socio ambiental de Área");
		labelsToStrings.put(PlanCierreFields.identificacion.name(), "9. Identificación y Evaluación de Impactos Ambientales");
		labelsToStrings.put(PlanCierreFields.anexos.name(), "Anexos, Bibliografía, Mapas, Fotografías");
		labelsToStrings
				.put(PlanCierreFields._10_equiposEstructuras.name(),
						"10.1 Ubicar y disponer adecuadamente los equipos y estructuras que se encuentran en sitios de trabajo");
		labelsToStrings
				.put(PlanCierreFields._10_desechos.name(),
						"10.2 Clasificación, tratamiento y disposición de desechos de origen doméstico e indutrial de acuerdo al plan de manejo");
		labelsToStrings.put(PlanCierreFields._10_reduccionDrenajes.name(),
				"10.3 Reducción de drenajes y reforestación del área que no vaya a ser reforestada");
		// labelsToStrings.put(PlanCierre.FILES_10_SELLAR_TAPON, "");
		// labelsToStrings.put(PlanCierre.FILES_10_SELLAR_COSTA_FUERA, "");
		// labelsToStrings.put(PlanCierre.FILES_10_INSTALACIONES, "");
		// labelsToStrings.put(PlanCierre.FILES_10_CONTROL_MITIGACION, "");
		// labelsToStrings.put(PlanCierre.FILES_10_REHABILITACION_ESCOMBRERAS,
		// "");
		// labelsToStrings.put(PlanCierre.FILES_10_MANEJO_LAGOS, "");
		// labelsToStrings.put(PlanCierre.FILES_10_REHABILITACION_TALUDES, "");
		// labelsToStrings.put(PlanCierre.FILES_10_IMPACTOS_ADVERSOS, "");
		// labelsToStrings.put(PlanCierre.FILES_10_REMEDIACION_SUELOS, "");
		// labelsToStrings.put(PlanCierre.FILES_10_MANTENIMIENTO_ESTRUCTURAS,
		// "");
		// labelsToStrings.put(PlanCierre.FILES_10_EMISION_POLVO, "");
		// labelsToStrings.put(PlanCierre.FILES_10_MANEJO_FLORA, "");
		// labelsToStrings.put(PlanCierre.FILES_10_DESMANTELAMIENTO, "");
		// labelsToStrings.put(PlanCierre.FILES_10_TAPON_MECANICO, "");
		labelsToStrings.put(PlanCierreFields._10_detallesMuestreo.name(), "10.4 Detalles de muestreo a realizarse");
		labelsToStrings.put(PlanCierreFields._10_otros.name(), "10.5 Otros");

		planCierre = new PlanCierre();
		planCierre.setProyecto(proyecto);
	}

	public StreamedContent getFileToDownload(final UploadedFile file) {
		DefaultStreamedContent content = null;
		if (file != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(file.getContents()));
			content.setName(file.getFileName());
		}
		return content;
	}

	public Map<String, List<Documento>> getDocumentosFromUpload() {
		final Map<String, List<Documento>> documentosFrom = new ConcurrentHashMap<String, List<Documento>>();
		final Iterator<String> iterator = files.keySet().iterator();
		while (iterator.hasNext()) {
			final String key = (String) iterator.next();
			final List<UploadedFile> uploadeds = files.get(key);
			final List<Documento> documentos = new ArrayList<Documento>();
			for (final UploadedFile uploadedFile : uploadeds) {
				final Documento documento = new Documento();
				documento.setNombre(uploadedFile.getFileName());
				documentos.add(documento);
			}
			documentosFrom.put(key, documentos);
		}
		return documentosFrom;
	}

	public void initPlanCierre(final PlanCierre planCierre) {
		this.planCierre = planCierre;
		for (final PlanCierreDocumentos pcd : planCierre.getPlanesCierreDocumentos()) {
			final DefaultUploadedFile uploadedFile = new DefaultUploadedFile();
			files.get(pcd.getTipoSeccion()).add(uploadedFile);
		}
	}
}
