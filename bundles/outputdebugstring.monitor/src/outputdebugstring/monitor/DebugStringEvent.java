package outputdebugstring.monitor;

import java.util.EventObject;

import outputdebugstring.monitor.internal.Psapi;

import com.sun.jna.examples.win32.W32API.HANDLE;

/**
 * Event sent to Listener.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 *
 */
public class DebugStringEvent extends EventObject {
	private static final long serialVersionUID = 8342417294918417860L;
	private final String text;
	private final int processId;
	private transient String processName;

	public DebugStringEvent(final Monitor source, final int processId, final String text) {
		super(source);
		this.processId = processId;
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public int getProcessId() {
		return this.processId;
	}

	public String getProcessName() {
		if (this.processName == null) {
			final HANDLE process = Kernel32.INSTANCE.OpenProcess(Kernel32.PROCESS_VM_READ
					| Kernel32.PROCESS_QUERY_INFORMATION, false, this.processId);
			this.processName = "unknown"; //$NON-NLS-1$
			if (process != null) {
				try {
					final char filename[] = new char[260];
					final int result = Psapi.INSTANCE.GetModuleFileNameEx(process, null, filename, filename.length);
					if (result > 0) {
						this.processName = String.valueOf(filename, 0, result);
					}
				} finally {
					Kernel32.INSTANCE.CloseHandle(process);
				}
			}
		}
		return this.processName;
	}

}
