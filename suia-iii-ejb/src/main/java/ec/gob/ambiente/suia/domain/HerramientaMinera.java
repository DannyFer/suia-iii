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

@Entity
@Table(name="mining_tools", schema = "suia_iii")
@NamedQueries({
	@NamedQuery(name="HerramientaMinera.findAll", query="SELECT t FROM HerramientaMinera t"),
    @NamedQuery(name = HerramientaMinera.findByRecord, query = "SELECT t FROM HerramientaMinera t WHERE t.estado = true AND t.fichaAmbientalMineria.id = :idMineria and idRegistroOriginal = null")
})
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "mito_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "mito_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "mito_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "mito_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "mito_user_update"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mito_status = 'TRUE'")
public class HerramientaMinera extends EntidadAuditable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2774154232537261498L;
	
	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
    public static final String findByRecord = PAQUETE + "HerramientaMinera.listarPorFicha";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "MINING_TOOLS_ID_GENERATOR", sequenceName = "mito_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MINING_TOOLS_ID_GENERATOR")
	@Column(name="mito_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="mito_tool_count")
	private Integer cantidadHerramientas;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="crtc_id", referencedColumnName="crtc_id")
	@ForeignKey(name = "fk_mining_tools_crtc_id_craft_tools_catalog_crtc_id")
	private CatalogoHerramientaArtesanal catalogoHerramienta;
	
	@Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "mipr_id")
    @ForeignKey(name = "fk_mining_tools_mien_id_mining_processes_mipr_id")
    private ProcesoMinero procesoMinero;
	
	@Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "tymc_id", referencedColumnName="tymc_id")
    @ForeignKey(name = "fk_mining_tools_mien_id_type_material_catalog_tymc_id")
    private CatalogoTipoMaterial tipoObtencion;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="mien_id", referencedColumnName="mien_id")
	@ForeignKey(name = "fk_mining_tools_mien_id_mining_enviromental_record_mien_id")
	private FichaAmbientalMineria fichaAmbientalMineria;
		
	@Getter
    @Setter
    @Transient
    private int indice;
	
	@Getter
	@Setter
	@Column(name="mito_description")
	private String descripcion;
	
	//Cris F: campos para historial
	@Getter
	@Setter
	@Column(name = "mito_original_record_id")
	private Integer idRegistroOriginal;
	
	@Getter
	@Setter
	@Column(name = "mito_historical_date")
	private Date fechaHistorico;

	public HerramientaMinera(Integer id) {
		super();
		this.id = id;
	}

	public HerramientaMinera() {
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
