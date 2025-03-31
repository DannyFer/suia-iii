package ec.gob.ambiente.suia.eia.descripcionProyecto.bean;

import java.util.ArrayList;
import java.util.List;

import ec.gob.ambiente.suia.domain.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author liliana.chacha
 */

public class DescripcionProyectoBean {

	@Getter
	@Setter
	public EstudioImpactoAmbiental estudioAmbiental;

	@Getter
	@Setter
	public ActividadLicenciamiento actividadLicenciamiento;

	@Getter
	@Setter
	public List<ActividadLicenciamiento> listaActividades;

	@Getter
	@Setter
	public List<ActividadLicenciamiento> listaActividadesEliminadas;

	@Getter
	@Setter
	public List<CatalogoCategoriaFase> fasesPorSubsector;

	@Getter
	@Setter
	public CoordenadaGeneral coordenadaGeneral;

	@Getter
	@Setter
	public List<CoordenadaGeneral> listaCoordenadasGenerales;

	@Getter
	@Setter
	public List<CoordenadaGeneral> coordenadasGeneralEliminadas;

	@Getter
	@Setter
	public List<SustanciaQuimicaEia> sustanciaQuimicaEiaEliminadas;

	@Getter
	@Setter
	private List<SustanciaQuimicaPeligrosa> sustanciaQuimicasPeligrosas;

	@Getter
	@Setter
	private List<SustanciaQuimicaPeligrosa> sustanciaQuimicasSeleccionadas, sustanciasQuimicasHistorico;

	@Setter
	@Getter
	private List<SustanciaQuimicaEia> sustanciaQuimicaEiaLista, sustanciaQuimicaEiaListaBdd;

	@Getter
	@Setter
	private String nombreDocumentoDescripcion;

	@Getter
	@Setter
	private String nombreDocumentoInsumos;

	@Getter
	@Setter
	private String nombreDocumentoCoordenada;

	@Getter
	@Setter
	public EtapasProyecto etapaPorFase;

	@Getter
	@Setter
	public List<EtapasProyecto> listaEtapasPorfaseYzonas;

	@Getter
	@Setter
	public ActividadesPorEtapa actividadesPorEtapa;

	@Getter
	@Setter
	public List<ActividadesPorEtapa> listaActividadesPorEtapas;

	@Getter
	@Setter
	public List<ActividadesPorEtapa> listaActividadesPorEtapasEliminadas;
	
	@Getter
	@Setter
	public Integer sustanciasModificadas;


	public void iniciar() {
		setActividadLicenciamiento(new ActividadLicenciamiento());
		setEstudioAmbiental(null);
		sustanciaQuimicasPeligrosas = new ArrayList<SustanciaQuimicaPeligrosa>();
		sustanciaQuimicasSeleccionadas = new ArrayList<SustanciaQuimicaPeligrosa>();
		setListaActividades(new ArrayList<ActividadLicenciamiento>());
		setListaActividadesEliminadas(new ArrayList<ActividadLicenciamiento>());
		setFasesPorSubsector(new ArrayList<CatalogoCategoriaFase>());
		setListaCoordenadasGenerales(new ArrayList<CoordenadaGeneral>());
		setCoordenadaGeneral(null);
		setCoordenadasGeneralEliminadas(new ArrayList<CoordenadaGeneral>());
		setSustanciaQuimicaEiaEliminadas(new ArrayList<SustanciaQuimicaEia>());
		setSustanciaQuimicaEiaLista(new ArrayList<SustanciaQuimicaEia>());
		setEtapaPorFase(null);
		setListaEtapasPorfaseYzonas(new ArrayList<EtapasProyecto>());
		setActividadesPorEtapa(new ActividadesPorEtapa());
		setListaActividadesPorEtapas(new ArrayList<ActividadesPorEtapa>());
		setListaActividadesPorEtapasEliminadas(new ArrayList<ActividadesPorEtapa>());
		setListaActividadesEliminadas(new ArrayList<ActividadLicenciamiento>());
	}
}