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
package outputdebugstring.monitor.internal;

import com.sun.jna.Native;
import com.sun.jna.examples.win32.W32API;

public interface Psapi extends W32API {

	Psapi INSTANCE = (Psapi) Native.loadLibrary("psapi", Psapi.class, DEFAULT_OPTIONS); //$NON-NLS-1$

	int GetModuleFileNameEx(HANDLE hProcess, HMODULE hModule, char[] lpFilename, int nSize);

	int GetModuleFileNameEx(HANDLE hProcess, HMODULE hModule, byte[] lpFilename, int nSize);
}
