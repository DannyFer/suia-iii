package ec.gob.ambiente.rcoa.digitalizacion;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.AutorizacionAdministrativa;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.dto.EntityLicenciaFisica;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.dto.Tarea;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@SessionScoped
public class TareasProcesosBean {
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public String dblinkSuiaVerde=Constantes.getDblinkSuiaVerde();
	
	public String dblinkSuiaHidro=Constantes.getDblinkBpmsHyD();
	
	public String dblinkSectorSubsector=Constantes.getDblinkSectorSubsector();

    @Getter
    @Setter
    private List<Tarea> tareas;
    @Getter
    @Setter
    private List<Documento> documentos;
	
	@Getter
	@Setter
	private List<AutorizacionAdministrativa> listaProyectosSeleccionados;
	
	@Getter
	@Setter
	private AutorizacionAdministrativaAmbiental autorizacionAdministrativaPrincipal;
	
	@Getter
	@Setter
	private Boolean esRegistroNuevo;
	
	@Getter
	@Setter
	private AutorizacionAdministrativa autorizacionAdministrativaSeleccionada;
	
	@Getter
	@Setter
	private EntityLicenciaFisica autorizacionFisicaSeleccionada;
	
	@Getter
	@Setter
	private AutorizacionAdministrativa autorizacionAdministrativaLista;


	public StreamedContent getStream(Documento documento) {
		if (documento.getContenidoDocumento() != null) {
			InputStream is = new ByteArrayInputStream(documento.getContenidoDocumento());
			return new DefaultStreamedContent(is, documento.getMime(), documento.getNombre());
		} else{
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return null;
	}

}
