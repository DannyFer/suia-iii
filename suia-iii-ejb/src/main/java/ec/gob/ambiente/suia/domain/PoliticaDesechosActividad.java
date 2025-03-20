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

@Entity
@Table(name = "wastes_policies_activities", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wsaac_status")) })
@NamedQueries({ @NamedQuery(name = PoliticaDesechosActividad.FIND_BY_WAAC, query = "SELECT p FROM PoliticaDesechosActividad p where p.politicaDesechoActividad.id = :waacid") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wsaac_status = 'TRUE'")
public class PoliticaDesechosActividad extends EntidadBase {

    private static final long serialVersionUID = -846392735976470554L;
    
    public static final String FIND_BY_WAAC = "ec.gob.ambiente.suia.domain.PoliticaDesechosActividad.findByWaac";

    @Id
    @Getter
    @Setter
    @SequenceGenerator(name = "WASTES_POLICIES_WSAAC_ID_GENERATOR", sequenceName = "seq_wsaac_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WASTES_POLICIES_WSAAC_ID_GENERATOR")
    @Column(name = "wsaac_id", unique = true, nullable = false)
    private Integer id;
    
    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "waac_id")
    @ForeignKey(name = "fk_wsaac_id_waac_id")
    @Filter(name = EntidadBase.FILTER_ACTIVE, condition = "waac_status = 'TRUE'")
    private PoliticaDesechoActividad politicaDesechoActividad;
    
    
    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "wada_id")
    @ForeignKey(name = "fk_waac_id_wada_id")
    @Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wada_status = 'TRUE'")
    private DesechoPeligroso desechoPeligroso;
    
}
