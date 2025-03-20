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

@Entity
@Table(name = "craft_tools_catalog", schema = "suia_iii")
@NamedQuery(name = "CatalogoHerramientaArtesanal.findAll", query = "SELECT h FROM CatalogoHerramientaArtesanal h")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "crtc_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "crtc_status = 'TRUE'")
public class CatalogoHerramientaArtesanal extends EntidadBase {

	private static final long serialVersionUID = 4218664800749092817L;

	@Id
	@SequenceGenerator(name = "CRAFT_TOOL_CATALOG_GENERATOR", sequenceName = "craft_tool_catalog_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CRAFT_TOOL_CATALOG_GENERATOR")
	@Column(name = "crtc_id")
	@Getter
	@Setter
	private Integer id;

	@Column(name = "crtc_name")
	@Getter
	@Setter
	private String nombre;

	@Column(name = "crtc_description")
	@Getter
	@Setter
	private String descripcion;
	
	@Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "mipr_id")
    @ForeignKey(name = "fk_mining_enviromental_record_mipr_id_mining_processes_mipr_id")
    private ProcesoMinero procesoMinero;
	
	@Column(name = "crtc_order")
	@Getter
	@Setter
	private Integer orden;
}