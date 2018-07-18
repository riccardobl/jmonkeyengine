/*
 * Copyright (c) 2009-2012 jMonkeyEngine
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
package com.jme3.shader.plugins;

import com.jme3.asset.*;
import com.jme3.asset.cache.AssetCache;

import java.io.*;
import java.util.*;

/**
 * GLSL File parser that supports #import pre-processor statement
 */
public class GLSLLoader implements AssetLoader {

    private AssetManager assetManager;
    private Map<String, ShaderDependencyNode> dependCache = new HashMap<>();

    /**
     * Used to load {@link ShaderDependencyNode}s.
     * Asset caching is disabled.
     */
    private class ShaderDependencyKey extends AssetKey<Reader> {

        public ShaderDependencyKey(String name) {
            super(name);
        }

        @Override
        public Class<? extends AssetCache> getCacheType() {
            // Disallow caching here
            return null;
        }
    }

    /**
     * Creates a {@link ShaderDependencyNode} from a stream representing shader code.
     *
     * @param reader   the reader with shader code
     * @param nodeName the node name.
     * @return the shader dependency node
     * @throws AssetLoadException if we failed to load the shader code.
     */
    private ShaderDependencyNode loadNode(Reader reader, String nodeName) {

        ShaderDependencyNode node = new ShaderDependencyNode(nodeName);
        StringBuilder sb = new StringBuilder();
        StringBuilder sbExt = new StringBuilder();

        try (final BufferedReader bufferedReader = new BufferedReader(reader)) {

            String ln;

            if (!nodeName.equals("[main]")) {
                sb.append("// -- begin import ").append(nodeName).append(" --\n");
            }

            while ((ln = bufferedReader.readLine()) != null) {
                if (ln.trim().startsWith("#import ")) {
                    ln = ln.trim().substring(8).trim();
                    if (ln.startsWith("\"") && ln.endsWith("\"") && ln.length() > 3) {
                        // import user code
                        // remove quotes to get filename
                        ln = ln.substring(1, ln.length() - 1);
                        if (ln.equals(nodeName)) {
                            throw new IOException("Node depends on itself.");
                        }

                        // check cache first
                        ShaderDependencyNode dependNode = dependCache.get(ln);

                        if (dependNode == null) {
                            Reader dependNodeReader = assetManager.loadAsset(new ShaderDependencyKey(ln));
                            dependNode = loadNode(dependNodeReader, ln);
                        }

                        node.addDependency(sb.length(), dependNode);
                    }
                } else if (ln.trim().startsWith("#extension ")) {
                    sbExt.append(ln).append('\n');
                } else {
                    sb.append(ln).append('\n');
                }
            }
            if (!nodeName.equals("[main]")) {
                sb.append("// -- end import ").append(nodeName).append(" --\n");
            }
        } catch (final IOException ex) {
            throw new AssetLoadException("Failed to load shader node: " + nodeName, ex);
        }

        node.setSource(sb.toString());
        node.setExtensions(sbExt.toString());
        dependCache.put(nodeName, node);
        return node;
    }

    private ShaderDependencyNode nextIndependentNode() throws IOException {
        Collection<ShaderDependencyNode> allNodes = dependCache.values();

        if (allNodes.isEmpty()) {
            return null;
        }

        for (ShaderDependencyNode node : allNodes) {
            if (node.getDependOnMe().isEmpty()) {
                return node;
            }
        }

        // Circular dependency found..
        for (ShaderDependencyNode node : allNodes){
            System.out.println(node.getName());
        }

        throw new IOException("Circular dependency.");
    }

