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

/**
 * object create cls_mba
 */
@NamedQueries({	@NamedQuery(name = ViabilidadTecnicaMaterialesDiagnostico.GET_ALL,
							query = "SELECT tvdm FROM ViabilidadTecnicaMaterialesDiagnostico tvdm"),
				@NamedQuery(name = ViabilidadTecnicaMaterialesDiagnostico.GET_DETALLE_MATERIALES,
							query = "SELECT tvdm FROM ViabilidadTecnicaMaterialesDiagnostico tvdm"
									+ " where estudioViabilidadTecnicaDiagnostico.id = :idEstudioViabilidadTecnicaDiagnostico"
									+ "   and viabilidadTecnicaTipoMateriales.id	 = :idViabilidadTecnicaTipoMateriales")})

@Entity

@Table(name = "technical_viability_diagnostic_materials", schema = "suia_iii")

@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "tvdm_status"))})

@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tvdm_status = 'TRUE'")

public class ViabilidadTecnicaMaterialesDiagnostico extends EntidadBase {

	private static final long serialVersionUID = -4770787779764614279L;

	public static final String GET_ALL 				 = "ec.com.magmasoft.business.domain.ViabilidadTecnicaMaterialesDiagnostico.getAll";
	public static final String GET_DETALLE_MATERIALES= "ec.com.magmasoft.business.domain.ViabilidadTecnicaMaterialesDiagnostico.getDetalleMateriales";
	
    @Id
    @Column(name = "tvdm_id")
    @SequenceGenerator(name = "TECH_VIABILITY_DIAG_MAT_TVDM_ID_GENERATOR", sequenceName = "seq_tvdm_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TECH_VIABILITY_DIAG_MAT_TVDM_ID_GENERATOR")
    @Getter
    @Setter
    private Integer id;
    
    @Getter
    @Setter
    @Column(name = "tvdm_value")
    private String valor;
    
    
    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    
    @ManyToOne
	@JoinColumn(name = "tvmt_id")
	@ForeignKey(name = "fk_technical_viability_diagnostic_materialstvmt_idtechnical_via")
    @Getter
	@Setter
	private ViabilidadTecnicaTipoMateriales viabilidadTecnicaTipoMateriales;
    
    @ManyToOne
	@JoinColumn(name = "tvsd_id")
	@ForeignKey(name = "fk_technical_viability_diagnostic_materialstvsd_id_technical_vi")
    @Getter
	@Setter
	private EstudioViabilidadTecnicaDiagnostico estudioViabilidadTecnicaDiagnostico;
    
}
