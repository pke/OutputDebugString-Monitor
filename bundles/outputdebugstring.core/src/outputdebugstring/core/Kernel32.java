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

import com.sun.jna.Native;

public interface Kernel32 extends com.sun.jna.examples.win32.Kernel32 {

	Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class, DEFAULT_OPTIONS); //$NON-NLS-1$

	void OutputDebugString(String lpOutputString);

	int PROCESS_TERMINATE = 0x0001;
	int PROCESS_CREATE_THREAD = 0x0002;
	int PROCESS_SET_SESSIONID = 0x0004;
	int PROCESS_VM_OPERATION = 0x0008;
	int PROCESS_VM_READ = 0x0010;
	int PROCESS_VM_WRITE = 0x0020;
	int PROCESS_DUP_HANDLE = 0x0040;
	int PROCESS_CREATE_PROCESS = 0x0080;
	int PROCESS_SET_QUOTA = 0x0100;
	int PROCESS_SET_INFORMATION = 0x0200;
	int PROCESS_QUERY_INFORMATION = 0x0400;
	int PROCESS_SUSPEND_RESUME = 0x0800;

	// int PROCESS_ALL_ACCESS (STANDARD_RIGHTS_REQUIRED | SYNCHRONIZE | 0xFFF);

	HANDLE OpenProcess(int dwDesiredAccess, boolean bInheritHandle, int dwProcessId);
}
