package outputdebugstring.monitor;

import com.sun.jna.Native;

public interface Kernel32 extends com.sun.jna.examples.win32.Kernel32 {

	Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class, DEFAULT_OPTIONS); //$NON-NLS-1$

	void OutputDebugString(String lpOutputString);

}
