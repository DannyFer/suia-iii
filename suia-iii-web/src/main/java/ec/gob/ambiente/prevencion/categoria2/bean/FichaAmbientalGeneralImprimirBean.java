/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.prevencion.categoria2.bean;

import ec.gob.ambiente.prevencion.categoria2.controllers.ImpresionFichaGeneralController;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.DocumentoPDFPlantillaHtml;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilFichaMineria;
import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author frank torres
 */
@ManagedBean
@ViewScoped
public class FichaAmbientalGeneralImprimirBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8044197283405671224L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(FichaAmbientalGeneralImprimirBean.class);
    @Getter
    @Setter
    ProyectoLicenciamientoAmbiental proyecto;
    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;
    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private CategoriaIIFacade categoriaIIFacade;
    @EJB
    private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;
    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    @Getter
    @Setter
    private FichaAmbientalPma fichaAmbiental;
    @Getter
    @Setter
    private String mensaje;
    @Getter
    @Setter
    private byte[] archivo;
    @Getter
    @Setter
    private String pdf;
    @Getter
    @Setter
    private File archivoFinal;
    @Getter
    @Setter
    private Boolean condiciones = false;
    @Getter
    @Setter
    private StreamedContent ficha;
    @Getter
    @Setter
    private boolean shouldRender = false;

	@PostConstruct
	private void cargarDatos() {
		proyecto = proyectosBean.getProyecto();
		mensaje = getNotaResponsabilidadInformacionRegistroProyecto(loginBean
				.getUsuario());
		proyecto.setSeleccionado(false);
		try {
//			cargarDatosBandeja();
		} catch (Exception ex) {
		}
	}

	public void ejecutoActualizacion() {
		cargarDatosBandeja();
		JsfUtil.redirectTo("/prevencion/categoria2/v2/fichaAmbiental/envioFicha.jsf");
		RequestContext.getCurrentInstance().update(
				":#{p:component('pgPdf')},frmEnviar");
	}

	public void cargarDatosBandeja() {

        if (!proyectosBean.getProyecto().isSeleccionado()) {
            proyectosBean.getProyecto().setSeleccionado(true);


            ImpresionFichaGeneralController impresionFichaGeneralController = JsfUtil
                    .getBean(ImpresionFichaGeneralController.class);
            MarcoLegalPmaImprimirBean marcoLegalPmaBean = JsfUtil
                    .getBean(MarcoLegalPmaImprimirBean.class);


            File archivoTmp = impresionFichaGeneralController
                    .imprimirFichaPdf();

            File archivoArticulosTmp = marcoLegalPmaBean.exportarPdf();

            List<File> listaFiles = new ArrayList<File>();
            listaFiles.add(archivoTmp);
            listaFiles.add(archivoArticulosTmp);

            FileOutputStream file;

            try {
            	
                File archivoUnirTmp = UtilFichaMineria.unirPdf(
                        listaFiles, "Ficha_Ambiental");                
                File archivocacatenadoTmp= JsfUtil.fileMarcaAgua(archivoUnirTmp, "BORRADOR",BaseColor.GRAY);
                Path path = Paths.get(archivocacatenadoTmp.getAbsolutePath());
                archivo = Files.readAllBytes(path);
                archivoFinal = new File(
                        JsfUtil.devolverPathReportesHtml(archivocacatenadoTmp
                                .getName()));
                file = new FileOutputStream(archivoFinal);
                file.write(archivo);
                file.close();
                setPdf(JsfUtil.devolverContexto("/reportesHtml/"
                        + archivocacatenadoTmp.getName()));
            } catch (Exception e) {
                LOG.error("Error al general el pdf", e);
            }
            this.fichaAmbiental = fichaAmbientalPmaFacade
                    .getFichaAmbientalPorIdProyecto(proyectosBean.getProyecto()
                            .getId());


        }

    }

    private String getNotaResponsabilidadInformacionRegistroProyecto(
            Usuario persona) {
        String[] parametros = {persona.getPersona().getNombre(), persona.getPersona().getPin()};
        return DocumentoPDFPlantillaHtml.getPlantillaConParametros(
                "nota_responsabilidad_certificado_interseccion", parametros);
    }

    public void descargarFichaTecnico() {
        String[] tokens = archivoFinal.getName().split("\\.(?=[^\\.]+$)");
        UtilFichaMineria.descargar(archivoFinal.getAbsolutePath(), tokens[0]);
    }

    public boolean generaDesechosEspeciales() {
        return proyecto.getTipoEstudio().getId() == 2
                && proyecto.getGeneraDesechos() != null
                && proyecto.getGeneraDesechos() == true;
    }
    
    public boolean ppc() throws ParseException {
		Date fechaproyecto=null;
		Date fechabloqueo=null;
		Date fechabloqueoObligatorioInicio=null;
		Date fechabloqueoObligatorioFin=null;
		
		Date fechabloqueoSIN=null;
		boolean bloquear=false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		fechabloqueo = sdf.parse(Constantes.getFechaProyectosSinPccAntes());
		fechabloqueoObligatorioInicio = sdf.parse(Constantes.getFechaProyectosObligatorioPccInicio());
		fechabloqueoObligatorioFin = sdf.parse(Constantes.getFechaProyectosObligatorioPccFin());		
		fechabloqueoSIN = sdf.parse(Constantes.getFechaProyectosSinPpcAdelante());
		
		fechaproyecto=sdf.parse(proyectosBean.getProyecto().getFechaRegistro().toString());
		if (fechaproyecto.before(fechabloqueo)){
			return false;
		}		
		if (fechaproyecto.after(fechabloqueoObligatorioInicio) && fechaproyecto.before(fechabloqueoObligatorioFin)){
			return true;
		}
		
		if (fechaproyecto.after(fechabloqueoObligatorioInicio) && fechaproyecto.before(fechabloqueoObligatorioFin)){
			return true;
		}
				
		if (fechaproyecto.after(fechabloqueoSIN)){
			return false;
		}		
		
		return bloquear;
		
	}
    
}
