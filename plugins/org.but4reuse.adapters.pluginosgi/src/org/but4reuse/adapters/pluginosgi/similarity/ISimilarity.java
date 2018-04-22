package org.but4reuse.adapters.pluginosgi.similarity;

import org.but4reuse.adapters.IElement;

public interface ISimilarity {
	public double similarity(IElement currentElement, IElement anotherElement);
}
