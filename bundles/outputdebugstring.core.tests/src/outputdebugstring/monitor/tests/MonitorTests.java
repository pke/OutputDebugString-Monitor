package outputdebugstring.monitor.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import outputdebugstring.core.DebugStringEvent;
import outputdebugstring.core.Kernel32;
import outputdebugstring.core.Monitor;
import outputdebugstring.core.osgi.component.Listener;
import outputdebugstring.core.osgi.component.internal.MonitorComponent;

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

	/**
	 * Makes the activate and deactivate methods accessible.
	 *
	 */
	class MonitorComponentMockup extends MonitorComponent {
		void start() {
			activate();
		}

		void stop() {
			deactivate();
		}
	}

	@Test
	public void componentListenerManagement() {
		final MonitorComponentMockup component = new MonitorComponentMockup();
		assertFalse(containsThreadName(findAllThreads(), "OutputDebugString Monitor")); //$NON-NLS-1$
		component.start();
		assertTrue(containsThreadName(findAllThreads(), "OutputDebugString Monitor")); //$NON-NLS-1$
		component.addListener(new Listener() {
			public void onDebugString(final DebugStringEvent event) {
				assertEquals("Test", event.getText()); //$NON-NLS-1$
			}
		});
		Kernel32.INSTANCE.OutputDebugString("Test"); //$NON-NLS-1$
		component.stop();
		assertFalse(containsThreadName(findAllThreads(), "OutputDebugString Monitor")); //$NON-NLS-1$
	}
}
