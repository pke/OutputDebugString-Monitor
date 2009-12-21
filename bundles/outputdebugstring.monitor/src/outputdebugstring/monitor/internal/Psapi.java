package outputdebugstring.monitor.internal;

import com.sun.jna.Native;
import com.sun.jna.examples.win32.W32API;

public interface Psapi extends W32API {

	Psapi INSTANCE = (Psapi) Native.loadLibrary("psapi", Psapi.class, DEFAULT_OPTIONS); //$NON-NLS-1$

	int GetModuleFileNameEx(HANDLE hProcess, HMODULE hModule, char[] lpFilename, int nSize);

	int GetModuleFileNameEx(HANDLE hProcess, HMODULE hModule, byte[] lpFilename, int nSize);
}
