package ec.gob.ambiente.proyectos.controllers;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProcesosArchivados;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PermisoRegistroGeneradorDesechosFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.retce.services.GeneradorDesechosPeligrososFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.MensajeNotificacion;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ViewScoped
@ManagedBean
public class ArchivarMasivoController implements Serializable {

	private static final long serialVersionUID = 4689463316622237704L;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorDesechosRcoaFacade;
	@EJB
	private PermisoRegistroGeneradorDesechosFacade permisoRegistroGeneradorDesechosFacade;
	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	@EJB
	private GeneradorDesechosPeligrososFacade generadorDesechosPeligrososFacade;
	@EJB
	private DocumentosCoaFacade documentosCoaFacade;
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	@EJB
	private DocumentosFacade documentosFacade;
	@EJB
    private CrudServiceBean crudServiceBean;

	@Setter
	private byte[] plantillaArchivoMasivo;
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa = new ProyectoLicenciaCoa();
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental = new ProyectoLicenciamientoAmbiental();
	@Getter
	@Setter
	private List<DocumentosCOA> listaDocumentosSolicitud = new ArrayList<DocumentosCOA>();
	@Getter
	@Setter
	private List<DocumentosCOA> listaDocumentosResolucion = new ArrayList<DocumentosCOA>();
	@Getter
	@Setter
	private List<ProyectoLicenciaCoa> listaProyectosCoa = new ArrayList<ProyectoLicenciaCoa>();
	@Getter
	@Setter
	private List<ProyectoLicenciamientoAmbiental> listaProyectosLicenciamiento = new ArrayList<ProyectoLicenciamientoAmbiental>();
	@Getter
	@Setter
	private List<ProyectoCustom> listaProyectoCustom = new ArrayList<ProyectoCustom>();
	@Getter
	@Setter
	private DocumentosCOA documentoSolicitud;
	@Getter
	@Setter
	private DocumentosCOA documentoResolucion;
	@Getter
	@Setter
    private UploadedFile uploadedFile;
	@Getter
	@Setter
	private ProyectosBean proyectosBean;
	
	@Getter
	@Setter
	private String codigoTramite;
	@Getter
	@Setter
	private String motivo, asociado, codigoRGD;
	@Getter
	@Setter
	private String motivoArchivacion;
	@Getter
	@Setter
	private Usuario usuarioProyecto;
	@Getter
	@Setter
	private Integer idProyecto, tipoProyecto;
	@Getter
	@Setter
	private Boolean proyectosfinalizados;
	@Getter
	@Setter
	private Boolean deshabilitado = true;
	@Getter
	@Setter
	private Boolean finalizado, panelesMasivo;
	@Getter
	@Setter
	private Date fechaArchivacion;
	@Getter
	@Setter
	private Date fechaRegistro;
	@Getter
	@Setter
	private Object mostrarDialogo;


	@PostConstruct
	public void init() {
		panelesMasivo = false;
		finalizado = false;
		try {
			plantillaArchivoMasivo = documentosFacade.descargarDocumentoPorNombreYDirectorioBase(Constantes.ARCHIVO_MASIVO_PROYECTOS, null);
        } catch (Exception e) {
        }
	}
	
