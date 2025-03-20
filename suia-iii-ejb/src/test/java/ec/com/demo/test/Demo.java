package ec.com.demo.test;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@RunWith(Arquillian.class)
public class Demo {

	@EJB
	private Greeter greeter;

	@EJB
	CertificadoInterseccionFacade certificadoInterseccionService;

	@Test
	public void deberiaRetornarHola_Rene() {
		Assert.assertEquals("Hola_Rene", greeter.createGreeting("Rene"));
	}

	@Deployment
	public static JavaArchive createDeployment() {
		return ShrinkWrap.create(JavaArchive.class).addClass(Greeter.class)
				.addClass(CertificadoInterseccionFacade.class).addClass(CrudServiceBean.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}
}
