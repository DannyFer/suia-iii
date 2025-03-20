/**
* Copyright (c) 2014 MAGMASOFT (Innovando tecnologia)
* Todos los derechos reservados.
* Este software es confidencial y debe usarlo de acorde con los términos de uso.
*/
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Clase persistente para la tabla de base de datos "risks"
 * @author Juan Gabriel Guzmán
 * @version 1.0
 */
@Entity
@Table(name = "risks", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "risk_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "risk_status = 'TRUE'")
@NamedQueries({
        @NamedQuery(name = Riesgo.BUSCAR_POR_SUBTIPO, query = "SELECT r FROM Riesgo r WHERE r.subTipo = :paramSubTipo ORDER BY r.id")
})
public class Riesgo extends EntidadBase implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9012424877934073758L;


	public static final String PAQUETE = "ec.gob.ambiente.suia.domain";
    public static final String BUSCAR_POR_SUBTIPO = PAQUETE +  "Riesgo.buscarPorTipo";
	

	@Id
	@SequenceGenerator(name = "RISKS_GENERATOR", sequenceName = "risk_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RISKS_GENERATOR")
	@Column(name = "risk_id")
	@Getter
	@Setter
	private Integer id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "risu_id", referencedColumnName = "risu_id")    
    @ForeignKey(name = "fk_risksrisk_id_risk_subtypesrisu_id")
    private SubTipoRiesgo subTipo;

    @Getter
    @Setter
    @Column(name = "risk_name" , length=100)
    private String nombre;


    @OneToMany(mappedBy = "riesgo", fetch = FetchType.LAZY)
    @Getter
    @Setter
    private List<AnalisisRiesgoEia> analisis;



    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Riesgo)) {
            return false;
        }
        Riesgo other = (Riesgo) obj;
        if (((this.id == null) && (other.id != null)) || ((this.id != null) && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }



    @Override
    public String toString() {
        return getNombre();
    }






}
