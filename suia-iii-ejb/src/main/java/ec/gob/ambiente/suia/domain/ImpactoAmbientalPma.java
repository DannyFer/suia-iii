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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


/**
 * The persistent class for the fapma_environmental_impacts database table.
 *
 */
@Entity
@Table(name = "fapma_environmental_impacts", schema = "suia_iii")
@NamedQuery(name = "ImpactoAmbientalPma.findAll", query = "SELECT f FROM ImpactoAmbientalPma f")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "faen_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "faen_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "faen_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "faen_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "faen_user_update"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "faen_status = 'TRUE'")
@NamedQueries({
    @NamedQuery(name = ImpactoAmbientalPma.OBTENER_POR_FICHA_ID, query = "SELECT i From ImpactoAmbientalPma i where i.fichaAmbientalPma.id =:idFicha and i.estado = true"),
        @NamedQuery(name = ImpactoAmbientalPma.OBTENER_POR_FICHA_ID_OTROS, query = "SELECT i From ImpactoAmbientalPma i where i.idFicha =:idFicha and i.actividadProcesoPma.actividadComercial.nombreActividad  IS NOT NULL and i.estado = true"),
    @NamedQuery(name = ImpactoAmbientalPma.OBTENER_POR_FASE, query = "SELECT c FROM ImpactoAmbientalPma c WHERE c.actividadProcesoPma.actividadComercial.categoriaFase.id = :p_categoriaFaseId")
})
public class ImpactoAmbientalPma extends EntidadAuditable {

    /**
     *
     */
    private static final long serialVersionUID = 3487777149854774295L;

    public static final String OBTENER_POR_FASE = "ec.com.magmasoft.business.domain.ImpactoAmbientalPma.obtenerPorFase";
    public static final String OBTENER_POR_FICHA_ID = "ec.com.magmasoft.business.domain.ImpactoAmbientalPma.obtenerPorFichaId";
    public static final String OBTENER_POR_FICHA_ID_OTROS = "ec.com.magmasoft.business.domain.ImpactoAmbientalPma.obtenerPorFichaIdOtros";

    @Id
    @SequenceGenerator(name = "ENVIRONMENTAL_IMPACTS_PMA_FAENID_GENERATOR", sequenceName = "fapma_environmental_impacts_faen_id_seq", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENVIRONMENTAL_IMPACTS_PMA_FAENID_GENERATOR")
    @Column(name = "faen_id")
    @Getter
    @Setter
    private Integer id;
    
    @Column(name = "faen_other_activity", length = 255)
    @Getter
    @Setter
    private String actividadOtros;
    
    @ManyToOne
    @JoinColumn(name = "cafa_id")
    @ForeignKey(name = "catii_fapma_fapma_environmental_impacts_fk")
    @Getter
    @Setter
    private FichaAmbientalPma fichaAmbientalPma;
    
    @Column(name = "cafa_id", insertable = false, updatable = false)
    @Getter
    @Setter
    private Integer idFicha;

    @ManyToOne
    @JoinColumn(name = "fapa_id")
    @Getter
    @Setter
    private ActividadProcesoPma actividadProcesoPma;
    
    /**
     * Cris F: aumento de campos para historial
     */
    @Getter
    @Setter
    @Column(name = "faen_original_record_id")
    private Integer IdRegistroOriginal;
    
    @Getter
    @Setter
    @Column(name = "faen_historical_date")
    private Date fechaHistorial;
    
    
}
