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
 package org.beaconmc.events;

public class EventException extends Exception {

    private final Throwable cause;

    public EventException(Throwable cause){
        this.cause = cause;
    }

    public EventException(){
        this.cause = null;
    }

    public EventException(Throwable cause, String message){
        super(message);
        this.cause = cause;
    }

    public EventException(String message){
        super(message);
        this.cause = null;
    }

    @Override
    public Throwable getCause(){
        return this.cause;
    }

}
