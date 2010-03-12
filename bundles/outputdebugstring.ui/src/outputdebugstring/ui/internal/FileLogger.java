package outputdebugstring.ui.internal;

import java.io.OutputStream;

import outputdebugstring.core.DebugStringEvent;
import outputdebugstring.core.DebugStringEventListener;

/**
 * Logs debug events to file.
 * 
 * The filelogger prepares an output stream and asks an formatter to format the debug event.
 * It then writes the formatted byte[] to the output stream.
 * 
 * Features:
 * Different formatters. The FileLogger 
 *
 */
public class FileLogger implements DebugStringEventListener {
	OutputStream os;

	public void onDebugStringEvent(final DebugStringEvent event) {
		// String output = String.format("[, args);
		// os.write(b)
	}
}
