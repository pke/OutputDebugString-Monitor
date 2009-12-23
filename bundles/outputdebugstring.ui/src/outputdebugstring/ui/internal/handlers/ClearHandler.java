package outputdebugstring.ui.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import outputdebugstring.ui.Clearable;

public class ClearHandler extends AbstractHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final Clearable clearable = (Clearable) HandlerUtil.getActivePartChecked(event).getAdapter(Clearable.class);
		if (clearable != null) {
			clearable.clear();
		}
		return null;
	}

}
