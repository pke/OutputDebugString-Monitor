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

import java.util.EventListener;


/**
 * Listener to receive debug string events.
 *
 * 
 */
public interface DebugStringEventListener extends EventListener {
	/**
	 * Called when a new debug string event occurs.
	 *
	 * @param event
	 */
	void onDebugStringEvent(DebugStringEvent event);
}