/**	
 * Beacon - Open Source Minecraft Server
 * Copyright (C) 2014  Jan Delius
 * Copyright (C) 2014  Blazecube
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @author Jan Delius
 */
 package org.beaconmc.logging.abstractlogger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.beaconmc.logging.ErrorStream;


public class ErrorStreamConsole extends ErrorStream{

    private final Logger logger;

    public ErrorStreamConsole(Logger logger){
        this.logger = logger;
    }

    @Override
    protected void abstractHandle(Throwable throwable) {
        this.logger.log(Level.ERROR, "", throwable);
    }

    @Override
    protected void abstractHandle(String title, String message) {
        this.logger.log(Level.ERROR, title);
        this.logger.log(Level.ERROR, String.format("%5s", "")+message);
    }
}
