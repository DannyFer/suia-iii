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

import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


/**
 * The persistent class for the filepma_detail database table.
 * 
 */
@NamedQueries({ @NamedQuery(name = DetalleFichaPma.GET_ALL, query = "SELECT d FROM DetalleFichaPma d") })
@Entity
@Table(name = "detail_sheet_pma", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "desp_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "desp_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "desp_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "desp_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "desp_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "desp_status = 'TRUE'")
public class DetalleFichaPma extends EntidadAuditable {
	
	private static final long serialVersionUID = 1L;
	
	public static final String GET_ALL = "ec.com.magmasoft.business.domain.categoriaii.DetalleFichaPma.getAll";
	public static final String SEQUENCE_CODE = "detail_sheet_pma_desp_id_seq";
	
	@Id
	@Column(name="desp_id")
	@SequenceGenerator(name = "DETAIL_SHEET_PMA_FIDEID_GENERATOR", sequenceName = "detail_sheet_pma_desp_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DETAIL_SHEET_PMA_FIDEID_GENERATOR")
	@Getter
	@Setter
	private Integer id;		
	
	//bi-directional many-to-one association to CatiiFapma
	@ManyToOne
	@JoinColumn(name="cafa_id")
	@ForeignKey(name = "catii_fapma_detail_sheet_pma_fk")
	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbientalPma;

	//bi-directional many-to-one association to GeneralCatalog
	@ManyToOne
	@JoinColumn(name="geca_id")
	@ForeignKey(name = "general_catalogs_detail_sheet_pma_fk")
	@Getter
	@Setter
	private CatalogoGeneral catalogoGeneral;
	
	/**
	 * Cris F: Aumento de variables
	 */
	@Getter
	@Setter
	@Column(name="desp_original_record_id")
	private Integer idRegistroOriginal;
	
	@Getter
	@Setter
	@Column(name = "desp_historical_date")
	private Date fechaHistorico;
	
	@Getter
	@Setter
	@Transient
	private String nuevoEnModificacion;
	
	@Getter
	@Setter
	@Transient
	private String otraInfraestructura;
	/**
	 * FIN
	 */

	}