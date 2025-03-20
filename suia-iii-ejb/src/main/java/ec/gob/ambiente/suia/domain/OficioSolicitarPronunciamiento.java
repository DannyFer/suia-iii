package ec.gob.ambiente.suia.domain;

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

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@NamedQueries({
	@NamedQuery(name = OficioSolicitarPronunciamiento.OBTENER_OFICIO_SOLICTAR_PRONUNCIAMIENTO_POR_ESTUDIO_TIPO, 
			query = "select i from OficioSolicitarPronunciamiento i where i.estudioImpactoAmbientalId = :p_estudioImpactoAmbientalId "
					+ "and i.tipoDocumentoId = :p_tipoDocumentoId") })
@Entity
@Table(name = "job_order_statement", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "jost_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "jost_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "jost_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "jost_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "jost_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "jost_status = 'TRUE'")
public class OficioSolicitarPronunciamiento extends EntidadAuditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6318425276206201047L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String OBTENER_OFICIO_SOLICTAR_PRONUNCIAMIENTO_POR_ESTUDIO_TIPO = PAQUETE + "OficioSolicitarPronunciamiento.obtenerOficioSolicitarPronunciamientoPorEstudioTipo";

	@Id
	@SequenceGenerator(name = "job_order_statement_id_generator", initialValue = 1, sequenceName = "seq_jost_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "job_order_statement_id_generator")
	@Column(name = "jost_id")
	@Getter
	@Setter
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "doty_id")
	@ForeignKey(name = "fk_jost_id_doty_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tydo_status = 'TRUE'")
	@Getter
	@Setter
	private TipoDocumento tipoDocumento;

	@ManyToOne
	@JoinColumn(name = "eist_id")
	@ForeignKey(name = "fk_jost_id_eist_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "jost_status = 'TRUE'")
	@Getter
	@Setter
	private EstudioImpactoAmbiental estudioImpactoAmbiental; 

	@Getter
	@Setter
	@Column(name = "eist_id", updatable = false, insertable = false)
	private Integer estudioImpactoAmbientalId;

	@Getter
	@Setter
	@Column(name = "doty_id", updatable = false, insertable = false)
	private Integer tipoDocumentoId;

	@Getter
	@Setter
	@Column(name = "jost_job_number")
	private String numeroOficio;
	
	@Getter
	@Setter
	@Column(name = "jost_job_date")
	private String fechaOficio;
	
	@Getter
	@Setter
	@Column(name = "jost_technical_name")
	private String nombreTecnico;

	@Getter
	@Setter
	@Column(name = "jost_director_name")
	private String nombreDirector;

	@Getter
	@Setter
	@Column(name = "jost_subject")
	private String asunto;

}
