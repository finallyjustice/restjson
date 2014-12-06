/** File:		JSONRESTApplication.java
 ** Author:		Dongli Zhang
 ** Contact:	dongli.zhang0129@gmail.com
 **
 ** Copyright (C) Dongli Zhang 2014
 **
 ** This program is free software;  you can redistribute it and/or modify
 ** it under the terms of the GNU General Public License as published by
 ** the Free Software Foundation; either version 2 of the License, or
 ** (at your option) any later version.
 **
 ** This program is distributed in the hope that it will be useful,
 ** but WITHOUT ANY WARRANTY;  without even the implied warranty of
 ** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
 ** the GNU General Public License for more details.
 **
 ** You should have received a copy of the GNU General Public License
 ** along with this program;  if not, write to the Free Software 
 ** Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package com.dongli.restlet;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import org.restlet.routing.Template;

public class JSONRESTApplication extends Application {
	/**
     * Creates a root Restlet that will process all incoming requests to /object/*.
     */
    @Override
    public synchronized Restlet createInboundRoot() {
        // Create a router Restlet that routes each request to a new instance of JsonResetResource.
        Router router = new Router(getContext());

        router.attach("/object", JSONRESTResource.class).setMatchingMode(Template.MODE_STARTS_WITH);

        return router;
    }
}
