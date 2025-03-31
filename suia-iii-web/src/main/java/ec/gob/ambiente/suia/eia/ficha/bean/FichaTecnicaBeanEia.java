package ec.gob.ambiente.suia.eia.ficha.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.Consultor;
import ec.gob.ambiente.suia.domain.ConsultorNoCalificado;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;

/**
 * 
 * @author lili
 *
 */
@ManagedBean
@ViewScoped
public class FichaTecnicaBeanEia implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8293394384138568012L;

	@Getter
    @Setter
	private EstudioImpactoAmbiental estudioImpactoAmbiental;
	
	@Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyecto;
	
	@Getter
    @Setter
    private List<UbicacionesGeografica> listaUbicacionProyecto;
	
	@Getter
    @Setter
    private UbicacionesGeografica ubicacionContacto;
	
	@Getter
    @Setter
	private List<Consultor> listaConsultoresCalificados;
	
	@Getter
    @Setter
	private Consultor consultorSeleccionado;
	
	@Getter
    @Setter
	private ConsultorNoCalificado consultorNoCalificado;
	
	@Getter
    @Setter
	private List<ConsultorNoCalificado> consultorNoCalificadosSeleccionados;
	
	@Getter
    @Setter
	private List<ConsultorNoCalificado> consultoresNoCalificadosEliminados;
	
	@Getter
    @Setter
	private Boolean verFichaTecnicaMineria;
	
	@Getter
    @Setter
	private String clausula;
	
	@Getter
    @Setter
	private String[] fasesMinerasSeleccionadas;
	
	@Getter
    @Setter
    private List<SelectItem> listaFasesMineras;
	
	@Getter
    @Setter
	private Boolean verBeneficio;
	
	@Getter
    @Setter
	private Boolean verExplotacion;
	
	@Getter
	@Setter
	private CoordenadaGeneral coordenadaGeneral, coordenadaGeneralOriginal;
	
	@Getter
    @Setter
	private List<CoordenadaGeneral> listaCoordenadasGenerales;
	
	@Getter
    @Setter
	private List<CoordenadaGeneral> coordenadasGeneralEliminadas;
    
    public void iniciar() {
        setEstudioImpactoAmbiental(null);
        setProyecto(new ProyectoLicenciamientoAmbiental());
        setUbicacionContacto(null);
        setConsultorSeleccionado(null);
        setConsultorNoCalificado(new ConsultorNoCalificado());
        setListaConsultoresCalificados(new ArrayList<Consultor>());
        setListaUbicacionProyecto(new ArrayList<UbicacionesGeografica>());
        setConsultorNoCalificadosSeleccionados(new ArrayList<ConsultorNoCalificado>());
        setConsultoresNoCalificadosEliminados(new ArrayList<ConsultorNoCalificado>());
        setVerFichaTecnicaMineria(false);
        setClausula("");
        setFasesMinerasSeleccionadas(null);
        setListaFasesMineras(new ArrayList<SelectItem>());
        setVerBeneficio(false);
        setVerExplotacion(false);
        setCoordenadaGeneral(new CoordenadaGeneral(new BigDecimal(0), new BigDecimal(0)));
        setListaCoordenadasGenerales(new ArrayList<CoordenadaGeneral>());
        setCoordenadasGeneralEliminadas(new ArrayList<CoordenadaGeneral>());
    }
}
