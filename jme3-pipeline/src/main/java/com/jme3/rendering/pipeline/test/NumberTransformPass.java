package com.jme3.rendering.pipeline.test;

import java.util.function.Function;

import com.jme3.rendering.pipeline.PipelinePass;
import com.jme3.rendering.pipeline.params.PipelineParam;
import com.jme3.rendering.pipeline.params.PipelineParamPointer;


public class NumberTransformPass  extends PipelinePass{
        PipelineParam<Number> inValue;
        PipelineParam<Number> outValue;
        Function<Number,Number> action=(n)->{return n;};

        public NumberTransformPass(){
        }
    
        public NumberTransformPass action(Function<Number,Number> action){
            this.action=action;
            return this;
        }

        public NumberTransformPass inValue(PipelineParamPointer<Number> val){
            inValue=val.get(this);
            return this;
        }

        public NumberTransformPass outValue(PipelineParamPointer<Number> val){
            outValue=val.get(this);
            return this;
        }

		@Override
		public void run(float tpf) {
			outValue.setValue(action.apply(inValue.getValue()));
		}

           
    
}