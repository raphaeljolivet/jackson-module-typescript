package java2typescript.jackson.module;

import java.beans.Transient;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java2typescript.jackson.module.conf.typename.SimpleJacksonTSTypeNamingStrategy;
import java2typescript.jackson.module.conf.typename.TSTypeNamingStrategy;
import java2typescript.jackson.module.grammar.ArrayType;
import java2typescript.jackson.module.grammar.base.AbstractType;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

public class Configuration {
	private Map<String, AbstractType> customTypes = Collections.emptyMap();
	private List<String> ignoredMethodNames = new ArrayList<String>();
	private TSTypeNamingStrategy namingStrategy = new SimpleJacksonTSTypeNamingStrategy();
	private boolean jaxrsRun = false;

	public Map<String, AbstractType> getCustomTypes() {
		return customTypes;
	}

	public Configuration addType(Class<?> klass, AbstractType tsType) {
		addType(klass.getName(), tsType);
		addArrayType(klass, tsType);
		return this;
	}

	public void addArrayType(Class<?> klass, AbstractType tsType) {
		addType("[L" + klass.getName() + ";", new ArrayType(tsType));
	}

	public void addType(String className, AbstractType tsType) {
		Map<String, AbstractType> tmp = new HashMap<String, AbstractType>();
		tmp.putAll(customTypes);
		tmp.put(className, tsType);
		customTypes = Collections.unmodifiableMap(tmp);
	}

	public void addIngoredMethod(String name) {
		ignoredMethodNames.add(name);
	}

	public boolean isIgnoredMethod(Method method) {
		if (method.getAnnotation(Transient.class) != null) {
			return true;
		}
		return isIgnoredMethod(method.getName());
	}

	private boolean isIgnoredMethod(String name) {
		return ignoredMethodNames.contains(name);
	}

	public TSTypeNamingStrategy getNamingStrategy() {
		return namingStrategy;
	}

	public void setNamingStrategy(TSTypeNamingStrategy namingStrategy) {
		this.namingStrategy = namingStrategy;
	}

	public boolean getJaxrsRun() {
		return jaxrsRun;
	}

	public void setJaxrsRun(boolean bool) {
		jaxrsRun = bool;
	}

	public boolean methodHasHTTPAnnotation(Method method) {
		if (method.getAnnotation(GET.class) != null ||
				method.getAnnotation(POST.class) != null ||
				method.getAnnotation(PUT.class) != null ||
				method.getAnnotation(DELETE.class) != null) {
			return true;
		}
		return false;
	}
}
