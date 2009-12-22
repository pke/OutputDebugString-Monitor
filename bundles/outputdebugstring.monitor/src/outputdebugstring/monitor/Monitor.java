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
package outputdebugstring.monitor;

import com.sun.jna.Pointer;
import com.sun.jna.examples.win32.Kernel32;
import com.sun.jna.examples.win32.W32API;
import com.sun.jna.examples.win32.W32API.HANDLE;

/**
 * Catches calls made to OutputDebugString.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 */
abstract public class Monitor implements Runnable {
	private static final String DBWIN_BUFFER_SHARED_FILE = "DBWIN_BUFFER"; //$NON-NLS-1$
	private static final String DBWIN_DATA_READY_EVENT = "DBWIN_DATA_READY"; //$NON-NLS-1$
	private static final String DBWIN_BUFFER_READY_EVENT = "DBWIN_BUFFER_READY"; //$NON-NLS-1$

	private final HANDLE bufferReadyEvent;
	private final HANDLE dataReadyEvent;
	private final HANDLE sharedFile;
	private final Pointer sharedMemory;

	private volatile boolean run = true;
	private final Thread thread;

	public Monitor() {
		this.thread = new Thread(this, "OutputDebugString Monitor"); //$NON-NLS-1$
		this.thread.setDaemon(true);

		this.bufferReadyEvent = Kernel32.INSTANCE.CreateEvent(null, false, false, DBWIN_BUFFER_READY_EVENT);
		this.dataReadyEvent = Kernel32.INSTANCE.CreateEvent(null, false, false, DBWIN_DATA_READY_EVENT);
		this.sharedFile = Kernel32.INSTANCE.CreateFileMapping(W32API.INVALID_HANDLE_VALUE, null,
				Kernel32.PAGE_READWRITE, 0, 4096, DBWIN_BUFFER_SHARED_FILE);
		this.sharedMemory = Kernel32.INSTANCE.MapViewOfFile(this.sharedFile, Kernel32.SECTION_MAP_READ, 0, 0, 4096);

		this.thread.start();
	}

	public void run() {
		try {
			while (this.run) {
				// Say that we are interested in receiving data ready events
				Kernel32.INSTANCE.SetEvent(this.bufferReadyEvent);

				final int ret = Kernel32.INSTANCE.WaitForSingleObject(this.dataReadyEvent, Kernel32.INFINITE);
				if (!this.run) {
					break;
				}

				if (ret == 0) { // WAIT_OBJECT_0
					// First 4 bytes contain the process ID of the process that called OutputDebugString
					final int processId = this.sharedMemory.getInt(0);
					// The remaining buffer is an ANSI zero-terminated (C style) string
					final String text = this.sharedMemory.getString(4, false);
					onDebugString(processId, text);
				}
			}
		} finally {
			if (this.sharedMemory != null) {
				Kernel32.INSTANCE.UnmapViewOfFile(this.sharedMemory);
			}
			if (!W32API.INVALID_HANDLE_VALUE.equals(this.sharedFile)) {
				Kernel32.INSTANCE.CloseHandle(this.sharedFile);
			}
			if (!W32API.INVALID_HANDLE_VALUE.equals(this.bufferReadyEvent)) {
				Kernel32.INSTANCE.CloseHandle(this.bufferReadyEvent);
			}
			if (!W32API.INVALID_HANDLE_VALUE.equals(this.dataReadyEvent)) {
				Kernel32.INSTANCE.CloseHandle(this.dataReadyEvent);
			}
		}
	}

	protected abstract void onDebugString(int processId, String text);

	/**
	 * Stop watching for ODS events.
	 */
	public void stop() {
		this.run = false;
		Kernel32.INSTANCE.SetEvent(this.dataReadyEvent);
		try {
			if (this.thread != null) {
				this.thread.join();
			}
		} catch (final InterruptedException e) {
		}
	}
}
