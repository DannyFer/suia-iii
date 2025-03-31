package ec.gob.ambiente.suia.domain;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "unqualified_consultants", schema = "suia_iii")
@NamedQueries({
        @NamedQuery(name = ConsultorNoCalificado.FIND_BY_ESTUDIO, query = "SELECT c FROM ConsultorNoCalificado c WHERE c.estudioImpactoAmbiental.id = :idEstudio AND c.idHistorico = null ")
})
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "unco_status")),
        @AttributeOverride(name = "fechaCreacion", column = @Column(name = "unco_date_creation")),
        @AttributeOverride(name = "fechaModificacion", column = @Column(name = "unco_date_modification")),
        @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "unco_user_creation")),
        @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "unco_user_modification"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "unco_status = 'TRUE'")
public class ConsultorNoCalificado extends EntidadAuditable implements Cloneable{

    /**
     *
     */
    private static final long serialVersionUID = -153421734625934903L;
    public static final String FIND_BY_ESTUDIO = "ec.gob.ambiente.suia.domain.ConsultorNoCalificado.find_by_estudio";

    @Getter
    @Setter
    @Id
    @SequenceGenerator(name = "UNQUALIFIED_CONSULTANTS_UNCO_ID_GENERATOR", sequenceName = "seq_unco_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UNQUALIFIED_CONSULTANTS_UNCO_ID_GENERATOR")
    @Column(name = "unco_id")
    private Integer id;

    @Getter
    @Setter
    @Column(name = "unco_name", length = 2147483647)
    private String nombre;

    @Getter
    @Setter
    @Column(name = "unco_pin", length = 13)
    private String identificacion;

    @Getter
    @Setter
    @Column(name = "unco_vocational_training", length = 25)
    private String formacionProfesional;

    @Getter
    @Setter
    @Column(name = "unco_component_description", length = 25)
    private String component_description;
    
	@Getter
	@Setter
	@Column(name = "unco_historical_id")
	private Integer idHistorico;
	
	@Getter
	@Setter
	@Column(name = "unco_notification_number")
	private Integer numeroNotificacion;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "eist_id")
    @ForeignKey(name = "fk_unqualified_consultants_eist_id_environmental_impact_studies_eist_id")
    private EstudioImpactoAmbiental estudioImpactoAmbiental;

    @Getter
    @Setter
    @JoinColumn(name = "pacc_id", referencedColumnName = "pacc_id")
    @ForeignKey(name = "fk_unqualified_consultants_unco_id_partic_com_catalog_pacc_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private CatalogoComponenteParticipacion componentePaticipacion;

    @Getter
    @Setter
    @Column(name = "unco_participation_description")
    private String descripcionParticipacionEquipConsultor;
    
	@Getter
	@Setter
	@Transient
	private Boolean nuevoEnModificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean registroModificado;
    
    @Override
	public ConsultorNoCalificado clone() throws CloneNotSupportedException {

		 ConsultorNoCalificado clone = (ConsultorNoCalificado)super.clone();
		 clone.setId(null);		 
		 return clone;
	}
}