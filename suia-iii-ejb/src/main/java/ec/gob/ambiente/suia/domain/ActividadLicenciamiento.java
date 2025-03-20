package ec.gob.ambiente.suia.domain;

import java.util.Date;
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
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * @author liliana.chacha
 */
@Entity
@Table(name = "activities_licensing", schema = "suia_iii")
@NamedQueries({
        @NamedQuery(name = ActividadLicenciamiento.findByEstudio, query = "SELECT a FROM ActividadLicenciamiento a WHERE a.estudioImpactoAmbiental.id = :idEstudio AND a.idHistorico = null"),
        @NamedQuery(name = ActividadLicenciamiento.FIND_BY_CATALOGO_CATEGORIA_FASE_AND_ESTUDIO, query = "SELECT a FROM ActividadLicenciamiento a WHERE a.catalogoCategoriaFase = :paramCatalogo AND a.estudioImpactoAmbiental = :paramEstudio AND a.idHistorico = null")
})
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "acli_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "acli_status = 'TRUE'")
public class ActividadLicenciamiento extends EntidadBase implements Cloneable {

    private static final long serialVersionUID = 7332663840889394644L;
    public static final String findByEstudio = "ec.gob.ambiente.suia.domain.ActividadLicenciamiento.find_by_estudio";
    public static final String FIND_BY_CATALOGO_CATEGORIA_FASE_AND_ESTUDIO = "ec.gob.ambiente.suia.domain.ActividadLicenciamiento.findByCatalogoCategoriaFaseAndEstudio";

    @Id
    @Getter
    @Setter
    @Column(name = "acli_id")
    @SequenceGenerator(name = "ACTIVITIES_LICENSING_GENERATOR", initialValue = 1, sequenceName = "seq_acli_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ACTIVITIES_LICENSING_GENERATOR")
    private Integer id;

    @Getter
    @Setter
    @Column(name = "acli_name_activity")
    private String nombreActividad;

    @Getter
    @Setter
    @Column(name = "acli_phase")
    private String fase;
    
	@Getter
	@Setter
	@Column(name = "acli_historical_id")
	private Integer idHistorico;
	
	@Getter
	@Setter
	@Column(name = "acli_notification_number")
	private Integer numeroNotificacion;

    @Getter
    @Setter
    @ManyToOne()
    @JoinColumn(name = "eist_id")
    @ForeignKey(name = "fk_activities_licensing_eist_id_environmental_impact_studies_eist_id")
    private EstudioImpactoAmbiental estudioImpactoAmbiental;

    @Getter
    @Setter
    @ManyToOne()
    @JoinColumn(name = "secp_id")
    @ForeignKey(name = "fk_activities_licensing_secp_id_sectors_classifications_phases_secp_id")
    private CatalogoCategoriaFase catalogoCategoriaFase;

    @Getter
    @Setter
    @OneToMany(mappedBy = "actividadLicenciamiento")
    @LazyCollection(LazyCollectionOption.FALSE)
    @Filter(name = EntidadBase.FILTER_ACTIVE, condition = "geco_status = 'TRUE'")//MARIELAG para recuperar solo coordenadas con estado true
    private List<CoordenadaGeneral> coordenadasGeneral;

    @Getter
    @Setter
    @OneToMany(mappedBy = "actividadLicenciamiento")
    @Filter(name = EntidadBase.FILTER_ACTIVE, condition = "enae_status = 'TRUE'")
    private List<EvaluacionAspectoAmbiental> evaluacionAspectoAmbientales;
    
    /**
     * Historico: campo para almacenar fecha
     */
    @Getter
    @Setter
    @Column(name = "acli_date_create")
    private Date fechaCreacion;
    
    @Getter
	@Setter
	@Transient
	private Boolean actividadModificada = false;
    
    @Getter
	@Setter
	@Transient
	private Boolean coordenadaModificada = false;
    
    @Getter
	@Setter
	@Transient
	private Boolean nuevoRegistro = false;
    
    @Getter
	@Setter
	@Transient
	private Boolean nuevoEnModificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean registroModificado;
    
    @Override
	public ActividadLicenciamiento clone() throws CloneNotSupportedException {

		 ActividadLicenciamiento clone = (ActividadLicenciamiento)super.clone();
		 clone.setId(null);		 
		 return clone;
	}

}
