package com.jme3.anim.tween.action;

import com.jme3.anim.AnimationMask;
import com.jme3.anim.tween.Tween;

public abstract class Action implements Tween {

    protected Action[] actions;
    private double length;
    private double speed = 1;
    private AnimationMask mask;

    protected Action(Tween... tweens) {
        this.actions = new Action[tweens.length];
        for (int i = 0; i < tweens.length; i++) {
            Tween tween = tweens[i];
            if (tween instanceof Action) {
                this.actions[i] = (Action) tween;
            } else {
                this.actions[i] = new BaseAction(tween);
            }
        }
    }

    @Override
    public boolean interpolate(double t) {
        t = t * speed;
        // make sure negative time is in [0, length] range
        t = (t % length + length) % length;
        return subInterpolate(t);
    }

    public abstract boolean subInterpolate(double t);

    @Override
    public double getLength() {
        return length;
    }

    protected void setLength(double length) {
        this.length = length;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public AnimationMask getMask() {
        return mask;
    }

    public void setMask(AnimationMask mask) {
        this.mask = mask;
    }
}
