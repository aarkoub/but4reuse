package org.but4reuse.adapters.eclipse.generator.interfaces;

/**
 * @author Julien Margarido
 * @author Felix Lima Gorito
 */
public interface ISender {
	void addListener(IListener listener);

	void sendToAll(String msg);
}