	public StreamedContent getPlantillaArchivoMasivo() throws Exception {
        DefaultStreamedContent content = null;
        try {
            if (plantillaArchivoMasivo != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(plantillaArchivoMasivo));
                content.setName(Constantes.ARCHIVO_MASIVO_PROYECTOS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;

    }
	
	public void handleFileUploadProyectos(final FileUploadEvent event) {

	    int rows = 0;

	    List<ProyectoCustom> listaProyectoCustom = new ArrayList<>();

	    try {
	        uploadedFile = event.getFile();
	        Workbook workbook = new HSSFWorkbook(uploadedFile.getInputstream());
	        Sheet sheet = workbook.getSheetAt(0);
	        Iterator<Row> rowIterator = sheet.iterator();

	        while (rowIterator.hasNext()) {
	            Row row = rowIterator.next();
	            boolean isEmptyRow = true;

	            for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
	                Cell cell = row.getCell(cellNum);
	                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK && StringUtils.isNotBlank(cell.toString())) {
	                    isEmptyRow = false;
	                }
	            }
	            if (isEmptyRow) {
	                break;
	            }

	            if (rows > 0) {
	                Iterator<Cell> cellIterator = row.cellIterator();

	                String codigoProyecto = cellIterator.next().getStringCellValue().toUpperCase();
	                String motivoArchivo = cellIterator.next().getStringCellValue();

	                ProyectoLicenciaCoa proyectoCoa = proyectoLicenciaCoaFacade.buscarProyecto(codigoProyecto);            
	                ProyectoLicenciamientoAmbiental proyectoLicenciamiento = proyectoLicenciamientoAmbientalFacade.buscarProyectoPorCodigoCompleto(codigoProyecto);
	                
	                if (proyectoLicenciamiento != null) {
	                	if(proyectoLicenciamiento.getCodigo() == null) {
	                		JsfUtil.addMessageError("El código de proyecto " + codigoProyecto + " se encuentra archivado.");
	                		break;
	                	} else {
	                    proyectoLicenciamiento.setMotivoEliminar(motivoArchivo);
	                    listaProyectosLicenciamiento.add(proyectoLicenciamiento);
	                    
	                    if (proyectoLicenciamiento.getId() != null) {
	        	            Integer idCOA = proyectoLicenciamiento.getId();
	        	            GeneradorDesechosPeligrosos rgd = generadorDesechosPeligrososFacade.buscarRGDPorProyectoCoa(idCOA);
	        	            if (rgd != null && proyectoLicenciamiento.getCatalogoCategoria().getCategoria().getId().equals(2) && proyectoLicenciamiento.isFinalizado()) {
	        	                JsfUtil.addMessageInfo("El Proyecto tiene asociado el Registro Generador de Residuos o Desechos Peligrosos y/o Especiales No. " + rgd.getCodigo());
	        	            }
	        	            deshabilitado = false;
	        	            motivoArchivacion = proyectoLicenciamiento.getMotivoEliminar();
	        	            fechaArchivacion = proyectoLicenciamiento.getFechaDesactivacion();
	        	            fechaRegistro = proyectoLicenciamiento.getFechaModificacion();
	                    }
	                    
	                    ProyectoCustom proyectoCustom = new ProyectoCustom();
	                    proyectoCustom.setId(proyectoLicenciamiento.getId().toString());
	                    proyectoCustom.setCodigo(proyectoLicenciamiento.getCodigo());
	                    proyectoCustom.setMotivoEliminar(proyectoLicenciamiento.getMotivoEliminar());
	                    String finalizado = (proyectoLicenciamiento.isCompletado()) ? "FINALIZADO" : "EN CURSO";
	                    proyectoCustom.setFinalizado(finalizado);
	                    listaProyectoCustom.add(proyectoCustom);
	                	}
	                } 
	                else if (proyectoCoa != null) {
	                	if (proyectoCoa.getCodigoUnicoAmbiental() == null) {
	                		JsfUtil.addMessageError("El código de proyecto " + codigoProyecto + " se encuentra archivado.");
	                		break;
	                	} else {
	                    proyectoCoa.setRazonEliminacion(motivoArchivo);
	                    listaProyectosCoa.add(proyectoCoa);
	                    
	                    if (proyectoCoa != null && proyectoCoa.getId() != null) {
	        	            if (!(proyectoCoa.getCategoria().getId().equals(4) || proyectoCoa.getCategoria().getId().equals(3) || proyectoCoa.getCategoria().getId().equals(1))) {
	        	                Integer idRCOA = proyectoCoa.getId();
	        	                RegistroGeneradorDesechosRcoa asociadoRGD = registroGeneradorDesechosRcoaFacade.buscarRGDPorProyectoRcoa(idRCOA);
	        	                if (asociadoRGD != null && proyectoCoa.getCategoria().getId().equals(2) && !proyectoCoa.getProyectoFinalizado()) {
	        	                    JsfUtil.addMessageInfo("El Proyecto tiene asociado el Registro Generador de Residuos o Desechos Peligrosos y/o Especiales No. " + asociadoRGD.getCodigo());
	        	                }

	        	                deshabilitado = false;
	        	                motivoArchivacion = proyectoCoa.getRazonEliminacion();
	        	                fechaArchivacion = proyectoCoa.getFechaDesactivacion();
	        	                fechaRegistro = proyectoCoa.getFechaModificacion();
	        	            }
	                    }

	                    ProyectoCustom proyectoCustom = new ProyectoCustom();
	                    proyectoCustom.setSourceType("RCOA");
	                    proyectoCustom.setId(proyectoCoa.getId().toString());
	                    proyectoCustom.setCodigo(proyectoCoa.getCodigoUnicoAmbiental());
	                    proyectoCustom.setMotivoEliminar(proyectoCoa.getRazonEliminacion());
	                    String finalizado = (proyectoCoa.getProyectoFinalizado()) ? "FINALIZADO" : "EN CURSO";
	                    proyectoCustom.setFinalizado(finalizado);
	                    listaProyectoCustom.add(proyectoCustom);
	                	}
	                }

	            }
	            rows++;
	        }

	        if (rows == 1) {
	            JsfUtil.addMessageError("El archivo de contiene datos erroneos.");
	            return;
	        }

	        this.listaProyectoCustom = listaProyectoCustom;
	        
	        boolean todosFinalizados = true;
	        boolean todosEnCurso = true;
	        
	        for (ProyectoCustom proyecto : this.listaProyectoCustom) {
	            if ("FINALIZADO".equals(proyecto.getFinalizado())) {
	            	todosEnCurso = false;
	            } else if("EN CURSO".equals(proyecto.getFinalizado())){
	            	todosFinalizados = false;
	            }
	        }
	        
	        if (todosFinalizados) {
	            this.finalizado = true;
	            this.panelesMasivo = true;
	            this.deshabilitado = false;
	        } else if (todosEnCurso) {
	            this.finalizado = false;
	            this.panelesMasivo = true;
	            this.deshabilitado = false;
	        } else {
	            this.finalizado = false;
	            this.panelesMasivo = false;
	            this.deshabilitado = true;
	            JsfUtil.addMessageError("El archivo masivo de proyectos se realiza si todos los proyectos están EN CURSO o FINALIZADOS");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_CARGAR_DATOS);
	    }
	}
	
