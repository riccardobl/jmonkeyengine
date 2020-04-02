package jme3tools.shader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Preprocessor
 * 
 * @author Riccardo Balbo
 */
public class Preprocessor {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Preprocessor.class.getName());

    public static InputStream apply(InputStream in) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte chunk[] = new byte[1024];
        int read;
        while ((read = in.read(chunk)) != -1) {
            bos.write(chunk, 0, read);
        }
        bos.close();
        in.close();

        String code = bos.toString("UTF-8");

        code = Preprocessor.forMacro(code);
        // code = Preprocessor.uboSSBOMacro(code);

        return new ByteArrayInputStream(code.getBytes("UTF-8"));
    }

    /**
     * #for i=0..100 ( #ifdef ENABLE_INPUT_$i $0 #endif ) do something with $i
     * #endfor
     */
    private static final Pattern FOR_REGEX = Pattern.compile("([^=]+)=\\s*([0-9]+)\\s*\\.\\.\\s*([0-9]+)\\s*\\((.+)\\)");

    public static String forMacro(String code) {
        StringBuilder expandedCode = new StringBuilder();
        StringBuilder currentFor = null;
        String forDec = null;
        int skip = 0;
        String codel[] = code.split("\n");
        boolean captured = false;
        for (String l : codel) {
            if (!captured) {
                String ln = l.trim();
                if (ln.startsWith("#for")) {
                    if (skip == 0) {
                        forDec = ln;
                        currentFor = new StringBuilder();
                        skip++;
                        continue;
                    }
                    skip++;
                } else if (ln.startsWith("#endfor")) {
                    skip--;
                    if (skip == 0) {
                        forDec = forDec.substring("#for ".length()).trim();

                        Matcher matcher = FOR_REGEX.matcher(forDec);
                        if (matcher.matches()) {
                            String varN = "$" + matcher.group(1);
                            int start = Integer.parseInt(matcher.group(2));
                            int end = Integer.parseInt(matcher.group(3));
                            String inj = matcher.group(4);
                            if (inj.trim().isEmpty()) inj = "$0";

                            String inCode = currentFor.toString();
                            currentFor = null;

                            for (int i = start; i < end; i++) {
                                expandedCode.append("\n").append(inj.replace("$0", "\n" + inCode + "\n").replace(varN, "" + i)).append("\n");
                            }
                            captured = true;
                            continue;
                        }
                    }
                }
            }
            if (currentFor != null) currentFor.append(l).append("\n");
            else expandedCode.append(l).append("\n");
        }
        code = expandedCode.toString();
        if (captured) code = forMacro(code);
        if (logger.isLoggable(java.util.logging.Level.FINER)) logger.log(java.util.logging.Level.FINER, "Expanded for macros {0}", code);
        return code;
    }

    private static final Pattern BO_PATTERN = Pattern.compile("(.*?^\\s*)(#ubo|#ssbo)\\s*([A-Z0-9a-z_]+)(.*)", Pattern.DOTALL | Pattern.MULTILINE);

    public static String uboSSBOMacro(String code) {


        


        while (true) {
            StringBuilder expandedCode = new StringBuilder();

            Matcher matcher = BO_PATTERN.matcher(code);
            if(matcher.find()){
                String type=matcher.group(2);
                String label=matcher.group(3);

                expandedCode.append(matcher.group(1));
                expandedCode.append(type.substring(1).toUpperCase()).append("(").append(label).append(")");


                String l=matcher.group(4);
                // expandedCode.append(l);
                int level=0;
                // if (label != null) {
                    for (int i = 0; i < l.length() && label != null; i++) {
                        char c = l.charAt(i);
                        if (c == '{') level++;
                        else if (c == '}') {
                            level--;
                            if (level == 0) {
                                expandedCode.append(c);
                                expandedCode.append(label);
                                expandedCode.append(l.substring(i+1));
                                label = null;
                                break;
                            }
                        }
                        expandedCode.append(c);
                    }
                // }

                code=expandedCode.toString();
            }       else{
                break;
            }

        }
        code="#ifndef UBO\n"+
        "#define UBO(name) layout (std140, binding = name##_BIND_ID) uniform name\n"+
        "#endif\n"+        
        "#ifndef SSBO\n"+
        "#define SSBO(name) layout (std140, binding = name##_BIND_ID) buffer name\n"+
        "#endif \n"+    code;

        return code;

    }

    public static void main(String[] args) throws Exception {
        FileInputStream fis=new FileInputStream("/DEV/jmonkeyengine/jme3-pipeline/src/main/resources/Pipeline/utils/WorldParams.glsl");
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        byte chunk[]=new byte[1024];
        int read;
        while((read=fis.read(chunk))!=-1){
            bos.write(chunk,0,read);
        }
        String s=bos.toString();
        System.out.println(Preprocessor.uboSSBOMacro(s));


    }
}