/*
 * Copyright (c) 2009-2019 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.renderer;

import com.jme3.util.NativeObject;

/**
 * Query Objects are OpenGL Objects that are used for asynchronous queries of certain kinds of information.
 * https://www.khronos.org/opengl/wiki/Query_Object
 * 
 * @author Riccardo Balbo, Juraj Papp
 */
public class QueryObject extends NativeObject {
    /**
     * The type of queries.
     */
    public enum Type {
        AnySamplesPassed,
        AnySamplesPassedConservative,
        PrimitivesGenerated,
        SamplesPassed,
        TimeElapsed,
    }

    
    protected Type type;
    protected Renderer renderer;


    
    /**
     * Creates a new query object with the given type and index
     * 
     * @param renderer
     * @param type PrimitivesGenerated or TransformFeedbackPrimitivesGenerated
     * @param index vertex stream index
     */
    public QueryObject(Renderer renderer, Type type) {
        this.renderer = renderer;
        this.type = type;
    }
    
    /**
     * Checks is the renderer is capable of rendering this query.
     * 
     * However, since "An implementation may support 0 bits in its counter, in which case query results are always undefined and essentially useless."
     * checking just the capabilities and extensions may not be sufficient
     * to determine if query is usable.
     * 
     * Thus, if true is returned check the result of getQueryCounterBits().
     * 
     * @return true is query is supported
     */
    public boolean isQuerySupported() {
        return Caps.supports(renderer.getCaps(), this);
    }
    
    /**
     * Returns the number of counter bits or zero if not supported.
     * @return number of bits
     */
    public int getQueryCounterBits() {
        Limits limit = null;
        switch(type) {
            case AnySamplesPassed: limit = Limits.QueryAnySamplesCounterBits; break;
            case AnySamplesPassedConservative: limit = Limits.QueryAnySamplesConservativeCounterBits; break;
            case PrimitivesGenerated: limit = Limits.QueryPrimitivesGeneratedCounterBits; break;
            case SamplesPassed: limit = Limits.QuerySamplesPassedCounterBits; break;
            case TimeElapsed: limit = Limits.QueryTimeElapsedCounterBits; break;
            default: throw new IllegalArgumentException("Unknown type " + type);
        }
        Integer i = renderer.getLimits().get(limit);
        if(i == null) return 0;
        return i;
    }
    
  
    /**
     * Returns the query result or -1 if it is not yet available.
     * @return the query result or -1 if not yet available.
     * 
     * @throws IllegalArgumentException if this query has not started and ended
     */
    public long getIfReady() {
        return isResultReady()?renderer.getQueryResult(this):-1;
    }
    
    /**
     * Waits until the query result is available and retrieves it.
     * @return the result of the query
     * 
     * @throws IllegalArgumentException if this query has not started and ended
     */
    public long getAndWait() {
        return renderer.getQueryResult(this);
    }
    
    /**
     * Checks if the query result is available.
     * @return true is result is available. 
     * 
     * @throws IllegalArgumentException if this query has not started and ended
     */
    public boolean isResultReady() {
        return renderer.isQueryResultReady(this);
    }

    /**
     * Returns the query type.
     * @return type of query 
     */
    public Type getType() {
        return type;
    }

    /**
     * Start the query.
     * 
     * @throws IllegalArgumentException if this query is already started
     * @throws IllegalArgumentException if a query with given
     * type and index is already started.
     */
    public void begin() {
        renderer.beginQuery(this);
    }
    
    /**
     * End the query.
     * 
     * @throws IllegalArgumentException if this query is not started
     */
    public void end() {
        renderer.endQuery(this);
    }
    
    @Override
    public void resetObject() {
        this.id = -1;
        setUpdateNeeded();
    }

    @Override
    public void deleteObject(Object rendererObject) {
        ((Renderer)rendererObject).deleteQuery(this);
    }

    @Override
    public NativeObject createDestructableClone() {
        QueryObject q = new QueryObject(renderer, type);
        q.setId(id);
        return q;
    }

    @Override
    public long getUniqueId() {
        return ((long)OBJTYPE_QUERY << 32) | ((long)id);
    }
    
}
