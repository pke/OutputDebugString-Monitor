package outputdebugstring.core.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import outputdebugstring.core.Kernel32;
import outputdebugstring.core.Monitor;

public class MonitorTests {

	private Thread[] findAllThreads() {
		ThreadGroup group = Thread.currentThread().getThreadGroup();

		ThreadGroup topGroup = group;

		while (group != null) {
			topGroup = group;
			group = group.getParent();
		}

		final int estimatedSize = topGroup.activeCount() * 2;
		final Thread[] slackList = new Thread[estimatedSize];

		final int actualSize = topGroup.enumerate(slackList);

		final Thread[] list = new Thread[actualSize];
		System.arraycopy(slackList, 0, list, 0, actualSize);

		return list;
	}

	private boolean containsThreadName(final Thread[] threads, final String name) {
		for (int i = 0; i < threads.length; ++i) {
			if (threads[i].getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	@Test
	public void callOutputDebugString() {
		Kernel32.INSTANCE.CloseHandle(null);
		Kernel32.INSTANCE.OutputDebugString("Test"); //$NON-NLS-1$
	}

	@Test
	public void checkMonitorStop() {
		final Monitor monitor = new Monitor() {
			@Override
			protected void onDebugString(final int processId, final String text) {
			}
		};
		assertTrue(containsThreadName(findAllThreads(), "OutputDebugString Monitor")); //$NON-NLS-1$
		monitor.stop();
		assertFalse(containsThreadName(findAllThreads(), "OutputDebugString Monitor")); //$NON-NLS-1$
	}

	@Test
	public void listenerShouldBeCalled() {
		final Monitor monitor = new Monitor() {
			@Override
			protected void onDebugString(final int processId, final String text) {
				assertEquals("Test", text); //$NON-NLS-1$
			}
		};
		Kernel32.INSTANCE.OutputDebugString("Test"); //$NON-NLS-1$
		monitor.stop();
		assertFalse(containsThreadName(findAllThreads(), "OutputDebugString Monitor")); //$NON-NLS-1$
	}
}