    private String resolveDependencies(ShaderDependencyNode node, Set<ShaderDependencyNode> alreadyInjectedSet,
            StringBuilder extensions, boolean injectDependencies) {
        if (alreadyInjectedSet.contains(node)) {
            return "// " + node.getName() + " was already injected at the top.\n";
        } else {
            alreadyInjectedSet.add(node);
        }
        if (!node.getExtensions().isEmpty()) {
            extensions.append(node.getExtensions());
        }
        if (node.getDependencies().isEmpty()) {
            return node.getSource();
        } else {
            if (injectDependencies) {
                StringBuilder sb = new StringBuilder(node.getSource());
                List<String> resolvedShaderNodes = new ArrayList<>();

                for (ShaderDependencyNode dependencyNode : node.getDependencies()) {
                    resolvedShaderNodes.add(
                            resolveDependencies(dependencyNode, alreadyInjectedSet, extensions, injectDependencies));
                }

                List<Integer> injectIndices = node.getDependencyInjectIndices();
                for (int i = resolvedShaderNodes.size() - 1; i >= 0; i--) {
                    // Must insert them backwards ..
                    sb.insert(injectIndices.get(i), resolvedShaderNodes.get(i));
                }
                return sb.toString();
            } else {
                for (ShaderDependencyNode dependencyNode : node.getDependencies()) {
                    resolveDependencies(dependencyNode, alreadyInjectedSet, extensions, injectDependencies);
                }
                return null;
            }

        }
    }
    
    public static int findEndFor(int i, String lines[]) {
        int nskip = 0;
        for (int j = i + 1; j < lines.length; j++) {
            if (lines[j].trim().startsWith("#for")) {
                nskip++;
            } else if (lines[j].trim().startsWith("#endfor")) {
                if (nskip == 0) {
                    return j;
                } else
                    nskip--;
            }
        }
        return -1;
    }

    public static String processFor(String s) {
        boolean noreplace = true;
        StringBuilder processed = new StringBuilder();
        String lines[] = s.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String statement =lines[i].trim();
            if (statement.startsWith("#for")) {
                noreplace = false;
                int end = findEndFor(i, lines);
                if (end == -1)
                    System.err.println("Can't fnd #endfor");
                else {
                    String forparts[] = statement.split(" : ");
                    String countv = forparts[0].trim().split(" ")[1].trim();
                    String range[] = forparts[1].trim().split(",");

                    int range_start = Integer.parseInt(range[0]);
                    int range_end = Integer.parseInt(range[1]);
                    for (int k = range_start; k <= range_end; k++) {
                        for (int j = i + 1; j < end ; j++) {
                            processed.append("\n");
                            processed.append(lines[j].replace(countv, "" + k));
                        }
                    }
                    i = end ;
                }
            } else {
                if (i != 0)
                    processed.append("\n");
                processed.append(lines[i]);
            }
        }
        if (noreplace)
            return processed.toString();
        else
            return processFor(processed.toString());
    }


    public String prePreProcessor(String s) {
        return processFor(s);
    }
    
    @Override
    public Object load(AssetInfo info) throws IOException {
        // The input stream provided is for the vertex shader,
        // to retrieve the fragment shader, use the content manager
        this.assetManager = info.getManager();
        Reader reader = new InputStreamReader(info.openStream());
        boolean injectDependencies = true;
        if (info.getKey() instanceof ShaderAssetKey) {
            injectDependencies = ((ShaderAssetKey) info.getKey()).isInjectDependencies();
        }
        if (info.getKey().getExtension().equals("glsllib")|info.getKey().getExtension().equals("glsl")) {
            // NOTE: Loopback, GLSLLIB is loaded by this loader
            // and needs data as InputStream
            return reader;
        } else {
            ShaderDependencyNode rootNode = loadNode(reader, "[main]");
            StringBuilder extensions = new StringBuilder();
            if (injectDependencies) {
                String code = resolveDependencies(rootNode, new HashSet<ShaderDependencyNode>(), extensions, injectDependencies);
                extensions.append(prePreProcessor(code));
                dependCache.clear();
                return extensions.toString();
            } else {
                Map<String, String> files = new LinkedHashMap<>();
                HashSet<ShaderDependencyNode> dependencies = new HashSet<>();
                String code = resolveDependencies(rootNode, dependencies, extensions, injectDependencies);
                extensions.append(prePreProcessor(code));
                files.put("[main]", extensions.toString());

                for (ShaderDependencyNode dependency : dependencies) {
                    files.put(dependency.getName(), dependency.getSource());
                }

                dependCache.clear();
                return files;
            }
        }
    }
}
