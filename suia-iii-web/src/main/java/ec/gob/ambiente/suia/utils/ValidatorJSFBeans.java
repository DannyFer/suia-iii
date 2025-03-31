package ec.gob.ambiente.suia.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.reflections.Reflections;

@ManagedBean(name = "suia_validator_jsf_beans_checker_names_and_annotation", eager = true)
@ApplicationScoped
public class ValidatorJSFBeans {

	private Map<String, String> jsfBeans = new HashMap<>();

	private boolean active = true;

	@PostConstruct
	private void validate() throws Exception {
		Reflections reflections = new Reflections("ec.gob.ambiente");
		Iterator<Class<?>> iteratorAnnotated = reflections.getTypesAnnotatedWith(ManagedBean.class).iterator();
		while (iteratorAnnotated.hasNext()) {
			Class<?> type = (Class<?>) iteratorAnnotated.next();
			String customName = type.getAnnotation(ManagedBean.class).annotationType().getDeclaredMethod("name")
					.invoke(type.getAnnotation(ManagedBean.class)).toString();
			String standarName = (type.getSimpleName().charAt(0) + "").toLowerCase()
					+ type.getSimpleName().substring(1);
			if (customName != null && !customName.isEmpty())
				standarName = customName;
			if (!jsfBeans.containsKey(standarName))
				jsfBeans.put(standarName, type.getName());
			else if(active)
				throw new IllegalArgumentException("@ManagedBean for " + type.getName() + " as '" + standarName
						+ "' already defined for " + jsfBeans.get(standarName));

		}
	}

}
