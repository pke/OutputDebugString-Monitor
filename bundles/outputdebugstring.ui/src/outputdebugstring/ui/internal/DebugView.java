package outputdebugstring.ui.internal;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import eu.wwuk.eclipse.extsvcs.core.ComponentContext;
import eu.wwuk.eclipse.extsvcs.core.InjectedComponent;

public class DebugView extends ViewPart implements InjectedComponent {

	private ComponentContext context;

	@Override
	public void createPartControl(final Composite parent) {

	}

	@Override
	public void setFocus() {
	}

	public void setComponentContext(final ComponentContext context) {
		this.context = context;
	}

	@Override
	public void dispose() {
		this.context.disposed();
		super.dispose();
	}

}
