package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "phase_zones", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "phzo_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "phzo_status = 'TRUE'")
public class ZonasFase extends EntidadBase{

    private static final long serialVersionUID = 1L;

    @Id
    @Getter
    @Setter
    @Column(name = "phzo_id")
    private Integer id;

    @Getter
    @Setter
    @Column(name = "phzo_name")
    private String nombre;

    @Getter
    @Setter
    @OneToMany(mappedBy = "fase")
    private List<ZonaPorFase> zonaPorFases;

    @Override
    public String toString() {
        return this.nombre;
    }

}
