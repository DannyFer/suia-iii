package ec.gob.ambiente.retce.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the waste_self_management database table.
 * 
 */
@Entity
@Table(name="waste_self_management", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "wsma_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "wsma_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "wsma_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "wsma_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "wsma_user_update")) })

@NamedQueries({
		@NamedQuery(name = "DesechoAutogestion.findAll", query = "SELECT a FROM DesechoAutogestion a"),
		@NamedQuery(name = DesechoAutogestion.GET_LISTA_DESECHOS_AUTOGESTION_POR_RGDRETCE, query = "SELECT t FROM DesechoAutogestion t WHERE t.idGeneradorRetce = :idRgdRetce and estado = true and idRegistroOriginal is null ORDER BY t.id desc"),
		@NamedQuery(name = DesechoAutogestion.GET_HISTORIAL_POR_ID, query = "SELECT t FROM DesechoAutogestion t WHERE t.idRegistroOriginal = :idDesecho and estado = true and numeroObservacion = :nroObservacion ORDER BY t.id desc"),
		@NamedQuery(name = DesechoAutogestion.GET_HISTORIAL_DESECHOS_ELIMINADOS_POR_RGDRETCE, 
		query = "SELECT t FROM DesechoAutogestion t WHERE t.idGeneradorRetce = :idRgdRetce and estado = true and idRegistroOriginal is not null and idRegistroOriginal in (SELECT a.id FROM DesechoAutogestion a where a.idGeneradorRetce = :idRgdRetce and a.estado = false and a.idRegistroOriginal is null ) ORDER BY t.id desc") })

public class DesechoAutogestion extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String GET_LISTA_DESECHOS_AUTOGESTION_POR_RGDRETCE = "ec.gob.ambiente.retce.model.DesechoAutogestion.getListaDesechosAutogestionPorRgdRetce";
	public static final String GET_HISTORIAL_POR_ID = "ec.gob.ambiente.retce.model.DesechoAutogestion.getHistorialPorId";
	public static final String GET_HISTORIAL_DESECHOS_ELIMINADOS_POR_RGDRETCE = "ec.gob.ambiente.retce.model.DesechoAutogestion.getHistorialDesechosEliminadosPorRgdRetce";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="wsma_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="wsma_total_tons")
	private Double totalToneladas;

//	@Getter
//	@Setter
//	@ManyToOne
//	@JoinColumn(name="hwgr_id")
//	private GeneradorDesechosPeligrososRetce generadorDesechosRetce;
	
	@Getter
	@Setter
	@Column(name="hwgr_id")
	private Integer idGeneradorRetce;

	@Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "wada_id")
    private DesechoPeligroso desechoPeligroso;
	
	@Getter
	@Setter
	@OneToMany(mappedBy = "idDesechoAutogestion")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filters({
		@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wdsm_status = 'TRUE'"),
		@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wdsm_original_record_id is null") })
	private List<DesechoEliminacionAutogestion> listaDesechosEliminacionAutogestion;
	
	@Getter
	@Setter
	@OneToMany(mappedBy = "idDesechoAutogestion")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filters({
		@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "sure_status = 'TRUE'"),
		@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "sure_original_record_id is null") })
	private List<SubstanciasRetce> listaSustanciasRetce;

	@Getter
	@Setter
	@Column(name="wsma_history")
	private Boolean historial;
    
    @Getter
	@Setter
	@Column(name="wsma_original_record_id")
	private Integer idRegistroOriginal;
    
    @Getter
	@Setter
	@Column(name="wsma_observation_number")
	private Integer numeroObservacion;
    
    @Getter
	@Setter
	@Transient
    private Boolean mostrarHistorialDesecho = false;
    
    @Getter
	@Setter
	@Transient
    private Boolean mostrarHistorialSustancias = false;
}