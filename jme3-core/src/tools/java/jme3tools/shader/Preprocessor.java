package jme3tools.shader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

        return  new ByteArrayInputStream(code.getBytes("UTF-8"));
    }

    /**
     * #for i=0..100 ( #ifdef ENABLE_INPUT_$i $0 #endif ) 
     *  do something with $i
     * #endfor
     */
    private static final Pattern FOR_REGEX=Pattern.compile(
        "([^=]+)=\\s*([0-9]+)\\s*\\.\\.\\s*([0-9]+)\\s*\\((.+)\\)"
    );
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

                        Matcher matcher=FOR_REGEX.matcher(forDec);
                        System.out.println(forDec);
                        if(matcher.matches()){
                            String varN = "$"+matcher.group(1);
                            int start = Integer.parseInt(matcher.group(2));
                            int end = Integer.parseInt(matcher.group(3));
                            String inj=matcher.group(4);
                            if(inj.trim().isEmpty())inj="$0";
                            
                            String inCode = currentFor.toString();
                            currentFor = null;

                            for (int i = start; i < end; i++){
                                expandedCode.append("\n").append(inj.replace("$0", "\n" + inCode + "\n").replace(varN, "" + i)).append("\n");
                            }
                            captured = true;
                            continue;
                        }
                    }
                }
            }
            if (currentFor != null)
                currentFor.append(l).append("\n");
            else
                expandedCode.append(l).append("\n");
        }
        code = expandedCode.toString();
        if (captured)
            code = forMacro(code);
        return code;
    }

}