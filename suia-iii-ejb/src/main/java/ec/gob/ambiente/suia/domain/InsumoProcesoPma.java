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
 * The persistent class for the fapma_process_supplies database table.
 * 
 */
@Entity
@Table(name="fapma_process_supplies", schema = "suia_iii")
@NamedQuery(name="InsumoProcesoPma.findAll", query="SELECT i FROM InsumoProcesoPma i")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "fapi_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "fapi_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "fapi_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "fapi_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "fapi_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fapi_status = 'TRUE'")
@NamedQueries({
    @NamedQuery(name = InsumoProcesoPma.OBTENER_POR_FASE, query = "SELECT c FROM InsumoProcesoPma c WHERE c.catalogoInsumo.categoriaFase.id = :p_categoriaFaseId")
})
public class InsumoProcesoPma extends EntidadAuditable {
	private static final long serialVersionUID = 1L;

	public static final String OBTENER_POR_FASE = "ec.com.magmasoft.business.domain.InsumoProcesoPma.obtenerPorFase";
	
	@Id
	@SequenceGenerator(name = "PROCCES_SUPPLIES_PMA_FAPDID_GENERATOR", sequenceName = "fapma_process_supplies_fapr_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROCCES_SUPPLIES_PMA_FAPDID_GENERATOR")
	@Column(name="fapi_id")
	@Getter
	@Setter
	private Integer id;
	
	//bi-directional many-to-one association to activity_type_catalog
	@ManyToOne
	@JoinColumn(name="suca_id")
	@ForeignKey(name = "suca_process_supplies_fk")
	@Getter
	@Setter
	private CatalogoInsumo catalogoInsumo;

	@Column(name="fapi_count")
	@Getter
	@Setter
	private Integer cantidad;
	
	@Column(name="fapi_other_description")
	@Getter
	@Setter
	private String descripcionOtros;
	
	//bi-directional many-to-one association to CatiiFapma
	@ManyToOne
	@JoinColumn(name="cafa_id")
	@ForeignKey(name = "catii_fapma_process_supplies_fk")
	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbientalPma;

    @ManyToOne
    @JoinColumn(name="meun_id")
    @ForeignKey(name = "measurement_units_fapma_process_supplies_fk")
    @Getter
    @Setter
    private UnidadMedida unidadMedida;
    
    /**
     * Cris F: campos nuevos para historial
     */
    @Getter
    @Setter
    @Column(name = "fapi_original_record_id")
    private Integer idRegistroOriginal;
    
    @Getter
    @Setter
    @Column(name = "fapi_historical_date")
    private Date fechaHistorico;
    
	@Getter
	@Setter
	@Transient
	private String nuevoEnModificacion;

	public InsumoProcesoPma() {
		super();
	}

	public InsumoProcesoPma(Integer id) {
		super();
		this.id = id;
	}    

}