package ec.gob.ambiente.suia.domain;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;


/**
 * The persistent class for the fapma_process_tools database table.
 * 
 */
@Entity
@Table(name="fapma_process_tools", schema = "suia_iii")
@NamedQuery(name="HerramientaProcesoPma.findAll", query="SELECT t FROM HerramientaProcesoPma t")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "fapt_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "fapt_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "fapt_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "fapt_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "fapt_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fapt_status = 'TRUE'")
@NamedQueries({
    @NamedQuery(name = HerramientaProcesoPma.OBTENER_POR_FASE, query = "SELECT c FROM HerramientaProcesoPma c WHERE c.herramienta.categoriaFase.id = :p_categoriaFaseId")
})
public class HerramientaProcesoPma extends EntidadAuditable {
	private static final long serialVersionUID = 1L;

	public static final String OBTENER_POR_FASE = "ec.com.magmasoft.business.domain.HerramientaProcesoPma.obtenerPorFase";
	
	@Id
	@SequenceGenerator(name = "PROCCES_TOOLS_PMA_FAPDID_GENERATOR", sequenceName = "fapma_process_tools_fapr_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROCCES_TOOLS_PMA_FAPDID_GENERATOR")
	@Column(name="fapt_id")
	@Getter
	@Setter
	private Integer id;
		
	@Column(name="fapt_tool_count")
	@Getter
	@Setter
	private Integer cantidadHerramientas;
	
	//bi-directional many-to-one association to tool_catalog
	@ManyToOne
	@JoinColumn(name="toca_id")
	@ForeignKey(name = "toca_process_tool_fk")
	@Getter
	@Setter
	private CatalogoHerramienta herramienta;
	
	@Column(name="fapt_other_description")
	@Getter
	@Setter
	private String descripcionOtras;
	
	//bi-directional many-to-one association to CatiiFapma
	@ManyToOne
	@JoinColumn(name="cafa_id")
	@ForeignKey(name = "catii_fapma_process_tools_fk")
	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbientalPma;
	
	/**
	 * Cris F: aumento de campo en bdd
	 */
	
	@Getter
	@Setter
	@Column(name = "fapt_original_record_id")
	private Integer idRegistroOriginal;
	
	@Getter
	@Setter
	@Column (name = "fapt_historical_date")
	private Date fechaHistorico;
	
	@Getter
	@Setter
	@Transient
	private String nuevoEnModificacion;

	public HerramientaProcesoPma(Integer id) {
		super();
		this.id = id;
	}

	public HerramientaProcesoPma() {
		super();
	}
}