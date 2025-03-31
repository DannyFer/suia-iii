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

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * @author lili
 *
 */
@Entity
@Table(name = "installations", schema = "suia_iii")
@NamedQueries({
	@NamedQuery(name = "Instalacion.findAll", query = "SELECT i FROM Instalacion i"),
    @NamedQuery(name = Instalacion.findByRecord, query = "SELECT i FROM Instalacion i WHERE i.estado = true AND i.fichaAmbientalMineria.id = :idFicha and idRegistroOriginal = null")
})
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "inst_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "inst_status = 'TRUE'")
public class Instalacion extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7708159139465910638L;
	
	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
    public static final String findByRecord = PAQUETE + "Instalacion.listarPorFicha";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "INSTALLATIONS_GENERATOR", sequenceName = "installations_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INSTALLATIONS_GENERATOR")
	@Column(name = "inst_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "inst_description")
	private String descripcion;
	
	@Getter
	@Setter
	@ManyToOne 
	@JoinColumn(name="inca_id", referencedColumnName="inca_id")
	@ForeignKey(name = "fk_installation_inca_id_installation_catalogs_inca_id")
	private CatalogoInstalacion catalogoInstalacion;
	
	@Getter
    @Setter
    @ManyToOne 
    @JoinColumn(name = "mien_id", referencedColumnName="mien_id")
    @ForeignKey(name = "fk_installation_mien_id_mining_enviromental_record_mien_id")
    private FichaAmbientalMineria fichaAmbientalMineria;
	
	@ManyToOne
	@JoinColumn(name="cafa_id")
	@ForeignKey(name = "catii_fapma_installations_fk")
	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbientalPma;
	
	@Getter
    @Setter
    @Transient
    private int indice;
	
	//Cris F: aumento de campo
	@Getter
	@Setter
	@Column(name = "inst_original_record_id")
	private Integer idRegistroOriginal;
	
	@Getter
	@Setter
	@Column(name = "inst_historical_date")
	private Date fechaHistorico;

	public Instalacion() {
		super();
	}

	public Instalacion(Integer id) {
		super();
		this.id = id;
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