package ec.gob.ambiente.suia.domain;

import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@NamedQueries({
	@NamedQuery(name = EstudioViabilidadTecnica.OBTENER_ESTUDIO_VIABILIDAD_POR_PROCESO, 
			query = "select e from EstudioViabilidadTecnica e where e.id = :idEstudio") })
@Entity
@Table(name = "technical_viability_study", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "tvst_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "tvst_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tvst_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tvst_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tvst_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tvst_status = 'TRUE'")

public class EstudioViabilidadTecnica extends EntidadAuditable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6318425276206201047L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String OBTENER_ESTUDIO_VIABILIDAD_POR_PROCESO = PAQUETE + "EstudioViabilidadTecnica.obtEstudioViabilidadTecnica";

	@Id
	@SequenceGenerator(name = "technical_viability_study_id_generator", initialValue = 1, sequenceName = "seq_tvst_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "technical_viability_study_id_generator")
	@Column(name = "tvst_id")
	@Getter @Setter
	private Integer id;

	@Getter @Setter
	@Column(name = "tvst_process_id")
	private Long procesoId;

	@Getter @Setter
	@Column(name = "tvst_code")
	private String codigo;

	@OneToMany(mappedBy = "estudioViabilidadTecnica")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tvsd_status = 'TRUE'")
	@Getter
	@Setter
	private List<EstudioViabilidadTecnicaDiagnostico> estudioViabilidadTecnicaDiagnosticos;
	
	@ManyToOne
	@JoinColumn(name = "unep_id")
	@ForeignKey(name = "fk_technical_viability_study_unep_id")
	@Getter
	@Setter
	private ProyectoAmbientalNoRegulado proyectoAmbientalNoRegulado; 
	
	
	public EstudioViabilidadTecnica() {
    }

    public EstudioViabilidadTecnica(Integer id) {
        this.id = id;
    } 
}