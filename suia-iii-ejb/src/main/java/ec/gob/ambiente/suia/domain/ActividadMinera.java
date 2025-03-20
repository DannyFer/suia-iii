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
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.util.Date;
import java.util.List;

import javax.persistence.NamedQueries;
import javax.persistence.Transient;

@Entity
@Table(name = "activity_mining", schema = "suia_iii")
@NamedQueries({
    @NamedQuery(name = "ActividadMinera.findAll", query = "SELECT a FROM ActividadMinera a WHERE a.estado = true"),
    @NamedQuery(name = ActividadMinera.LISTAR_POR_FICHA, query = "SELECT a FROM ActividadMinera a WHERE a.estado = true AND a.idMineria = :idMineria AND a.idRegistroOriginal = null")
})

@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "acmi_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "acmi_status = 'TRUE'")
public class ActividadMinera extends EntidadBase {

    private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
    public static final String LISTAR_POR_FICHA = PAQUETE + "ActividadMinera.listarPorFicha";

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Getter
    @Setter
    @Column(name = "acmi_id")
    @SequenceGenerator(name = "ACTIVITY_MINING_ACMI_GENERATOR", sequenceName = "activity_mining_seq", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ACTIVITY_MINING_ACMI_GENERATOR")
    private Integer id;

    @Getter
    @Setter
    @Column(name = "acmi_duration_days")
    private Integer diasDuracion;

    @Getter
    @Setter
    @Column(name = "acmi_description")
    private String descripcion;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "coac_id")
    @ForeignKey(name = "activity_mining_coac_id_commercial_activity_catalog_coac_id_fk")
    private CatalogoActividadComercial actividadComercial;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "mien_id")
    @ForeignKey(name = "activity_mining_mien_id_mining_enviromental_record_mien_id_fk")
    private FichaAmbientalMineria fichaAmbientalMineria;

    @Getter
    @Setter
    @Column(name = "mien_id", insertable = false, updatable = false)
    private Integer idMineria;

    @Getter
    @Setter
    @Transient
    private List<MatrizFactorImpacto> listaMatrizFactorImpacto;

    @Getter
    @Setter
    @Transient
    private int indice;
    
    /**
     * Cris F: aumento de campos para historial
     */
    @Getter
    @Setter
    @Column(name = "acmi_original_record_id")
    private Integer idRegistroOriginal;
    
    @Getter
    @Setter
    @Column(name = "acmi_historical_date")
    private Date fechaHistorico;

	public ActividadMinera(Integer id) {
		super();
		this.id = id;
	}

	public ActividadMinera() {
		super();
	}
	
	@Getter
    @Setter
    @Transient
    private Boolean nuevoEnModificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean registroModificado;
}
