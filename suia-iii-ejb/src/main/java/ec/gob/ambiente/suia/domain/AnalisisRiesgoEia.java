/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import ec.gob.ambiente.suia.dto.EntityAdjunto;

/**
 *
 * @author christian
 */
@Entity
@Table(name = "risk_analysis", schema = "suia_iii")
@NamedQueries({
    @NamedQuery(name = AnalisisRiesgoEia.LISTAR_POR_EIA, query = "SELECT a FROM AnalisisRiesgoEia a WHERE a.estado = true AND a.idEstudioImpactoAmbiental = :idEstudioImpactoAmbiental AND a.idHistorico = null order by 1"),
    @NamedQuery(name = AnalisisRiesgoEia.LISTAR_TODOS_POR_EIA, query = "SELECT a FROM AnalisisRiesgoEia a WHERE a.estado = true AND a.idEstudioImpactoAmbiental = :idEstudioImpactoAmbiental order by 1")
})
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "rian_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "rian_status = 'TRUE'")
public class AnalisisRiesgoEia extends EntidadBase implements Serializable, Cloneable {

	private static final long serialVersionUID = -3008378014327680316L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
    public static final String LISTAR_POR_EIA = PAQUETE + "AnalisisRiesgoEia.listarPorEIA";
    public static final String LISTAR_TODOS_POR_EIA = PAQUETE + "AnalisisRiesgoEia.listarTodosPorEIA";

    @Id
    @Getter
    @Setter
    @SequenceGenerator(name = "ANALISIS_RIESGO_ID_GENERATOR", initialValue = 1, sequenceName = "seq_rian_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ANALISIS_RIESGO_ID_GENERATOR")
    @Column(name = "rian_id")
    private Integer id;

    @Getter
    @Setter
    @JoinColumn(name = "eist_id", referencedColumnName = "eist_id")
    @ForeignKey(name = "fk_risk_analysis_eist_id_environmental_impact_studies_eist_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private EstudioImpactoAmbiental eistId;

    @Getter
    @Setter
    @Column(name = "eist_id", insertable = false, updatable = false)
    private Integer idEstudioImpactoAmbiental;

    @Getter
    @Setter
    @JoinColumn(name = "geca_id", referencedColumnName = "geca_id")
    @ForeignKey(name = "fk_risk_analysis_geca_id_general_catalog_geca_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private CatalogoGeneral gecaId;

    @Getter
    @Setter
    @Column(name = "geca_id", insertable = false, updatable = false)
    private Integer idCatalogo;

    @Getter
    @Setter
    @Column(name = "rian_results", length = 500)
    private String resultados;

    @Getter
    @Setter
    @Transient
    private EntityAdjunto entityAdjunto;

    @Getter
    @Setter
    @Transient
    private int indice;



    @Getter
    @Setter
    @Column(name = "rian_other_risk", length = 500)
    private String otroRiesgo;


    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "risk_id")
	@ForeignKey(name = "fk_risk_analysisrian_id_risksriskr_id")
    private Riesgo riesgo;
    
    @Getter
	@Setter
	@Column(name = "rian_historical_id")
	private Integer idHistorico;
    
    @Getter
    @Setter
    @Column(name = "rian_notification_number")
    private Integer numeroNotificacion;
    
    @Getter
	@Setter
	@Transient
	private Boolean nuevoEnModificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean registroModificado;
    
    public AnalisisRiesgoEia() {
    }

    public AnalisisRiesgoEia(Integer id) {
        this.id = id;
    }



    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AnalisisRiesgoEia)) {
            return false;
        }
        AnalisisRiesgoEia other = (AnalisisRiesgoEia) obj;
        if (((this.id == null) && (other.id != null)) || ((this.id != null) && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }



    @Override
    public String toString() {
        return getId().toString();
    }
    
    @Override
	public AnalisisRiesgoEia clone() throws CloneNotSupportedException {
		
		AnalisisRiesgoEia clone = (AnalisisRiesgoEia) super.clone();
		clone.setId(null);
		return clone;
	}
    
    @Getter
    @Setter
    @Column(name = "rian_date_create")
    private Date fechaCreacion;
}
