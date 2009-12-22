package outputdebugstring.ui.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import outputdebugstring.ui.Eraseable;

public class EraseHandler extends AbstractHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final Eraseable eraseable = (Eraseable) HandlerUtil.getActivePartChecked(event).getAdapter(Eraseable.class);
		if (eraseable != null) {
			eraseable.erase();
		}
		return null;
	}

}
