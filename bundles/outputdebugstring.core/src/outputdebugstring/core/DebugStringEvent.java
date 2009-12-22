/*******************************************************************************
* Copyright (c) 2009 Philipp Kursawe.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Philipp Kursawe - initial API and implementation
******************************************************************************/
package outputdebugstring.core;

import java.util.Date;
import java.util.EventObject;

import outputdebugstring.core.internal.Psapi;

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
	private final long time;
	private transient Date date;
	private static final long firstTime = System.currentTimeMillis();

	public DebugStringEvent(final Monitor source, final int processId, final String text) {
		super(source);
		this.processId = processId;
		this.text = text;
		this.time = System.currentTimeMillis();
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

	public long getTime() {
		return this.time;
	}

	/**
	 * @return the difference from the first captured <code>DebugStringEvent</code>.
	 */
	public long getTimeOffset() {
		return this.time - firstTime;
	}

	public Date getDate() {
		if (this.date == null) {
			this.date = new Date(this.time);
		}

		return this.date;
	}
}
