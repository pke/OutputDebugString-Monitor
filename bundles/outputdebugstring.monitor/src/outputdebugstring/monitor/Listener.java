package outputdebugstring.monitor;

import outputdebugstring.monitor.internal.MonitorComponent;

/**
 * Listener used by the {@link MonitorComponent} to report debug string events.
 *
 * <p>
 * OSGi services should implement and publish this interface to be notified by 
 * the {@link MonitorComponent} about new events.
 * 
 */
public interface Listener {
	/**
	 * Called when a new debug string event occurs.
	 *
	 * @param event
	 */
	void onDebugString(DebugStringEvent event);
}