package ec.gob.ambiente.retce.model;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the water_use database table.
 * 
 */
@Entity
@Table(name="water_use", schema = "retce")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "waus_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "waus_date_create")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "waus_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "waus_user_create")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "waus_user_update")) })

@NamedQueries({
	@NamedQuery(name="ConsumoAgua.findAll", query="SELECT a FROM ConsumoAgua a"),
	@NamedQuery(name = ConsumoAgua.GET_POR_CONSUMO, query = "SELECT t FROM ConsumoAgua t WHERE t.idConsumoRecurso = :idConsumo and estado = true ORDER BY t.id desc") })
public class ConsumoAgua extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String GET_POR_CONSUMO = "ec.gob.ambiente.retce.model.ConsumoAgua.getPorConsumo";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "waus_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="waus_annual_value_m3")
	private Double consumoAnual;

	@Getter
	@Setter
	@Column(name="waus_coordinate_x")
	private Double coordenadaX;

	@Getter
	@Setter
	@Column(name="waus_coordinate_y")
	private Double coordenadaY;

	@Getter
	@Setter
	@Column(name="waus_name_body_water")
	private String nombreCuerpoHidrico;

	@Getter
	@Setter
	@Column(name="waus_authorization_resolution_number")
	private String nroResolucionAutorizacion;	

	@Getter
	@Setter
	@Column(name="waus_validity_from")
	private Date vigenciaDesde;

	@Getter
	@Setter
	@Column(name="waus_validity_to")
	private Date vigenciaHasta;

	@Getter
	@Setter
	@Column(name="waus_name_watershed")
	private String cuencaHidrografica;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_source_type")
	private DetalleCatalogoGeneral tipoFuente;
	
	@Getter
	@Setter
	@Column(name="reco_id")
	private Integer idConsumoRecurso;
	
	@Getter
	@Setter
	@OneToMany(mappedBy = "idConsumoAgua")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cpty_status = 'TRUE'")
	private List<TipoProcesoConsumo> listaTipoProcesos;
	
	@Getter
	@Setter
	@Transient
	private List<DetalleCatalogoGeneral> listaProcesos;
	
	@Getter
	@Setter
	@Transient
	private List<Documento> listaMediosVerificacion;
	
	@Getter
	@Setter
	@Transient
	private Documento resolucionAprovechamiento;
	
}