	public void uploadSolicitud(FileUploadEvent event) {
		documentoSolicitud = new DocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoSolicitud.setContenidoDocumento(contenidoDocumento);
		documentoSolicitud.setNombreDocumento(event.getFile().getFileName());
		documentoSolicitud.setExtencionDocumento(".pdf");
		documentoSolicitud.setTipo("application/pdf");
		documentoSolicitud.setIdTabla(idProyecto);

		listaDocumentosSolicitud.add(documentoSolicitud);
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}

	public void uploadResolucion(FileUploadEvent event) {
		documentoResolucion = new DocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoResolucion.setContenidoDocumento(contenidoDocumento);
		documentoResolucion.setNombreDocumento(event.getFile().getFileName());
		documentoResolucion.setExtencionDocumento(".pdf");
		documentoResolucion.setTipo("application/pdf");
		documentoResolucion.setIdTabla(idProyecto);
		
		listaDocumentosResolucion.add(documentoResolucion);
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}
	
	@SuppressWarnings("unchecked")
	public ProcesosArchivados getPorCodigoProyecto(String codigo) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("codigoProyecto", codigo);

		try {
			List<ProcesosArchivados> lista = (List<ProcesosArchivados>) crudServiceBean.findByNamedQuery(ProcesosArchivados.GET_POR_PROYECTO,parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	public void archivarMasivo() {
	    try {
	        if (listaDocumentosSolicitud.isEmpty()) {
	            JsfUtil.addMessageError("Debe adjuntar la Solicitud del pedido del Proponente.");
	            return;
	        }

	        if (this.finalizado && listaDocumentosResolucion.isEmpty()) {
	            JsfUtil.addMessageError("Debe adjuntar la Resolución de extinción del proyecto.");
	            return;
	        }

	        for (ProyectoCustom proyecto : this.listaProyectoCustom) {
	            String codigoProyecto = proyecto.getCodigo();
	            Integer idProyecto = Integer.valueOf(proyecto.getId());
	            String motivoArchivo = proyecto.getMotivoEliminar();
	            String nombreTabla;

	            ProyectoLicenciaCoa proyectoCoa = null;
	            ProyectoLicenciamientoAmbiental proyectoLicenciamiento = null;

	            if ("RCOA".equals(proyecto.getSourceType())) {
	                nombreTabla = ProyectoLicenciaCoa.class.getSimpleName();
	                proyectoCoa = proyectoLicenciaCoaFacade.buscarProyecto(codigoProyecto);
	            } else {
	                nombreTabla = ProyectoLicenciamientoAmbiental.class.getSimpleName();
	                proyectoLicenciamiento = proyectoLicenciamientoAmbientalFacade.buscarProyectoPorCodigoCompleto(codigoProyecto);
	            }

	            for (DocumentosCOA documento : listaDocumentosSolicitud) {
	                if (documento.getId() == null) {
	                    documento.setIdTabla(idProyecto);
	                    documento.setNombreTabla(nombreTabla);
	                    documento = documentosCoaFacade.guardarDocumentoAlfresco(codigoProyecto, Constantes.CARPETA_ARCHIVO_PROYECTO, 0L, documento, TipoDocumentoSistema.ARCHIVAR_PROYECTOS_MESADEAYUDA);
	                }
	            }

	            for (DocumentosCOA documento : listaDocumentosResolucion) {
	                if (documento.getId() == null) {
	                    documento.setIdTabla(idProyecto);
	                    documento.setNombreTabla(nombreTabla);
	                    documento = documentosCoaFacade.guardarDocumentoAlfresco(codigoProyecto, Constantes.CARPETA_ARCHIVO_PROYECTO, 0L, documento, TipoDocumentoSistema.ARCHIVAR_PROYECTOS_MESADEAYUDA);
	                }
	            }
	            
	            if (proyectoLicenciamiento != null) {
	                String resultadoCoa = proyectoLicenciaCoaFacade.archivarTramiteCoa(codigoProyecto, motivoArchivo);
	                if ("1".equals(resultadoCoa)) {
	                    JsfUtil.addMessageInfo("Se ha archivado el proyecto: " + codigoProyecto);
	                } else {
	                    JsfUtil.addMessageError("No se ha encontrado un proyecto válido con ese código.");
	                }
	                notificacionOperador(proyectoLicenciamiento, motivoArchivo);
	            } else if (proyectoCoa != null) {
	                String resultadoCoa = proyectoLicenciaCoaFacade.archivarTramiteRcoa(codigoProyecto, motivoArchivo);
	                if ("1".equals(resultadoCoa)) {
	                    JsfUtil.addMessageInfo("Se ha archivado el proyecto: " + codigoProyecto);
	                } else {
	                    JsfUtil.addMessageError("No se ha encontrado un proyecto válido con ese código.");
	                }
	                notificacionOperador(proyectoCoa, motivoArchivo);
	            } 
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        listaDocumentosSolicitud.clear();
	        listaDocumentosResolucion.clear();
	        JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_CARGAR_DATOS);
	    }

	    deshabilitado = true;
	    panelesMasivo = false;
	    documentoResolucion = new DocumentosCOA();
        documentoSolicitud = new DocumentosCOA();
		listaProyectoCustom.clear();
	}
	
	public void notificacionOperador(Object proyecto, String motivoArchivo) {
	    try {
	        String nombreOperador = "";
	        String proyectoCodigo = "";
	        Usuario usuarioProyecto = new Usuario();

	        if (proyecto instanceof ProyectoLicenciaCoa) {
	            ProyectoLicenciaCoa proyectoCoa = (ProyectoLicenciaCoa) proyecto;
	            usuarioProyecto = proyectoCoa.getUsuario();
	            proyectoCodigo = proyectoCoa.getCodigoUnicoAmbiental();
	        } else if (proyecto instanceof ProyectoLicenciamientoAmbiental) {
	            ProyectoLicenciamientoAmbiental proyectoLicenciamiento = (ProyectoLicenciamientoAmbiental) proyecto;
	            usuarioProyecto = proyectoLicenciamiento.getUsuario();
	            proyectoCodigo = proyectoLicenciamiento.getCodigo();
	        }

	        if (usuarioProyecto.getId() != null) {
	            if (usuarioProyecto.getPersona().getOrganizaciones().isEmpty()) {
	                nombreOperador = usuarioProyecto.getPersona().getNombre();
	            } else {
	                Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioProyecto.getNombre());
	                if (organizacion != null) {
	                    usuarioProyecto.getPersona().setContactos(organizacion.getContactos());
	                    nombreOperador = organizacion.getNombre();
	                }
	            }

	            MensajeNotificacion mensajeNotificacion = mensajeNotificacionFacade.buscarMensajesNotificacion("bodyNotificacionArchivoProyectoMesa");

	            if (mensajeNotificacion != null) {
	                Object[] parametrosCorreoNuevo = new Object[] { nombreOperador, proyectoCodigo, motivoArchivo };
	                String notificacion = String.format(mensajeNotificacion.getValor(), parametrosCorreoNuevo);

	                Email.sendEmail(usuarioProyecto, mensajeNotificacion.getAsunto(), notificacion, proyectoCodigo, JsfUtil.getLoggedUser());
	            } else {
	                JsfUtil.addMessageError("No se encontró el mensaje de notificación configurado.");
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void cancelarMasivo() {
		deshabilitado = true;
		motivo = "";
		codigoTramite = "";
		finalizado = false;
        panelesMasivo = false;
        documentoResolucion = new DocumentosCOA();
        documentoSolicitud = new DocumentosCOA();
		listaProyectoCustom.clear();
	}
}