package ec.gob.ambiente.prevencion.licenciamientoambiental.controller;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.licenciamientoambiental.bean.DescargarDocumentoLABean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class DescargarDocumentoLAController implements Serializable {

    private static final long serialVersionUID = -35903712834217786L;
    private static final Logger LOGGER = Logger
            .getLogger(DescargarDocumentoLAController.class);
    @EJB
    private ProcesoFacade procesoFacade;


    @EJB
    private AreaFacade areaFacade;
    
    @EJB
	private DocumentosFacade documentosFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{descargarDocumentoLABean}")
    private DescargarDocumentoLABean descargarDocumentoLABean;


	public String iniciarTarea() throws ParseException {
		if (validarInicioTarea()) {
			try {
				Map<String, Object> data = new ConcurrentHashMap<String, Object>();
				procesoFacade.aprobarTarea(loginBean.getUsuario(),
						bandejaTareasBean.getTarea().getTaskId(),
						bandejaTareasBean.getProcessId(), data);
				
				JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
				procesoFacade.envioSeguimientoLicenciaAmbiental(
						loginBean.getUsuario(),
						bandejaTareasBean.getProcessId());
				
				return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
			} catch (Exception e) {
				LOGGER.error(e);
				JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
			}
		}

		return "";
	}
    
    private boolean validarInicioTarea() throws ParseException{
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fechabloqueo = sdf.parse(Constantes.getFechaBloqueoTdrMineria());
        Date fechaproyecto=sdf.parse(descargarDocumentoLABean.getProyecto().getFechaRegistro().toString());
        boolean bloquear=false;        
        if (fechaproyecto.before(fechabloqueo)){
        	bloquear=true;
        }   
    	if ((descargarDocumentoLABean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.03.01") 
    			|| descargarDocumentoLABean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.03.02")
    			||  descargarDocumentoLABean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.03.03")
    			|| descargarDocumentoLABean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.03.04")    			
    			|| descargarDocumentoLABean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.04.01")
    			|| descargarDocumentoLABean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.04.02")    			
    			|| descargarDocumentoLABean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.05.01")
    			|| descargarDocumentoLABean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.05.02")    			
    			|| descargarDocumentoLABean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.08.01")    			
    			|| descargarDocumentoLABean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.07.01")
    			|| descargarDocumentoLABean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.07.02")
    			|| descargarDocumentoLABean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.06.01")
    			|| descargarDocumentoLABean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.06.03")
    			) && descargarDocumentoLABean.getProyecto().getIdEstadoAprobacionTdr()==null && bloquear){   		    	
			if (descargarDocumentoLABean.isDescargado()) {
				if(descargarDocumentoLABean.getProyecto().getIdEstadoAprobacionTdr()==null && bloquear)
				{
					JsfUtil.addMessageError("Estimado proponente los Términos de Referencia que acaba de descargar deben ser elaborados en función de su proyecto en particular, los mismos, que deben ser presentados ante la Autoridad Ambiental Competente en forma física a través de un oficio, para ser sometidos a evaluación y aprobación ya sea del Ministerio del Ambiente y Agua o de la Autoridad Ambiental de Aplicación Responsable, en cumplimiento del Art. 21 del Reglamento Ambiental de Actividades Mineras.\n"
							+ "\n"
							+ "Se le recuerda a Usted,  que la documentación requerida debe ser presentada en un término no mayor a 90 días desde el presente requerimiento, en cumplimiento a  la Disposición Transitoria Quinta del Acuerdo Ministerial No. 061, publicado en el Registro Oficial No. 316 de 4 de mayo de 2015.");
					return false;
				}
				else				
					return true;
				
			} else {
				JsfUtil.addMessageError("Para continuar debe descargar el documento.");
				return false;
			}
		} else {
			if (descargarDocumentoLABean.isDescargado()) {								
				return true;				
			} else {
				JsfUtil.addMessageError("Para continuar debe descargar el documento.");
				return false;
			}
		}
    }
}
