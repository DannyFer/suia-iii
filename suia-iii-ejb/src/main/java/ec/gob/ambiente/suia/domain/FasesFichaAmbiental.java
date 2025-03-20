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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name="phases_environmental_record", schema="suia_iii")
@NamedQueries({
	@NamedQuery(name = FasesFichaAmbiental.LISTAR_POR_FICHA_ID, query = "SELECT c FROM FasesFichaAmbiental c where c.estado = true and c.fichaAmbientalPma.id = :p_fichaId and c.idRegistroOriginal = null order by c.id") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "pher_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pher_status = 'TRUE'")
public class FasesFichaAmbiental extends EntidadBase{

	private static final long serialVersionUID = 4550228153568889626L;
	
	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_FICHA_ID = PAQUETE
			+ "FasesFichaAmbiental.listarPorSubsectorCodigo";
	
	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "FAPMA_PHASES_ID_GENERATOR", sequenceName = "seq_pher_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FAPMA_PHASES_ID_GENERATOR")
	@Column(name = "pher_id")
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "secp_id", referencedColumnName = "secp_id")
	@ForeignKey(name = "fk_categories_catalog_phases_cacp_id_phases_fapma")
	private CatalogoCategoriaFase catalogoCategoriaFase;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "cafa_id", referencedColumnName = "cafa_id")
	@ForeignKey(name = "fk_phases_fapma_phas_id_phase_phas_id")
	private FichaAmbientalPma fichaAmbientalPma;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "mien_id", referencedColumnName = "mien_id")
	@ForeignKey(name = "fk_phases_famin_phas_id_phase_phas_id")
	private FichaAmbientalMineria fichaAmbientalMineria;
	
	/**
	 * Cris F: campos para historial
	 */
	@Getter
	@Setter
	@Column(name="pher_original_record_id")
	private Integer idRegistroOriginal;
	
	@Getter
	@Setter
	@Column(name="pher_historical_date")
	private Date fechaHistorico;
	
}
