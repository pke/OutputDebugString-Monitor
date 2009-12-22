package outputdebugstring.ui.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "outputdebugstring.ui.internal.messages"; //$NON-NLS-1$
	public static String DebugView_DebugPrintolumnName;
	public static String DebugView_TimeColumnName;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
