package com.yp.core.ref;

import java.io.Serializable;
import java.util.HashMap;

public class RefContainer<T> implements Serializable {

	private static final long serialVersionUID = 7167742062055767723L;

	private HashMap<T, IReference<T>> self;

	public RefContainer(int pSize) {
		super();
		self = new HashMap<>(pSize);
	}

	public RefContainer() {
		super();
		self = new HashMap<>();
	}

	@SafeVarargs
	public RefContainer(IReference<T>... pRefrences) {
		super();
		self = new HashMap<>(pRefrences.length);
		for (int i = 0; i < pRefrences.length; i++) {
			self.put(pRefrences[i].getKey(), pRefrences[i]);
		}
	}

	public void add(IReference<T> pRefrence) {
		self.put(pRefrence.getKey(), pRefrence);
	}

	public IReference<T> get(T pKey) {
//		if (containsKey(pKey))
//			return self.get(pKey);
//		return null;
		
		return self.computeIfPresent(pKey, (k, v) -> v);
	}

	public String getValue(T pKey) {
		if (containsKey(pKey))
			return self.get(pKey).getValue();
		return null;
	}

	public String getDescription(T pKey) {
		if (containsKey(pKey))
			return self.get(pKey).getDescription();
		return null;
	}

	public boolean containsKey(T pKey) {
		return pKey != null && self.containsKey(pKey);
	}
}
