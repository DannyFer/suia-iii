package ec.gob.ambiente.rcoa.digitalizacion.model;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;	

import java.math.BigDecimal;

@Entity
@Table(name="coordinates", schema = "coa_digitalization_linkage")
@NamedQueries({
@NamedQuery(name="CoordenadaDigitalizacion.findAll", query="SELECT c FROM CoordenadaDigitalizacion c")})

@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "coor_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "coor_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "coor_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "coor_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "coor_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "coor_status = 'TRUE'")

public class CoordenadaDigitalizacion extends EntidadAuditable {
    private static final long serialVersionUID = 5016494380218175574L;    
    @Getter
    @Setter
    @Id
    @SequenceGenerator(name = "ADMINISTRACIONAUTORIZACIONCOORDENADA_ID_GENERATOR", sequenceName = "coordinates_coor_id_seq", schema = "coa_digitalization_linkage", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ADMINISTRACIONAUTORIZACIONCOORDENADA_ID_GENERATOR")     
    @Column(name = "coor_id")
    private Integer id;
    @Getter
    @Setter
    @Column(name = "coor_description", length = 255)
    private String descripcion;    
    @Getter
    @Setter
    @Column(name = "coor_order")
    private Integer orden;
    @Getter
    @Setter
    @Column(name = "prsh_type")
    private Integer tipo;    
    @Getter
    @Setter
    @Column(name = "coor_update_number")
    private Integer numeroActualizacion;
    @Getter
    @Setter
    @Column(name = "coor_x")
    private BigDecimal x;
    @Getter
    @Setter
    @Column(name = "coor_y")
    private BigDecimal y;
    @Getter
    @Setter
    @Column(name = "coor_geographic_area")
    private Integer areaGeografica;
    
	@ManyToOne
	@JoinColumn(name = "shap_id")
	@ForeignKey(name = "fk_eash_id")
	@Getter
	@Setter
	private ShapeDigitalizacion shapeDigitalizacion;
    
	@ManyToOne
	@JoinColumn(name = "enaa_id")
	@ForeignKey(name = "fk_enaa_id")
	@Getter
	@Setter
	private AutorizacionAdministrativaAmbiental autorizacionAdministrativaAmbiental;
	
	public boolean isDataComplete() {
		return (orden != null && x != null && y != null);
	}
	
	public CoordenadaDigitalizacion() {
		super();
	}
	
	public CoordenadaDigitalizacion(Object[] array) {
		this.x = (array[1] == null) ? null : new BigDecimal((String)array[1]);
		this.y = (array[1] == null) ? null : new BigDecimal((String)array[2]);
	}
	
}
