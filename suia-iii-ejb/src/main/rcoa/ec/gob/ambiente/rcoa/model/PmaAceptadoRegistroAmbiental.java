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

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="accept_management_plan", schema="coa_environmental_record")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "acmp_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "acmp_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "acmp_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "acmp_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "acmp_user_update")) })

public class PmaAceptadoRegistroAmbiental extends EntidadAuditable implements Serializable, Comparable<PmaAceptadoRegistroAmbiental> {
	private static final long serialVersionUID = 1L;
	
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="acmp_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="acmp_proposed_measure")
	private String medidaPropuesta;

	@Getter
	@Setter
	@Column(name="acmp_half_verification")
	private String medida;

	@Getter
	@Setter
	@Column(name="acmp_frequency")
	private String frecuencia;

	@Getter
	@Setter
	@Column(name="acmp_term")
	private Double plazo;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "acmp_term_date")
	private Date plazoFecha;

	@Getter
	@Setter
	@Column(name="acmp_weighing")
	private Double ponderacion;

	@Getter
	@Setter
	@Column(name = "enre_id")
	private Integer registroAmbientalId;

	@Getter
	@Setter
	@Column(name = "acmp_justification")
	private String justificacion;

	@Getter
	@Setter
	@Column(name = "acmp_justification_status")
	private boolean aceptado;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "enmp_id")
	private MedidaVerificacionPma medidaVerificacionPma;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "enas_id")
	private AspectoAmbientalPma aspectoAmbientalPma;

	@Getter
	@Setter
	@Transient
	private Integer subPlanId;

	@Getter
	@Setter
	@Transient
	private Integer orden;

    @Override
     public int compareTo(PmaAceptadoRegistroAmbiental o) {
        String a=new String(String.valueOf(this.getOrden()));
        String b=new String(String.valueOf(o.getOrden()));
        return a.compareTo(b);
    }
}