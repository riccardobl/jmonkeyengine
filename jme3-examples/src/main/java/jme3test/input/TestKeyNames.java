package jme3test.input;

import com.jme3.app.SimpleApplication;
import com.jme.igui.*;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;

public class TestKeyNames extends SimpleApplication implements ActionListener {

    private IGui iGui;
    
    private boolean forward,backward,left,right,launch;
    
    public static void main(String[] args) {
        TestKeyNames app=new TestKeyNames();
        app.start();
    }


    @Override
    public void simpleInitApp() {

        // Positional mapping
        int jmeW=KeyInput.KEY_W;
        int jmeA=KeyInput.KEY_A;
        int jmeS=KeyInput.KEY_S;
        int jmeD=KeyInput.KEY_D;

        System.out.println("KeyInput.KEY_W = " + inputManager.getKeyName(jmeW));
        System.out.println("KeyInput.KEY_A = " + inputManager.getKeyName(jmeA));
        System.out.println("KeyInput.KEY_S = " + inputManager.getKeyName(jmeS));
        System.out.println("KeyInput.KEY_D = " + inputManager.getKeyName(jmeD));

        // mnemonic mapping
        int launch=KeyInput.KEY_L;
        for(int i=0;i<KeyInput.KEY_LAST;i++){
            String layoutName=inputManager.getKeyName(i);
            if(layoutName!=null){
                layoutName=layoutName.toUpperCase();
                if(layoutName.equals("L")||layoutName.equals("Б")){
                    launch=i;
                    break;
                }
            }
        }
        System.out.println("L-aunch Key (mnemonic)" + inputManager.getKeyName(launch));

        inputManager.addMapping("Launch",new KeyTrigger(launch));
        inputManager.addMapping("Forward",new KeyTrigger(jmeW));
        inputManager.addMapping("Backward",new KeyTrigger(jmeS));
        inputManager.addMapping("Left",new KeyTrigger(jmeA));
        inputManager.addMapping("Right",new KeyTrigger(jmeD));

        inputManager.addListener(this, "Launch","Forward","Backward","Left","Right");
        
        iGui=IGuiAppState.newRelative(assetManager,stateManager,guiNode,cam.getWidth(),cam.getHeight());
        

        iGui.textFont("Interface/Fonts/Default.fnt").textSize(0.04f).textColor(ColorRGBA.White);
        iGui.text("Press "+ inputManager.getKeyName(jmeW).toUpperCase(), 0f,1f,true);
        iGui.text("Press "+ inputManager.getKeyName(jmeS).toUpperCase(), 0,1f-0.05f,true);
        iGui.text("Press "+ inputManager.getKeyName(jmeA).toUpperCase(), 0,1f-0.10f,true);
        iGui.text("Press "+ inputManager.getKeyName(jmeD).toUpperCase(), 0,1f-0.15f,true);
        iGui.text("Press "+ inputManager.getKeyName(launch).toUpperCase(), 0,1f-0.20f,true);        


    }

    

    public void simpleUpdate(float tpf){
        iGui.textColor(forward?ColorRGBA.Green:ColorRGBA.Red);
        iGui.text(forward?"PRESSED":"NOT PRESSED", 0.4f,1f);

        iGui.textColor(backward?ColorRGBA.Green:ColorRGBA.Red);
        iGui.text(backward?"PRESSED":"NOT PRESSED", 0.4f,1f-0.05f);

        iGui.textColor(left?ColorRGBA.Green:ColorRGBA.Red);
        iGui.text(left?"PRESSED":"NOT PRESSED", 0.4f,1f-0.10f);

        iGui.textColor(right?ColorRGBA.Green:ColorRGBA.Red);
        iGui.text(right?"PRESSED":"NOT PRESSED", 0.4f,1f-0.15f);

        iGui.textColor(launch?ColorRGBA.Green:ColorRGBA.Red);
        iGui.text(launch?"PRESSED":"NOT PRESSED", 0.4f,1f-0.20f);
    }


    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch(name){
            case "Forward":
                forward=isPressed;
                break;
            case "Backward":
                backward=isPressed;
                break;
            case "Left":
                left=isPressed;
                break;
            case "Right":
                right=isPressed;
                break;
            case "Launch":
                launch=isPressed;
                break;
        }
    }

    

}