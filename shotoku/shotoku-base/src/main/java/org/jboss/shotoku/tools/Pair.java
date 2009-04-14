/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.jboss.shotoku.tools;

/**
 * A pair of objects.
 * @param <T1>
 * @param <T2>
 * @author <a href="mailto:adam.warski@jboss.org">Adam Warski</a>
 */
public class Pair<T1, T2> {
	private T1 obj1;
	private T2 obj2;
	
	public Pair(T1 obj1, T2 obj2) {
		this.obj1 = obj1;
		this.obj2 = obj2;
	}
	
	public T1 getFirst() {
		return obj1;
	}
	
	public T2 getSecond() {
		return obj2;
	}

    public boolean equals(Object o) {
        if (o instanceof Pair) {
            Pair<?,?> p = (Pair) o;
            return p.getFirst().equals(obj1) && p.getSecond().equals(obj2);
        }

        return false;
    }

    public int hashCode() {
        return obj1.hashCode() + obj2.hashCode();
    }

    public String toString() {
        return "(" + obj1.toString() + ", " + obj2.toString() + ")";
    }
}
