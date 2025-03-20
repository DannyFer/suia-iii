package ec.gob.ambiente.rcoa.digitalizacion.model;


import java.math.BigDecimal;

import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;	

@Entity
@Table(name="shapes", schema = "coa_digitalization_linkage")
@NamedQueries({
@NamedQuery(name="ShapeDigitalizacion.GETFINDALL", query="SELECT c FROM ShapeDigitalizacion c")})

@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "shap_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "shap_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "shap_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "shap_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "shap_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "shap_status = 'TRUE'")


public class ShapeDigitalizacion extends EntidadAuditable {
    private static final long serialVersionUID = 5016494380218175574L;    
    @Getter
    @Setter
    @Id        
    @SequenceGenerator(name = "SHAPE_DIGITALIZATION_ID_GENERATOR", sequenceName = "shapes_shap_id_seq", schema = "coa_digitalization_linkage", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SHAPE_DIGITALIZATION_ID_GENERATOR")
    @Column(name = "shap_id")
    private Integer id;
    @Getter
    @Setter
    @Column(name = "shap_type")
    private Integer tipo;
    @Getter
    @Setter
    @Column(name = "shap_type_entry")
    private Integer tipoIngreso;
    @Getter
    @Setter
    @Column(name = "shap_update_number")
    private Integer numeroActualizaciones;
    @Getter
    @Setter
    @Column(name = "shap_surface")
    private BigDecimal superficie;
	@ManyToOne
	@JoinColumn(name = "enaa_id")
	@ForeignKey(name = "fk_enaa_id")
	@Getter
	@Setter
	private AutorizacionAdministrativaAmbiental autorizacionAdministrativaAmbiental;
	@ManyToOne
	@JoinColumn(name = "shty_id")
	@ForeignKey(name = "fk_shty_id")
	@Getter
	@Setter
	private TipoForma tipoForma;	
	@Getter
	@Setter
	@Column(name = "shap_reference_system")
	private String sistemaReferencia;
	@Getter
	@Setter
	@Column(name = "shap_zone", length = 10)
	private String zona;
	
	@Transient
	@Getter
	@Setter
	private Integer idTipoForma;
	
	public ShapeDigitalizacion(String array, Integer id) {
		this.id = id;
		this.idTipoForma = (array == null) ? null : Integer.valueOf(array);
	}

	public ShapeDigitalizacion() {
		super();
	}	

}
