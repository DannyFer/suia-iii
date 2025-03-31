package ec.gob.ambiente.rcoa.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

@Entity
@Table(name="environmental_measure_pma", schema="coa_environmental_record")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "enmp_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "enmp_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "enmp_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "enmp_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "enmp_user_update")) })
public class MedidaVerificacionPma extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="enmp_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="enmp_descrption")
	private String descripcion;

	@Getter
	@Setter
	@Column(name="enmp_order")
	private Integer orden;

	@Getter
	@Setter
	@Column(name="enmp_half_verification")
	private String medidaVerificacion;

	@Getter
	@Setter
	@Column(name="enmp_frequency")
	private String frecuencia;

	@Getter
	@Setter
	@Column(name="enmp_term_type")
	private boolean tipoPlazo;

	@Getter
	@Setter
	@Column(name="enmp_required")
	private boolean obligatorio;

	@Getter
	@Setter
	@Column(name="enmp_type")
	private boolean tipoEstandar;

	@Getter
	@Setter
	@Column(name="enmp_tern_apply")
	private boolean aplicaPlazo;

	@Getter
	@Setter
	@Column(name="enmp_status_ciuu")
	private boolean estadoCatalogoCiu;

	@Getter
	@Setter
	@Column(name = "enmp_rgd_own_management")
	private boolean rgdGestioPropia;

	@Getter
	@Setter
	@Column(name = "enmp_rgd_frequency")
	private boolean mostrarRGD;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "enas_id")
	private AspectoAmbientalPma aspectoAmbientalPma;

	@Getter
	@Setter
	@Transient
	private boolean eliminado;

	@Getter
	@Setter
	@Transient
	private Integer plazo;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Transient
	private Date plazoFecha;
	
}