package com.jme3.rendering.pipeline.params.smartobj;

import com.jme3.material.MatParam;
import com.jme3.rendering.pipeline.Pipeline;
import com.jme3.rendering.pipeline.PipelinePass;

public class SmartMatParam extends SmartObject<MatParam> {


  
    private final MatParam resolvedOut;
    protected SmartMatParam(MatParam value) {
        super(value);
        resolvedOut=new MatParam(value.getVarType(), value.getName(), value.getValue());
    }
    


    


    public MatParam get(Pipeline pipeline,PipelinePass pass){
        MatParam rv= super.get(pipeline, pass);
        if(rv.getValue()==null)return rv;
        SmartObject smv=SmartObject.from(rv.getValue());
        assert !(smv instanceof SmartMatParam);
        Object resValue=smv.get(pipeline,pass);
        resolvedOut.setValue(resValue);
      
         return resolvedOut;
    }
}
