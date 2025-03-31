package ec.gob.ambiente.proyectos.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;

import ec.gob.ambiente.proyectos.datamodel.LazyProyectRGDDataModel;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.PuntoRecuperacion;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoSuspendidoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ProyectosCategoriasController implements Serializable {

	private static final long serialVersionUID = 5147000484930491102L;
	private static final Logger LOG = Logger.getLogger(ProyectosCategoriasController.class);
	

	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	
	@EJB
	private ProcesoSuspendidoFacade procesoSuspendidoFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@Getter
	@Setter	
	private List<GeneradorDesechosPeligrosos> generadorDesechosList;
	
	@Setter
	@Getter
	private LazyDataModel<GeneradorDesechosPeligrosos> proyectosLazy;
	
	@Getter
	@Setter	
	private GeneradorDesechosPeligrosos generadorSeleccionado;
	
	@Getter
	@Setter
	private String motivoEliminar;
	
	@Getter
	@Setter
	private String pagoRgd;
	
	@Getter
    @Setter
    private Documento documentoAdjunto;
	
	@Getter
	@Setter
	private boolean deletionActive;
	
	@EJB
	private TransaccionFinancieraFacade  transaccionFinancieraFacade;
	
	@PostConstruct
	private void init() {
		try {
			deletionActive = Usuario.isUserInRole(JsfUtil.getLoggedUser(), "admin") || Usuario.isUserInRole(JsfUtil.getLoggedUser(), "AUTORIDAD AMBIENTAL")|| Usuario.isUserInRole(JsfUtil.getLoggedUser(), "AUTORIDAD AMBIENTAL MAE");
			//generadorDesechosList=registroGeneradorDesechosFacade.buscarRegistrosGeneradorDesechos(Usuario.isUserInRole(JsfUtil.getLoggedUser(), "admin")?JsfUtil.getLoggedUser().getArea():JsfUtil.getLoggedUser().getArea(),true);
			proyectosLazy=new LazyProyectRGDDataModel(JsfUtil.getLoggedUser());
			documentoAdjunto=new Documento();
			
		} catch (Exception e) {
			LOG.error("Error cargando proyectos", e);
		}
	}
	
}