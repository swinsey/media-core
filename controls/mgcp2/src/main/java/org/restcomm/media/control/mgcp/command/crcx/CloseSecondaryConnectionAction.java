/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2017, Telestax Inc and individual contributors
 * by the @authors tag. 
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.restcomm.media.control.mgcp.command.crcx;

import org.restcomm.media.control.mgcp.connection.MgcpConnection;
import org.squirrelframework.foundation.fsm.AnonymousAction;

/**
 * Action that closes the primary connection.
 * 
 * @author Henrique Rosa (henrique.rosa@telestax.com)
 *
 */
class CloseSecondaryConnectionAction
        extends AnonymousAction<CreateConnectionFsm, CreateConnectionState, CreateConnectionEvent, CreateConnectionContext>
        implements CreateConnectionAction {

    static final CloseSecondaryConnectionAction INSTANCE = new CloseSecondaryConnectionAction();

    CloseSecondaryConnectionAction() {
        super();
    }

    @Override
    public void execute(CreateConnectionState from, CreateConnectionState to, CreateConnectionEvent event, CreateConnectionContext context, CreateConnectionFsm stateMachine) {
        final MgcpConnection connection = context.getSecondaryConnection();

        // Close connection
        CloseConnectionCallback callback = new CloseConnectionCallback(false, stateMachine, context);
        connection.close(callback);

        // Callback will handle rest of the logic
    }

}
