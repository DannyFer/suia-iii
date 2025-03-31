package ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.suia.domain.Periodicidad;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoEspecialRecoleccion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.LavadoContenedoresYTratamientoEfluentes;

@ManagedBean
@ViewScoped
public class LavadoContenedoresYTratamientoEfluentesBean {

	@Setter
	@Getter
	private LavadoContenedoresYTratamientoEfluentes lavadoContenedoresYTratamientoEfluentes;

	@Setter
	@Getter
	private List<Periodicidad> listaFrecuenciasLavado;

	@Setter
	@Getter
	private List<LavadoContenedoresYTratamientoEfluentes> listaLavadosContenedores;

	@Setter
	@Getter
	private List<LavadoContenedoresYTratamientoEfluentes> listaLavadosContenedoresModificados;

	@Setter
	@Getter
	private List<LavadoContenedoresYTratamientoEfluentes> listaLavadosContenedoresEliminados;

	@Setter
	@Getter
	private List<DesechoEspecialRecoleccion> desechosEspeciales;

	@Setter
	@Getter
	private List<DesechoPeligroso> desechosEsp;

	@Setter
	@Getter
	private List<DesechoEspecialRecoleccion> desechosEspecialeseliminar;
        
	@Setter
	@Getter
	private DesechoEspecialRecoleccion desechoEspecial;

	@Setter
	@Getter
	private UploadedFile file;

	@Setter
	@Getter
	private boolean editar;
        
	@Setter
	@Getter
	private boolean mostrarFotografia;
	@Setter
	@Getter
	private AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicos;

        
        
}
