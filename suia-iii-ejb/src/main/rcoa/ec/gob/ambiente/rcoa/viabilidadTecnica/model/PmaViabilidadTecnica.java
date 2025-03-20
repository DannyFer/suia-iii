package ec.gob.ambiente.rcoa.viabilidadTecnica.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.rcoa.model.PlanManejoAmbientalPma;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import java.sql.Timestamp;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the technical_viability_pma database table.
 * 
 */
@Entity
@Table(name="technical_viability_pma", schema="coa_viability_technical")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "tvip_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "tvip_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tvip_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tvip_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tvip_user_update")) })
@NamedQuery(name="PmaViabilidadTecnica.findAll", query="SELECT p FROM PmaViabilidadTecnica p")
public class PmaViabilidadTecnica extends EntidadAuditable implements Serializable, Comparable<PmaViabilidadTecnica>{
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="tvip_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="mapr_id")
	private PlanManejoAmbientalPma planManejoAmbientalPma;

	@Getter
	@Setter
	@Column(name="tvip_aspect")
	private String aspectoAmbiental;
	
	@Getter
	@Setter
	@Column(name="tvip_frequency")
	private String frecuencia;

	@Getter
	@Setter
	@Column(name="tvip_half_verification")
	private String medioVerificacion;

	@Getter
	@Setter
	@Column(name="tvip_modify")
	private Boolean permiteModificacion;

	@Getter
	@Setter
	@Column(name="tvip_proposed_measure")
	private String medidaPropuesta;

	@Getter
	@Setter
	@Column(name="tvip_term")
	private String plazo;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="aspe_id")
	private AspectoViabilidadTecnica aspectoViabilidadTecnica;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="tvph_id")
	private FaseViabilidadTecnicaRcoa faseViabilidadTecnica;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="mefm_id")
	private MedioFrecuenciaMedida medioFrecuenciaMedida;
	
	@Getter
	@Setter
	@Column(name = "tvip_justification")
	private String justificacion;
	
	@Getter
	@Setter
	@Column(name = "tvip_justification_status")
	private boolean aceptado;
	
	@Getter
	@Setter
	@Column(name="tvip_ponderation")
	private Double ponderacion;
	
	@Getter
	@Setter
	@Transient
	private Integer subPlanId;
	
	@Getter
	@Setter
	@Transient
	private Integer orden;
	
	@Getter
	@Setter
	@Transient
	private Date plazoFecha;
	
	@Override
    public int compareTo(PmaViabilidadTecnica o) {
       String a=new String(String.valueOf(this.getOrden()));
       String b=new String(String.valueOf(o.getOrden()));
       return a.compareTo(b);
   }

	public PmaViabilidadTecnica() {
	}

	

}