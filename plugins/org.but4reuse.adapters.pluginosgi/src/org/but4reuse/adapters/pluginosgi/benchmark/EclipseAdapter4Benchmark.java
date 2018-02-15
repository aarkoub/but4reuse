package org.but4reuse.adapters.pluginosgi.benchmark;

import java.util.List;

import org.but4reuse.adapters.IElement;
import org.but4reuse.adapters.pluginosgi.PluginOsgiAdapter;
import org.but4reuse.adapters.pluginosgi.FileElement;
import org.but4reuse.adapters.pluginosgi.PluginElement;

/**
 * Eclipse adapter for benchmark. Do not include file elements
 */
public class EclipseAdapter4Benchmark extends PluginOsgiAdapter {

	@Override
	protected void addElement(List<IElement> elements, FileElement newElement) {
		if (newElement instanceof PluginElement) {
			elements.add(newElement);

			// System.out.println(((PluginElement)newElement).getSymbName());

			// if(((PluginElement)newElement).getName() == null ||
			// ((PluginElement)newElement).getName().contains("%")){
			// System.out.println(((PluginElement)newElement).getSymbName() +
			// " " + ((PluginElement)newElement).getName());
			// }
		}
	}

}
