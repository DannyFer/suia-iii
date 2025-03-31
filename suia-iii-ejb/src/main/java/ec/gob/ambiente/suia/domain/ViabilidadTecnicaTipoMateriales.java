package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the fapma_activities database table.
 * object create cls_mba
 */
@NamedQueries({	@NamedQuery(name = ViabilidadTecnicaTipoMateriales.GET_ALL,
							query = "SELECT tvmt FROM ViabilidadTecnicaTipoMateriales tvmt")})

@Entity

@Table(name = "technical_viability_materials_types", schema = "suia_iii")

@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "tvmt_status"))})

@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tvmt_status = 'TRUE'")

public class ViabilidadTecnicaTipoMateriales extends EntidadBase {
	
	
	private static final long serialVersionUID = 2136880072051779476L;

	public static final String GET_ALL = "ec.com.magmasoft.business.domain.ViabilidadTecnicaTipoMateriales.getAll";
	
    @Id
    @Column(name = "tvmt_id")
    @SequenceGenerator(name = "TECH_VIABILITY_MAT_TYPES_TVMT_ID_GENERATOR", sequenceName = "seq_tvmt_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TECH_VIABILITY_MAT_TYPES_TVMT_ID_GENERATOR")
    @Getter
    @Setter
    private Integer id;
    
    @Getter
    @Setter
    @Column(name = "tvmt_name", length=255)
    private String nombre;
    
    @Getter
    @Setter
    @Transient
    private String detalleNombre;
    
    @Getter
    @Setter
    @Column(name = "tvmt_group_code", length=255)
    private String codigoGrupo;
    
    
    @OneToMany(mappedBy = "viabilidadTecnicaTipoMateriales")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tvdm_status = 'TRUE'")
	@Getter
	@Setter
	private List<ViabilidadTecnicaMaterialesDiagnostico> viabilidadTecnicaMaterialesDiagnostico;
    
    
    
   
}
