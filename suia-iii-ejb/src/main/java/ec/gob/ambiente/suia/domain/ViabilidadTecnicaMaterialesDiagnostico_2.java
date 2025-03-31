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

import ec.gob.ambiente.suia.domain.base.EntidadBase;

@NamedQueries({	@NamedQuery(name = ViabilidadTecnicaMaterialesDiagnostico_2.GET_ALL,
							query = "SELECT tvdm2 FROM ViabilidadTecnicaMaterialesDiagnostico_2 tvdm2"),

@NamedQuery(name = ViabilidadTecnicaMaterialesDiagnostico_2.GET_DETALLE_MATERIALES_2,
query = "SELECT tvdm2 FROM ViabilidadTecnicaMaterialesDiagnostico_2 tvdm2"
  + " where gestionIntegral2.id = :idGestionIntegral2"
  + " and viabilidadTecnicaTipoMateriales.id  = :idMaterialesDiagnostico")})



@Entity
@Table(name = "technical_viability_diagnostic_materials_2", schema = "suia_iii")
@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "tvdm2_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tvdm2_status = 'TRUE'")

public class ViabilidadTecnicaMaterialesDiagnostico_2 extends EntidadBase {

	private static final long serialVersionUID = -4770787779764614279L;

	public static final String GET_ALL  = "ec.com.magmasoft.business.domain.ViabilidadTecnicaMaterialesDiagnostico_2.getAll";
	public static final String GET_DETALLE_MATERIALES_2 = "ec.com.magmasoft.business.domain.ViabilidadTecnicaMaterialesDiagnostico_2.getDetalleMateriales_2";
	
    @Id
    @Column(name = "tvdm2_id")
    @SequenceGenerator(name = "TECH_VIABILITY_DIAG_MAT_TVDM_ID_GENERATOR_2", sequenceName = "seq_tvdm2_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TECH_VIABILITY_DIAG_MAT_TVDM_ID_GENERATOR_2")
    @Getter
    @Setter
    private Integer id;
    
    @Getter
    @Setter
    @Column(name = "tvdm2_value")
    private String valor;
       
    @ManyToOne
	@JoinColumn(name = "tvmt_id")
	@ForeignKey(name = "fk_technical_viability_diagnostic_materialstvmt_idtechnical_via2")
    @Getter
	@Setter
	private ViabilidadTecnicaTipoMateriales viabilidadTecnicaTipoMateriales;
    
    @ManyToOne
	@JoinColumn(name = "im_id")
	@ForeignKey(name = "fk_technical_viability_diagnostic_material_gestionIntegral_id")
    @Getter
	@Setter
	private GestionIntegral2 gestionIntegral2;
}
